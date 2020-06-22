package com.voltskiya.core.mobs.mobs;

import com.voltskiya.core.utils.Pair;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class Mobs {
    private static final Map<String, Pair<EntityType, SpawnableMob>> mobs = new HashMap<>();

    static {
        mobs.put("forestCarno", new Pair<>(EntityType.ZOMBIE, new SpawnableForestCarno()));
    }

    public static Pair<EntityType, SpawnableMob> getMobStructure(String name) {
        return mobs.get(name);
    }
}
