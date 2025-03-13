package com.xironite.buildedit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getSyntax();
    public abstract String getPermission();
    public boolean hasPermission(CommandSender sender) {
        return getPermission() == null || sender.hasPermission(getPermission());
    }
    public abstract void performCommand(Player player, String[] args);
    public List<String> performTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

}
