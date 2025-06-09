package com.xironite.buildedit.commands.edits;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.SessionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

@CommandAlias("copy")
public class CopyCommand extends BaseCommand {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final SessionManager sessionManager;

    @Inject
    public CopyCommand(JavaPlugin paramPlugin, ConfigManager paramConfigManager, SessionManager sessionManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.sessionManager = sessionManager;
    }

    @Default
    @Conditions("wands|sound")
    public void onCopy(CommandSender sender) {
        if (sender instanceof Player player) {
            PlayerSession session = sessionManager.getSession(player);
            if (session.getSelection().getBlockPos1() != null && session.getSelection().getBlockPos2() != null) {
                session.executeCopy();
            }
        }
    }

}
