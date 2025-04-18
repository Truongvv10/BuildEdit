package com.xironite.buildedit.commands.sub.wand;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.ListBlockFilter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WandAmountCommand extends CommandAbstract {

    private final ItemsConfig itemConfig;

    public WandAmountCommand(JavaPlugin paramPlugin, PlayerSessionManager paramPlayerSessionManager, MessageConfig paramMessageConfig, String paramName, String paramPermission, ItemsConfig itemsConfig) {
        super(paramPlugin, paramPlayerSessionManager, paramMessageConfig, paramName, paramPermission);
        this.itemConfig = itemsConfig;
    }

    @Override
    public boolean onExecute(CommandSender sender, Command cmd, String label, String[] args) {


        return true;
    }
}
