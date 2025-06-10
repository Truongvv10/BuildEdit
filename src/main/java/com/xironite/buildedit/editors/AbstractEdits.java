package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.enums.EditStatus;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.utils.BlockCalculator;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractEdits implements Iterable<BlockLocation> {

    @Getter @Setter
    protected Player player;
    @Getter @Setter
    protected Selection selection;
    @Getter @Setter
    protected EditStatus status;
    protected final ConfigManager configManager;
    protected final WandManager wandManager;

    public AbstractEdits(Player paramPlayer, Selection paramSelection, ConfigManager paramConfigManager, WandManager paramWandManager) {
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
            configManager.messages().getFromCache(ConfigSection.ACTION_NO_WAND)
                    .toPlayer(player)
                    .build();
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
        if (wandManager.isTimingMessageEnabled(player.getInventory().getItemInMainHand())) {
            final double expectedSeconds = (double) totalBlocks / blocksPerExecution * placeSpeedInTicks / 20.0;
            configManager.messages().getFromCache(ConfigSection.ACTION_STATUS_START)
                    .replace("%size%", getSize())
                    .replace("%seconds%", Math.min(expectedSeconds, maxSeconds))
                    .toPlayer(player)
                    .build();
        }

        // Place blocks
        new BukkitRunnable() {

            // Variables
            final Iterator<BlockLocation> iterator = iterator();

            @Override
            public void run() {
                try {
                    // Process calculated blocks per execution
                    BlockLocation blockLocation = null;
                    Block block = null;
                    for (int i = 0; i < blocksPerExecution && iterator.hasNext(); i++) {
                        if (calculator.hasBlocksRemaining()) {
                            finish();
                            return;
                        }
                        blockLocation = iterator.next();
                        block = blockLocation.getWorld().getBlockAt(blockLocation.toLocation());
                        BlockPlaceInfo placeInfo = calculator.selectBlock();
                        block.setType(placeInfo.getBlock(), false);
                    }
                    assert block != null;
                    SoundGroup soundGroup = block.getBlockData().getSoundGroup();
                    Sound sound = soundGroup.getPlaceSound();
                    block.getWorld().playSound(block.getLocation(), sound, 0.5f, 0.5f);

                    // Check if there are still blocks to place
                    if (!iterator.hasNext() || calculator.hasBlocksRemaining()) {
                        finish();
                    }
                } catch (Exception error) {
                    configManager.messages().getFromCache(ConfigSection.ACTION_ERROR)
                            .toPlayer(player)
                            .build();
                    setStatus(EditStatus.FAILED);
                    cancel();
                }
            }

            private void finish() {
                if (wandManager.isTimingMessageEnabled(player.getInventory().getItemInMainHand())) {
                    long endTime = System.currentTimeMillis();
                    long elapsedTimeMs = endTime - startTime;
                    String elapsedTimeSeconds = String.format("%.2f", elapsedTimeMs / 1000.0);
                    Component c = configManager.messages().getFromCache(ConfigSection.ACTION_STATUS_FINISH)
                            .replace("%seconds%", elapsedTimeSeconds)
                            .replace("%size%", getSize())
                            .toPlayer(player)
                            .build();
                }
                setStatus(EditStatus.COMPLETED);
                cancel();
            }

        }.runTaskTimer(Main.getPlugin(), 0, placeSpeedInTicks);
    }

    protected boolean isCreative() {
        return player.getGameMode().equals(org.bukkit.GameMode.CREATIVE);
    }

    protected boolean consumeWandUsage() {
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

    protected boolean consumeBlocks(List<BlockPlaceInfo> blocks) {
        final BlockCalculator calculator = new BlockCalculator(getSize(), blocks);
        final Inventory inventory = this.player.getInventory();
        if (!calculator.hasBlocks(inventory)) {
            Map<Material, Long> missingBlocks = calculator.getMissingBlocks(inventory);
            String delimiter = configManager.messages().get(ConfigSection.ACTION_MISSING_DELIMITER);
            String separator = configManager.messages().get(ConfigSection.ACTION_MISSING_SEPARATOR);
            String missing = missingBlocks.entrySet().stream()
                    .map(x -> x.getKey().toString().toLowerCase() + separator + x.getValue())
                    .collect(Collectors.joining(delimiter));
            configManager.messages().getFromCache(ConfigSection.ACTION_MISSING)
                    .replace("%missing%", missing)
                    .toPlayer(player)
                    .build();
            this.setStatus(EditStatus.FAILED);
            return false;

        } else {
            if (!consumeWandUsage()) return false;
            calculator.consumeBlocks(inventory);
            return true;
        }
    }
}
