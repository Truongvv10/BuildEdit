package com.xironite.buildedit.commands.edits;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.ConfigManager;
import com.xironite.buildedit.services.SessionManager;
import com.xironite.buildedit.utils.BlockMapper;
import com.xironite.buildedit.utils.MessageBuilder;
import com.xironite.buildedit.utils.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.List;

@CommandAlias("set")
public class SetCommand extends BaseCommand {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final SessionManager sessionManager;

    @Inject
    public SetCommand(JavaPlugin paramPlugin, ConfigManager paramConfigManager, SessionManager sessionManager) {
        this.plugin = paramPlugin;
        this.configManager = paramConfigManager;
        this.sessionManager = sessionManager;
    }

    @Default
    @CommandCompletion("@blocks @nothing")
    @Conditions("wands|sound")
    public void onWand(CommandSender sender, @Optional String blockTypes) {
        if (sender instanceof Player player) {
            // Check if blockType is null
            if (blockTypes == null) {
                new MessageBuilder(configManager.messages().get(ConfigSection.SYNTAX_SET) + "\n" + configManager.messages().get(ConfigSection.DESC_SET))
                        .toPlayer(player)
                        .build();
                return;
            }

            // Check if block types are valid
            if (!BlockMapper.areAllValidMaterials(blockTypes)) {
                configManager.messages().getFromCache(ConfigSection.ACTION_INVALID_BLOCKS)
                        .toPlayer(sender)
                        .build();
                return;
            }

            // Else execute command
            List<BlockPlaceInfo> blocks = BlockMapper.mapParamsToBlockInfo(blockTypes);
            PlayerSession session = sessionManager.getSession(player);
            if (session.getSelection().getBlockPos1() != null && session.getSelection().getBlockPos2() != null) {
                session.executeSet(blocks);
            }
        }
    }
}
