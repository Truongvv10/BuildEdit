package com.xironite.buildedit.services;

import co.aikar.commands.PaperCommandManager;
import com.xironite.buildedit.commands.MainCommand;
import com.xironite.buildedit.commands.edits.*;
import com.xironite.buildedit.exceptions.NoWandException;
import com.xironite.buildedit.exceptions.PositionsException;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.utils.ListBlockFilter;
import com.xironite.buildedit.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {

    private final JavaPlugin plugin;
    private final SessionManager sessionManager;
    private final ConfigManager configManager;
    private final WandManager wandManager;
    private final RecipeManager recipeManager;
    private PaperCommandManager commands;

    public CommandManager(JavaPlugin paramPlugin, ConfigManager paramConfigManager, WandManager paramWandManager, RecipeManager paramRecipeManager, SessionManager paramSessionManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.wandManager = paramWandManager;
        this.recipeManager = paramRecipeManager;
        this.sessionManager = paramSessionManager;
        registerCommands();

    }

    private void registerCommands() {
        commands = new PaperCommandManager(plugin);

        // Register dependency
        commands.registerDependency(ConfigManager.class, configManager);
        commands.registerDependency(SessionManager.class, sessionManager);

        // Register command completions and conditions
        registerCommandCompletions();
        registerCommandConditionExceptions();
        registerCommandConditions();


        // Register command
        commands.registerCommand(new MainCommand(plugin, configManager, wandManager, recipeManager));
        commands.registerCommand(new SetCommand(plugin, configManager, sessionManager));
        commands.registerCommand(new WallCommand(plugin, configManager, sessionManager));
        commands.registerCommand(new CopyCommand(plugin, configManager, sessionManager));
        commands.registerCommand(new PasteCommand(plugin, configManager, sessionManager));
        commands.registerCommand(new RotateCommand(plugin, configManager, sessionManager));
        commands.registerCommand(new ReplaceCommand(plugin, configManager, sessionManager));

    }

    private void registerCommandCompletions() {
        // Register wands completion
        commands.getCommandCompletions().registerCompletion("wands",
                c -> configManager.items().getKeys(ConfigSection.ITEM_WANDS));

        // Register all minecraft blocks completion
        commands.getCommandCompletions().registerCompletion("replace", c -> {
            if (c.getPlayer() == null) return List.of();
            ListBlockFilter filter = new ListBlockFilter(configManager.blacklist());
            return filter.getBlocks(c.getInput());
        });

        // Register blocks completion
        commands.getCommandCompletions().registerCompletion("blocks", c -> {
            if (c.getPlayer() == null) return List.of();
            ListBlockFilter filter = new ListBlockFilter(configManager.blacklist(), c.getPlayer());
            return filter.getInventoryBlocks(c.getInput());
        });
    }

    private void registerCommandConditions() {
        // Register sound
        commands.getCommandConditions().addCondition("sound", c -> {
            Player player = c.getIssuer().getPlayer();
            if (player == null) return;
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, 0.5f, 1.0f);
        });

        // Register wands completion
        commands.getCommandConditions().addCondition("wands", c -> {
            Player player = c.getIssuer().getPlayer();

            // Check if position 1 is selected
            if (sessionManager.getSession(player).getSelection().getBlockPos1() == null)
                throw new PositionsException(configManager.messages().get(ConfigSection.NOT_SELECTION_POS1));

            // Check if position 2 is selected
            if (sessionManager.getSession(player).getSelection().getBlockPos2() == null)
                throw new PositionsException(configManager.messages().get(ConfigSection.NOT_SELECTION_POS2));

            // Check if wand exists
            ItemStack item = player.getInventory().getItemInMainHand();
            String wandName = wandManager.getName(item);
            if (wandName == null || !wandManager.contains(item))
                throw new NoWandException(configManager.messages().get(ConfigSection.ACTION_NO_WAND));

            // Check Size
            long selectionSize = sessionManager.getSession(player).getSize();
            if (wandManager.isExceedingMaxSize(item, selectionSize))
                throw new NoWandException(configManager.messages().get(ConfigSection.ACTION_MAX_SIZE)
                        .replace("%max%", String.valueOf(wandManager.getSize(item)))
                        .replace("%size%", String.valueOf(selectionSize)));
        });
    }


    private void registerCommandConditionExceptions() {
        commands.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            Player player = Bukkit.getPlayer(sender.getUniqueId());
            assert player != null;

            if (t instanceof NoWandException) {
                player.sendMessage(StringUtil.translateColor(t.getMessage()));
                return true;
            }

            if ( t instanceof PositionsException) {
                player.sendMessage(StringUtil.translateColor(t.getMessage()));
                return true;
            }

            return false;
        }, false);
    }



}
