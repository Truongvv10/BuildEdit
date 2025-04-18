package com.xironite.buildedit.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public class StringUtil {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component translateColor(String message) {
        return MiniMessage.miniMessage().deserialize(message).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static List<Component> translateColor(List<String> messages) {
        List<Component> components = new java.util.ArrayList<>();
        for (String message : messages) components.add(translateColor(message));
        return components;
    }

    public static Component replace(Component component, String target, String replacement) {
        String plainText = miniMessage.serialize(component);
        plainText = plainText.replace(target, replacement);
        return MiniMessage.miniMessage().deserialize(plainText);
    }

    public static String toRawString(Component component) {
        return miniMessage.serialize(component);
    }

}
