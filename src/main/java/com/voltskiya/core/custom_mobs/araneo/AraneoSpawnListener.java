package com.voltskiya.core.custom_mobs.araneo;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AraneoSpawnListener implements Listener {
    public static final String ARANEO_TAG = "ai.araneo";

    public AraneoSpawnListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAraneoSpawn(CreatureSpawnEvent event) {
        @NotNull LivingEntity entity = event.getEntity();
        @NotNull Set<String> tags = entity.getScoreboardTags();
        if (tags.contains(ARANEO_TAG) && entity instanceof Mob) {
            new AraneoAI((Mob) entity);
        }
    }
}
