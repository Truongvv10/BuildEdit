package com.xironite.buildedit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.List;

@CommandAlias("buildedit|bedit|be")
public class MainCommand extends BaseCommand {

    private final JavaPlugin plugin;
    private final MessageConfig messageConfig;
    private final ItemsConfig itemsConfig;

    @Inject
    public MainCommand(JavaPlugin plugin, MessageConfig messageConfig, ItemsConfig itemsConfig) {
        this.plugin = plugin;
        this.messageConfig = messageConfig;
        this.itemsConfig = itemsConfig;
    }

    @Default
    @Subcommand("help")
    public void onHelp(Player player, String[] args) {
        Component c = messageConfig.getComponent(ConfigSection.HELP);
        player.sendMessage(c);
    }

    @Subcommand("wand")
    @CommandCompletion("@wands @players amount @nothing")
    public void onWand(CommandSender sender, @Optional String wandType, @Optional OnlinePlayer targetPlayer, @Optional Integer amount) {
        if (sender instanceof Player player) {

            // If no args, show syntax
            if (wandType == null) {
                Component c = StringUtil.translateColor(messageConfig.get(ConfigSection.SYNTAX_WAND) + "\n" + messageConfig.get(ConfigSection.DESC_WAND));
                player.sendMessage(c);
                return;
            }

            // If there's multiple args
            Player target = targetPlayer != null ? targetPlayer.getPlayer() : player;
            amount = amount != null ? amount : 1;
            giveWandToPlayer(player, target, wandType, amount);
        }
    }

    private void giveWandToPlayer(Player executor, Player target, String wandName, int amount) {
        // Item attributes
        Material item = Material.getMaterial(itemsConfig.get(ConfigSection.ITEM_WAND_MATERIAL.value.replace("$1", wandName)));
        String display = itemsConfig.get(ConfigSection.ITEM_WAND_NAME.value.replace("$1", wandName));
        List<String> lore = itemsConfig.getStringList(ConfigSection.ITEM_WAND_LORE.value.replace("$1", wandName));
        int dur = itemsConfig.getInt(ConfigSection.ITEM_WAND_USAGES.value.replace("$1", wandName));

        // Give item
        if (item != null) {
            ItemStack wand = new ItemStack(item, amount);
            ItemMeta meta = wand.getItemMeta();

            meta.displayName(StringUtil.translateColor(display));
            meta.lore(StringUtil.translateColor(lore));

            NamespacedKey key = new NamespacedKey(plugin, "type");
            PersistentDataContainer data = meta.getPersistentDataContainer();

            data.set(key, PersistentDataType.STRING, wandName);
            wand.setItemMeta(meta);

            // Message for target
            Component targetMessage = messageConfig.getComponent(ConfigSection.WAND);
            targetMessage = StringUtil.replace(targetMessage, "%amount%", String.valueOf(amount));
            targetMessage = StringUtil.replace(targetMessage, "%wand%", display);

            // Message for executor
            Component exuctorMessage = messageConfig.getComponent(ConfigSection.WAND2);
            exuctorMessage = StringUtil.replace(exuctorMessage, "%amount%", String.valueOf(amount));
            exuctorMessage = StringUtil.replace(exuctorMessage, "%wand%", display);
            exuctorMessage = StringUtil.replace(exuctorMessage, "%player%", target.getName());

            // If same player
            if (!executor.getName().equals(target.getName())) executor.sendMessage(exuctorMessage);
            target.sendMessage(targetMessage);

            // Give item to target
            giveItemMainHand(target, wand);

        }
    }

    public void giveItemMainHand(Player player, ItemStack item) {
        // For non-stackable items (stack size of 1)
        if (item.getMaxStackSize() == 1 && item.getAmount() > 1) {
            ItemStack singleItem = item.clone();
            singleItem.setAmount(1);

            // First try main hand for the first item
            if (player.getInventory().getItemInMainHand().getType().isAir()) {
                player.getInventory().setItemInMainHand(singleItem);

                // Give remaining items to inventory or drop
                for (int i = 1; i < item.getAmount(); i++) {
                    if (player.getInventory().firstEmpty() != -1) {
                        player.getInventory().addItem(singleItem.clone());
                    } else {
                        player.getWorld().dropItemNaturally(player.getLocation(), singleItem.clone());
                    }
                }

                // Else try to add to inventory
            } else {
                // Main hand not empty, try to add all to inventory
                for (int i = 0; i < item.getAmount(); i++) {
                    if (player.getInventory().firstEmpty() != -1) {
                        player.getInventory().addItem(singleItem.clone());
                    } else {
                        player.getWorld().dropItemNaturally(player.getLocation(), singleItem.clone());
                    }
                }
            }

        } else {

            // For stackable items, use the original logic
            if (player.getInventory().getItemInMainHand().getType().isAir()) {
                player.getInventory().setItemInMainHand(item);
            } else if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

}
