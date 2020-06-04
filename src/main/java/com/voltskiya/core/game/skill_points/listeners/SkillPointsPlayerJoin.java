package com.voltskiya.core.game.skill_points.listeners;

import com.voltskiya.core.game.skill_points.UpdateSkills;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillPointsPlayerJoin implements Listener {
    public SkillPointsPlayerJoin(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UpdateSkills.updateAll(event.getPlayer());
    }
}
