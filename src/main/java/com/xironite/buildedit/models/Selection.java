package com.xironite.buildedit.models;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleColorData;
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

import java.util.ArrayList;
import java.util.List;

public class Selection {

    @Getter @Setter
    private World world;
    @Getter @Setter
    private BlockLocation blockPos1;
    @Getter @Setter
    private BlockLocation blockPos2;
    private List<Vector3d> cachedParticlePoints = null;
    private long lastX1, lastY1, lastZ1, lastX2, lastY2, lastZ2;

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

    public boolean isValid() {
        return getBlockPos1() != null && getBlockPos2() != null && getWorld() != null;
    }

    public void displaySelectionBox(Player player, long x1, long y1, long z1, long x2, long y2, long z2) {
        if (cachedParticlePoints == null ||
                x1 != lastX1 || y1 != lastY1 || z1 != lastZ1 ||
                x2 != lastX2 || y2 != lastY2 || z2 != lastZ2) {

            // Store new values
            lastX1 = x1; lastY1 = y1; lastZ1 = z1;
            lastX2 = x2; lastY2 = y2; lastZ2 = z2;

            double minX = Math.min(x1, x2) - 0.015;
            double maxX = Math.max(x1, x2) + 1.015;
            double minY = Math.min(y1, y2) - 0.015;
            double maxY = Math.max(y1, y2) + 1.015;
            double minZ = Math.min(z1, z2) - 0.015;
            double maxZ = Math.max(z1, z2) + 1.015;

            cachedParticlePoints = generateBoxEdgePoints(minX, minY, minZ, maxX, maxY, maxZ);
        }

        // Use cached points
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Vector3d pos : cachedParticlePoints) {
                    sendParticle(player, pos.x, pos.y, pos.z);
                }
            }
        }.runTaskLater(Main.getPlugin(), 0L);
    }

    private List<Vector3d> generateBoxEdgePoints(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        double density = 0.2; // Adjust density for particle spacing (5 particles per block)
        List<Vector3d> points = new ArrayList<>();

        // bottom
        points.addAll(drawLine(minX, minY, minZ, maxX, minY, minZ, density));
        points.addAll(drawLine(minX, minY, maxZ, maxX, minY, maxZ, density));
        points.addAll(drawLine(minX, minY, minZ, minX, minY, maxZ, density));
        points.addAll(drawLine(maxX, minY, minZ, maxX, minY, maxZ, density));

        // top
        points.addAll(drawLine(minX, maxY, minZ, maxX, maxY, minZ, density));
        points.addAll(drawLine(minX, maxY, maxZ, maxX, maxY, maxZ, density));
        points.addAll(drawLine(minX, maxY, minZ, minX, maxY, maxZ, density));
        points.addAll(drawLine(maxX, maxY, minZ, maxX, maxY, maxZ, density));

        // verticals
        points.addAll(drawLine(minX, minY, minZ, minX, maxY, minZ, density));
        points.addAll(drawLine(maxX, minY, minZ, maxX, maxY, minZ, density));
        points.addAll(drawLine(minX, minY, maxZ, minX, maxY, maxZ, density));
        points.addAll(drawLine(maxX, minY, maxZ, maxX, maxY, maxZ, density));

        return points;
    }

    private List<Vector3d> drawLine(double x1, double y1, double z1, double x2, double y2, double z2, double density) {
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
        int particles = (int) Math.ceil(distance / density);
        List<Vector3d> points = new ArrayList<>();

        for (int i = 0; i <= particles; i++) {
            double ratio = (double) i / particles;
            double x = x1 + (x2 - x1) * ratio;
            double y = y1 + (y2 - y1) * ratio;
            double z = z1 + (z2 - z1) * ratio;
            points.add(new Vector3d(x, y, z));
        }

        return points;
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
