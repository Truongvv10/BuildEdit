package com.xironite.buildedit;

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
    }

    public void onDisable() {

    }
}
