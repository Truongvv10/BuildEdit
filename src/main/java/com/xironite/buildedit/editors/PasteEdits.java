package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.*;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.enums.ClipBoardStatus;
import com.xironite.buildedit.models.enums.EditStatus;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class PasteEdits extends AbstractEdits {

    @Setter
    private Clipboard clipboard;

    public PasteEdits(Player paramPlayer, Selection paramSelection, ConfigManager paramConfigManager, WandManager paramWandManager, Clipboard paramClipboard) {
        super(paramPlayer, paramSelection, paramConfigManager, paramWandManager);
        this.clipboard = paramClipboard;
    }

    @Override
    public long getSize() {
        return clipboard.getSize();
    }

    @Override
    public @NotNull Iterator<BlockLocation> iterator() {
        return new Iterator<>() {

            int blockCount = 0;
            final Location pasteOrigin = getPlayer().getLocation().getBlock().getLocation();

            @Override
            public boolean hasNext() {
                return blockCount < getSize();
            }

            @Override
            public BlockLocation next() {
                BlockInfo blockInfo = clipboard.getBlocks().get(blockCount);
                long worldX = pasteOrigin.getBlockX() + blockInfo.relX();
                long worldY = pasteOrigin.getBlockY() + blockInfo.relY();
                long worldZ = pasteOrigin.getBlockZ() + blockInfo.relZ();
                blockCount++;
                return new BlockLocation(player.getWorld(), worldX, worldY, worldZ);
            }
        };
    }

    public void paste(int placeSpeedInTicks) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (wandManager.contains(item)) {
            int maxSeconds = wandManager.getMaxSeconds(item) < 0 ? Integer.MAX_VALUE : wandManager.getMaxSeconds(item);
            paste(placeSpeedInTicks, maxSeconds);
        } else {
            configManager.messages().getFromCache(ConfigSection.ACTION_NO_WAND)
                    .toPlayer(player)
                    .build();
        }
    }

    public void paste(int placeSpeedInTicks, int maxSeconds) {
        // Variables
        this.setStatus(EditStatus.IN_PROGRESS);
        final long startTime = System.currentTimeMillis();

        // Check if player is in creative mode
        if (!isCreative()) {
            clipboard.consumeBlocks();
            consumeWandUsage();
        }

        // Calculate blocks per execution to fit within time limit
        final long totalCopiedBlocks = getSize();
        final int maxTicks = maxSeconds * 20; // Convert seconds to ticks
        final int totalExecutions = maxTicks / placeSpeedInTicks; // How many times task will run
        final int blocksPerExecution = Math.max(1, (int) Math.ceil((double) totalCopiedBlocks / totalExecutions));

        // Calculate expected time with the new blocks per execution rate
        if (wandManager.isTimingMessageEnabled(player.getInventory().getItemInMainHand())) {
            final double expectedSeconds = (double) totalCopiedBlocks / blocksPerExecution * placeSpeedInTicks / 20.0;
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
            int currentIndex = 0;

            @Override
            public void run() {
                try {
                    Block block = null;
                    for (int i = 0; i < blocksPerExecution && iterator.hasNext(); i++) {
                        BlockLocation blockLocation = iterator.next();
                        BlockInfo blockInfo = clipboard.getBlocks().get(currentIndex++);
                        block = blockLocation.getWorld().getBlockAt(blockLocation.toLocation());
                        if (configManager.blacklist().isBlacklisted(block.getType().name())) {
                            i--;
                            continue;
                        }
                        block.setType(blockInfo.material(), false);
                        block.setBlockData(blockInfo.data(), false);
                    }
                    assert block != null;
                    SoundGroup soundGroup = block.getBlockData().getSoundGroup();
                    Sound sound = soundGroup.getPlaceSound();
                    block.getWorld().playSound(block.getLocation(), sound, 0.5f, 0.5f);

                    // Check if there are still blocks to place
                    if (!iterator.hasNext()) {
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
                clipboard.setStatus(ClipBoardStatus.COMPLETED);
                cancel();
            }

        }.runTaskTimer(Main.getPlugin(), 0, placeSpeedInTicks);
    }


}
