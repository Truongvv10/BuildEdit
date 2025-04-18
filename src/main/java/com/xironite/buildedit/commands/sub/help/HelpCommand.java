package com.xironite.buildedit.commands.sub.help;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class HelpCommand extends CommandAbstract {

    public HelpCommand(JavaPlugin paramPlugin, PlayerSessionManager paramSession, MessageConfig paramMessageConf, String paramName, String paramPermission, String paramSyntax, String paramDescription) {
        super(paramPlugin, paramSession, paramMessageConf, paramName, paramPermission, paramSyntax, paramDescription);
    }

    @Override
    public boolean onExecute(CommandSender sender, Command cmd, String label, String[] args) {
        // Check permission
        if (!hasPermission(sender)) return true;

        // Send help message
        TextComponent textComponent = Component.text("<rainbow>Help help help</rainbow>");
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component test = miniMessage.deserialize(textComponent.content());
        sender.sendMessage(test);
        return true;
    }
}
