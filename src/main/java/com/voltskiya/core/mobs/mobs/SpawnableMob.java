package com.voltskiya.core.mobs.mobs;

import org.bukkit.entity.Entity;

public interface SpawnableMob {
    void spawn(Entity mob);
    float getSpawnableToRealPercentage();
    int getGroupInstance();
    float getGroupMean();
}
