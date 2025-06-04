package com.xironite.buildedit.services;

import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    @Getter
    private final MessageConfig messages;
    @Getter
    private final ItemsConfig items;

    public ConfigManager(JavaPlugin paramPlugin) {
        this.plugin = paramPlugin;
        this.messages = new MessageConfig(plugin, "messages");
        this.items = new ItemsConfig(plugin, "items");
    }

    public void reload() {
        messages.reload();
        items.reload();
    }

    public MessageConfig messages() {
        return this.getMessages();
    }

    public ItemsConfig items() {
        return this.getItems();
    }


}
