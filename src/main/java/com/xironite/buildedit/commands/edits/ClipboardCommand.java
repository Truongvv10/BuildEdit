package com.xironite.buildedit.commands.edits;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.SessionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

@CommandAlias("clipboard|cb")
public class ClipboardCommand extends BaseCommand {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final SessionManager sessionManager;

    @Inject
    public ClipboardCommand(JavaPlugin paramPlugin, ConfigManager paramConfigManager, SessionManager sessionManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.sessionManager = sessionManager;
    }

    @Default
    @Subcommand("help")
    @Conditions("wands|sound")
    public void onWand(CommandSender sender) {

    }

    @Subcommand("copy")
    @Conditions("wands|sound")
    public void onCopy(CommandSender sender) {
        if (sender instanceof Player player) {
            PlayerSession session = sessionManager.getSession(player);
            if (session.getSelection().getBlockPos1() != null && session.getSelection().getBlockPos2() != null) {
                session.executeCopy();
            }
        }
    }

    @Subcommand("paste")
    @Conditions("wands|sound")
    public void onPaste(CommandSender sender) {
        if (sender instanceof Player player) {
            PlayerSession session = sessionManager.getSession(player);
            if (session.getSelection().getBlockPos1() != null && session.getSelection().getBlockPos2() != null) {
                session.executePaste();
            }
        }
    }

    @Subcommand("rotate")
    @Conditions("wands|sound")
    public void onRotate(CommandSender sender) {
        if (sender instanceof Player player) {
            PlayerSession session = sessionManager.getSession(player);
            if (session.getSelection().getBlockPos1() != null && session.getSelection().getBlockPos2() != null) {
                session.executeRotate();
            }
        }
    }
}
