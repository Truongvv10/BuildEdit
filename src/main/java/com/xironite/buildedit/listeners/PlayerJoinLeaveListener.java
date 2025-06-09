package com.xironite.buildedit.listeners;

import com.xironite.buildedit.services.RecipeManager;
import com.xironite.buildedit.services.SessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinLeaveListener implements Listener {

    private final JavaPlugin plugin;
    private final RecipeManager recipeManager;
    private final SessionManager session;

    public PlayerJoinLeaveListener(JavaPlugin paramPlugin, RecipeManager paramRecipeManager, SessionManager paramSessionManager) {
        this.plugin = paramPlugin;
        this.recipeManager = paramRecipeManager;
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
        recipeManager.unlockRecipe(player);
        session.addSession(player);
    }


}
