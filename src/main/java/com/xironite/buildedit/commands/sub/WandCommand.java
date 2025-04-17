package com.xironite.buildedit.commands.sub;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
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

    private final ItemsConfig itemConfig;

    public WandCommand(JavaPlugin paramPlugin, PlayerSessionManager paramSession, MessageConfig paramMessageConf, String paramName, String paramPermission, String paramSyntax, String paramDescription, ItemsConfig config) {
        super(paramPlugin, paramSession, paramMessageConf, paramName, paramPermission, paramSyntax, paramDescription);
        this.itemConfig = config;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            Material material = Material.getMaterial(itemConfig.get(ConfigSection.ITEM_MATERIAL));
            if (material == null) {
                ItemStack wand = new ItemStack(material);
                ItemMeta meta = wand.getItemMeta();
                meta.displayName(Component.text(itemConfig.get(ConfigSection.ITEM_NAME)));
                NamespacedKey key = new NamespacedKey(plugin, "type");
                PersistentDataContainer data = meta.getPersistentDataContainer();
                data.set(key, PersistentDataType.STRING, "buildedit:wand");
                wand.setItemMeta(meta);
                player.give(wand);
            }
        }
        return true;
    }
}
