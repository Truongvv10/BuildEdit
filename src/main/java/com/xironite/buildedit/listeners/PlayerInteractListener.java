package com.xironite.buildedit.listeners;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.models.PlayerSession;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.MessageConfig;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInteractListener implements Listener {

    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final PlayerSessionManager session;
    @Getter
    private final MessageConfig messageConfig;

    public PlayerInteractListener(JavaPlugin paramPlugin, PlayerSessionManager paramSessionManager, MessageConfig paramMessageConfig) {
        this.plugin = paramPlugin;
        this.session = paramSessionManager;
        this.messageConfig = paramMessageConfig;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            // Initialize variables
            Player player = event.getPlayer();
            Action action = event.getAction();
            PlayerSession s = this.getSession().getSession(player);

            // Only process events for the main hand to avoid duplicates
            if (event.getHand() != EquipmentSlot.HAND)
                return;

            // Check if the player has the required items
            if (event.getItem() != null && event.getItem().getType() == Material.NETHERITE_AXE)
                event.setCancelled(true); else return;

            // Initialize block locations
            if (action == Action.LEFT_CLICK_BLOCK) {
                assert event.getClickedBlock() != null;
                s.setPosition1(event.getClickedBlock().getLocation());
                Component message = messageConfig.getComponent(ConfigSection.SELECTION_POS1)
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%x%")
                                .replacement(String.valueOf(s.getSelection().getBlockPos1().getX()))
                                .build())
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%y%")
                                .replacement(String.valueOf(s.getSelection().getBlockPos1().getY()))
                                .build())
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%z%")
                                .replacement(String.valueOf(s.getSelection().getBlockPos1().getZ()))
                                .build())
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%size%")
                                .replacement(String.valueOf(s.getSize()))
                                .build());
                player.sendMessage(message);
            }

            if (action == Action.RIGHT_CLICK_BLOCK) {
                assert event.getClickedBlock() != null;
                s.setPosition2(event.getClickedBlock().getLocation());
                Component message = messageConfig.getComponent(ConfigSection.SELECTION_POS2)
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%x%")
                                .replacement(String.valueOf(s.getSelection().getBlockPos2().getX()))
                                .build())
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%y%")
                                .replacement(String.valueOf(s.getSelection().getBlockPos2().getY()))
                                .build())
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%z%")
                                .replacement(String.valueOf(s.getSelection().getBlockPos2().getZ()))
                                .build())
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%size%")
                                .replacement(String.valueOf(s.getSize()))
                                .build());
                player.sendMessage(message);
            }

        } catch (NullPointerException ex) {
            Main.getPlugin().getLogger().warning(ex.getMessage());
        }
    }

}
