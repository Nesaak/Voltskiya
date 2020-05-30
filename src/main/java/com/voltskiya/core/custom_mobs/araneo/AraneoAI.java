package com.voltskiya.core.custom_mobs.araneo;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class AraneoAI {
    private static final long SHORT_CHECK_SIGHT_COOLDOWN = 10;
    private static final long NORMAL_CHECK_SIGHT_COOLDOWN = 20;
    private static final long LONG_CHECK_SIGHT_COOLDOWN = 25 * 20;
    private static final double WEB_SPEED = 2;
    private static JavaPlugin plugin;
    private Mob araneo;

    public static void initialize(JavaPlugin pl) {
        plugin = pl;
    }

    public AraneoAI(Mob entity) {
        this.araneo = entity;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doCheckSight, NORMAL_CHECK_SIGHT_COOLDOWN);
    }

    private void doCheckSight() {
        if (!araneo.isValid()) return;
        @Nullable LivingEntity target = araneo.getTarget();
        if (target != null) {
            // we see someone!
            if (araneo.hasLineOfSight(target)) {
                if (araneo.getNearbyEntities(8, 8, 8).contains(target)) {
                    // we should fire!
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doCheckSight, LONG_CHECK_SIGHT_COOLDOWN);
                    fireProjectile(target);
                    return;
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doCheckSight, SHORT_CHECK_SIGHT_COOLDOWN);
                return;
            } else {
                // wait until we see them, but keep searching
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doCheckSight, SHORT_CHECK_SIGHT_COOLDOWN);
                return;
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doCheckSight, NORMAL_CHECK_SIGHT_COOLDOWN);
    }

    private void fireProjectile(LivingEntity target) {
        Location targetLoc = target.getEyeLocation().subtract(0, 0.85, 0);
        Location araneoLoc = araneo.getEyeLocation();
        double x = targetLoc.getX() - araneoLoc.getX();
        double y = targetLoc.getY() - araneoLoc.getY();
        double z = targetLoc.getZ() - araneoLoc.getZ();
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude > .1) {
            x /= magnitude;
            y /= magnitude;
            z /= magnitude;
        }
        x *= WEB_SPEED;
        y *= WEB_SPEED;
        z *= WEB_SPEED;
        World world = araneoLoc.getWorld();
        if (world == null)
            // world really shouldn't be null
            return;
        world.playSound(araneoLoc, Sound.ENTITY_SPIDER_AMBIENT, SoundCategory.HOSTILE, 20, 1);
        new WebProjectile(araneoLoc.subtract(0, 1.5, 0), new Vector(x, y, z));
    }

}
