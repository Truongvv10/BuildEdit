package com.xironite.buildedit.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageBuilder {

    private String message;
    private final MiniMessage colorUtil;

    public MessageBuilder(String message) {
        this.message = message;
        this.colorUtil = MiniMessage.miniMessage();
    }

    public MessageBuilder replace(String placeholder, String replacement) {
        this.message = message.replace(placeholder, replacement);
        return this;
    }

    public MessageBuilder replace(String placeholder, Component replacement) {
        String replacementString = colorUtil.serialize(replacement);
        this.message = message.replace(placeholder, replacementString);
        return this;
    }

    public MessageBuilder replace(String placeholder, int replacement) {
        this.message = message.replace(placeholder, NumberUtil.toFormattedNumber(replacement));
        return this;
    }

    public MessageBuilder replace(String placeholder, double replacement) {
        this.message = message.replace(placeholder, NumberUtil.toFormattedNumber(replacement));
        return this;
    }

    public MessageBuilder replace(String placeholder, long replacement) {
        this.message = message.replace(placeholder, NumberUtil.toFormattedNumber(replacement));
        return this;
    }

    public Component build() {
        return colorUtil.deserialize(message);
    }
}
