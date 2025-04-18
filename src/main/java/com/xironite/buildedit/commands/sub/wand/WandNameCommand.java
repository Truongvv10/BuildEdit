package com.xironite.buildedit.commands.sub.wand;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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

import java.util.List;
import java.util.stream.Collectors;

public class WandNameCommand extends CommandAbstract {

    private final ItemsConfig itemConfig;

    public WandNameCommand(JavaPlugin paramPlugin, PlayerSessionManager paramSession, MessageConfig paramMessageConf, String paramName, String paramPermission, ItemsConfig itemConfig) {
        super(paramPlugin, paramSession, paramMessageConf, paramName, paramPermission);
        this.itemConfig = itemConfig;

        // Add WandAmountCommand directly instead of through WandPlayerCommand
        WandAmountCommand wandAmountCommand = new WandAmountCommand(
                paramPlugin,
                paramSession,
                paramMessageConf,
                "amount",
                "buildedit.wand",
                itemConfig);
        addSubCommand(wandAmountCommand);
    }

    @Override
    public boolean onExecute(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            // Check permission
            if (!hasPermission(sender)) return true;

            // If args are provided, check if it's a player name
            if (args.length > 0) {
                // Try to find the target player
                Player target = Bukkit.getPlayer(args[0]);
                if (isPlayerOnline(target)) {
                    // Give wand to target player
                    giveWandToPlayer(player, target);
                    return true;
                }

                // If not a player, check if it's a subcommand
                CommandAbstract subCommand = this.subCommands.get(args[0].toLowerCase());
                if (subCommand != null) {
                    return subCommand.onCommand(sender, cmd, label, args);
                }

                // Not a valid player or command
                player.sendMessage(messageConfig.getComponent(ConfigSection.ACTION_OFFLINE));
                return true;
            }

            // No args, give item to self
            giveWandToSelf(player);
        }
        return true;
    }

    private void giveWandToSelf(Player player) {
        // Item attributes
        Material item = Material.getMaterial(itemConfig.get(ConfigSection.ITEM_WAND_MATERIAL.value.replace("$1", this.name)));
        String display = itemConfig.get(ConfigSection.ITEM_WAND_NAME.value.replace("$1", this.name));
        List<String> lore = itemConfig.getStringList(ConfigSection.ITEM_WAND_LORE.value.replace("$1", this.name));
        int dur = itemConfig.getInt(ConfigSection.ITEM_WAND_USAGES.value.replace("$1", this.name));

        // Give item
        if (item != null) {
            ItemStack retrieve = new ItemStack(item);
            ItemMeta meta = retrieve.getItemMeta();

            meta.displayName(StringUtil.translateColor(display));
            meta.lore(StringUtil.translateColor(lore));

            NamespacedKey key = new NamespacedKey(plugin, "type");
            PersistentDataContainer data = meta.getPersistentDataContainer();

            data.set(key, PersistentDataType.STRING, this.name);
            retrieve.setItemMeta(meta);

            Component c = messageConfig.getComponent(ConfigSection.WAND);
            c = StringUtil.replace(c, "%amount%", "1");
            c = StringUtil.replace(c, "%wand%", display);
            giveItemToMainHand(player, retrieve);
            player.sendMessage(c);
        }
    }

    private void giveWandToPlayer(Player executor, Player target) {
        // Item attributes
        Material item = Material.getMaterial(itemConfig.get(ConfigSection.ITEM_WAND_MATERIAL.value.replace("$1", this.name)));
        String display = itemConfig.get(ConfigSection.ITEM_WAND_NAME.value.replace("$1", this.name));
        List<String> lore = itemConfig.getStringList(ConfigSection.ITEM_WAND_LORE.value.replace("$1", this.name));
        int dur = itemConfig.getInt(ConfigSection.ITEM_WAND_USAGES.value.replace("$1", this.name));

        // Give item
        if (item != null) {
            ItemStack retrieve = new ItemStack(item);
            ItemMeta meta = retrieve.getItemMeta();

            meta.displayName(StringUtil.translateColor(display));
            meta.lore(StringUtil.translateColor(lore));

            NamespacedKey key = new NamespacedKey(plugin, "type");
            PersistentDataContainer data = meta.getPersistentDataContainer();

            data.set(key, PersistentDataType.STRING, this.name);
            retrieve.setItemMeta(meta);

            // Message for target
            Component c2 = messageConfig.getComponent(ConfigSection.WAND);
            c2 = StringUtil.replace(c2, "%amount%", "1");
            c2 = StringUtil.replace(c2, "%wand%", display);

            // Message for executor
            Component c1 = messageConfig.getComponent(ConfigSection.WAND2);
            c1 = StringUtil.replace(c1, "%amount%", "1");
            c1 = StringUtil.replace(c1, "%wand%", display);
            c1 = StringUtil.replace(c1, "%player%", target.getName());

            // If same player
            if (!executor.getName().equals(target.getName())) executor.sendMessage(c1);
            target.sendMessage(c2);

            // Give item to target
            if (target.getInventory().firstEmpty() != -1) {
                target.getInventory().addItem(retrieve);
            } else {
                target.getWorld().dropItemNaturally(target.getLocation(), retrieve);
            }
        }
    }

    public void giveItemToMainHand(Player player, ItemStack item) {
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.getInventory().setItemInMainHand(item);
        } else {
            player.getInventory().addItem(item);
        }
    }

    @Override
    protected List<String> onTabbing(CommandSender sender, Command command, String label, String[] args) {
        // First argument shows players and subcommands
        if (args.length == 0 || args.length == 1) {
            List<String> players = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> args.length == 0 || name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
            return players;
        }
        return List.of();
    }
}