package com.xironite.buildedit.commands.main;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BuildEditCommand extends CommandAbstract {
    public BuildEditCommand(JavaPlugin paramPlugin, PlayerSessionManager paramPlayerSessionManager, MessageConfig paramMessageConf, String paramName, String paramPermission, String paramSyntax, String paramDescription) {
        super(paramPlugin, paramPlayerSessionManager, paramMessageConf, paramName, paramPermission, paramSyntax, paramDescription);
    }

    @Override
    public boolean onExecute(CommandSender sender, Command cmd, String label, String[] args) {
        // Check permission
        if (!hasPermission(sender)) return true;

        // Send help message
        sender.sendMessage(messageConfig.getComponent(ConfigSection.HELP));
        return true;
    }
}
