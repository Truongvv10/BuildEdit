package com.xironite.buildedit;

import com.xironite.buildedit.commands.main.BuildEditCommand;
import com.xironite.buildedit.commands.sub.HelpCommand;
import com.xironite.buildedit.listeners.PlayerInteract;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    public static Main plugin;

    public Main() {
        plugin = this;
    }

    public void onEnable() {
        getLogger().info(ChatColor.GOLD + "Plugin started up");
        this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        registerCommands();
    }

    public void onDisable() {

    }

    private void registerCommands() {
        BuildEditCommand buildEditCommand = new BuildEditCommand(
                this,                          // pass the plugin instance
                "buildedit",                   // command name
                "buildedit.use",               // permission
                "Main command for BuildEdit",  // description
                "/buildedit test"      // syntax
        );
        HelpCommand helpCommand = new HelpCommand(
                this,
                "help",
                "buildedit.help",
                "Help command",
                "/buildedit help"
        );
        buildEditCommand.addSubCommand(helpCommand);
        buildEditCommand.register();
    }
}
