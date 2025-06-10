package com.xironite.buildedit.models;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.editors.CopyEdits;
import com.xironite.buildedit.editors.PasteEdits;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.enums.ClipBoardStatus;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Clipboard {

    @Getter
    private final Player player;
    @Getter @Setter
    private List<BlockInfo> blocks;
    @Getter @Setter
    private ClipBoardStatus status;
    @Getter
    private Location copyOrigin;
    private final ConfigManager configManager;
    private final WandManager wandManager;
    private Selection selection;


    public Clipboard(Player paramPlayer, ConfigManager paramConfigManager, WandManager paramWandManager) {
        this.status = ClipBoardStatus.NOT_STARTED;
        this.player = paramPlayer;
        this.configManager = paramConfigManager;
        this.wandManager = paramWandManager;
        this.blocks = new ArrayList<>();
        this.selection = null;
    }

    public void clear() {
        this.blocks.clear();
    }

    public long getSize() {
        return blocks.size();
    }

    public String getStatusString() {
        return switch (this.status) {
            case COMPLETED -> configManager.messages().get(ConfigSection.CLIPBOARD_COMPLETED);
            case FAILED -> configManager.messages().get(ConfigSection.CLIPBOARD_FAILED);
            case IN_PROGRESS_COPYING -> configManager.messages().get(ConfigSection.CLIPBOARD_COPYING);
            case IN_PROGRESS_PASTING -> configManager.messages().get(ConfigSection.CLIPBOARD_PASTING);
            case IN_PROGRESS_ROTATING -> configManager.messages().get(ConfigSection.CLIPBOARD_ROTATING);
            default -> configManager.messages().get(ConfigSection.CLIPBOARD_NONE);
        };
    }

    public boolean isReady() {
        return this.status == ClipBoardStatus.COMPLETED || this.status == ClipBoardStatus.NOT_STARTED || this.status == ClipBoardStatus.FAILED;
    }

    public boolean hasBlocks() {
        Inventory inventory = player.getInventory();

        // Group blocks by material and count them
        Map<Material, Long> materialCounts = blocks.stream()
                .map(BlockInfo::material)
                .filter(material -> material != Material.AIR)
                .collect(Collectors.groupingBy(
                        material -> material,
                        Collectors.counting()
                ));

        // Check if player has enough of each material
        for (Map.Entry<Material, Long> entry : materialCounts.entrySet()) {
            Material material = entry.getKey();
            long requiredAmount = entry.getValue();

            if (!inventory.contains(material, (int) requiredAmount)) {
                return false;
            }
        }

        return true;
    }

    public boolean consumeBlocks() {
        Inventory inventory = player.getInventory();

        // Group blocks by material and count them
        Map<Material, Long> materialCounts = blocks.stream()
                .map(BlockInfo::material)
                .filter(material -> material != Material.AIR)
                .collect(Collectors.groupingBy(
                        material -> material,
                        Collectors.counting()
                ));

        // Remove the required amount of each material from inventory
        for (Map.Entry<Material, Long> entry : materialCounts.entrySet()) {
            Material material = entry.getKey();
            int amount = entry.getValue().intValue();

            inventory.removeItem(new ItemStack(material, amount));
        }
        return true;
    }

    public Map<Material, Long> getMissingBlocks() {
        Map<Material, Long> missingBlocks = new HashMap<>();

        Map<Material, Long> required = blocks.stream()
                .map(BlockInfo::material)
                .filter(material -> material != Material.AIR)
                .collect(Collectors.groupingBy(material -> material, Collectors.counting()));

        for (Map.Entry<Material, Long> entry : required.entrySet()) {
            Material material = entry.getKey();
            long needed = entry.getValue();
            long has = 0;

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == material) {
                    has += item.getAmount();
                }
            }

            if (has < needed) {
                missingBlocks.put(material, needed - has);
            }
        }

        return missingBlocks;
    }

    public void rotateAsync() {
        this.status = ClipBoardStatus.IN_PROGRESS_ROTATING;

        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            List<BlockInfo> rotatedBlocks = blocks.stream()
                    .map(BlockInfo::rotate)
                    .collect(Collectors.toList());

            // Switch back to main thread
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                this.blocks = rotatedBlocks;
                this.status = ClipBoardStatus.COMPLETED;

                configManager.messages()
                        .getFromCache(ConfigSection.EXECUTOR_ROTATE)
                        .replace("%size%", blocks.size())
                        .toPlayer(player)
                        .build();
            });
        });
    }

    public void copyAsync(Selection selection) {
        this.selection = selection;
        if (selection.isValid()) {
            clear();
            this.copyOrigin = player.getLocation().getBlock().getLocation();
            this.status = ClipBoardStatus.IN_PROGRESS_COPYING;
            CopyEdits edit = new CopyEdits(player, selection, configManager, wandManager);
            edit.setClipboard(this);
            edit.copy(player.getLocation().toBlockLocation());
        }
    }

    public void pasteAsync(int placeSpeedInTicks) {
        if (selection.isValid()) {
            this.status = ClipBoardStatus.IN_PROGRESS_PASTING;
            if (this.player.getGameMode() != GameMode.CREATIVE && !hasBlocks()) {
                String delimiter = configManager.messages().get(ConfigSection.ACTION_MISSING_DELIMITER);
                String separator = configManager.messages().get(ConfigSection.ACTION_MISSING_SEPARATOR);
                String missing = getMissingBlocks().entrySet().stream()
                        .map(x -> x.getKey().toString().toLowerCase() + separator + x.getValue())
                        .collect(Collectors.joining(delimiter));
                configManager.messages().getFromCache(ConfigSection.ACTION_MISSING)
                        .replace("%missing%", missing)
                        .toPlayer(player)
                        .build();
                this.status = ClipBoardStatus.FAILED;
                return;
            }
            PasteEdits edit = new PasteEdits(player, selection, configManager, wandManager, this);
            edit.paste(placeSpeedInTicks);
        }
    }

}
