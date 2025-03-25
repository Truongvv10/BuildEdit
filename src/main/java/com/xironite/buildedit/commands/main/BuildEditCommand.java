package com.xironite.buildedit.commands.main;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.services.PlayerSessionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BuildEditCommand extends CommandAbstract {
    public BuildEditCommand(JavaPlugin paramPlugin, PlayerSessionManager paramPlayerSessionManager, String paramName, String paramPermission, String paramDescription, String paramSyntax) {
        super(paramPlugin, paramPlayerSessionManager, paramName, paramPermission, paramDescription, paramSyntax);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        TextComponent textComponent = Component.text("<rainbow>Testing</rainbow>");
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component test = miniMessage.deserialize(textComponent.content());
        sender.sendMessage(test);
        return true;
    }
}
