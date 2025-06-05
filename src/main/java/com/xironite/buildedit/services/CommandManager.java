package com.xironite.buildedit.services;

import co.aikar.commands.PaperCommandManager;
import com.xironite.buildedit.commands.MainCommand;
import com.xironite.buildedit.commands.edits.SetCommand;
import com.xironite.buildedit.commands.edits.WallCommand;
import com.xironite.buildedit.exceptions.NoWandException;
import com.xironite.buildedit.exceptions.PositionsException;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.utils.ListBlockFilter;
import com.xironite.buildedit.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CommandManager {

    private final JavaPlugin plugin;
    private final SessionManager playerSessionManager;
    private final ConfigManager configManager;
    private final WandManager wandManager;
    private PaperCommandManager commands;

    public CommandManager(JavaPlugin paramPlugin, ConfigManager paramConfigManager, WandManager paramWandManager, SessionManager paramPlayerSessionManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.wandManager = paramWandManager;
        this.playerSessionManager = paramPlayerSessionManager;
        registerCommands();

    }

    private void registerCommands() {
        commands = new PaperCommandManager(plugin);

        // Register dependency
        commands.registerDependency(ConfigManager.class, configManager);
        commands.registerDependency(SessionManager.class, playerSessionManager);

        // Register command completions and conditions
        registerCommandCompletions();
        registerCommandConditionExceptions();
        registerCommandConditions();


        // Register command
        commands.registerCommand(new MainCommand(plugin, configManager, wandManager));
        commands.registerCommand(new SetCommand(plugin, configManager, playerSessionManager));
        commands.registerCommand(new WallCommand(plugin, configManager, playerSessionManager));
    }

    private void registerCommandCompletions() {
        // Register wands completion
        commands.getCommandCompletions().registerCompletion("wands",
                c -> configManager.items().getKeys(ConfigSection.ITEM_WANDS));

        // Register blocks completion
        commands.getCommandCompletions().registerCompletion("blocks", c -> {
            if (c.getPlayer() == null) return List.of();
            ListBlockFilter filter = new ListBlockFilter(c.getPlayer());
            return filter.getTabCompletions(c.getInput());
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
            if (playerSessionManager.getSession(player).getSelection().getBlockPos1() == null)
                throw new PositionsException(configManager.messages().get(ConfigSection.NOT_SELECTION_POS1));

            // Check if position 2 is selected
            if (playerSessionManager.getSession(player).getSelection().getBlockPos2() == null)
                throw new PositionsException(configManager.messages().get(ConfigSection.NOT_SELECTION_POS2));

            // Check if wand exists
            ItemStack item = player.getInventory().getItemInMainHand();
            String wandName = wandManager.getName(item);
            if (wandName == null || !wandManager.contains(item))
                throw new NoWandException(configManager.messages().get(ConfigSection.ACTION_NO_WAND));

            // Check Size
            long selectionSize = playerSessionManager.getSession(player).getSize();
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
