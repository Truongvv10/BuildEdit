package com.xironite.buildedit.editors;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

public class BlockLocation {

    public BlockLocation(World paramWorld, long paramX, long paramY, long paramZ) {
        this.setWorld(paramWorld);
        this.setX(paramX);
        this.setY(paramY);
        this.setZ(paramZ);
    }

    public BlockLocation(Location paramLocation) {
        this(paramLocation.getWorld(), paramLocation.getBlockX(), paramLocation.getBlockY(), paramLocation.getBlockZ());
    }

    @Getter @Setter
    private World world;
    @Getter @Setter
    private long x;
    @Getter @Setter
    private long y;
    @Getter @Setter
    private long z;

    public Location toLocation() {
        return new Location(this.getWorld(), this.getX(), this.getY(), this.getZ());
    }

    @Override
    public String toString() {
        return x + " : " + y + " : " + z;
    }

}
