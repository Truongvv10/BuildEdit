package com.xironite.buildedit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import com.xironite.buildedit.hooks.WorldGuardHook;
import com.xironite.buildedit.services.*;
import com.xironite.buildedit.listeners.PlayerInteractListener;
import com.xironite.buildedit.listeners.PlayerJoinLeaveListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Main extends JavaPlugin {

    @Getter
    public static Main plugin;
    private ConfigManager configManager;
    private WandManager wandManager;
    private RecipeManager recipeManager;
    private SessionManager playerSessionManager;
    private CommandManager commandManager;
    private HookManager hookManager;
    public Main() {
        plugin = this;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEventsSettings settings = PacketEvents.getAPI().getSettings();
        settings.checkForUpdates(false);
        settings.debug(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        registerManagers();
        if (configManager.hooks().isPacketEventsEnabled()) PacketEvents.getAPI().init();
        registerListeners();
    }

    @Override
    public void onDisable() {
        if (configManager.hooks().isPacketEventsEnabled()) PacketEvents.getAPI().terminate();
    }

    private void registerManagers() {
        this.configManager = new ConfigManager(this);
        this.hookManager = new HookManager(this);
        this.wandManager = new WandManager(this, configManager);
        this.recipeManager = new RecipeManager(this, wandManager);
        this.playerSessionManager = new SessionManager(this, configManager, wandManager);
        this.commandManager = new CommandManager(this, configManager, hookManager, wandManager, recipeManager, playerSessionManager);
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, configManager, hookManager, wandManager, playerSessionManager), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this, recipeManager, playerSessionManager), this);
    }
}
