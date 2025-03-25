package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.Selection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SetEditor extends Editor {

    private final long deltaX;
    private final long deltaY;
    private final long deltaZ;

    public SetEditor(Player paramPlayer, Selection paramSelection) {
        super(paramPlayer, paramSelection);
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
        return new Iterator<BlockLocation>() {

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
                Main.getPlugin().getLogger().info(String.format("%s. %s, %s, %s", blockCount, x, y, z));
                return new BlockLocation(getSelection().getWorld(), x, y, z);
            }

        };
    }
}
