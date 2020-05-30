package com.voltskiya.core.custom_mobs.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class RotateCustomModel {
    public static void rotate(Collection<Entity> entities, Location center, JavaPlugin plugin) {
        for (Entity entity : entities) {
            Location location = entity.getLocation();
            location.setDirection(location.getDirection().rotateAroundY(Math.toRadians(-1)));

            double xCenter = center.getX();
            double zCenter = center.getZ();
            double xPrime = xCenter - location.getX();
            double zPrime = zCenter - location.getZ();
            double xResult = Math.cos(Math.toRadians(1)) * xPrime - Math.sin(Math.toRadians(1)) * zPrime;
            double zResult = Math.cos(Math.toRadians(1)) * zPrime + Math.sin(Math.toRadians(1)) * xPrime;
            location.setX(xCenter - xResult);
            location.setZ(zCenter - zResult);
            entity.teleport(location);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> RotateCustomModel.rotate(entities, center, plugin), 2);
    }
}
