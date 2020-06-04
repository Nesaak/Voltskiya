package com.voltskiya.core.game.skill_points.stamina;

import com.voltskiya.core.Voltskiya;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StaminaPlayerListener implements Listener {
    private static final long LOGIN_DELAY = 30L;

    public StaminaPlayerListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void givePlayerStamina(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Voltskiya.get(), () -> new PlayerAirObject().doObjectAirTick(player), LOGIN_DELAY);
    }
}
