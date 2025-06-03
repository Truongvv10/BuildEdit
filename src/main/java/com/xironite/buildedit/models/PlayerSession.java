package com.xironite.buildedit.models;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.editors.SetEdits;
import com.xironite.buildedit.storage.configs.MessageConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class PlayerSession {

    // region Fields
    @Getter @Setter
    private Player player;
    @Getter @Setter
    private Selection selection;
    @Getter @Setter
    private MessageConfig messageConfig;
    private BukkitTask particleTask;
    // endregion

    // region Constructors
    public PlayerSession(Player paramPlayer, MessageConfig paramMessageConfig) {
        this.setPlayer(paramPlayer);
        this.setSelection(new Selection());
        this.setMessageConfig(paramMessageConfig);
    }
    // endregion

    // region Methods
    public void setPosition1(Location paramLocation) {
        BlockLocation location = new BlockLocation(paramLocation);
        this.getSelection().setWorld(player.getLocation().getWorld());

        // Cancel previous task if exists
        if (this.particleTask != null) {
            this.particleTask.cancel();
        }

        // Set position 1 and reset position 2
        this.getSelection().setBlockPos2(null);
        this.getSelection().setBlockPos1(location);
    }

    public void setPosition2(Location paramLocation) {
        BlockLocation location = new BlockLocation(paramLocation);
        this.getSelection().setBlockPos2(location);

        // Cancel previous task if exists
        if (this.particleTask != null) {
            this.particleTask.cancel();
        }

        // Start new particle display task
        this.particleTask = new BukkitRunnable() {
            private int count = 0;
            @Override
            public void run() {
                displayParticle();
                count++;
                if (count >= 15) this.cancel();
            }
        }.runTaskTimer(Main.getPlugin(), 0L, 10L);
    }

    public void displayParticle() {
        if (selection.getBlockPos1() != null && selection.getBlockPos2() != null) {
            long x1 = selection.getBlockPos1().getX();
            long y1 = selection.getBlockPos1().getY();
            long z1 = selection.getBlockPos1().getZ();
            long x2 = selection.getBlockPos2().getX();
            long y2 = selection.getBlockPos2().getY();
            long z2 = selection.getBlockPos2().getZ();
            selection.displaySelectionBox(player, x1, y1, z1, x2, y2, z2);
        }
    }

    public long getSize() {
        return selection.getSize();
    }

    public String getSizeFormatted() {
        return selection.getSizeFormatted();
    }

    public void executeSet(List<BlockPlaceInfo> paramBlocks) {
        if (selection.getBlockPos1() != null && selection.getBlockPos2() != null) {
            SetEdits edit = new SetEdits(player, selection, messageConfig);
            edit.placeBlock(paramBlocks, 1);
        }
    }
    // endregion

}
