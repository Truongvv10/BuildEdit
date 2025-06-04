package com.xironite.buildedit.storage.configs;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.enums.ConfigSection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MessageConfig extends ConfigAbtract {

    private final MiniMessage miniMessage;
    private final String prefix;

    public MessageConfig(JavaPlugin paramPlugin, String paramFileName) {
        super(paramPlugin, paramFileName);
        this.miniMessage = MiniMessage.miniMessage();
        this.prefix = get(ConfigSection.PREFIX);
    }

    public Component getComponent(ConfigSection section) {
        if (config.contains(section.value)) {
            if (config.isList(section.value)) {
                List<String> messages = config.getStringList(section.value);
                String message = String.join("\n", messages);
                message = message.replace("%prefix%", prefix);
                return miniMessage.deserialize(message);
            } else {
                String message = config.getString(section.value);
                assert message != null;
                message = message.replace("%prefix%", prefix);
                return miniMessage.deserialize(message);
            }
        }
        return Component.empty();
    }

    public Component modifyComponent(Component component, String value, String replacement) {
        if (component instanceof TextComponent textComponent) {
            return textComponent.replaceText(TextReplacementConfig.builder()
                    .matchLiteral(value)
                    .replacement(replacement)
                    .build());
        }
        return component;
    }
}
