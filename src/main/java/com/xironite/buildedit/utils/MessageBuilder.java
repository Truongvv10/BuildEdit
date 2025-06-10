package com.xironite.buildedit.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageBuilder {

    private final String template;
    private final MiniMessage colorUtil;
    private String message;
    private Player player;

    public MessageBuilder(String message) {
        this.template = message;
        this.message = message;
        this.player = null;
        this.colorUtil = MiniMessage.miniMessage();
    }

    public MessageBuilder replace(String placeholder, String replacement) {
        if (message == null) this.message = template;
        this.message = message.replace(placeholder, replacement);
        return this;
    }

    public MessageBuilder replace(String placeholder, Component replacement) {
        String replacementString = colorUtil.serialize(replacement);
        if (message == null) this.message = template;
        this.message = message.replace(placeholder, replacementString);
        return this;
    }

    public MessageBuilder replace(String placeholder, int replacement) {
        if (message == null) this.message = template;
        this.message = message.replace(placeholder, NumberUtil.toFormattedNumber(replacement));
        return this;
    }

    public MessageBuilder replace(String placeholder, double replacement) {
        if (message == null) this.message = template;
        this.message = message.replace(placeholder, NumberUtil.toFormattedNumber(replacement));
        return this;
    }

    public MessageBuilder replace(String placeholder, long replacement) {
        if (message == null) this.message = template;
        this.message = message.replace(placeholder, NumberUtil.toFormattedNumber(replacement));
        return this;
    }

    public MessageBuilder toPlayer(Player player) {
        this.player = player;
        return this;
    }

    public MessageBuilder toPlayer(CommandSender player) {
        if (player instanceof Player) this.player = (Player) player;
        return this;
    }

    public Component build() {
        Component c = colorUtil.deserialize(message);
        if (player != null) player.sendMessage(c);
        this.message = template;
        this.player = null;
        return c;
    }
}
