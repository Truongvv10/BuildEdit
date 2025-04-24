package com.xironite.buildedit.models;

import com.xironite.buildedit.Main;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockPlaceInfo {

    @Getter
    private double percentage;
    @Getter
    private String state;
    @Getter
    private Material block;

    public BlockPlaceInfo(String percentage, String state, String block) {
        this.percentage = Double.parseDouble(percentage.isEmpty() ? "1" : percentage.replace("%", ""));
        this.state = state;
        this.block = Material.getMaterial(block.replace(" ", "_").toUpperCase());

    }

    public BlockPlaceInfo(double percentage, String state, Material block) {
        this.percentage = percentage;
        this.state = state;
        this.block = block;
    }
}
