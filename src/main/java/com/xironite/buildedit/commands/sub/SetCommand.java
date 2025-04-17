package com.xironite.buildedit.commands.sub;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.ListBlockFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SetCommand extends CommandAbstract {

    public SetCommand(JavaPlugin paramPlugin, PlayerSessionManager paramSession, MessageConfig paramMessageConf, String paramName, String paramPermission, String paramSyntax, String paramDescription) {
        super(paramPlugin, paramSession, paramMessageConf, paramName, paramPermission, paramSyntax, paramDescription);
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
        } else {
            if (sender instanceof Player player) {
                player.sendMessage(getUsage());
            }
        }

        // Otherwise execute this command
        return execute(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        ListBlockFilter filter = new ListBlockFilter((Player) sender);
        return filter.getTabCompletions(args[0]);
    }
}
