package com.xironite.buildedit.services;

import com.xironite.buildedit.models.items.Wand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class RecipeManager {

    private final JavaPlugin plugin;
    private final WandManager wandManager;

    public RecipeManager(JavaPlugin paramPlugin, WandManager paramWandManager) {
        this.plugin = paramPlugin;
        this.wandManager = paramWandManager;
        registerRecipes();
    }

    public void reload() {
        for (Wand w : wandManager.getWands().values()) {
            NamespacedKey key = new NamespacedKey(plugin, w.getId());
            if (w.getRecipe() == null) continue;
            Bukkit.removeRecipe(key);
        }
        registerRecipes();
    }

    public void registerRecipes() {
        for (Wand w : wandManager.getWands().values()) {
            if (w.getRecipe() == null) continue;
            ItemStack wand = w.build();
            NamespacedKey key = new NamespacedKey(plugin, w.getId());
            if (isRecipeRegistered(key)) continue;
            ShapedRecipe recipe = new ShapedRecipe(key, wand);
            recipe.shape("012", "345", "678");
            for (int i = 0; i < 9; i++) {
                char c = (char) ('0' + i);
                Material material = w.getRecipe()[i / 3][i % 3];
                if (material != Material.AIR) {
                    recipe.setIngredient(c, material);
                }
            }
            Bukkit.addRecipe(recipe);
        }
    }

    public boolean isRecipeRegistered(NamespacedKey recipeKey) {
        return Bukkit.getRecipe(recipeKey) != null;
    }

    public void unlockRecipe(Player player) {
        for (Wand w : wandManager.getWands().values()) {
            if (w.getRecipe() == null) continue;
            NamespacedKey key = new NamespacedKey(plugin, w.getId());
            if (!player.hasDiscoveredRecipe(key)) player.discoverRecipe(key);
        }
    }

}
