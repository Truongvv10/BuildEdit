package com.xironite.buildedit.models;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.editors.CopyEdits;
import com.xironite.buildedit.editors.PasteEdits;
import com.xironite.buildedit.editors.SetEdits;
import com.xironite.buildedit.models.enums.CopyStatus;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Clipboard {

    @Getter
    private final Player player;
    @Getter
    private List<BlockInfo> blocks;
    @Getter
    private World world;
    @Getter
    private CopyStatus status;
    @Getter
    private Location copyOrigin;
    private final ConfigManager configManager;
    private final WandManager wandManager;
    private Selection selection;


    public Clipboard(Player paramPlayer, ConfigManager paramConfigManager, WandManager paramWandManager) {
        this.status = CopyStatus.NOT_STARTED;
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

    public void copy(Selection selection) {
        this.selection = selection;
        this.world = selection.getWorld();
        if (selection.isValid()) {
            clear();
            this.copyOrigin = player.getLocation().getBlock().getLocation();
            this.status = CopyStatus.IN_PROGRESS;
            CopyEdits edit = new CopyEdits(player, selection, configManager, wandManager);
            edit.copyBlocks(1024, player.getLocation().toBlockLocation()).thenAccept(b -> {
                blocks.addAll(b);
                this.status = CopyStatus.COMPLETED;
                edit.setClipboard(this);
                player.sendMessage("Copied " + blocks.size() + " blocks"); // Add this
                Main.getPlugin().getLogger().info("Copied " + blocks.size() + " blocks");
            });
        }
    }

    public void paste(Location pasteLocation, int placeSpeedInTicks) {
        if (selection.isValid()) {
            PasteEdits edit = new PasteEdits(player, selection, configManager, wandManager, this);
            edit.paste(1);
        }
    }

}
