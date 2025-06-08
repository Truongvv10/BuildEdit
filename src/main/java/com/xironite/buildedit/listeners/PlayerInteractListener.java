package com.xironite.buildedit.listeners;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.items.Wand;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.SessionManager;
import com.xironite.buildedit.utils.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInteractListener implements Listener {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final WandManager wandManager;
    private final SessionManager session;

    public PlayerInteractListener(JavaPlugin paramPlugin, ConfigManager paramConfigManager, WandManager paramItemsConfig, SessionManager paramSessionManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.wandManager = paramItemsConfig;
        this.session = paramSessionManager;
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
            PlayerSession s = session.getSession(player);
            ItemStack holdingItem = event.getItem();

            // Check if the player has the required items
            if (wandManager.contains(holdingItem)) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, 0.2f, 1.2f);
            } else return;

            // Check world validity
            Wand wand = wandManager.get(holdingItem);
            assert wand != null;
            if (wand.getWorlds().contains(location.getWorld().getName())) {
                configManager.messages().getFromCache(ConfigSection.ACTION_INVALID_WORLD)
                        .toPlayer(player)
                        .build();
                return;
            }

            // Initialize block locations
            if (action == Action.LEFT_CLICK_BLOCK) {
                s.setPosition1(location);
                if (wand.isSelectionMessageEnabled())
                    configManager.messages().getFromCache(ConfigSection.SELECTION_POS1)
                        .replace("%x%", String.valueOf(s.getSelection().getBlockPos1().getX()))
                        .replace("%y%", String.valueOf(s.getSelection().getBlockPos1().getY()))
                        .replace("%z%", String.valueOf(s.getSelection().getBlockPos1().getZ()))
                        .replace("%size%", s.getSizeFormatted())
                        .toPlayer(player)
                        .build();
            }

            if (action == Action.RIGHT_CLICK_BLOCK && isSelectionValid(s, location, player, holdingItem)) {
                s.setPosition2(location);
                if (wand.isSelectionMessageEnabled())
                    configManager.messages().getFromCache(ConfigSection.SELECTION_POS2)
                        .replace("%x%", String.valueOf(s.getSelection().getBlockPos2().getX()))
                        .replace("%y%", String.valueOf(s.getSelection().getBlockPos2().getY()))
                        .replace("%z%", String.valueOf(s.getSelection().getBlockPos2().getZ()))
                        .replace("%size%", s.getSizeFormatted())
                        .toPlayer(player)
                        .build();
            }

        } catch (NullPointerException ex) {
            plugin.getLogger().warning(ex.getMessage());
        }
    }

    public boolean isSelectionValid(PlayerSession s, Location l, Player p, ItemStack item) {
        Selection testSelection = new Selection(s.getSelection().getWorld(), s.getSelection().getBlockPos1(), new BlockLocation(l));
        if (wandManager.isExceedingMaxSize(item, testSelection.getSize())) {
            configManager.messages().getFromCache(ConfigSection.ACTION_MAX_SIZE)
                    .replace("%max%", wandManager.getSizeFormatted(item))
                    .replace("%size%", testSelection.getSizeFormatted())
                    .toPlayer(p)
                    .build();
            return false;
        } else return true;
    }

}
