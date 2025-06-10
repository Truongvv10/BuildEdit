package com.xironite.buildedit.editors;

import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.Selection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SetEdits extends AbstractEdits {

    private final long deltaX;
    private final long deltaY;
    private final long deltaZ;

    public SetEdits(Player paramPlayer, Selection paramSelection, ConfigManager paramConfigManager, WandManager paramWandManager) {
        super(paramPlayer, paramSelection, paramConfigManager, paramWandManager);
        this.deltaX = Math.abs(super.getSelection().getBlockPos1().getX() - super.getSelection().getBlockPos2().getX()) + 1;
        this.deltaY = Math.abs(super.getSelection().getBlockPos1().getY() - super.getSelection().getBlockPos2().getY()) + 1;
        this.deltaZ = Math.abs(super.getSelection().getBlockPos1().getZ() - super.getSelection().getBlockPos2().getZ()) + 1;
    }

    @Override
    public long getSize() {
        return this.deltaX * this.deltaZ * this.deltaY;
    }

    @Override
    public @NotNull Iterator<BlockLocation> iterator() {
        return new Iterator<>() {

            long blockCount = 0;
            final long minX = Math.min(getSelection().getBlockPos1().getX(), getSelection().getBlockPos2().getX());
            final long minY = Math.min(getSelection().getBlockPos1().getY(), getSelection().getBlockPos2().getY());
            final long minZ = Math.min(getSelection().getBlockPos1().getZ(), getSelection().getBlockPos2().getZ());

            public boolean hasNext() {
                return blockCount < getSize();
            }

            public BlockLocation next() {
                long x = (blockCount % deltaX) + minX;
                long y = (blockCount % (deltaX * deltaZ * deltaY)) / (deltaZ * deltaX) + minY;
                long z = (blockCount % (deltaX * deltaZ)) / deltaX + minZ;
                blockCount++;
                return new BlockLocation(getSelection().getWorld(), x, y, z);
            }

        };
    }
}
