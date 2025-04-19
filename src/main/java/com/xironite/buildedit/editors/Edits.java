package com.xironite.buildedit.editors;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.enums.EditStatus;
import com.xironite.buildedit.models.BlockLocation;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.Selection;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.BlockCalculator;
import com.xironite.buildedit.utils.StringUtil;
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

    protected String getSizeFormatted() {
        return String.format("%,d", getSize());
    }

    public void placeBlock(List<BlockPlaceInfo> blocks, int placeSpeedInTicks) {

        // Check if selection is valid
        if (selection.getBlockPos1() == null || selection.getBlockPos2() == null) return;

        // Variables
        this.setStatus(EditStatus.IN_PROGRESS);
        final BlockCalculator calculator = new BlockCalculator(getSize(), blocks);
        final long startTime = System.currentTimeMillis();

        // Check if player has blocks to place
        Inventory inventory = this.player.getInventory();
        if (!calculator.hasBlocks(inventory)) {
            Map<Material, Long> missingBlocks = calculator.getMissingBlocks(inventory);
            String delimiter = messageConfig.get(ConfigSection.ACTION_MISSING_DELIMITER);
            String separator = messageConfig.get(ConfigSection.ACTION_MISSING_SEPARATOR);
            String missing = missingBlocks.entrySet().stream()
                    .map(x -> x.getKey().toString().toLowerCase() + separator + x.getValue())
                    .collect(Collectors.joining(delimiter));
            Component c = StringUtil.replace(messageConfig.getComponent(ConfigSection.ACTION_MISSING), "%missing%", missing);
            player.sendMessage(c);
            this.setStatus(EditStatus.FAILED);
            return;

        } else {
            calculator.consumeBlocks(inventory);
            Component c = messageConfig.getComponent(ConfigSection.ACTION_STATUS_START);
            c = StringUtil.replace(c, "%size%", getSizeFormatted());
            c = StringUtil.replace(c, "%seconds%", String.format("%.2f", calculator.getExpectedTime(placeSpeedInTicks)));
            player.sendMessage(c);
        }

        // Place blocks
        new BukkitRunnable() {

            // Variables
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
                    String elapsedTimeSeconds = String.format("%.2f", elapsedTimeMs / 1000.0);

                    Component c = m.getComponent(ConfigSection.ACTION_STATUS_FINISH);
                    c = StringUtil.replace(c, "%seconds%", elapsedTimeSeconds);
                    c = StringUtil.replace(c, "%size%", getSizeFormatted());
                    player.sendMessage(c);
                    setStatus(EditStatus.COMPLETED);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, placeSpeedInTicks);
    }

//    public void performUndo();
//    public void performRedo();

}
