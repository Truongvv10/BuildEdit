package com.xironite.buildedit.listeners;

import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.editors.SetEditor;
import com.xironite.buildedit.services.PlayerSessionManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInteractListener implements Listener {

    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final PlayerSessionManager session;

    public PlayerInteractListener(JavaPlugin paramPlugin, PlayerSessionManager paramSessionManager) {
        this.plugin = paramPlugin;
        this.session = paramSessionManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            // Initialize variables
            Player player = event.getPlayer();
            Action action = event.getAction();
            PlayerSession s = this.getSession().getSession(player);

            // Only process events for the main hand to avoid duplicates
            if (event.getHand() != EquipmentSlot.HAND)
                return;

            // Check if the player has the required items
            if (event.getItem() != null && event.getItem().getType() == Material.NETHERITE_AXE)
                event.setCancelled(true); else return;

            // Initialize block locations
            if (action == Action.LEFT_CLICK_BLOCK) {
                assert event.getClickedBlock() != null;
                s.setPosition1(event.getClickedBlock().getLocation());
                player.sendMessage("pos2 " + s.getSelection().getBlockPos1().getX() + ", " + s.getSelection().getBlockPos1().getY() + ", " + s.getSelection().getBlockPos1().getZ());
            }

            if (action == Action.RIGHT_CLICK_BLOCK) {
                assert event.getClickedBlock() != null;
                s.setPosition2(event.getClickedBlock().getLocation());
                player.sendMessage("pos2 " + s.getSelection().getBlockPos2().getX() + ", " + s.getSelection().getBlockPos2().getY() + ", " + s.getSelection().getBlockPos2().getZ());
            }

        } catch (NullPointerException ignored) { }
    }

}
