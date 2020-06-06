package com.voltskiya.core.game.skill_points.no_sprint;

import com.voltskiya.core.game.skill_points.thirst.ThirstBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerListener implements Listener {
    public HungerListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFoodChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player)
            if (NoSprint.updateHunger((Player) event.getEntity(), event.getFoodLevel())) {
                event.setCancelled(true);
            }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        if (event.getItem().getType().isEdible() && !NoSprint.shouldEat(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
        ThirstBar.calc(event.getPlayer());
    }
}
