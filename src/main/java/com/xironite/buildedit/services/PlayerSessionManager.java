package com.xironite.buildedit.services;

import com.xironite.buildedit.models.PlayerSession;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSessionManager {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerSession> sessions;

    public PlayerSessionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.sessions = new HashMap<>();
    }

    public void addSession(Player player) {
        sessions.put(player.getUniqueId(), new PlayerSession(player));
    }

    public void removeSession(Player player) {
        sessions.remove(player.getUniqueId());
    }

    public boolean hasSession(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public PlayerSession getSession(Player player) {
        return sessions.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerSession(player));
    }

    public void cleanup() {
        sessions.clear();
    }
}
