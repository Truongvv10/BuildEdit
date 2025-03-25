package com.xironite.buildedit.commands.sub;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.services.PlayerSessionManager;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class WandCommand  extends CommandAbstract {

    public WandCommand(JavaPlugin paramPlugin, PlayerSessionManager paramPlayerSessionManager, String paramName, String paramPermission, String paramDescription, String paramSyntax) {
        super(paramPlugin, paramPlayerSessionManager, paramName, paramPermission, paramDescription, paramSyntax);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            ItemStack wand = new ItemStack(Material.NETHERITE_AXE, 1);
            ItemMeta meta = wand.getItemMeta();
            meta.displayName(Component.text("Builder Wand"));
            NamespacedKey key = new NamespacedKey(plugin, "wand_type");
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(key, PersistentDataType.STRING, "buildedit:wand");
            wand.setItemMeta(meta);
            player.give(wand);
        }
        return true;
    }
}
