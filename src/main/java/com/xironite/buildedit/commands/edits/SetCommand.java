package com.xironite.buildedit.commands.edits;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.xml.XmlEscapers;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.BlockMapper;
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
    private final MessageConfig messageConfig;
    private final ItemsConfig itemsConfig;
    private final PlayerSessionManager sessionManager;
    private final String syntax;

    @Inject
    public SetCommand(JavaPlugin plugin, MessageConfig messageConfig, ItemsConfig itemsConfig, PlayerSessionManager sessionManager) {
        this.plugin = plugin;
        this.messageConfig = messageConfig;
        this.itemsConfig = itemsConfig;
        this.sessionManager = sessionManager;
        this.syntax = messageConfig.get(ConfigSection.SYNTAX_SET) + "\n" + messageConfig.get(ConfigSection.DESC_SET);
    }

    @Default
    @CommandCompletion("@blocks")
    @Conditions("wands|sound")
    public void onWand(CommandSender sender, @Optional String blockTypes) {
        if (sender instanceof Player player) {
            // Check if blockType is null
            if (blockTypes == null) {
                Component c = StringUtil.translateColor(messageConfig.get(ConfigSection.SYNTAX_SET) + "\n" + messageConfig.get(ConfigSection.DESC_SET));
                player.sendMessage(c);
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
