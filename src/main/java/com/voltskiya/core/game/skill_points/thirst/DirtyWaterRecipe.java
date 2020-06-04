package com.voltskiya.core.game.skill_points.thirst;

import com.voltskiya.core.Voltskiya;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class DirtyWaterRecipe {
    public DirtyWaterRecipe(Voltskiya plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "dirty-water");

        net.minecraft.server.v1_15_R1.ItemStack potion = CraftItemStack.asNMSCopy(new ItemStack(Material.POTION));
        NBTTagCompound tag = potion.getOrCreateTag();
        tag.set("Potion", NBTTagString.a("minecraft:water"));
        potion.setTag(tag);
        ItemStack result = CraftItemStack.asBukkitCopy(potion);

        Recipe recipe = new org.bukkit.inventory.FurnaceRecipe(key, result, Material.POTION, 0, 100);
        Bukkit.addRecipe(recipe);
    }
}
