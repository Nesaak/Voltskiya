package com.voltskiya.core.game.skill_points.thirst;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagInt;
import net.minecraft.server.v1_15_R1.NBTTagString;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class WaterFillListener implements Listener {
    public WaterFillListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWaterFill(PlayerInteractEvent event) {
        final ItemStack itemInHand = event.getItem();
        if (itemInHand == null) {
            return;
        }
        Material materialInHand = itemInHand.getType();
        if (materialInHand == Material.GLASS_BOTTLE) {
            @Nullable Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null)
                return;

            Location playerLoc = event.getPlayer().getEyeLocation();
            World playerWorld = playerLoc.getWorld();
            if (playerWorld == null) return;
            @Nullable RayTraceResult rayTrace = playerWorld.rayTraceBlocks(playerLoc, playerLoc.getDirection(), 5.5, FluidCollisionMode.SOURCE_ONLY);
            if (rayTrace == null) return;
            Block hitBlock = rayTrace.getHitBlock();
            if (hitBlock == null) return;
            if (hitBlock.getType() == Material.WATER) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                net.minecraft.server.v1_15_R1.ItemStack dirtyWater = CraftItemStack.asNMSCopy(new ItemStack(Material.POTION));
                NBTTagCompound tag = dirtyWater.getOrCreateTag();
                tag.set("CustomPotionColor", NBTTagInt.a(7558441));
                tag.set("Dirty", NBTTagString.a("true"));
                dirtyWater.setTag(tag);
                ItemStack dirtyWaterItemStack = CraftItemStack.asBukkitCopy(dirtyWater.cloneItemStack());
                ItemMeta im = dirtyWaterItemStack.getItemMeta();
                if (im != null) {
                    im.setDisplayName("Dirty Water");
                    dirtyWaterItemStack.setItemMeta(im);
                }
                @NotNull HashMap<Integer, ItemStack> success = event.getPlayer().getInventory().addItem(dirtyWaterItemStack);
                if (success.isEmpty())
                    event.setCancelled(true);
                else {
                    Entity entity = playerWorld.spawnEntity(playerLoc, EntityType.DROPPED_ITEM);
                    if (entity instanceof Item) {
                        ((Item) entity).setItemStack(dirtyWaterItemStack);
                        entity.setVelocity(playerLoc.getDirection().divide(new Vector(3, 3, 3)));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
