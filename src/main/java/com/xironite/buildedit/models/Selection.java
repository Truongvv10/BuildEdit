package com.xironite.buildedit.models;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import com.xironite.buildedit.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Selection {

    @Getter @Setter
    private World world;
    @Getter @Setter
    private BlockLocation blockPos1;
    @Getter @Setter
    private BlockLocation blockPos2;

    public Selection() {
        this.setWorld(null);
        this.setBlockPos1(null);
        this.setBlockPos2(null);
    }

    public Selection(World paramWorld, BlockLocation paramBlockPos1, BlockLocation paramBlockPos2) {
        this.setWorld(paramWorld);
        this.setBlockPos1(paramBlockPos1);
        this.setBlockPos2(paramBlockPos2);
    }

    public long getSize() {
        if (getBlockPos1() != null && getBlockPos2() != null) {
            long deltaX = Math.abs(getBlockPos1().getX() - getBlockPos2().getX()) + 1;
            long deltaY = Math.abs(getBlockPos1().getY() - getBlockPos2().getY()) + 1;
            long deltaZ = Math.abs(getBlockPos1().getZ() - getBlockPos2().getZ()) + 1;
            return deltaX * deltaY * deltaZ;
        } else return 0;
    }
    public String getSizeFormatted() {
        return String.format("%,d", getSize());
    }

    public void displaySelectionBox(Player player, long x1, long y1, long z1, long x2, long y2, long z2) {
        double minX = Math.min(x1, x2) - 0.015;
        double maxX = Math.max(x1, x2) + 1.015;
        double minY = Math.min(y1, y2) - 0.015;
        double maxY = Math.max(y1, y2) + 1.015;
        double minZ = Math.min(z1, z2) - 0.015;
        double maxZ = Math.max(z1, z2) + 1.015;
        displayBoxEdges(player, minX, minY, minZ, maxX, maxY, maxZ);
    }

    private void displayBoxEdges(Player player, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        // Particle density - how many particles per block
        double particleDensity = 0.2; // 4 particles per block edge

        // Bottom face edges (4 edges)
        drawLine(player, minX, minY, minZ, maxX, minY, minZ, particleDensity); // Front bottom
        drawLine(player, minX, minY, maxZ, maxX, minY, maxZ, particleDensity); // Back bottom
        drawLine(player, minX, minY, minZ, minX, minY, maxZ, particleDensity); // Left bottom
        drawLine(player, maxX, minY, minZ, maxX, minY, maxZ, particleDensity); // Right bottom

        // Top face edges (4 edges)
        drawLine(player, minX, maxY, minZ, maxX, maxY, minZ, particleDensity); // Front top
        drawLine(player, minX, maxY, maxZ, maxX, maxY, maxZ, particleDensity); // Back top
        drawLine(player, minX, maxY, minZ, minX, maxY, maxZ, particleDensity); // Left top
        drawLine(player, maxX, maxY, minZ, maxX, maxY, maxZ, particleDensity); // Right top

        // Vertical edges (4 edges)
        drawLine(player, minX, minY, minZ, minX, maxY, minZ, particleDensity); // Front-left vertical
        drawLine(player, maxX, minY, minZ, maxX, maxY, minZ, particleDensity); // Front-right vertical
        drawLine(player, minX, minY, maxZ, minX, maxY, maxZ, particleDensity); // Back-left vertical
        drawLine(player, maxX, minY, maxZ, maxX, maxY, maxZ, particleDensity); // Back-right vertical
    }

    private void drawLine(Player player, double x1, double y1, double z1, double x2, double y2, double z2, double density) {
        double distance = Math.sqrt(
                Math.pow(x2 - x1, 2) +
                        Math.pow(y2 - y1, 2) +
                        Math.pow(z2 - z1, 2)
        );
        int particles = (int) Math.ceil(distance / density);
        for (int i = 0; i <= particles; i++) {
            double ratio = (double) i / particles;
            double x = x1 + (x2 - x1) * ratio;
            double y = y1 + (y2 - y1) * ratio;
            double z = z1 + (z2 - z1) * ratio;

            sendParticle(player, x, y, z);
        }
    }

    private void sendParticle(Player player, double x, double y, double z) {
        Vector3d position = new Vector3d(x, y, z);
        Vector3f offset = new Vector3f(0, 0, 0);
        Particle<?> p = new Particle<>(ParticleTypes.OMINOUS_SPAWNING);
        WrapperPlayServerParticle particlePacket = new WrapperPlayServerParticle(
                p,
                false,
                position,
                offset,
                0,
                1,
                false
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, particlePacket);
    }

}
