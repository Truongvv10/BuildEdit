package com.xironite.buildedit;

import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import com.xironite.buildedit.commands.MainCommand;
import com.xironite.buildedit.commands.edits.SetCommand;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.exceptions.NoWandException;
import com.xironite.buildedit.exceptions.PositionsException;
import com.xironite.buildedit.listeners.PlayerInteractListener;
import com.xironite.buildedit.listeners.PlayerJoinLeaveListener;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.ListBlockFilter;
import com.xironite.buildedit.utils.StringUtil;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class Main extends JavaPlugin {

    @Getter
    public static Main plugin;
    private PlayerSessionManager playerSessionManager;
    private MessageConfig messageConf;
    private ItemsConfig itemConf;
    private PaperCommandManager commands;

    public Main() {
        plugin = this;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEventsSettings settings = PacketEvents.getAPI().getSettings();
        settings.checkForUpdates(false);
        settings.debug(false);
        PacketEvents.getAPI().load();
    }

    public void onEnable() {
        PacketEvents.getAPI().init();
        registerConfigs();
        registerSessions();
        registerListeners();
        registerCommands();
    }

    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    private void registerConfigs() {
        this.messageConf = new MessageConfig(this, "messages");
        this.itemConf = new ItemsConfig(this, "items");
    }

    private void registerSessions() {
        this.playerSessionManager = new PlayerSessionManager(this, messageConf);
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, playerSessionManager, messageConf, itemConf), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this, playerSessionManager), this);
    }

    private void registerCommands() {
        commands = new PaperCommandManager(this);

        // Register dependency
        commands.registerDependency(PlayerSessionManager.class, playerSessionManager);
        commands.registerDependency(MessageConfig.class, messageConf);
        commands.registerDependency(ItemsConfig.class, itemConf);

        // Register command completions and conditions
        registerCommandCompletions();
        registerCommandConditionExceptions();
        registerCommandConditions();

        commands.getFormat(MessageType.ERROR).format("test");
        commands.getFormat(MessageType.INFO).format("test");
        commands.getFormat(MessageType.SYNTAX).format("test");
        commands.getFormat(MessageType.HELP).format("test");


        // Register command
        commands.registerCommand(new MainCommand(this, messageConf, itemConf));
        commands.registerCommand(new SetCommand(this, messageConf, itemConf, playerSessionManager));
    }

    private void registerCommandCompletions() {
        // Register wands completion
        commands.getCommandCompletions().registerCompletion("wands",
                c -> itemConf.getKeys(ConfigSection.ITEM_WANDS));

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
                throw new PositionsException(messageConf.get(ConfigSection.NOT_SELECTION_POS1));

            // Check if position 2 is selected
            if (playerSessionManager.getSession(player).getSelection().getBlockPos2() == null)
                throw new PositionsException(messageConf.get(ConfigSection.NOT_SELECTION_POS2));

            // Check if wand is in hand
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR && item.getItemMeta() == null)
                throw new NoWandException(messageConf.get(ConfigSection.ACTION_NO_WAND));

            // Check if wand exists
            ItemMeta meta = item.getItemMeta();
            NamespacedKey keyId = new NamespacedKey(plugin, "id");
            String wandName = meta.getPersistentDataContainer().get(keyId, PersistentDataType.STRING);
            if (!itemConf.containsWand(wandName))
                throw new NoWandException(messageConf.get(ConfigSection.ACTION_NO_WAND));

            // Check Size
            long selectionSize = playerSessionManager.getSession(player).getSize();
            if (itemConf.hasWandOverMaxSize(wandName, selectionSize))
                throw new NoWandException(messageConf.get(ConfigSection.ACTION_MAX_SIZE)
                        .replace("%max%", String.valueOf(itemConf.getWandSize(wandName)))
                        .replace("%size%", String.valueOf(selectionSize)));

            // Check durability
            NamespacedKey usageId = new NamespacedKey(plugin, "usages");
            Long usages = meta.getPersistentDataContainer().get(usageId, PersistentDataType.LONG);
            if (!itemConf.hasWandUsages(item, selectionSize))
                throw new NoWandException(messageConf.get(ConfigSection.ACTION_NO_USAGES)
                        .replace("%usage%", String.valueOf(usages)));
            itemConf.decrementWandUsages(item, selectionSize);

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
