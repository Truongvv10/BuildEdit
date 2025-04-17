package com.xironite.buildedit;

import com.xironite.buildedit.commands.main.BuildEditCommand;
import com.xironite.buildedit.commands.sub.BlockCommand;
import com.xironite.buildedit.commands.sub.HelpCommand;
import com.xironite.buildedit.commands.sub.SetCommand;
import com.xironite.buildedit.commands.sub.WandCommand;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.listeners.PlayerInteractListener;
import com.xironite.buildedit.listeners.PlayerJoinLeaveListener;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.storage.configs.PermissionConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Main extends JavaPlugin {

    @Getter
    public static Main plugin;
    private PlayerSessionManager playerSessionManager;
    private MessageConfig messageConf;
    private ItemsConfig itemConf;
    private PermissionConfig permissionConf;

    public Main() {
        plugin = this;
    }

    public void onEnable() {
        registerConfigs();
        playerSessionManager = new PlayerSessionManager(this, messageConf);
        registerCommands();
        this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, playerSessionManager, messageConf), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this, playerSessionManager), this);
    }

    public void onDisable() {

    }

    private void registerConfigs() {
        this.messageConf = new MessageConfig(this, "messages");
        this.itemConf = new ItemsConfig(this, "items");
        this.permissionConf = new PermissionConfig(this, "permissions");
    }

    private void registerCommands() {
        BuildEditCommand buildEditCommand = new BuildEditCommand(
                this,
                this.playerSessionManager,
                this.messageConf,
                "buildedit",
                "buildedit.use",
                "Main command for BuildEdit",
                "/buildedit"
        );
        SetCommand setCommand = new SetCommand(
                this,
                this.playerSessionManager,
                this.messageConf,
                "set",
                "buildedit.set",
                messageConf.get(ConfigSection.SYNTAX_SET),
                messageConf.get(ConfigSection.DESC_SET)
        );
        BlockCommand blockCommand = new BlockCommand(
                this,
                this.playerSessionManager,
                this.messageConf,
                "block",
                "buildedit.set"
        );
        HelpCommand helpCommand = new HelpCommand(
                this,
                this.playerSessionManager,
                this.messageConf,
                "help",
                "buildedit.help",
                messageConf.get(ConfigSection.SYNTAX_SET),
                messageConf.get(ConfigSection.DESC_SET)
        );
        WandCommand wandCommand = new WandCommand(
                this,
                this.playerSessionManager,
                this.messageConf,
                "wand",
                "buildedit.wand",
                "Wand command",
                "/buildedit wand",
                itemConf
        );
        setCommand.addSubCommand(blockCommand);
        buildEditCommand.addSubCommand(helpCommand);
        buildEditCommand.addSubCommand(wandCommand);
        buildEditCommand.addSubCommand(setCommand);
        buildEditCommand.register();
    }
}
