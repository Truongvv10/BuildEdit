package com.xironite.buildedit.models;

import lombok.Getter;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseItem {

    @Getter
    private Material material;
    @Getter
    private String displayName;
    @Getter
    private List<String> lore;
    @Getter
    private int modelId;
    @Getter
    private List<Enchantment> enchantments;
    @Getter
    private List<ItemFlag> flags;
    @Getter
    private int slot;
    private final MiniMessage miniMessage;

    public BaseItem() {
        this.material = Material.BARRIER;
        this.displayName = "Error";
        this.lore = new ArrayList<>();
        this.modelId = -1;
        this.enchantments = new ArrayList<>();
        this.flags = new ArrayList<>();
        this.slot = -1;
        this.miniMessage = MiniMessage.miniMessage();
    }

    public BaseItem addMaterial(String material) {
        if (material != null && Material.getMaterial(material) != null) this.material = Material.getMaterial(material);
        else this.material = Material.BARRIER;
        return this;
    }

    public BaseItem addDisplayName(String displayName) {
        if (displayName != null) this.displayName = displayName;
        return this;
    }

    public BaseItem addLore(String lore) {
        if (lore != null) this.lore.add(lore);
        return this;
    }

    public BaseItem addLore(List<String> lore) {
        if (lore != null) this.lore.addAll(lore);
        return this;
    }

    public BaseItem addModelId(int modelId) {
        if (modelId > 0) this.modelId = modelId;
        return this;
    }

    public BaseItem addEnchantment(String enchantment) {
        if (enchantment != null && Enchantment.getByName(enchantment) != null)
            this.enchantments.add(Enchantment.getByName(enchantment));
        return this;
    }

    public BaseItem addEnchantment(List<String> enchantment) {
        for (String s : enchantment) this.addEnchantment(s);
        return this;
    }

    public BaseItem addEnchantment(Enchantment enchantment) {
        if (enchantment != null) this.enchantments.add(enchantment);
        return this;
    }

    public BaseItem addFlag(String flag) {
        if (flag != null) {
            ItemFlag.valueOf(flag);
            this.flags.add(ItemFlag.valueOf(flag));
        }
        return this;
    }

    public BaseItem addFlag(List<String> flag) {
        for (String s : flag) this.addFlag(s);
        return this;
    }

    public BaseItem addFlag(ItemFlag flag) {
        if (flag != null) this.flags.add(flag);
        return this;
    }

    public BaseItem addSlot(int slot) {
        if (slot > -1) this.slot = slot;
        return this;
    }

    public ItemStack build() {
        ItemStack result = new ItemStack(this.material);
        ItemMeta meta = result.getItemMeta();

        if (meta != null) {
            // Set display name
            meta.displayName(miniMessage.deserialize(this.getDisplayName()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

            // Set lore
            if (!this.lore.isEmpty()) {
                meta.lore(this.getLore().stream()
                        .map(x -> miniMessage.deserialize(x).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .toList()
                );
            }

            // Set custom model data
            if (this.modelId > -1) {
                meta.setCustomModelData(this.modelId);
            }

            // Add item flags
            for (ItemFlag flag : this.flags) {
                meta.addItemFlags(flag);
            }

            // Apply the meta to the item
            result.setItemMeta(meta);

            // Add enchantments (after setting meta to avoid overrides)
            for (Enchantment enchantment : this.enchantments) {
                result.addUnsafeEnchantment(enchantment, 1); // Default level 1
            }
        }

        return result;
    }

}
