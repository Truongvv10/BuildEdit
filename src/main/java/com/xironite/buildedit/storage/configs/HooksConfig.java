package com.xironite.buildedit.storage.configs;

import com.github.retrooper.packetevents.PacketEvents;
import com.xironite.buildedit.models.enums.ConfigSection;
import org.bukkit.plugin.java.JavaPlugin;

public class HooksConfig extends ConfigAbtract {
    // region Constructors
    public HooksConfig(JavaPlugin paramPlugin, String paramFileName) {
        super(paramPlugin, paramFileName);
    }
    // endregion

    // region Methods
    public boolean isPacketEventsEnabled() {
        return getBoolean(ConfigSection.HOOKS_PACKET_EVENT_ENABLED) && PacketEvents.getAPI().isLoaded();
    }

    public boolean isWorldGuardEnabled() {
        return getBoolean(ConfigSection.HOOKS_WORLD_GUARD_ENABLED);
    }
    // endregion
}
