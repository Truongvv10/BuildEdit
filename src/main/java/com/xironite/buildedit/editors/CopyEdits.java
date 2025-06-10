package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.BlockInfo;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.Clipboard;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.models.enums.ClipBoardStatus;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.enums.EditStatus;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CopyEdits extends AbstractEdits {

    @Setter
    private Clipboard clipboard;
    private final long deltaX;
    private final long deltaY;
    private final long deltaZ;

    public CopyEdits(Player paramPlayer, Selection paramSelection, ConfigManager paramConfigManager, WandManager paramWandManager) {
        super(paramPlayer, paramSelection, paramConfigManager, paramWandManager);
        this.deltaX = Math.abs(super.getSelection().getBlockPos1().getX() - super.getSelection().getBlockPos2().getX()) + 1;
        this.deltaY = Math.abs(super.getSelection().getBlockPos1().getY() - super.getSelection().getBlockPos2().getY()) + 1;
        this.deltaZ = Math.abs(super.getSelection().getBlockPos1().getZ() - super.getSelection().getBlockPos2().getZ()) + 1;
    }
    @Override
    public long getSize() {
        return clipboard.getSize();
    }

    @Override
    public @NotNull Iterator<BlockLocation> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public BlockLocation next() {
                return null;
            }
        };
    }


    public void copy(Location origin) {
        this.status = EditStatus.IN_PROGRESS;

        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            List<BlockInfo> blocks = new ArrayList<>();

            long minX = Math.min(getSelection().getBlockPos1().getX(), getSelection().getBlockPos2().getX());
            long minY = Math.min(getSelection().getBlockPos1().getY(), getSelection().getBlockPos2().getY());
            long minZ = Math.min(getSelection().getBlockPos1().getZ(), getSelection().getBlockPos2().getZ());

            // Do all the heavy work on background thread
            for (long y = minY; y < minY + deltaY; y++) {
                for (long x = minX; x < minX + deltaX; x++) {
                    for (long z = minZ; z < minZ + deltaZ; z++) {
                        Block block = getSelection().getWorld().getBlockAt((int) x, (int) y, (int) z);

                        if (block.getType() != Material.AIR) {
                            long relX = x - origin.getBlockX();
                            long relY = y - origin.getBlockY();
                            long relZ = z - origin.getBlockZ();

                            blocks.add(new BlockInfo(
                                    block.getType(),
                                    block.getBlockData(),
                                    relX,
                                    relY,
                                    relZ
                            ));
                        }
                    }
                }
            }

            // Switch back to main thread to update game state
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                clipboard.setBlocks(blocks);
                clipboard.setStatus(ClipBoardStatus.COMPLETED);
                setStatus(EditStatus.COMPLETED);

                // Notify the player
                if (player != null && player.isOnline()) {
                    configManager.messages()
                            .getFromCache(ConfigSection.EXECUTOR_COPY)
                            .replace("%size%", blocks.size())
                            .toPlayer(player)
                            .build();
                }
            });
        });
    }

}
