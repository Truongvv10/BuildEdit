package com.xironite.buildedit.storage.configs;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.Wand;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ItemsConfig extends ConfigAbtract {

    // region Fields
    public HashMap<String, Wand> wands;
    // endregion

    // region Constructors
    public ItemsConfig(Main paramPlugin, String paramFileName) {
        super(paramPlugin, paramFileName);
        this.wands = new HashMap<>();
        loadWands();
    }
    // endregion

    // region Methods
    public void reloadWands() {
        wands.clear();
        loadWands();
    }

    public boolean containsWand(String id) {
        return wands.containsKey(id);
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

    public boolean decrementWandUsages(ItemStack wandItem, long usages) {
        if (wandItem != null && wandItem.hasItemMeta()) {
            ItemMeta meta = wandItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey idKey = new NamespacedKey(plugin, "id");
            NamespacedKey usagesKey = new NamespacedKey(plugin, "usages");
            if (container.has(idKey, PersistentDataType.STRING) && container.has(usagesKey, PersistentDataType.LONG)) {
                Wand wand = wands.get(container.get(idKey, PersistentDataType.STRING));
                Long currentUsage = container.get(usagesKey, PersistentDataType.LONG);
                container.set(usagesKey, PersistentDataType.LONG, currentUsage - usages);
                wandItem.setItemMeta(meta);
                return true;
            } else return false;
        } else return false;
    }

    private void loadWands() {
        for (String wandName : getKeys(ConfigSection.ITEM_WANDS)) {

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

                if (contains(keyMaterial))
                    wand.addMaterial(get(keyMaterial));

                if (contains(keyDisplayName))
                    wand.addDisplayName(get(keyDisplayName));

                if (contains(keyLore))
                    wand.addLore(getStringList(keyLore));

                if (contains(keyModel))
                    wand.addModelId(getInt(keyModel));

                if (contains(keyEnchants))
                    wand.addEnchantment(getStringList(keyEnchants));

                if (contains(keyFlags))
                    wand.addFlag(getStringList(keyFlags));

                if (contains(keyMaxSize))
                    wand.setMaxSize(getInt(keyMaxSize));

                if (contains(keyUsages))
                    wand.addUsages(getInt(keyUsages));

                if (contains(keyPermission))
                    wand.setPermission(get(keyPermission));

                if (contains(keyWorlds))
                    wand.setWorlds(getStringList(keyWorlds));

                wands.put(wandName, wand);
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading wand " + wandName + ": " + e.getMessage());
            }
        }
    }
    // endregion

}
