package com.xironite.buildedit.listeners;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.StringUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInteractListener implements Listener {

    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final PlayerSessionManager session;
    @Getter
    private final MessageConfig messageConfig;

    public PlayerInteractListener(JavaPlugin paramPlugin, PlayerSessionManager paramSessionManager, MessageConfig paramMessageConfig) {
        this.plugin = paramPlugin;
        this.session = paramSessionManager;
        this.messageConfig = paramMessageConfig;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            // Initialize variables
            Player player = event.getPlayer();
            Action action = event.getAction();
            PlayerSession s = this.getSession().getSession(player);

            // Only process events for the main hand to avoid duplicates
            if (event.getHand() != EquipmentSlot.HAND) return;

            // Check if clicked block is null
            if (event.getClickedBlock() == null) return;

            // Check if the player has the required items
            if (hasData(event.getItem(), "wand1"))
                event.setCancelled(true); else return;

            // Initialize block locations
            if (action == Action.LEFT_CLICK_BLOCK) {
                s.setPosition1(event.getClickedBlock().getLocation());
                Component c = messageConfig.getComponent(ConfigSection.SELECTION_POS1);
                c = StringUtil.replace(c, "%x%", String.valueOf(s.getSelection().getBlockPos1().getX()));
                c = StringUtil.replace(c, "%y%", String.valueOf(s.getSelection().getBlockPos1().getY()));
                c = StringUtil.replace(c, "%z%", String.valueOf(s.getSelection().getBlockPos1().getZ()));
                c = StringUtil.replace(c, "%size%", String.valueOf(s.getSize()));
                player.sendMessage(c);
            }

            if (action == Action.RIGHT_CLICK_BLOCK) {
                s.setPosition2(event.getClickedBlock().getLocation());
                Component c = messageConfig.getComponent(ConfigSection.SELECTION_POS2);
                c = StringUtil.replace(c, "%x%", String.valueOf(s.getSelection().getBlockPos2().getX()));
                c = StringUtil.replace(c, "%y%", String.valueOf(s.getSelection().getBlockPos2().getY()));
                c = StringUtil.replace(c, "%z%", String.valueOf(s.getSelection().getBlockPos2().getZ()));
                c = StringUtil.replace(c, "%size%", String.valueOf(s.getSize()));
                player.sendMessage(c);
            }

        } catch (NullPointerException ex) {
            Main.getPlugin().getLogger().warning(ex.getMessage());
        }
    }

    public boolean hasData(ItemStack item, String keyName) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        NamespacedKey key = new NamespacedKey(plugin, "type");
        return meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

}
