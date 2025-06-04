package com.xironite.buildedit.services;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.items.Wand;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class WandManager {

    // region Fields
    @Getter @Setter
    private HashMap<String, Wand> wands;
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final ItemsConfig items;
    // endregion

    // region Constructors
    public WandManager(JavaPlugin paramPlugin, ConfigManager paranConfigManager) {
        this.setWands(new HashMap<>());
        this.plugin = paramPlugin;
        this.configManager = paranConfigManager;
        this.items = configManager.items();
        loadWands();
    }
    // endregion

    // region Methods
    public void reload() {
        configManager.items().reload();
        wands.clear();
        loadWands();
    }

    public long getWandSize(String wandName) {
        if (wands.containsKey(wandName)) return wands.get(wandName).getMaxSelectionSize();
        else return -1;
    }

    public String getWandSizeFormatted(String wandName) {
        return String.format("%,d", getWandSize(wandName));
    }

    public boolean isWandValidWorld(Player player, ItemStack item) {
        String wandName = getWandName(item);
        if (wandName == null) return false;
        Wand wand = getWand(wandName);
        if (wand == null) return false;
        return wand.getWorlds().isEmpty() || wand.getWorlds().contains(player.getWorld().getName());
    }

    @Nullable
    public Wand getWand(String wandName) {
        return wands.getOrDefault(wandName, null);
    }

    @Nullable
    public ItemStack getWandItem(String wandName) {
        return wands.getOrDefault(wandName, null).build();
    }

    @Nullable
    public ItemStack getWandItem(String wandName, int amount) {
        return wands.getOrDefault(wandName, null).build();
    }

    public boolean hasWandOverMaxSize(ItemStack wandItem, long selectionSize) {
        String wandName = getWandName(wandItem);
        if (wandName == null) return false;
        else return hasWandOverMaxSize(wandName, selectionSize);
    }

    public boolean hasWandOverMaxSize(String wandName, long selectionSize) {
        Wand wand = getWand(wandName);
        if (wand == null) return false;
        return selectionSize > wand.getMaxSelectionSize();
    }

    public boolean hasWandUsages(ItemStack wandItem, long usages) {
        if (validItem(wandItem)) {
            ItemMeta meta = wandItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey idKey = new NamespacedKey(plugin, "id");
            NamespacedKey usagesKey = new NamespacedKey(plugin, "usages");
            if (container.has(idKey, PersistentDataType.STRING) && container.has(usagesKey, PersistentDataType.LONG)) {
                Wand wand = wands.get(container.get(idKey, PersistentDataType.STRING));
                Long currentUsage = container.get(usagesKey, PersistentDataType.LONG);
                if (currentUsage == null) currentUsage = 0L;
                long usagesLeft = currentUsage - usages;
                return usagesLeft >= 0L && usagesLeft <= wand.getUsages();
            } else return false;
        } else return false;
    }

    public boolean containsWand(String id) {
        return wands.containsKey(id);
    }

    public boolean containsWand(ItemStack wandItem) {
        String wandName = getWandName(wandItem);
        if (wandName == null) return false;
        return containsWand(wandName);
    }

    public void decrementWandUsages(ItemStack wandItem, long usages) {
        if (validItem(wandItem)) {
            ItemMeta meta = wandItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey usagesKey = new NamespacedKey(plugin, "usages");
            if (container.has(usagesKey, PersistentDataType.LONG)) {
                Long currentUsage = container.get(usagesKey, PersistentDataType.LONG);
                if (currentUsage == null) currentUsage = 0L;
                if (hasWandUsages(wandItem, currentUsage))
                    container.set(usagesKey, PersistentDataType.LONG, currentUsage - usages);
                else container.set(usagesKey, PersistentDataType.LONG, 0L);
                wandItem.setItemMeta(meta);
            }
        } else throw new RuntimeException("Invalid wand item!");
    }

    public void incrementWandUsages(ItemStack wandItem, long usages) {
        if (validItem(wandItem)) {
            ItemMeta meta = wandItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey idKey = new NamespacedKey(plugin, "id");
            NamespacedKey usagesKey = new NamespacedKey(plugin, "usages");
            if (container.has(idKey, PersistentDataType.STRING) && container.has(usagesKey, PersistentDataType.LONG)) {
                Wand wand = wands.get(container.get(idKey, PersistentDataType.STRING));
                Long currentUsage = container.get(usagesKey, PersistentDataType.LONG);
                if (currentUsage == null) currentUsage = 0L;
                if (hasWandUsages(wandItem, currentUsage))
                    container.set(usagesKey, PersistentDataType.LONG, currentUsage + usages);
                else container.set(usagesKey, PersistentDataType.LONG, wand.getUsages());
                wandItem.setItemMeta(meta);
            }
        } else throw new RuntimeException("Invalid wand item!");
    }

    public void modifyWandUsages(ItemStack wandItem, long usages) {
        if (validItem(wandItem)) {
            ItemMeta meta = wandItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey usagesKey = new NamespacedKey(plugin, "usages");
            container.set(usagesKey, PersistentDataType.LONG, usages);
            wandItem.setItemMeta(meta);
        } else throw new RuntimeException("Invalid wand item!");
    }

    private void loadWands() {
        for (String wandName : items.getKeys(ConfigSection.ITEM_WANDS)) {

            Wand wand = new Wand(wandName);
            String section = "wands." + wandName + ".";

            String keyMaterial = section + ConfigSection.ITEM_MATERIAL.value;
            String keyDisplayName = section + ConfigSection.ITEM_DISPLAY.value;
            String keyLore = section + ConfigSection.ITEM_LORE.value;
            String keyModel = section + ConfigSection.ITEM_MODEL.value;
            String keyEnchants = section + ConfigSection.ITEM_ENCHANTS.value;
            String keyFlags = section + ConfigSection.ITEM_FLAGS.value;

            String keyMaxSize = section + ConfigSection.ITEM_WAND_MAX_SIZE.value;
            String keyUsages = section + ConfigSection.ITEM_WAND_USAGES.value;
            String keyPermission = section + ConfigSection.ITEM_WAND_PERMISSION.value;
            String keyWorlds = section + ConfigSection.ITEM_WAND_WORLDS.value;

            try {

                if (items.contains(keyMaterial))
                    wand.addMaterial(items.get(keyMaterial));

                if (items.contains(keyDisplayName))
                    wand.addDisplayName(items.get(keyDisplayName));

                if (items.contains(keyLore))
                    wand.addLore(items.getStringList(keyLore));

                if (items.contains(keyModel))
                    wand.addModelId(items.getInt(keyModel));

                if (items.contains(keyEnchants))
                    wand.addEnchantment(items.getStringList(keyEnchants));

                if (items.contains(keyFlags))
                    wand.addFlag(items.getStringList(keyFlags));

                if (items.contains(keyMaxSize))
                    wand.setMaxSelectionSize(items.getInt(keyMaxSize));

                if (items.contains(keyUsages))
                    wand.addUsages(items.getInt(keyUsages));

                if (items.contains(keyPermission))
                    wand.setPermission(items.get(keyPermission));

                if (items.contains(keyWorlds))
                    wand.setWorlds(items.getStringList(keyWorlds));

                wands.put(wandName, wand);
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading wand " + wandName + ": " + e.getMessage());
            }
        }
    }

    private boolean validItem(ItemStack item) {
        return item != null && item.hasItemMeta();
    }

    @Nullable
    public String getWandName(ItemStack wandItem) {
        if (!validItem(wandItem)) { return null; }
        ItemMeta meta = wandItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey idKey = new NamespacedKey(plugin, "id");
        if (container.has(idKey, PersistentDataType.STRING)) {
            return container.get(idKey, PersistentDataType.STRING);
        } else return null;
    }
    // endregion
}
