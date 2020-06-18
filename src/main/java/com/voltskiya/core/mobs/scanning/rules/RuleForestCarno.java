package com.voltskiya.core.mobs.scanning.rules;

import com.voltskiya.core.mobs.scanning.SpawningEnvironment;
import com.voltskiya.core.mobs.scanning.groupUtils.UtilsMaterial;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class RuleForestCarno extends SpawningRule {
    public static RuleForestCarno instance = new RuleForestCarno();
    private static Set<Material> nonSpawnableBlocks = new HashSet<>();

    static {
        nonSpawnableBlocks.addAll(UtilsMaterial.getLeaves());
    }

    public static RuleForestCarno get() {
        return instance;
    }

    @Override
    public boolean isSpawnable(SpawningEnvironment environment) {
        return !nonSpawnableBlocks.contains(environment.blockTypeOn);
        // we don't care about elevation
    }
}
