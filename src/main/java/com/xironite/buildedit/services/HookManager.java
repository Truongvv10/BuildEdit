package com.xironite.buildedit.services;

import com.xironite.buildedit.hooks.WorldGuardHook;
import com.xironite.buildedit.storage.configs.HooksConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class HookManager {

    @Getter
    private final HooksConfig hooks;
    @Getter
    private final WorldGuardHook worldGuardHook;

    public HookManager(JavaPlugin paramPlugin) {
        this.hooks = new HooksConfig(paramPlugin, "hooks");
        if (hooks.isWorldGuardEnabled()) this.worldGuardHook = new WorldGuardHook(paramPlugin); else this.worldGuardHook = null;
    }

    public WorldGuardHook worldguard() {
        return worldGuardHook;
    }

}
