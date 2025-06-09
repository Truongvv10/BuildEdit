package com.xironite.buildedit.models;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.editors.SetEdits;
import com.xironite.buildedit.editors.WallEdits;
import com.xironite.buildedit.models.enums.CopyStatus;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
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
    @Getter
    private final Clipboard clipboard;
    private final ConfigManager configManager;
    private final WandManager wandManager;
    private BukkitTask particleTask;
    // endregion

    // region Constructors
    public PlayerSession(Player paramPlayer, ConfigManager paramConfigManager, WandManager paramWandManager) {
        this.setPlayer(paramPlayer);
        this.setSelection(new Selection());
        this.configManager = paramConfigManager;
        this.wandManager = paramWandManager;
        this.clipboard = new Clipboard(paramPlayer, paramConfigManager, paramWandManager);
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
                if (count >= 50) this.cancel();
            }
        }.runTaskTimer(Main.getPlugin(), 0L, 25L);
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

    public void executeCopy() {
        try {
            if (clipboard.getStatus() != CopyStatus.IN_PROGRESS_COPYING) {
                clipboard.copy(selection);
            } else player.sendMessage("Copy in progress, please wait until it completes.");
        } catch (Exception e) {
            Main.getPlugin().getLogger().warning("Error during copy: " + e.getMessage());
        }
    }

    public void executePaste() {
        try {
            if (clipboard.getStatus() != CopyStatus.IN_PROGRESS_COPYING) {
                clipboard.paste(player.getLocation().toBlockLocation(), 1);
            } else player.sendMessage("Copy in progress, please wait until it completes.");
        } catch (Exception e) {
            Main.getPlugin().getLogger().warning("Error during paste: " + e.getMessage());
        }
    }

    public void executeRotate() {
        try {
            if (clipboard.getStatus() != CopyStatus.IN_PROGRESS_PASTING) {
                clipboard.rotate();
            } else player.sendMessage("Paste in progress, please wait until it completes.");
        } catch (Exception e) {
            Main.getPlugin().getLogger().warning("Error during rotate: " + e.getMessage());
        }
    }

    public void executeSet(List<BlockPlaceInfo> paramBlocks) {
        if (selection.isValid()) {
            SetEdits edit = new SetEdits(player, selection, configManager, wandManager);
            edit.start(paramBlocks, 1);
        }
    }

    public void executeWalls(List<BlockPlaceInfo> paramBlocks) {
        if (selection.isValid()) {
            WallEdits edit = new WallEdits(player, selection, configManager, wandManager);
            edit.start(paramBlocks, 1);
        }
    }
    // endregion

}
