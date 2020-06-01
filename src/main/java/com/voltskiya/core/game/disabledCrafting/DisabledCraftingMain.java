package com.voltskiya.core.game.disabledCrafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class DisabledCraftingMain {

    // keep it a method even though it could be just static for easy disabling
    public static void enable() {
        List<ItemStack> recipeToRemove = Collections.singletonList(new ItemStack(Material.HAY_BLOCK));
        @NotNull List<Recipe> recipes = Bukkit.getRecipesFor(new ItemStack(Material.WHEAT));
        for (Recipe recipe : recipes) {
            if (recipe instanceof ShapelessRecipe) {
                ShapelessRecipe recipeSpecific = ((ShapelessRecipe) recipe);
                if (shapelessIsSameRecipe(recipeSpecific.getIngredientList(), recipeToRemove)) {
                    Bukkit.removeRecipe(recipeSpecific.getKey());
                    // don't break
                }
            }
        }

        recipeToRemove = Collections.singletonList(new ItemStack(Material.DRIED_KELP_BLOCK));
        recipes = Bukkit.getRecipesFor(new ItemStack(Material.DRIED_KELP));
        for (Recipe recipe : recipes) {
            if (recipe instanceof ShapelessRecipe) {
                ShapelessRecipe recipeSpecific = ((ShapelessRecipe) recipe);
                if (shapelessIsSameRecipe(recipeSpecific.getIngredientList(), recipeToRemove)) {
                    Bukkit.removeRecipe(recipeSpecific.getKey());
                    // don't break
                }
            }
        }

    }

    private static boolean shapelessIsSameRecipe(List<ItemStack> ingredientList1, List<ItemStack> ingredientList2) {
        for (ItemStack item1 : ingredientList1) {
            boolean isContained = false;
            for (ItemStack item2 : ingredientList2) {
                if (item1.isSimilar(item2)) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained)
                return false;
        }
        return true;
    }
}
