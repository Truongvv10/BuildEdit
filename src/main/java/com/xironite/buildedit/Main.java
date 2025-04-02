package com.xironite.buildedit;

import com.xironite.buildedit.commands.main.BuildEditCommand;
import com.xironite.buildedit.commands.sub.BlockCommand;
import com.xironite.buildedit.commands.sub.HelpCommand;
import com.xironite.buildedit.commands.sub.SetCommand;
import com.xironite.buildedit.commands.sub.WandCommand;
import com.xironite.buildedit.listeners.PlayerInteractListener;
import com.xironite.buildedit.listeners.PlayerJoinLeaveListener;
import com.xironite.buildedit.services.PlayerSessionManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Main extends JavaPlugin {

    @Getter
    public static Main plugin;
    @Getter
    private PlayerSessionManager playerSessionManager;

    public Main() {
        plugin = this;
        playerSessionManager = new PlayerSessionManager(this);
    }

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, playerSessionManager), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this, playerSessionManager), this);
        registerCommands();
    }

    public void onDisable() {

    }

    private void registerCommands() {
        BuildEditCommand buildEditCommand = new BuildEditCommand(
                this,
                this.getPlayerSessionManager(),
                "buildedit",
                "buildedit.use",
                "Main command for BuildEdit",
                "/buildedit"
        );
        SetCommand setCommand = new SetCommand(
                this,
                this.getPlayerSessionManager(),
                "set",
                "buildedit.set",
                "Set command",
                "/buildedit set"
        );
        BlockCommand blockCommand = new BlockCommand(
                this,
                this.getPlayerSessionManager(),
                "block",
                "buildedit.set",
                "Set command",
                "/buildedit set"
        );
        HelpCommand helpCommand = new HelpCommand(
                this,
                this.getPlayerSessionManager(),
                "help",
                "buildedit.help",
                "Help command",
                "/buildedit help"
        );
        WandCommand wandCommand = new WandCommand(
                this,
                this.getPlayerSessionManager(),
                "wand",
                "buildedit.wand",
                "Wand command",
                "/buildedit wand"
        );
        setCommand.addSubCommand(blockCommand);
        buildEditCommand.addSubCommand(helpCommand);
        buildEditCommand.addSubCommand(wandCommand);
        buildEditCommand.addSubCommand(setCommand);
        buildEditCommand.register();
    }
}
