package com.xironite.buildedit.services;

import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.storage.configs.MessageConfig;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerSession> sessions;
    private final ConfigManager configManager;
    private final WandManager wandManager;

    public SessionManager(JavaPlugin paramPlugin, ConfigManager paramConfigManager, WandManager paramWandManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.wandManager = paramWandManager;
        this.sessions = new HashMap<>();
    }

    public void addSession(Player player) {
        sessions.put(player.getUniqueId(), new PlayerSession(player, configManager, wandManager));
    }

    public void removeSession(Player player) {
        sessions.remove(player.getUniqueId());
    }

    public boolean hasSession(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public PlayerSession getSession(Player player) {
        return sessions.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerSession(player, configManager, wandManager));
    }

    public void cleanup() {
        sessions.clear();
    }
}
