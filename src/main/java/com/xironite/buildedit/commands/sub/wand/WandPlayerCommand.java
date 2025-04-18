package com.xironite.buildedit.commands.sub.wand;

import com.xironite.buildedit.commands.CommandAbstract;
import com.xironite.buildedit.enums.ConfigSection;
import com.xironite.buildedit.services.PlayerSessionManager;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import com.xironite.buildedit.storage.configs.MessageConfig;
import com.xironite.buildedit.utils.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class WandPlayerCommand extends CommandAbstract {

    private final ItemsConfig itemConfig;

    public WandPlayerCommand(JavaPlugin paramPlugin, PlayerSessionManager paramSession, MessageConfig paramMessageConf, String paramName, String paramPermission, ItemsConfig config) {
        super(paramPlugin, paramSession, paramMessageConf, paramName, paramPermission);
        this.itemConfig = config;
        WandAmountCommand wandAmountCommand = new WandAmountCommand(
                paramPlugin,
                paramSession,
                paramMessageConf,
                "amount",
                "buildedit.wand",
                config);
        addSubCommand(wandAmountCommand);
    }

    @Override
    public boolean onExecute(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player executor) {
            // Check permission
            if (!hasPermission(sender)) return true;

            // Check if player is online
            Player target = Bukkit.getPlayer(args[0]);
            if (!isPlayerOnline(target)) {
                executor.sendMessage(messageConfig.getComponent(ConfigSection.ACTION_OFFLINE));
                return true;
            }

            // Item attributes
            String wand = parentCommand.getName();
            Material item = Material.getMaterial(itemConfig.get(ConfigSection.ITEM_WAND_MATERIAL.value.replace("$1", wand)));
            String display = itemConfig.get(ConfigSection.ITEM_WAND_NAME.value.replace("$1", wand));
            List<String> lore = itemConfig.getStringList(ConfigSection.ITEM_WAND_LORE.value.replace("$1", wand));
            int dur = itemConfig.getInt(ConfigSection.ITEM_WAND_USAGES.value.replace("$1", wand));

            // Give item
            if (item != null) {
                ItemStack retrieve = new ItemStack(item);
                ItemMeta meta = retrieve.getItemMeta();

                meta.displayName(StringUtil.translateColor(display));
                meta.lore(StringUtil.translateColor(lore));

                NamespacedKey key = new NamespacedKey(plugin, "type");
                PersistentDataContainer data = meta.getPersistentDataContainer();

                data.set(key, PersistentDataType.STRING, wand);
                retrieve.setItemMeta(meta);

                // Message for target
                Component c2 = messageConfig.getComponent(ConfigSection.WAND);
                c2 = StringUtil.replace(c2, "%amount%", "1");
                c2 = StringUtil.replace(c2, "%wand%", display);

                // Message for executor
                Component c1 = messageConfig.getComponent(ConfigSection.WAND2);
                c1 = StringUtil.replace(c1, "%amount%", "1");
                c1 = StringUtil.replace(c1, "%wand%", display);
                c1 = StringUtil.replace(c1, "%player%", target.getName());

                // If same player
                if (!executor.getName().equals(target.getName())) executor.sendMessage(c1);
                target.sendMessage(c2);
                target.give(retrieve);
            }
        }
        return true;
    }

    @Override
    protected List<String> onTabbing(CommandSender sender, Command command, String label, String[] args) {
        return List.of("test");
    }
}
