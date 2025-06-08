package com.xironite.buildedit.services;

import com.xironite.buildedit.models.enums.ConfigSection;
import com.xironite.buildedit.models.items.Wand;
import com.xironite.buildedit.storage.configs.ItemsConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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
        load();
    }
    // endregion

    // region
    public void reload() {
        configManager.items().reload();
        wands.clear();
        load();
    }

    private void load() {
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
            String keyMaxBuildSeconds = section + ConfigSection.ITEM_WAND_MAX_SECONDS.value;
            String keyUsages = section + ConfigSection.ITEM_WAND_USAGES.value;
            String keyPermission = section + ConfigSection.ITEM_WAND_PERMISSION.value;
            String keySelectionMessage = section + ConfigSection.ITEM_WAND_SELECTION_MESSAGE.value;
            String keyTimingMessage = section + ConfigSection.ITEM_WAND_TIMINGS_MESSAGE.value;
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

                if (items.contains(keyMaxBuildSeconds))
                    wand.setMaxBuildSeconds(items.getInt(keyMaxBuildSeconds));

                if (items.contains(keyUsages)) {
                    wand.addUsages(items.getInt(keyUsages));
                    wand.addMaxDurability(items.getInt(keyUsages));
                }

                if (items.contains(keyPermission))
                    wand.setPermission(items.get(keyPermission));

                if (items.contains(keySelectionMessage))
                    wand.setSelectionMessageEnabled(items.getBoolean(keySelectionMessage));

                if (items.contains(keyTimingMessage))
                    wand.setTimingMessageEnabled(items.getBoolean(keyTimingMessage));

                if (items.contains(keyWorlds))
                    wand.setWorlds(items.getStringList(keyWorlds));


                wands.put(wandName, wand);
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading wand " + wandName + ": " + e.getMessage());
            }
        }
    }

    public void removeUsages(ItemStack wandItem, long usages) {
        if (isValidItem(wandItem)) {
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

    public void addUsages(ItemStack wandItem, long usages) {
        if (isValidItem(wandItem)) {
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

    public void setUsages(ItemStack wandItem, long usages) {
        if (isValidItem(wandItem)) {
            ItemMeta meta = wandItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey usagesKey = new NamespacedKey(plugin, "usages");
            container.set(usagesKey, PersistentDataType.LONG, usages);
            wandItem.setItemMeta(meta);
        } else throw new RuntimeException("Invalid wand item!");
    }

    public void addDamage(ItemStack wandItem, int damage) {
        if (isValidItem(wandItem)) {
            ItemMeta meta = wandItem.getItemMeta();

            if (meta instanceof Damageable damageable) {
                int currentDamage = damageable.getDamage();
                int newDamage = currentDamage + damage;

                // Get max durability to prevent exceeding it
                int maxDurability = damageable.hasMaxDamage() ?
                        damageable.getMaxDamage() :
                        wandItem.getType().getMaxDurability();

                // Clamp to max durability (item breaks at max damage)
                newDamage = Math.min(newDamage, maxDurability);

                damageable.setDamage(newDamage);
                wandItem.setItemMeta(meta);
            }
        } else throw new RuntimeException("Invalid wand item!");
    }

    public void setDamage(ItemStack wandItem, long damage) {
        if (isValidItem(wandItem)) {
            ItemMeta meta = wandItem.getItemMeta();

            if (meta instanceof Damageable damageable) {
                // Get max durability (either custom or material default)
                int maxDurability = damageable.hasMaxDamage() ?
                        damageable.getMaxDamage() :
                        wandItem.getType().getMaxDurability();

                // Calculate damage needed to achieve desired durability
                int targetDamage = Math.max(0, maxDurability - (int) damage);

                // Ensure we don't exceed max durability
                targetDamage = Math.min(targetDamage, maxDurability);

                damageable.setDamage(targetDamage);
                wandItem.setItemMeta(meta);
            }
        } else throw new RuntimeException("Invalid wand item!");
    }

    public String getSizeFormatted(ItemStack wandItem) {
        if (!isValidItem(wandItem)) return "0";
        return String.format("%,d", getSize(wandItem));
    }

    public boolean isInValidWorld(Player player, ItemStack wandItem) {
        String wandName = getName(wandItem);
        if (wandName == null) return false;
        Wand wand = get(wandName);
        if (wand == null) return false;
        return wand.getWorlds().isEmpty() || wand.getWorlds().contains(player.getWorld().getName());
    }

    public boolean isExceedingMaxSize(ItemStack wandItem, long selectionSize) {
        String wandName = getName(wandItem);
        Wand wand = get(wandName);
        if (wand == null) return false;
        return selectionSize > wand.getMaxSelectionSize();
    }

    public boolean isSelectionMessageEnabled(ItemStack wandItem) {
        if (!isValidItem(wandItem)) return false;
        String wandName = getName(wandItem);
        Wand wand = get(wandName);
        if (wand == null) return false;
        return wand.isSelectionMessageEnabled();
    }

    public boolean isTimingMessageEnabled(ItemStack wandItem) {
        if (!isValidItem(wandItem)) return false;
        String wandName = getName(wandItem);
        Wand wand = get(wandName);
        if (wand == null) return false;
        return wand.isTimingMessageEnabled();
    }

    public boolean hasWandUsages(ItemStack wandItem, long selectionSize) {
        if (isValidItem(wandItem)) {
            ItemMeta meta = wandItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey idKey = new NamespacedKey(plugin, "id");
            NamespacedKey usagesKey = new NamespacedKey(plugin, "usages");
            if (container.has(idKey, PersistentDataType.STRING) && container.has(usagesKey, PersistentDataType.LONG)) {
                Wand wand = wands.get(container.get(idKey, PersistentDataType.STRING));
                Long currentUsage = container.get(usagesKey, PersistentDataType.LONG);
                if (currentUsage == null) currentUsage = 0L;
                long usagesLeft = currentUsage - selectionSize;
                return usagesLeft >= 0L && usagesLeft <= wand.getUsages();
            } else return false;
        } else return false;
    }

    public boolean contains(ItemStack wandItem) {
        String wandName = getName(wandItem);
        if (wandName == null) return false;
        return wands.containsKey(wandName);
    }

    private boolean isValidItem(ItemStack item) {
        return item != null && item.hasItemMeta();
    }

    public long getUsages(ItemStack wandItem) {
        if (!isValidItem(wandItem)) return 0;
        ItemMeta meta = wandItem.getItemMeta();
        NamespacedKey usageId = new NamespacedKey(plugin, "usages");
        Long usages = meta.getPersistentDataContainer().get(usageId, PersistentDataType.LONG);
        return usages == null ? 0 : usages;
    }

    public long getSize(ItemStack wandItem) {
        if (!isValidItem(wandItem)) return 0;
        String wandName = getName(wandItem);
        if (wands.containsKey(wandName)) return wands.get(wandName).getMaxSelectionSize();
        else return 0;
    }

    public int getMaxSeconds(ItemStack wandItem) {
        if (!isValidItem(wandItem)) return 0;
        String wandName = getName(wandItem);
        if (wands.containsKey(wandName)) return wands.get(wandName).getMaxBuildSeconds();
        else return 0;
    }

    @Nullable
    public Wand get(String wand) {
        return wands.getOrDefault(wand, null);
    }

    @Nullable
    public Wand get(ItemStack wandItem) {
        if (!isValidItem(wandItem)) return null;
        String wand = getName(wandItem);
        return wands.getOrDefault(wand, null);
    }

    @Nullable
    public String getName(ItemStack wandItem) {
        if (!isValidItem(wandItem)) { return null; }
        ItemMeta meta = wandItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey idKey = new NamespacedKey(plugin, "id");
        if (container.has(idKey, PersistentDataType.STRING)) {
            return container.get(idKey, PersistentDataType.STRING);
        } else return null;
    }
    // endregion
}
