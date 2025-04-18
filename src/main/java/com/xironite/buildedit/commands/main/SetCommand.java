package com.xironite.buildedit.commands.main;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.ListBlockFilter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetCommand extends CommandAbstract {

    public SetCommand(JavaPlugin paramPlugin, PlayerSessionManager paramSession, MessageConfig paramMessageConf, String paramName, String paramPermission, String paramSyntax, String paramDescription) {
        super(paramPlugin, paramSession, paramMessageConf, paramName, paramPermission, paramSyntax, paramDescription);
    }

    @Override
    public boolean onExecute(CommandSender sender, Command cmd, String label, String[] args) {


        // Check permission
        if (!hasPermission(sender)) return true;

        // If we have args and the first arg matches a subcommand, delegate to that subcommand
        if (args.length > 0) {
            CommandAbstract subCommand = subCommands.get("air");
            return subCommand.onCommand(sender, cmd, label, args);
        } else {
            if (sender instanceof Player player) {
                player.sendMessage(getUsage());
            }
        }

        // Otherwise execute this command
        return true;
    }

    @Override
    protected List<String> onTabbing(CommandSender sender, Command command, String label, String[] args) {
        ListBlockFilter filter = new ListBlockFilter((Player) sender);
        return filter.getTabCompletions(args[0]);
    }
}
