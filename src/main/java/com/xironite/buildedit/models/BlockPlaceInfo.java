package com.xironite.buildedit.models;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

public class BlockPlaceInfo {

    @Getter
    private double percentage;
    @Getter
    private String wildcard;
    @Getter
    private Material block;
    @Getter
    private BlockData blockData;

    public BlockPlaceInfo(String percentage, String wildcard, String block) {
        this.percentage = Double.parseDouble(percentage.isEmpty() ? "1" : percentage.replace("%", ""));
        this.wildcard = wildcard;
        this.block = Material.getMaterial(block.replace(" ", "_").toUpperCase());
        this.blockData = null;
    }

    public BlockPlaceInfo(double percentage, String wildcard, Material block) {
        this.percentage = percentage;
        this.wildcard = wildcard;
        this.block = block;
    }
}
