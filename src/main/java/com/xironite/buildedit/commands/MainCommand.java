package com.xironite.buildedit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.RecipeManager;
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
    private final RecipeManager recipeManager;

    @Inject
    public MainCommand(JavaPlugin paramPlugin, ConfigManager paramConfigManager, WandManager paramWandManager, RecipeManager paramRecipeManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.wandManager = paramWandManager;
        this.recipeManager = paramRecipeManager;
    }

    @Default
    @Subcommand("help")
    public void onHelp(Player player, String[] paramArgs) {
        Component c = configManager.messages().getFromCache(ConfigSection.TARGET_HELP).build();
        player.sendMessage(c);
    }

    @Subcommand("reload")
    @CommandCompletion("all|config|messages|wands|hooks|blacklist @nothing")
    public void onReload(CommandSender sender, @Optional String paramConfig) {
        try {
            String syntax = configManager.messages().get(ConfigSection.SYNTAX_RELOAD);
            String description = configManager.messages().get(ConfigSection.DESC_RELOAD);
            if (paramConfig != null) {
                switch (paramConfig) {
                    case "all":
                        plugin.reloadConfig();
                        wandManager.reload();
                        configManager.reload();
                        break;
                    case "config":
                        plugin.reloadConfig();
                        break;
                    case "messages":
                        configManager.messages().reload();
                        break;
                    case "hooks":
                        configManager.hooks().reload();
                        break;
                    case "wands":
                        wandManager.reload();
                        recipeManager.reload();
                        break;
                    case "blacklist":
                        configManager.blacklist().reload();
                        break;
                    default:
                        sendMessage(sender, syntax + "\n" + description);
                        return;
                }
                configManager.messages().getFromCache(ConfigSection.TARGET_RELOAD)
                        .replace("%config%", paramConfig.equals("all") ? "for all files" : paramConfig + ".yml")
                        .toPlayer(sender)
                        .build();
            } else {
                plugin.reloadConfig();
                wandManager.reload();
                recipeManager.reload();
                configManager.reload();
                configManager.messages().getFromCache(ConfigSection.TARGET_RELOAD)
                        .replace("%config%", "for all files")
                        .toPlayer(sender)
                        .build();
            }
        } catch (Exception e) {
            plugin.getLogger().warning(e.getMessage());
        }
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
                    configManager.messages().getFromCache(ConfigSection.TARGET_USAGE)
                            .replace("%amount%", String.valueOf(argAmount))
                            .toPlayer(player)
                            .build();
                } else if (argAction.equalsIgnoreCase("add")) {
                    wandManager.addUsages(handItem, argAmount);
                    configManager.messages().getFromCache(ConfigSection.TARGET_USAGE)
                            .replace("%amount%", String.valueOf(argAmount))
                            .toPlayer(player)
                            .build();
                } else if (argAction.equalsIgnoreCase("remove")) {
                    wandManager.removeUsages(handItem, argAmount);
                    configManager.messages().getFromCache(ConfigSection.TARGET_USAGE)
                            .replace("%amount%", String.valueOf(argAmount))
                            .toPlayer(player)
                            .build();
                }
            } else {
                sendMessage(player, configManager.messages().get(ConfigSection.ACTION_NO_WAND));
            }
        }

    }

    @Subcommand("give")
    @CommandCompletion("wand @wands @players amount @nothing")
    public void onWand(CommandSender sender, @Optional String argItem, @Optional String argWand, @Optional OnlinePlayer argTarget, @Optional Integer argAmount) {
        try {
            if (sender instanceof Player player) {

                // If no args, show syntax
                if (argItem == null || argWand == null) {
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
            Component targetMessage = configManager.messages().getFromCache(ConfigSection.TARGET_WAND)
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%wand%", wand.getItemMeta().displayName())
                    .build();

            // Message for executor
            Component executorMessage = configManager.messages().getFromCache(ConfigSection.EXECUTOR_WAND)
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%wand%", wand.getItemMeta().displayName())
                    .replace("%player%", target.getName())
                    .build();

            // If same player
            if (!executor.getName().equals(target.getName())) executor.sendMessage(executorMessage);
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
