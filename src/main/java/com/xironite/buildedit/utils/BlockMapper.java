package com.xironite.buildedit.utils;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.BlockPlaceInfo;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockMapper {
    public static List<BlockPlaceInfo> mapParamsToBlockInfo(String args) {
        String[] splitArgs = args.split(",");
        Pattern pattern = Pattern.compile("^(\\d+%?)?(\\^+|\\*+)?([a-zA-Z_]+)");
        return Arrays.stream(splitArgs)
                .map(x -> {
                    Matcher matcher = pattern.matcher(x);
                    if (matcher.find()) {
                        String percentage = matcher.group(1) != null ? matcher.group(1) : "";
                        String state = matcher.group(2) != null ? matcher.group(2) : "";
                        String block = matcher.group(3) != null ? matcher.group(3) : "";
                        Main.getPlugin().getLogger().info("percentage: " + percentage);
                        Main.getPlugin().getLogger().info("state: " + state);
                        Main.getPlugin().getLogger().info("block: " + block);
                        return new BlockPlaceInfo(percentage, state, block);
                    } else return null;
                }).toList();
    }
}
