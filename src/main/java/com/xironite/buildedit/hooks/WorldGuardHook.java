package com.xironite.buildedit.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardHook {

    private final JavaPlugin plugin;
    @Getter
    private WorldGuardPlugin worldGuard;
    @Getter
    private final boolean isEnabled;

    public WorldGuardHook(JavaPlugin paramPlugin) {
        this.plugin = paramPlugin;
        Plugin p = this.plugin.getServer().getPluginManager().getPlugin("WorldGuard");

        if (p instanceof WorldGuardPlugin) {
            this.isEnabled = true;
            this.worldGuard = (WorldGuardPlugin) p;
        } else {
            this.isEnabled = false;
            this.worldGuard = null;
        }
    }

    private boolean canBypass(Player p) {
        LocalPlayer player = worldGuard.wrapPlayer(p); // Use your stored instance
        return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(player, player.getWorld());
    }

    public boolean canBuild(Player player) {
        if (!isEnabled || worldGuard == null) return true;
        if (canBypass(player)) return true;
        try {
            LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
            Location loc = player.getLocation();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            return query.testState(BukkitAdapter.adapt(loc), localPlayer, Flags.BUILD, Flags.BLOCK_BREAK, Flags.BLOCK_PLACE);

        } catch (Exception e) {
            plugin.getLogger().warning("Error checking build permission: " + e.getMessage());
            return true;
        }
    }

    public boolean canBuild(Player player, Location location) {
        if (!isEnabled || worldGuard == null) return true;
        if (canBypass(player)) return true;
        try {
            LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            return query.testState(BukkitAdapter.adapt(location), localPlayer, Flags.BUILD, Flags.BLOCK_BREAK, Flags.BLOCK_PLACE);

        } catch (Exception e) {
            plugin.getLogger().warning("Error checking build permission: " + e.getMessage());
            return true;
        }
    }

}
