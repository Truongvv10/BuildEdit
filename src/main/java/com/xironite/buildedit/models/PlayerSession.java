package com.xironite.buildedit.models;

import com.xironite.buildedit.editors.SetEdits;
import com.xironite.buildedit.storage.configs.MessageConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerSession {

    // region Fields
    @Getter @Setter
    private Player player;
    @Getter @Setter
    private Selection selection;
    @Getter @Setter
    private MessageConfig messageConfig;
    // endregion

    // region Constructors
    public PlayerSession(Player paramPlayer, MessageConfig paramMessageConfig) {
        this.setPlayer(paramPlayer);
        this.setSelection(new Selection(null ,null, null));
        this.setMessageConfig(paramMessageConfig);
    }
    // endregion

    // region Methods
    public void setPosition1(Location paramLocation) {
        BlockLocation location = new BlockLocation(paramLocation);
        this.getSelection().setWorld(player.getLocation().getWorld());
        this.getSelection().setBlockPos1(location);
    }

    public void setPosition2(Location paramLocation) {
        BlockLocation location = new BlockLocation(paramLocation);
        this.getSelection().setWorld(player.getLocation().getWorld());
        this.getSelection().setBlockPos2(location);
        this.displayParticle();
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
        if (selection.getBlockPos1() != null && selection.getBlockPos2() != null) {
            long deltaX = Math.abs(selection.getBlockPos1().getX() - selection.getBlockPos2().getX()) + 1;
            long deltaY = Math.abs(selection.getBlockPos1().getY() - selection.getBlockPos2().getY()) + 1;
            long deltaZ = Math.abs(selection.getBlockPos1().getZ() - selection.getBlockPos2().getZ()) + 1;
            return deltaX * deltaY * deltaZ;
        } else return 0;
    }

    public String getSizeFormatted() {
        return String.format("%,d", getSize());
    }

    public void executeSet(List<BlockPlaceInfo> paramBlocks) {
        if (selection.getBlockPos1() != null && selection.getBlockPos2() != null) {
            SetEdits edit = new SetEdits(player, selection, messageConfig);
            edit.placeBlock(paramBlocks, 1);
        }
    }
    // endregion

}
