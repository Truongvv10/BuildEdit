package com.xironite.buildedit.commands.sub;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.PlayerSessionManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockCommand extends CommandAbstract {

    public BlockCommand(JavaPlugin paramPlugin, PlayerSessionManager paramPlayerSessionManager, String paramName, String paramPermission, String paramDescription, String paramSyntax) {
        super(paramPlugin, paramPlayerSessionManager, paramName, paramPermission, paramDescription, paramSyntax);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            Material material = Material.getMaterial(args[0].toUpperCase());
            PlayerSession s = playerSessionManager.getSession(player);
            if (s.getSelection().getBlockPos1() != null && s.getSelection().getBlockPos2() != null) {
                s.executeSet(material);
            }
        }

        return true;
    }
}
