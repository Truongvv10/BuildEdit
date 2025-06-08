package com.xironite.buildedit.storage.configs;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.utils.MessageBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class MessageConfig extends ConfigAbtract {

    private final String prefix;
    private final HashMap<ConfigSection, MessageBuilder> cache;
    private final MiniMessage miniMessage;

    public MessageConfig(JavaPlugin paramPlugin, String paramFileName) {
        super(paramPlugin, paramFileName);
        this.prefix = get(ConfigSection.PREFIX);
        this.cache = new HashMap<>();
        this.miniMessage = MiniMessage.miniMessage();
        load();
    }

    @Override
    public void reload() {
        super.reload();
        load();
    }

    public void load() {
        cache.clear();
        List<ConfigSection> sections = List.of(ConfigSection.values());
        for (ConfigSection section : sections) {
            if (config.contains(section.value)) {
                if (config.isList(section.value)) {
                    List<String> messages = config.getStringList(section.value);
                    String message = String.join("\n", messages);
                    message = message.replace("%prefix%", prefix);
                    cache.put(section, new MessageBuilder(message));
                } else {
                    String message = config.getString(section.value);
                    assert message != null;
                    message = message.replace("%prefix%", prefix);
                    cache.put(section, new MessageBuilder(message));
                }
            } else {
                cache.put(section, new MessageBuilder(null));
            }
        }
    }

    public MessageBuilder getFromCache(ConfigSection section) {
        if (cache.containsKey(section)) {
            return cache.get(section);
        }
        return null;
    }
}
