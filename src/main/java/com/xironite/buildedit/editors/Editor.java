package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.Selection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public abstract class Editor implements Iterable<BlockLocation> {

    public Editor(Player paramPlayer, Selection paramSelection) {
        this.setPlayer(paramPlayer);
        this.setSelection(paramSelection);
        this.setStatus(EditStatus.PENDING);
    }

    @Getter @Setter
    private  Player player;
    @Getter @Setter
    private Selection selection;
    @Getter @Setter
    private EditStatus status;

    public abstract long getSize();

    public void place(Material material) {
        this.setStatus(EditStatus.IN_PROGRESS);
        new BukkitRunnable() {
            final Iterator<BlockLocation> iterator = iterator();

            @Override
            public void run() {
                if (iterator.hasNext()) {
                    BlockLocation blockLocation = iterator.next();
                    blockLocation.getWorld().getBlockAt(blockLocation.toLocation()).setType(material);
                } else {
                    setStatus(EditStatus.COMPLETED);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 1);
    }

//    public void performUndo();
//    public void performRedo();

}
