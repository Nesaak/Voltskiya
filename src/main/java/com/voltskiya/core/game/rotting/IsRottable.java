package com.voltskiya.core.game.rotting;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IsRottable {
    private static Set<Material> rottables;
    private static Set<Material> nonRottables;

    static {
        HashSet<Material> rottablesHash = new HashSet<>();
        HashSet<Material> nonRottablesHash = new HashSet<>();
        rottablesHash.add(Material.KELP);
        rottablesHash.add(Material.BROWN_MUSHROOM);
        rottablesHash.add(Material.RED_MUSHROOM);
        rottablesHash.add(Material.BROWN_MUSHROOM_BLOCK);
        rottablesHash.add(Material.RED_MUSHROOM_BLOCK);
        rottablesHash.add(Material.WHEAT);
        rottablesHash.add(Material.CAKE);
        rottablesHash.add(Material.COCOA_BEANS);
        rottablesHash.add(Material.SUGAR_CANE);

        nonRottablesHash.add(Material.SPIDER_EYE);
        nonRottablesHash.add(Material.ROTTEN_FLESH);
        nonRottablesHash.add(Material.GOLDEN_APPLE);

        rottables = Collections.unmodifiableSet(rottablesHash);
        nonRottables = Collections.unmodifiableSet(nonRottablesHash);

    }

    public static boolean isRottable(Material type) {
        return !nonRottables.contains(type) && (type.isEdible() || rottables.contains(type));
    }
}
