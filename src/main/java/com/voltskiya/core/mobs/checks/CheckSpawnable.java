package com.voltskiya.core.mobs.checks;

import com.voltskiya.core.mobs.checks.mechanics.MechanicForestFloor;
import com.voltskiya.core.mobs.checks.mechanics.SpawningMechanic;
import com.voltskiya.core.mobs.scanning.SpawningEnvironment;
import org.bukkit.block.Biome;

import java.util.HashMap;
import java.util.Map;

public class CheckSpawnable {
    public static Map<Biome, SpawningMechanic> biomeToRules = new HashMap<>();

    static {
        CheckSpawnable.biomeToRules.put(Biome.GIANT_TREE_TAIGA, new MechanicForestFloor());
    }

    public static boolean isSpawnable(String mobName, SpawningEnvironment environment) {
        SpawningMechanic mechanic = biomeToRules.get(environment.biome);
        return mechanic != null && mechanic.isSpawnable(mobName, environment);
    }
}
