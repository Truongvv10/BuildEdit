package com.xironite.buildedit.listeners;

import com.xironite.buildedit.services.PlayerSessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinLeaveListener implements Listener {

    private final JavaPlugin plugin;
    private final PlayerSessionManager session;

    public PlayerJoinLeaveListener(JavaPlugin paramPlugin, PlayerSessionManager paramSessionManager) {
        this.plugin = paramPlugin;
        this.session = paramSessionManager;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        session.removeSession(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        session.addSession(player);
    }


}
