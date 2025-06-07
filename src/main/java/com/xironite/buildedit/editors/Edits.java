package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.exceptions.NoWandException;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.enums.EditStatus;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.BlockCalculator;
import com.xironite.buildedit.utils.NumberUtil;
import com.xironite.buildedit.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Edits implements Iterable<BlockLocation> {

    @Getter
    @Setter
    private Player player;
    @Getter
    @Setter
    private Selection selection;
    @Getter
    @Setter
    private EditStatus status;
    private final ConfigManager configManager;
    private final WandManager wandManager;

    public Edits(Player paramPlayer, Selection paramSelection, ConfigManager paramConfigManager, WandManager paramWandManager) {
        this.setPlayer(paramPlayer);
        this.setSelection(paramSelection);
        this.setStatus(EditStatus.PENDING);
        this.configManager = paramConfigManager;
        this.wandManager = paramWandManager;
    }

    public abstract long getSize();

    public void start(List<BlockPlaceInfo> blocks, int placeSpeedInTicks) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (wandManager.contains(item)) {
            int maxSeconds = wandManager.getMaxSeconds(item) < 0 ? Integer.MAX_VALUE : wandManager.getMaxSeconds(item);
            start(blocks, placeSpeedInTicks, maxSeconds);
        } else {
            Component c = configManager.messages().getComponent(ConfigSection.ACTION_NO_WAND);
            player.sendMessage(c);
        }
    }

    public void start(List<BlockPlaceInfo> blocks, int placeSpeedInTicks, int maxSeconds) {
        // Check if selection is valid
        if (selection.getBlockPos1() == null || selection.getBlockPos2() == null) return;

        // Variables
        this.setStatus(EditStatus.IN_PROGRESS);
        final BlockCalculator calculator = new BlockCalculator(getSize(), blocks);
        final long startTime = System.currentTimeMillis();

        // Check if player has blocks to place
        if (!isCreative()) if (!consumeBlocks(blocks)) return;
        
        // Calculate blocks per execution to fit within time limit
        final long totalBlocks = getSize();
        final int maxTicks = maxSeconds * 20; // Convert seconds to ticks
        final int totalExecutions = maxTicks / placeSpeedInTicks; // How many times task will run
        final int blocksPerExecution = Math.max(1, (int) Math.ceil((double) totalBlocks / totalExecutions));

        // Calculate expected time with the new blocks per execution rate
        final double expectedSeconds = (double) totalBlocks / blocksPerExecution * placeSpeedInTicks / 20.0;

        Component c = configManager.messages().getComponent(ConfigSection.ACTION_STATUS_START);
        c = StringUtil.replace(c, "%size%", NumberUtil.toFormattedNumber(getSize()));
        c = StringUtil.replace(c, "%seconds%", String.format("%.2f", Math.min(expectedSeconds, maxSeconds)));
        player.sendMessage(c);

        // Place blocks
        new BukkitRunnable() {

            // Variables
            final Iterator<BlockLocation> iterator = iterator();

            @Override
            public void run() {
                try {
                    // Process calculated blocks per execution
                    for (int i = 0; i < blocksPerExecution && iterator.hasNext(); i++) {
                        if (calculator.hasBlocksRemaining()) {
                            finish();
                            return;
                        }

                        BlockLocation blockLocation = iterator.next();
                        Block block = blockLocation.getWorld().getBlockAt(blockLocation.toLocation());
                        BlockPlaceInfo placeInfo = calculator.selectBlock();
                        block.setType(placeInfo.getBlock(), false);
                    }

                    // Check if there are still blocks to place
                    if (!iterator.hasNext() || calculator.hasBlocksRemaining()) {
                        finish();
                    }
                } catch (Exception error) {
                    Main.plugin.getLogger().warning(error.getMessage());
                    player.sendMessage(configManager.messages().getComponent(ConfigSection.ACTION_ERROR));
                    setStatus(EditStatus.FAILED);
                    cancel();
                }
            }

            private void finish() {
                long endTime = System.currentTimeMillis();
                long elapsedTimeMs = endTime - startTime;
                String elapsedTimeSeconds = String.format("%.2f", elapsedTimeMs / 1000.0);
                Component c = configManager.messages().getComponent(ConfigSection.ACTION_STATUS_FINISH);
                c = StringUtil.replace(c, "%seconds%", elapsedTimeSeconds);
                c = StringUtil.replace(c, "%size%", NumberUtil.toFormattedNumber(getSize()));
                player.sendMessage(c);
                setStatus(EditStatus.COMPLETED);
                cancel();
            }

        }.runTaskTimer(Main.getPlugin(), 0, placeSpeedInTicks);
    }

    private boolean isCreative() {
        return player.getGameMode().equals(org.bukkit.GameMode.CREATIVE);
    }

    private boolean consumeBlocks(List<BlockPlaceInfo> blocks) {
        final BlockCalculator calculator = new BlockCalculator(getSize(), blocks);
        final Inventory inventory = this.player.getInventory();
        if (!calculator.hasBlocks(inventory)) {
            Map<Material, Long> missingBlocks = calculator.getMissingBlocks(inventory);
            String delimiter = configManager.messages().get(ConfigSection.ACTION_MISSING_DELIMITER);
            String separator = configManager.messages().get(ConfigSection.ACTION_MISSING_SEPARATOR);
            String missing = missingBlocks.entrySet().stream()
                    .map(x -> x.getKey().toString().toLowerCase() + separator + x.getValue())
                    .collect(Collectors.joining(delimiter));
            Component c = StringUtil.replace(configManager.messages().getComponent(ConfigSection.ACTION_MISSING), "%missing%", missing);
            player.sendMessage(c);
            this.setStatus(EditStatus.FAILED);
            return false;

        } else {
            if (!consumeWandUsage()) return false;
            calculator.consumeBlocks(inventory);
            return true;
        }
    }

    private boolean consumeWandUsage() {
        // Check if player has wand in hand
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if item is null or not a wand
        if (!wandManager.contains(item)) {
            player.sendMessage(configManager.messages().get(ConfigSection.ACTION_NO_WAND));
            return false;
        }

        // Check if wand has usages
        if (!wandManager.hasWandUsages(item, getSize())) {
            player.sendMessage(configManager.messages().get(ConfigSection.ACTION_NO_USAGES));
            return false;
        }

        // If all checks pass, remove usages
        wandManager.removeUsages(item, getSize());
        wandManager.setDamage(item, wandManager.getUsages(item));
        return true;
    }
}
