package com.xironite.buildedit.models;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

public record BlockInfo(Material material, BlockData data, long relX, long relY, long relZ) {
    public BlockInfo rotate() {
        BlockData rotatedData = data.clone();

        // Rotate directional blocks (like stairs, logs, etc.)
        if (rotatedData instanceof org.bukkit.block.data.Directional directional) {
            var facing = directional.getFacing();
            var rotatedFacing = switch (facing) {
                case NORTH -> org.bukkit.block.BlockFace.EAST;
                case EAST  -> org.bukkit.block.BlockFace.SOUTH;
                case SOUTH -> org.bukkit.block.BlockFace.WEST;
                case WEST  -> org.bukkit.block.BlockFace.NORTH;
                default    -> facing;
            };
            directional.setFacing(rotatedFacing);
            rotatedData = (BlockData) directional;
        }

        // Rotate coordinates around Y axis
        long newRelX = -relZ;
        long newRelZ = relX;

        return new BlockInfo(material, rotatedData, newRelX, relY, newRelZ);
    }
}
