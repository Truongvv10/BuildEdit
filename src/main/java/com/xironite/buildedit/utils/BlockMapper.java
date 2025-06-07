package com.xironite.buildedit.utils;

import com.xironite.buildedit.models.BlockPlaceInfo;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockMapper {

    private static final String patternString = "^(\\d+%?)?(\\^+|\\*+)?([a-zA-Z_]+)";

    public static List<BlockPlaceInfo> mapParamsToBlockInfo(String args) {
        String[] splitArgs = args.split(",");
        Pattern pattern = Pattern.compile(patternString);
        return Arrays.stream(splitArgs)
                .map(x -> {
                    Matcher matcher = pattern.matcher(x);
                    if (matcher.find()) {
                        String percentage = matcher.group(1) != null ? matcher.group(1) : "";
                        String wildcards = matcher.group(2) != null ? matcher.group(2) : "";
                        String block = matcher.group(3) != null ? matcher.group(3) : "";
                        return new BlockPlaceInfo(percentage, wildcards, block);
                    } else return null;
                }).toList();
    }

    public static boolean areAllValidMaterials(String args) {
        String[] splitArgs = args.split(",");
        Pattern pattern = Pattern.compile(patternString);
        return Arrays.stream(splitArgs)
                .allMatch(x -> {
                    Matcher matcher = pattern.matcher(x);
                    if (matcher.find()) {
                        String block = matcher.group(3) != null ? matcher.group(3) : "";
                        return Material.matchMaterial(block) != null;
                    }
                    return false;
                });
    }
}
