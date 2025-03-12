package com.xironite.buildedit.editors;

import io.papermc.paper.math.BlockPosition;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

public class Selection {

    public Selection(World paramWorld, BlockLocation paramBlockPos1, BlockLocation paramBlockPos2) {
        this.setWorld(paramWorld);
        this.setBlockPos1(paramBlockPos1);
        this.setBlockPos2(paramBlockPos2);
    }

    @Getter @Setter
    private World world;
    @Getter @Setter
    private BlockLocation blockPos1;
    @Getter @Setter
    private BlockLocation blockPos2;


}
