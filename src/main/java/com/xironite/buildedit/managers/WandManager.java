package com.xironite.buildedit.managers;

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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class WandManager {

    // region Fields
    @Getter @Setter
    private Main plugin;
    @Getter @Setter
    private ItemsConfig configuration;
    @Getter @Setter
    private HashMap<String, Wand> wands;
    // endregion

    // region Constructors
    public WandManager(ItemsConfig configuration) {
        this.setPlugin(Main.getPlugin());
        this.setConfiguration(configuration);
        this.setWands(new HashMap<>());
        loadWands();
    }
    // endregion

    // region Methods
    public void reload() {
        this.getConfiguration().reload();
        wands.clear();
        loadWands();
    }

    public boolean containsWand(String id) {
        return wands.containsKey(id);
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

    public boolean hasWandOverMaxSize(ItemStack item, long selectionSize) {
        String wandName = getWandName(item);
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
        for (String wandName : this.configuration.getKeys(ConfigSection.ITEM_WANDS)) {

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

                if (this.configuration.contains(keyMaterial))
                    wand.addMaterial(this.configuration.get(keyMaterial));

                if (this.configuration.contains(keyDisplayName))
                    wand.addDisplayName(this.configuration.get(keyDisplayName));

                if (this.configuration.contains(keyLore))
                    wand.addLore(this.configuration.getStringList(keyLore));

                if (this.configuration.contains(keyModel))
                    wand.addModelId(this.configuration.getInt(keyModel));

                if (this.configuration.contains(keyEnchants))
                    wand.addEnchantment(this.configuration.getStringList(keyEnchants));

                if (this.configuration.contains(keyFlags))
                    wand.addFlag(this.configuration.getStringList(keyFlags));

                if (this.configuration.contains(keyMaxSize))
                    wand.setMaxSelectionSize(this.configuration.getInt(keyMaxSize));

                if (this.configuration.contains(keyUsages))
                    wand.addUsages(this.configuration.getInt(keyUsages));

                if (this.configuration.contains(keyPermission))
                    wand.setPermission(this.configuration.get(keyPermission));

                if (this.configuration.contains(keyWorlds))
                    wand.setWorlds(this.configuration.getStringList(keyWorlds));

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
    public String getWandName(ItemStack item) {
        if (!validItem(item)) { return null; }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey idKey = new NamespacedKey(plugin, "id");
        if (container.has(idKey, PersistentDataType.STRING)) {
            return container.get(idKey, PersistentDataType.STRING);
        } else return null;
    }
    // endregion
}
