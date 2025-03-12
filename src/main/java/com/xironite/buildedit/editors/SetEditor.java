package com.xironite.buildedit.editors;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SetEditor extends Editor {

    private final long width;
    private final long length;
    private final long height;

    public SetEditor(Player paramPlayer, Selection paramSelection) {
        super(paramPlayer, paramSelection);
        this.width = Math.abs(super.getSelection().getBlockPos1().getX() - super.getSelection().getBlockPos2().getX()) + 1;
        this.length = Math.abs(super.getSelection().getBlockPos1().getY() - super.getSelection().getBlockPos2().getY()) + 1;
        this.height = Math.abs(super.getSelection().getBlockPos1().getZ() - super.getSelection().getBlockPos2().getZ()) + 1;
    }

    @Override
    public long getSize() {
        return this.width * this.length * this.height;
    }

    @Override
    public @NotNull Iterator<BlockLocation> iterator() {
        return new Iterator<BlockLocation>() {

            long current = 0;
            final long minX = Math.min(getSelection().getBlockPos1().getX(), getSelection().getBlockPos2().getX());
            final long minZ = Math.min(getSelection().getBlockPos1().getZ(), getSelection().getBlockPos2().getZ());
            final long minY = Math.min(getSelection().getBlockPos1().getY(), getSelection().getBlockPos2().getY());

            public boolean hasNext() {
                return current <= getSize();
            }

            public BlockLocation next() {
                long x = (current % width) + minX;
                long z = (current % (width * length)) / length + minZ;
                long y = (current % (width * length * height)) / (length * width) + minY;
                current++;
                return new BlockLocation(getSelection().getWorld(), x, y, z);
            }

        };
    }
}
