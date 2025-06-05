package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class WallEdits extends Edits {
    private final long deltaX;
    private final long deltaY;
    private final long deltaZ;

    public WallEdits(Player paramPlayer, Selection paramSelection, ConfigManager paramConfigManager, WandManager paramWandManager) {
        super(paramPlayer, paramSelection, paramConfigManager, paramWandManager);
        this.deltaX = Math.abs(super.getSelection().getBlockPos1().getX() - super.getSelection().getBlockPos2().getX()) + 1;
        this.deltaY = Math.abs(super.getSelection().getBlockPos1().getY() - super.getSelection().getBlockPos2().getY()) + 1;
        this.deltaZ = Math.abs(super.getSelection().getBlockPos1().getZ() - super.getSelection().getBlockPos2().getZ()) + 1;
    }

    @Override
    public long getSize() {
        // If all dimensions are 1, it's just 1 block
        if (deltaX == 1 && deltaY == 1 && deltaZ == 1) {
            return 1;
        }

        // If Y is 1 (flat selection), calculate perimeter only
        if (deltaY == 1) {
            if (deltaX == 1 || deltaZ == 1) {
                // If it's a line, return all blocks
                return deltaX * deltaZ;
            }
            // Calculate perimeter for flat rectangle
            return 2 * (deltaX + deltaZ) - 4;
        }

        // If X or Z is 1 (vertical wall), it's a single wall face
        if (deltaX == 1 || deltaZ == 1) {
            return deltaX * deltaY * deltaZ;
        }

        // 3D box: calculate just the 4 walls (no top/bottom)
        // Each layer has the same perimeter
        long perimeterPerLayer = 2 * (deltaX + deltaZ) - 4;
        return perimeterPerLayer * deltaY;
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
                // Variables for positions
                long x, y, z;

                // Special case for 1x1x1
                if (deltaX == 1 && deltaY == 1 && deltaZ == 1) {
                    x = 0;
                    y = 0;
                    z = 0;
                }
                // Special case for vertical lines (1xNx1)
                else if (deltaX == 1 && deltaZ == 1) {
                    x = 0;
                    y = blockCount;
                    z = 0;
                }

                // Special case for flat lines
                else if (deltaY == 1 && (deltaX == 1 || deltaZ == 1)) {
                    if (deltaX == 1) {
                        x = 0;
                        z = blockCount;
                    } else {
                        x = blockCount;
                        z = 0;
                    }
                    y = 0;
                }

                // Regular wall cases
                else {
                    long blocksPerLayer = (deltaX == 1 || deltaZ == 1) ?
                            deltaX * deltaZ :
                            2 * (deltaX + deltaZ) - 4;

                    long currentY = blockCount / blocksPerLayer;
                    long posInLayer = blockCount % blocksPerLayer;

                    y = currentY;

                    // Special case for 1-thick dimensions
                    if (deltaX == 1) {
                        z = posInLayer % deltaZ;
                        x = 0;
                    } else if (deltaZ == 1) {
                        x = posInLayer % deltaX;
                        z = 0;
                    } else {
                        // Calculate perimeter position for this layer
                        if (posInLayer < deltaX) {
                            // Top edge (moving right)
                            x = posInLayer;
                            z = 0;
                        } else if (posInLayer < deltaX + deltaZ - 1) {
                            // Right edge (moving down)
                            x = deltaX - 1;
                            z = posInLayer - deltaX + 1;
                        } else if (posInLayer < 2 * deltaX + deltaZ - 2) {
                            // Bottom edge (moving left)
                            x = deltaX - 1 - (posInLayer - deltaX - deltaZ + 2);
                            z = deltaZ - 1;
                        } else {
                            // Left edge (moving up)
                            x = 0;
                            z = deltaZ - 1 - (posInLayer - 2 * deltaX - deltaZ + 3);
                        }
                    }
                }

                blockCount++;
                return new BlockLocation(getSelection().getWorld(), minX + x, minY + y, minZ + z);
            }

        };
    }
}
