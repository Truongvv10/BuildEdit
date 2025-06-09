package com.xironite.buildedit.models.items;

import com.xironite.buildedit.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Wand extends BaseItem {

    @Getter
    private String id;
    @Getter
    private long usages;
    @Getter @Setter
    private long maxSelectionSize;
    @Getter @Setter
    private int maxBuildSeconds;
    @Getter @Setter
    private boolean isSelectionMessageEnabled;
    @Getter @Setter
    private boolean isTimingMessageEnabled;
    @Getter @Setter
    private String permission;
    @Getter @Setter
    private Material[][] recipe;
    @Getter @Setter
    private List<String> worlds;

    public Wand(String paramId) {
        super();
        this.addId(paramId);
        this.addUsages(-1);
        this.setMaxSelectionSize(-1);
        this.setMaxBuildSeconds(-1);
        this.setSelectionMessageEnabled(true);
        this.setTimingMessageEnabled(true);
        this.setPermission("");
        this.setRecipe(null);
        this.setWorlds(new ArrayList<>());
    }

    public Wand addId(String id) {
        if (id != null) this.id = id;
        return this;
    }

    public Wand addUsages(long usages) {
        if (usages > 0) this.usages = usages;
        return this;
    }

    @Override
    public ItemStack build() {

        // Create a new ItemStack with the specified material
        ItemStack wand = super.build();
        ItemMeta meta = wand.getItemMeta();

        if (meta != null) {

            // Data container for storing custom data
            PersistentDataContainer container = meta.getPersistentDataContainer();

            // Store wand properties as metadata
            NamespacedKey idKey = new NamespacedKey(Main.getPlugin(), "id");
            NamespacedKey usageKey = new NamespacedKey(Main.getPlugin(), "usages");

            container.set(idKey, PersistentDataType.STRING, this.getId());
            container.set(usageKey, PersistentDataType.LONG, this.getUsages());

            // Apply updated metadata
            wand.setItemMeta(meta);
        }

        return wand;

    }
}