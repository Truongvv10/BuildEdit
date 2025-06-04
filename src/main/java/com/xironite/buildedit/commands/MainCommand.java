package com.xironite.buildedit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.utils.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.Objects;

@CommandAlias("buildedit|bedit|be")
public class MainCommand extends BaseCommand {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final WandManager wandManager;

    @Inject
    public MainCommand(JavaPlugin paramPlugin, ConfigManager paramConfigManager, WandManager paramWandManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.wandManager = paramWandManager;
    }

    @Default
    @Subcommand("help")
    public void onHelp(Player player, String[] paramArgs) {
        Component c = configManager.messages().getComponent(ConfigSection.TARGET_HELP);
        player.sendMessage(c);
    }

    @Subcommand("reload")
    @CommandCompletion("all|config|messages|wands @nothing")
    public void onReload(CommandSender sender, @Optional String paramConfig) {
        String syntax = configManager.messages().get(ConfigSection.SYNTAX_RELOAD);
        String description = configManager.messages().get(ConfigSection.DESC_RELOAD);
        if (paramConfig != null) {
            Component c = configManager.messages().getComponent(ConfigSection.TARGET_RELOAD);
            c = StringUtil.replace(c, "%config%", paramConfig.equals("all") ? "for all files" : paramConfig + ".yml");
            switch (paramConfig) {
                case "all":
                    plugin.reloadConfig();
                    wandManager.reload();
                    configManager.messages().reload();
                    break;
                case "config":
                    plugin.reloadConfig();
                    break;
                case "messages":
                    configManager.messages().reload();
                    break;
                case "wands":
                    wandManager.reload();
                    break;
                default:
                    sendMessage(sender, syntax + "\n" + description);
                    return;
            }
            sendMessage(sender, c);
        } else sendMessage(sender, syntax + "\n" + description);
    }

    @Subcommand("usage")
    @CommandCompletion("set|add|remove amount @nothing")
    public void onUsage(Player player, @Optional String argAction, @Optional Integer argAmount) {
        if (argAction == null || argAmount == null) {
            Component c = StringUtil.translateColor(configManager.messages().get(ConfigSection.SYNTAX_USAGE) + "\n" + configManager.messages().get(ConfigSection.DESC_USAGE));
            player.sendMessage(c);
        } else {
            ItemStack handItem = player.getInventory().getItemInMainHand();
            String wandName = wandManager.getName(handItem);
            if (wandName != null) {
                if (argAction.equalsIgnoreCase("set")) {
                    wandManager.setUsages(handItem, argAmount);
                    Component c = StringUtil.replace(configManager.messages().getComponent(ConfigSection.TARGET_USAGE), "%amount%", String.valueOf(argAmount));
                    player.sendMessage(c);
                } else if (argAction.equalsIgnoreCase("add")) {
                    wandManager.addUsages(handItem, argAmount);
                    Component c = StringUtil.replace(configManager.messages().getComponent(ConfigSection.TARGET_USAGE), "%amount%", String.valueOf(argAmount));
                    player.sendMessage(c);
                } else if (argAction.equalsIgnoreCase("remove")) {
                    wandManager.removeUsages(handItem, argAmount);
                    Component c = StringUtil.replace(configManager.messages().getComponent(ConfigSection.TARGET_USAGE), "%amount%", String.valueOf(argAmount));
                    player.sendMessage(c);
                }
            } else {
                sendMessage(player, configManager.messages().get(ConfigSection.ACTION_NO_WAND));
            }
        }

    }

    @Subcommand("wand")
    @CommandCompletion("give @wands @players amount @nothing")
    public void onWand(CommandSender sender, @Optional String argAction, @Optional String argWand, @Optional OnlinePlayer argTarget, @Optional Integer argAmount) {
        try {
            if (sender instanceof Player player) {

                // If no args, show syntax
                if (argAction == null || argWand == null) {
                    Component c = StringUtil.translateColor(configManager.messages().get(ConfigSection.SYNTAX_WAND) + "\n" + configManager.messages().get(ConfigSection.DESC_WAND));
                    player.sendMessage(c);
                    return;
                }

                // If there's multiple args
                Player target = argTarget != null ? argTarget.getPlayer() : player;
                argAmount = argAmount != null ? argAmount : 1;
                giveWandToPlayer(player, target, argWand, argAmount);
            }
        } catch (Exception e) {
            plugin.getLogger().warning(e.getMessage());
            sendMessage(sender, configManager.messages().get(ConfigSection.ACTION_ERROR));
        }
    }

    private void sendMessage(CommandSender sender, Component component) {
        if (sender instanceof Player player) {
            player.sendMessage(component);
        } else {
            sender.sendMessage(StringUtil.toPlainText(component));
        }
    }

    private void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            player.sendMessage(StringUtil.translateColor(message));
        } else {
            sender.sendMessage(StringUtil.toPlainText(message));
        }
    }

    private void giveWandToPlayer(Player executor, Player target, String wandName, int amount) {
        try {
            // Check if wand exists, if so get it
            ItemStack wand = Objects.requireNonNull(wandManager.get(wandName))
                    .addAmount(amount)
                    .build();

            // Message for target
            Component targetMessage = configManager.messages().getComponent(ConfigSection.TARGET_WAND);
            targetMessage = StringUtil.replace(targetMessage, "%amount%", String.valueOf(amount));
            targetMessage = StringUtil.replace(targetMessage, "%wand%", wand.getItemMeta().displayName());

            // Message for executor
            Component exuctorMessage = configManager.messages().getComponent(ConfigSection.EXECUTOR_WAND);
            exuctorMessage = StringUtil.replace(exuctorMessage, "%amount%", String.valueOf(amount));
            exuctorMessage = StringUtil.replace(exuctorMessage, "%wand%", wand.getItemMeta().displayName());
            exuctorMessage = StringUtil.replace(exuctorMessage, "%player%", target.getName());

            // If same player
            if (!executor.getName().equals(target.getName())) executor.sendMessage(exuctorMessage);
            target.sendMessage(targetMessage);

            // Give item to target
            giveItemMainHand(target, wand);

        } catch (Exception error) {
            plugin.getLogger().warning(error.getMessage());
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
