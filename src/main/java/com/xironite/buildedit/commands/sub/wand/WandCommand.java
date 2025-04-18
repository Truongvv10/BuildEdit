package com.xironite.buildedit.commands.sub.wand;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WandCommand  extends CommandAbstract {

    private final ItemsConfig itemConfig;
    private final List<String> wands;

    public WandCommand(JavaPlugin paramPlugin, PlayerSessionManager paramSession, MessageConfig paramMessageConf, String paramName, String paramPermission, String paramSyntax, String paramDescription, ItemsConfig itemConfig) {
        super(paramPlugin, paramSession, paramMessageConf, paramName, paramPermission, paramSyntax, paramDescription);
        this.itemConfig = itemConfig;
        this.wands = itemConfig.getKeys(ConfigSection.ITEM_WANDS);
        for (String wand : this.wands) {
            String permission = "buildedit.wand." + wand;
            WandNameCommand wandNameCommand = new WandNameCommand(
                    paramPlugin,
                    paramSession,
                    paramMessageConf,
                    wand,
                    permission,
                    this.itemConfig);
            addSubCommand(wandNameCommand);
        }
    }

    @Override
    public boolean onExecute(CommandSender sender, Command cmd, String label, String[] args) {
        // Check permission
        if (!hasPermission(sender)) return true;

        // Check if sender is a player
        if (sender instanceof Player player) {

            // If no args are provided, show usage
            if (args.length == 0) player.sendMessage(this.getUsage());

            // If we have args and the first arg matches a subcommand, delegate to that subcommand
            if (args.length > 0) {
                CommandAbstract subCommand = this.subCommands.get(args[0]);
                return subCommand.onCommand(sender, cmd, label, args);
            }
        }
        return true;
    }
}
