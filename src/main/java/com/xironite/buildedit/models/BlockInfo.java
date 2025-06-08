package com.xironite.buildedit.models;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public record BlockInfo(Material material, BlockData data, long relX, long relY, long relZ) { }
