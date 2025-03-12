package com.xironite.buildedit.listeners;

import com.xironite.buildedit.editors.BlockLocation;
import com.xironite.buildedit.editors.Selection;
import com.xironite.buildedit.editors.SetEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {

    BlockLocation block1;
    BlockLocation block2;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            Action action = event.getAction();
            if (action == Action.RIGHT_CLICK_BLOCK) {
                block2 = null;
                assert event.getClickedBlock() != null;
                block1 = new BlockLocation(event.getClickedBlock().getLocation());
            } else if (action == Action.LEFT_CLICK_BLOCK) {
                assert event.getClickedBlock() != null;
                block2 = new BlockLocation(event.getClickedBlock().getLocation());
            }
        } catch (NullPointerException ignored) { }
        if (block1 != null && block2 != null) {
            Selection selection = new Selection(event.getPlayer().getWorld(), block1, block2);
            SetEditor edit = new SetEditor(event.getPlayer(), selection);
            edit.place();
        }
    }

}
