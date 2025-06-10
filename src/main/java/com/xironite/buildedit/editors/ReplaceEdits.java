package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReplaceEdits extends AbstractEdits {

    @Getter
    private final List<BlockLocation> blocks;
    @Getter
    private final List<BlockPlaceInfo> targetBlocks;
    private final long deltaX;
    private final long deltaY;
    private final long deltaZ;

    public ReplaceEdits(Player paramPlayer, Selection paramSelection, ConfigManager paramConfigManager, WandManager paramWandManager, List<BlockPlaceInfo> paramTargetBlocks) {
        super(paramPlayer, paramSelection, paramConfigManager, paramWandManager);
        this.blocks = new ArrayList<>();
        this.targetBlocks = paramTargetBlocks;
        this.deltaX = Math.abs(super.getSelection().getBlockPos1().getX() - super.getSelection().getBlockPos2().getX()) + 1;
        this.deltaY = Math.abs(super.getSelection().getBlockPos1().getY() - super.getSelection().getBlockPos2().getY()) + 1;
        this.deltaZ = Math.abs(super.getSelection().getBlockPos1().getZ() - super.getSelection().getBlockPos2().getZ()) + 1;
        this.find();
    }

    @Override
    public long getSize() {
        return blocks.size();
    }

    private void find() {
        long startTime = System.currentTimeMillis();
        blocks.clear();

        long minX = Math.min(getSelection().getBlockPos1().getX(), getSelection().getBlockPos2().getX());
        long minY = Math.min(getSelection().getBlockPos1().getY(), getSelection().getBlockPos2().getY());
        long minZ = Math.min(getSelection().getBlockPos1().getZ(), getSelection().getBlockPos2().getZ());

        for (long y = 0; y < deltaY; y++) {
            for (long x = 0; x < deltaX; x++) {
                for (long z = 0; z < deltaZ; z++) {
                    BlockLocation loc = new BlockLocation(getSelection().getWorld(), minX + x, minY + y, minZ + z);
                    Block block = loc.getWorld().getBlockAt((int)loc.getX(), (int)loc.getY(), (int)loc.getZ());

                    for (BlockPlaceInfo targetBlock : targetBlocks) {
                        if (block.getType() == targetBlock.getBlock()) {
                            blocks.add(loc);
                            break;
                        }
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        Main.getPlugin().getLogger().info("Block finding completed in " + duration + "ms. Found " + blocks.size() + " blocks.");
    }

    @Override
    public @NotNull Iterator<BlockLocation> iterator() {
        return blocks.iterator();
    }
}
