package com.xironite.buildedit.utils;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ListBlockFilter {

    // region Fields
    private List<String> availableBlocks;
    private Pattern pattern;
    // endregion

    // region Constructor
    public ListBlockFilter(Player player) {
        this.pattern = Pattern.compile("^(\\d+%?)?(\\^+|\\*+)?([a-zA-Z_]+)?");
        this.availableBlocks = Arrays.stream(player.getInventory().getContents())
                .filter(item -> item != null && item.getType().isBlock())
                .map(item -> item.getType().name().toLowerCase())
                .distinct()
                .collect(Collectors.toList());
        availableBlocks.add("air");
    }
    // endregion

    // region Methods
    public List<String> getTabCompletions(String args) {
        if (args.contains(",")) {
            return handleCommaInput(args, availableBlocks);
        } else {
            return handleSingleInput(args, availableBlocks);
        }
    }

    private List<String> handleCommaInput(String args, List<String> blockTypes) {
        String current = args.substring(args.lastIndexOf(',') + 1);
        String previous = args.substring(0, args.lastIndexOf(',') + 1);
        Matcher matcher = pattern.matcher(current);

        if (matcher.find()) {
            String percentage = matcher.group(1) != null ? matcher.group(1) : "";
            String state = matcher.group(2) != null ? matcher.group(2) : "";
            String block = matcher.group(3) != null ? matcher.group(3) : "";

            return blockTypes.stream()
                    .filter(x -> x.startsWith(block))
                    .map(x -> previous + percentage + state + x)
                    .toList();
        } else return List.of();
    }

    private List<String> handleSingleInput(String current, List<String> blockTypes) {
        Matcher matcher = pattern.matcher(current);

        if (matcher.find()) {
            String percentage = matcher.group(1) != null ? matcher.group(1) : "";
            String state = matcher.group(2) != null ? matcher.group(2) : "";
            String block = matcher.group(3) != null ? matcher.group(3) : "";

            return blockTypes.stream()
                    .filter(x -> x.startsWith(block))
                    .map(x -> percentage + state + x)
                    .toList();
        } else return List.of();
    }
    // endregion
}
