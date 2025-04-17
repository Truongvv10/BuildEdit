package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.enums.EditStatus;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.BlockCalculator;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Edits implements Iterable<BlockLocation> {

    @Getter @Setter
    private  Player player;
    @Getter @Setter
    private Selection selection;
    @Getter @Setter
    private EditStatus status;
    private final MessageConfig messageConfig;

    public Edits(Player paramPlayer, Selection paramSelection, MessageConfig paramMessageConfig) {
        this.setPlayer(paramPlayer);
        this.setSelection(paramSelection);
        this.setStatus(EditStatus.PENDING);
        this.messageConfig = paramMessageConfig;
    }

    public abstract long getSize();

    public void placeBlock(List<BlockPlaceInfo> blocks) {

        // Check if selection is valid
        if (selection.getBlockPos1() == null || selection.getBlockPos2() == null) return;

        // Variables
        this.setStatus(EditStatus.IN_PROGRESS);
        final BlockCalculator calculator = new BlockCalculator(getSize(), blocks);
        final long startTime = System.currentTimeMillis();

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
            final MessageConfig m = messageConfig;
            final long taskStartTime = startTime;

            @Override
            public void run() {
                if (iterator.hasNext()) {
                    BlockLocation blockLocation = iterator.next();
                    Block block = blockLocation.getWorld().getBlockAt(blockLocation.toLocation());
                    block.setType(c.selectBlock().getBlock());
                } else {
                    long endTime = System.currentTimeMillis();
                    long elapsedTimeMs = endTime - taskStartTime;
                    // Convert to seconds with 1 decimal place
                    String elapsedTimeSeconds = String.format("%.1f", elapsedTimeMs / 1000.0);

                    Component c = m.getComponent(ConfigSection.ACTION_SUCCESS)
                            .replaceText(TextReplacementConfig.builder()
                                    .match("%size%")
                                    .replacement(String.valueOf(getSize()))
                                    .build())
                            .replaceText(TextReplacementConfig.builder()
                                    .match("%seconds%")
                                    .replacement(elapsedTimeSeconds)
                                    .build());
                    player.sendMessage(c);
                    setStatus(EditStatus.COMPLETED);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 1);
    }

//    public void performUndo();
//    public void performRedo();

}
