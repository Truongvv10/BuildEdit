package com.xironite.buildedit.commands.sub;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.utils.BlockMapper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BlockCommand extends CommandAbstract {

    public BlockCommand(JavaPlugin paramPlugin, PlayerSessionManager paramSession, String paramName, String paramPermission, String paramDescription, String paramSyntax) {
        super(paramPlugin, paramSession, paramName, paramPermission, paramDescription, paramSyntax);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
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
