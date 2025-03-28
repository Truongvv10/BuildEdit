package com.xironite.buildedit.commands.sub;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.services.PlayerSessionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SetCommand extends CommandAbstract {

    public SetCommand(JavaPlugin paramPlugin, PlayerSessionManager paramPlayerSessionManager, String paramName, String paramPermission, String paramDescription, String paramSyntax) {
        super(paramPlugin, paramPlayerSessionManager, paramName, paramPermission, paramDescription, paramSyntax);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        // If we have args and the first arg matches a subcommand, delegate to that subcommand
        if (args.length > 0) {
            CommandAbstract subCommand = subCommands.get("block");
            return subCommand.onCommand(sender, command, label, args);
        }

        // Otherwise execute this command
        return execute(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        // Initialize variables
        Player player = (Player) sender;
        Inventory inventory = player .getInventory();
        String current = args[0];

        // Pattern to match the block type and percentage
        Pattern patternSingleBlock = Pattern.compile("(\\d+%?)?(\\w+)");

        // Initialize the list of block types
        List<String> blockTypes = Arrays.stream(inventory.getContents())
                .filter(item -> item != null && item.getType().isBlock())
                .map(item -> item.getType().name().toLowerCase())
                .distinct()
                .collect(Collectors.toList());
        blockTypes.add("air");

        // Filter the block types based on the current input
        if (current.contains(",")) {
            String word = current.substring(current.lastIndexOf(',') + 1);
            return blockTypes.stream()
                    .filter(x -> x.startsWith(word))
                    .map(x -> current + x.substring(word.length()))
                    .toList();
        } else  {
            return blockTypes.stream()
                    .filter(x -> x.startsWith(current))
                    .toList();
        }
    }
}
