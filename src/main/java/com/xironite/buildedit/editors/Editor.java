package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.enums.EditStatus;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.utils.BlockCalculator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Editor implements Iterable<BlockLocation> {

    @Getter @Setter
    private  Player player;
    @Getter @Setter
    private Selection selection;
    @Getter @Setter
    private EditStatus status;

    public Editor(Player paramPlayer, Selection paramSelection) {
        this.setPlayer(paramPlayer);
        this.setSelection(paramSelection);
        this.setStatus(EditStatus.PENDING);
    }

    public abstract long getSize();

    public void placeBlock(List<BlockPlaceInfo> blocks) {
        this.setStatus(EditStatus.IN_PROGRESS);
        final BlockCalculator calculator = new BlockCalculator(getSize(), blocks);

        // Check if player has blocks to place
        Inventory inventory = this.player.getInventory();
        if (!calculator.hasBlocks(inventory)) {
            Map<Material, Long> missing = calculator.getMissingBlocks(inventory);
            String message = missing.entrySet().stream()
                    .map(entry -> entry.getKey().toString() + ": " + entry.getValue())
                    .collect(Collectors.joining(", "));
            player.sendMessage(message);

            this.setStatus(EditStatus.FAILED);
            return;
        } else {
            calculator.consumeBlocks(inventory);
        }

        // Place blocks
        new BukkitRunnable() {
            final Iterator<BlockLocation> iterator = iterator();
            final BlockCalculator c = calculator;

            @Override
            public void run() {
                if (iterator.hasNext()) {
                    BlockLocation blockLocation = iterator.next();
                    Block block = blockLocation.getWorld().getBlockAt(blockLocation.toLocation());
                    block.setType(c.selectBlock().getBlock());
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
