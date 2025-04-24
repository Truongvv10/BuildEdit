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

    public Selection(World paramWorld, BlockLocation paramBlockPos1, BlockLocation paramBlockPos2) {
        this.setWorld(paramWorld);
        this.setBlockPos1(paramBlockPos1);
        this.setBlockPos2(paramBlockPos2);
    }

    private void sendParticle(Player player, long x, long y, long z) {
        // For block corners, we need to use the exact coordinates
        // For the "maximum" edges, add 1 to represent the outer boundary
        Vector3d position = new Vector3d(x, y, z);
        Vector3f offset = new Vector3f(0, 0, 0); // No spread

        // Using a more visible particle
        Particle<?> p = new Particle<>(ParticleTypes.END_ROD);

        WrapperPlayServerParticle particlePacket = new WrapperPlayServerParticle(
                p,
                true,
                position,
                offset,
                0,
                1,
                true
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, particlePacket);

    }

}
