package com.xironite.buildedit.models;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.editors.SetEdits;
import com.xironite.buildedit.models.enums.CopyStatus;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.WandManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Clipboard {

    @Getter
    private final Player player;
    @Getter
    private List<BlockInfo> blocks;
    @Getter
    private CopyStatus status;
    private final ConfigManager configManager;
    private final WandManager wandManager;


    public Clipboard(Player paramPlayer, ConfigManager paramConfigManager, WandManager paramWandManager) {
        this.status = CopyStatus.NOT_STARTED;
        this.player = paramPlayer;
        this.configManager = paramConfigManager;
        this.wandManager = paramWandManager;
        this.blocks = new ArrayList<>();
    }

    public void clear() {
        this.blocks.clear();
    }

    public void copy(Selection selection) {
        if (selection.isValid()) {
            clear();
            SetEdits edit = new SetEdits(player, selection, configManager, wandManager);
            this.status = CopyStatus.IN_PROGRESS;
            edit.copyBlocks(1024).thenAccept(b -> {
                blocks.addAll(b);
                this.status = CopyStatus.COMPLETED;
                Main.getPlugin().getLogger().info("Copied " + blocks.size() + " blocks");
            });
        }
    }

}
