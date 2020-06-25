package com.voltskiya.core.mobs.checks.rules;

import com.voltskiya.core.mobs.scanning.SpawningEnvironment;
import com.voltskiya.core.mobs.checks.groupUtils.UtilsMaterial;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class RuleForestCarno extends SpawningRule {
    public static RuleForestCarno instance = new RuleForestCarno();
    private static final List<Material> nonSpawnableBlocks = new ArrayList<>();

    static {
        nonSpawnableBlocks.addAll(UtilsMaterial.getLeaves());
        nonSpawnableBlocks.addAll(UtilsMaterial.getWood());
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
