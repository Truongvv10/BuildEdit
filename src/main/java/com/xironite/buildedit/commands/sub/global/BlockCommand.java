package com.xironite.buildedit.commands.sub.global;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.BlockMapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BlockCommand extends CommandAbstract {

    public BlockCommand(JavaPlugin paramPlugin, PlayerSessionManager paramSession, MessageConfig paramMessageConf, String paramName, String paramPermission) {
        super(paramPlugin, paramSession, paramMessageConf, paramName, paramPermission);
    }

    @Override
    public boolean onExecute(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            List<BlockPlaceInfo> blocks = BlockMapper.mapParamsToBlockInfo(args[0]);
            PlayerSession session = playerSessionManager.getSession(player);
            if (session.getSelection().getBlockPos1() != null && session.getSelection().getBlockPos2() != null) {
                session.executeSet(blocks);
            }
        }
        return true;
    }
}
