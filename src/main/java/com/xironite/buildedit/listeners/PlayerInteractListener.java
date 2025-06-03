package com.xironite.buildedit.listeners;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.managers.WandManager;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.StringUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInteractListener implements Listener {

    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final PlayerSessionManager session;
    private final MessageConfig messageConfig;
    private final WandManager wandManager;

    public PlayerInteractListener(JavaPlugin paramPlugin, PlayerSessionManager paramSessionManager, MessageConfig paramMessageConfig, WandManager paramItemsConfig) {
        this.plugin = paramPlugin;
        this.session = paramSessionManager;
        this.messageConfig = paramMessageConfig;
        this.wandManager = paramItemsConfig;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            // Only process events for the main hand to avoid duplicates
            if (event.getHand() != EquipmentSlot.HAND) return;

            // Check if clicked block is null
            if (event.getClickedBlock() == null) return;
            Location location = event.getClickedBlock().getLocation();

            // Initialize variables
            Player player = event.getPlayer();
            Action action = event.getAction();
            PlayerSession s = this.getSession().getSession(player);
            ItemStack holdingItem = event.getItem();

            // Check if the player has the required items
            if (isWand(holdingItem)) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, 0.2f, 1.2f);
            } else return;

            // Check world validity
            if (!wandManager.isWandValidWorld(player, holdingItem)) {
                Component c = messageConfig.getComponent(ConfigSection.ACTION_INVALID_WORLD);
                player.sendMessage(c);
                return;
            }

            // Initialize block locations
            if (action == Action.LEFT_CLICK_BLOCK) {
                s.setPosition1(location);
                Component c = messageConfig.getComponent(ConfigSection.SELECTION_POS1);
                c = StringUtil.replace(c, "%x%", String.valueOf(s.getSelection().getBlockPos1().getX()));
                c = StringUtil.replace(c, "%y%", String.valueOf(s.getSelection().getBlockPos1().getY()));
                c = StringUtil.replace(c, "%z%", String.valueOf(s.getSelection().getBlockPos1().getZ()));
                c = StringUtil.replace(c, "%size%", s.getSizeFormatted());
                player.sendMessage(c);
            }

            if (action == Action.RIGHT_CLICK_BLOCK && isSelectionValid(s, location, player, holdingItem)) {
                s.setPosition2(location);
                Component c = messageConfig.getComponent(ConfigSection.SELECTION_POS2);
                c = StringUtil.replace(c, "%x%", String.valueOf(s.getSelection().getBlockPos2().getX()));
                c = StringUtil.replace(c, "%y%", String.valueOf(s.getSelection().getBlockPos2().getY()));
                c = StringUtil.replace(c, "%z%", String.valueOf(s.getSelection().getBlockPos2().getZ()));
                c = StringUtil.replace(c, "%size%", s.getSizeFormatted());
                player.sendMessage(c);
            }

        } catch (NullPointerException ex) {
            Main.getPlugin().getLogger().warning(ex.getMessage());
        }
    }

    public boolean isSelectionValid(PlayerSession s, Location l, Player p, ItemStack item) {
        Selection testSelection = new Selection(s.getSelection().getWorld(), s.getSelection().getBlockPos1(), new BlockLocation(l));
        String wandName = wandManager.getWandName(item);
        if (wandManager.hasWandOverMaxSize(wandName, testSelection.getSize())) {
            Component c = messageConfig.getComponent(ConfigSection.ACTION_MAX_SIZE);
            c = StringUtil.replace(c, "%max%", wandManager.getWandSizeFormatted(wandName));
            c = StringUtil.replace(c, "%size%", testSelection.getSizeFormatted());
            p.sendMessage(c);
            return false;
        } else return true;
    }

    public boolean isWand(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Create the NamespacedKey for the wand ID
        NamespacedKey wandIdKey = new NamespacedKey(plugin, "id");

        // Get the wand ID if it exists
        if (container.has(wandIdKey, PersistentDataType.STRING)) {
            String wandId = container.get(wandIdKey, PersistentDataType.STRING);
            return wandManager.containsWand(wandId);
        }

        return false;
    }

}
