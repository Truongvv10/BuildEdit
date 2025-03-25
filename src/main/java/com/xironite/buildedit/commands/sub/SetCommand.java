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
        Player player = (Player) sender;
        Inventory inventory = player.getInventory();
        List<String> distinctItemNames = Arrays.stream(inventory.getContents())
                .filter(item -> item != null && item.getType().isBlock())  // Filter out null and air items
                .map(item -> item.getType().name().toLowerCase())  // Convert ItemStack to material name
                .distinct()  // Remove duplicates
                .toList();
        return distinctItemNames.isEmpty() ? List.of() : distinctItemNames ;
    }
}
