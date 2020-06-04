package com.voltskiya.core.game.skill_points.thirst;

import com.voltskiya.core.Voltskiya;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public class ThirstPlayerListener implements Listener {
    private static final Map<Material, Double> drinks = new HashMap<>();

    static {
        drinks.put(Material.POTION, 7D);
    }

    public ThirstPlayerListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEquipUpdate(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.CRAFTING) {
            // this is the players inventory that contains the armor slots
            // just recalculate the armor. it would probably be the same computation to check if the player equiped something
            // probably is a player btw
            @NotNull HumanEntity whoClicked = event.getWhoClicked();
            if (whoClicked instanceof Player)
                Bukkit.getScheduler().scheduleSyncDelayedTask(Voltskiya.get(), () -> ThirstBar.calc((Player) whoClicked), 0);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ThirstBar.scheduleDecrement(event.getPlayer()); // this will quit when the player logs off
    }

    @EventHandler
    public void onPlayerDrink(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            net.minecraft.server.v1_15_R1.ItemStack dirtyWater = CraftItemStack.asNMSCopy(event.getItem());
            NBTTagCompound tag = dirtyWater.getOrCreateTag();
            @Nullable String potion = tag.getString("Dirty");
            if (potion != null && potion.equals("true")) {
                ThirstBar.drink(event.getPlayer(), -1);
                return;
            }

        }
        double drinkAmount = drinks.getOrDefault(event.getItem().getType(), 0D);
        if (drinkAmount != 0)
            ThirstBar.drink(event.getPlayer(), drinkAmount);
    }

    @EventHandler
    public void onPlayerReset(PlayerDeathEvent event) {
        ThirstBar.resetPlayerThirst(event.getEntity());
    }
}
