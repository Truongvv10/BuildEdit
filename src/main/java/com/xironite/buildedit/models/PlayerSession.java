package com.xironite.buildedit.models;

import com.xironite.buildedit.editors.SetEditor;
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
    // endregion

    // region Constructors
    public PlayerSession(Player paramPlayer) {
        this.setPlayer(paramPlayer);
        this.setSelection(new Selection(null ,null, null));
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
    }

    public boolean executeSet(List<BlockPlaceInfo> paramBlocks) {
        if (selection.getBlockPos1() != null && selection.getBlockPos2() != null) {
            SetEditor edit = new SetEditor(player, selection);
            edit.place(paramBlocks);
            return true;
        }
        return false;
    }
    // endregion

}
