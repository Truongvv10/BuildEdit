package com.xironite.buildedit.listeners;

import com.xironite.buildedit.editors.BlockLocation;
import com.xironite.buildedit.player.Selection;
import com.xironite.buildedit.editors.SetEditor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteract implements Listener {

    BlockLocation block1;
    BlockLocation block2;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            Action action = event.getAction();

            // Only process events for the main hand to avoid duplicates
            if (event.getHand() != EquipmentSlot.HAND) {
                return;
            }

            if (event.getItem() != null && event.getItem().getType() == Material.GOLDEN_AXE)
                event.setCancelled(true); else return;

            if (action == Action.LEFT_CLICK_BLOCK) {
                block1 = null;
                assert event.getClickedBlock() != null;
                block2 = new BlockLocation(event.getClickedBlock().getLocation());
                player.sendMessage("pos2 " + block2.getX() + ", " + block2.getY() + ", " + block2.getZ());
            }

            if (action == Action.RIGHT_CLICK_BLOCK) {
                assert event.getClickedBlock() != null;
                block1 = new BlockLocation(event.getClickedBlock().getLocation());
                player.sendMessage("pos1 " + block1.getX() + ", " + block1.getY() + ", " + block1.getZ());
            }

            if (block1 != null && block2 != null) {
                Selection selection = new Selection(event.getPlayer().getWorld(), block1, block2);
                SetEditor edit = new SetEditor(event.getPlayer(), selection);
                edit.place();
            }

        } catch (NullPointerException ignored) { }
    }

}
