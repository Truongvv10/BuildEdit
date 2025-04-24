package com.xironite.buildedit.storage.configs;

import com.sun.source.tree.IfTree;
import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.Wand;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ItemsConfig extends ConfigAbtract {

    // region Fields
    public HashMap<String, ItemStack> wands;
    // endregion

    // region Constructors
    public ItemsConfig(Main paramPlugin, String paramFileName) {
        super(paramPlugin, paramFileName);
        this.wands = new HashMap<>();
        loadWands();
    }
    // endregion

    public void reloadWands() {
        wands.clear();
        loadWands();
    }

    public boolean containsWand(String id) {
        return wands.containsKey(id);
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

                wands.put(wandName, wand.build());
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading wand " + wandName + ": " + e.getMessage());
            }
        }
    }

    public @Nullable ItemStack getWand(String wandName, int amount) {
        return wands.getOrDefault(wandName, null);
    }
    // endregion

}
