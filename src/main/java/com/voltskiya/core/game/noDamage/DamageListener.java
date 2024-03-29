package com.voltskiya.core.game.noDamage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

class DamageListener implements Listener {
    private JavaPlugin plugin;
    private HashSet<EntityDamageEvent.DamageCause> cooldowns = new HashSet<>();

    public DamageListener(JavaPlugin plugin) {
        cooldowns.add(EntityDamageEvent.DamageCause.CUSTOM);
        cooldowns.add(EntityDamageEvent.DamageCause.MAGIC);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        EntityDamageEvent.DamageCause cause = e.getCause();
        if (cooldowns.contains(cause)) {
            if (entity instanceof LivingEntity) {
                LivingEntity live = (LivingEntity) entity;
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> live.setNoDamageTicks(0), 0);
            }
        }
    }


}
