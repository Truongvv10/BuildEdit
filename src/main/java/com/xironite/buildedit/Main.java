package com.xironite.buildedit;

import co.aikar.commands.PaperCommandManager;
import com.xironite.buildedit.commands.MainCommand;
import com.xironite.buildedit.commands.edits.SetCommand;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.listeners.PlayerInteractListener;
import com.xironite.buildedit.listeners.PlayerJoinLeaveListener;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.storage.configs.PermissionConfig;
import com.xironite.buildedit.utils.ListBlockFilter;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class Main extends JavaPlugin {

    @Getter
    public static Main plugin;
    private PlayerSessionManager playerSessionManager;
    private MessageConfig messageConf;
    private ItemsConfig itemConf;
    private PermissionConfig permissionConf;
    private PaperCommandManager commands;

    public Main() {
        plugin = this;
    }

    public void onEnable() {
        registerConfigs();
        playerSessionManager = new PlayerSessionManager(this, messageConf);
        this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, playerSessionManager, messageConf), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this, playerSessionManager), this);
        setupCommandManager();
    }

    public void onDisable() {

    }

    private void setupCommandManager() {
        commands = new PaperCommandManager(this);

        // Register dependency
        commands.registerDependency(PlayerSessionManager.class, playerSessionManager);
        commands.registerDependency(MessageConfig.class, messageConf);
        commands.registerDependency(ItemsConfig.class, itemConf);
        commands.registerDependency(PermissionConfig.class, permissionConf);

        // Register command completions
        registerCommandCompletions();

        // Register command
        commands.registerCommand(new MainCommand(this, messageConf, itemConf));
        commands.registerCommand(new SetCommand(this, messageConf, playerSessionManager));
    }

    private void registerCommandCompletions() {

        // Register wands completion
        commands.getCommandCompletions().registerCompletion("wands",
                c -> itemConf.getKeys(ConfigSection.ITEM_WANDS));

        // Register blocks completion
        commands.getCommandCompletions().registerCompletion("blocks", c -> {
            if (c.getPlayer() == null) return List.of();
            ListBlockFilter filter = new ListBlockFilter(c.getPlayer());
            return filter.getTabCompletions(c.getInput());
        });
    }

    private void registerConfigs() {
        this.messageConf = new MessageConfig(this, "messages");
        this.itemConf = new ItemsConfig(this, "items");
        this.permissionConf = new PermissionConfig(this, "permissions");
    }
}
