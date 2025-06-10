package com.xironite.buildedit.commands.edits;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.SessionManager;
import com.xironite.buildedit.utils.BlockMapper;
import com.xironite.buildedit.utils.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("replace")
public class ReplaceCommand extends BaseCommand {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final SessionManager sessionManager;

    @Inject
    public ReplaceCommand(JavaPlugin paramPlugin, ConfigManager paramConfigManager, SessionManager sessionManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.sessionManager = sessionManager;
    }

    @Default
    @Conditions("wands|sound")
    @CommandCompletion("@replace @blocks @nothing")
    public void onReplace(CommandSender sender, @Optional String target, @Optional String replace) {
        if (sender instanceof Player player) {
            // Check if blockType is null
            if (target == null || replace == null) {
                new MessageBuilder(configManager.messages().get(ConfigSection.SYNTAX_REPLACE) + "\n" + configManager.messages().get(ConfigSection.DESC_REPLACE))
                        .toPlayer(player)
                        .build();
                return;
            }

            // Check if block types are valid
            if (!BlockMapper.areAllValidMaterials(target) && !BlockMapper.areAllValidMaterials(replace)) {
                configManager.messages().getFromCache(ConfigSection.ACTION_INVALID_BLOCKS)
                        .toPlayer(sender)
                        .build();
                return;
            }

            // Else execute command
            List<BlockPlaceInfo> targetBlocks = BlockMapper.mapParamsToBlockInfo(target);
            List<BlockPlaceInfo> replaceBlocks = BlockMapper.mapParamsToBlockInfo(replace);
            PlayerSession session = sessionManager.getSession(player);
            if (session.getSelection().getBlockPos1() != null && session.getSelection().getBlockPos2() != null) {
                session.executeReplace(targetBlocks, replaceBlocks);
            }
        }
    }
}
