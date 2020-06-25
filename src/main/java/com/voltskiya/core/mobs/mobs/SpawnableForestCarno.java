package com.voltskiya.core.mobs.mobs;

import org.bukkit.entity.Entity;

public class SpawnableForestCarno implements SpawnableMob {
    @Override
    public void spawn(Entity mob) {

    }

    @Override
    public float getSpawnableToRealPercentage() {
        return 0.1f;
    }

    @Override
    public int getGroupInstance() {
        return 10;
    }

    @Override
    public float getGroupMean() {
        return 10;
    }
}
