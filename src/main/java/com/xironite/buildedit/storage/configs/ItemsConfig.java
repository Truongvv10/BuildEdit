package com.xironite.buildedit.storage.configs;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.items.Wand;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ItemsConfig extends ConfigAbtract {
    // region Constructors
    public ItemsConfig(Main paramPlugin, String paramFileName) {
        super(paramPlugin, paramFileName);
    }
    // endregion

}
