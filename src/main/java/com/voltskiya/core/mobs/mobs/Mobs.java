package com.voltskiya.core.mobs.mobs;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.voltskiya.core.utils.Pair;
import org.bukkit.entity.EntityType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Mobs {
    private static final Map<String, Pair<EntityType, SpawnableMob>> mobs = new HashMap<>();
    public static final float MOB_PERCENTAGE = 0.02f; // this is the percentage of spawnable places to chosen spawnable places
    private static File mobLocationsFolder;
    private static final Gson gson = new Gson();

    static {
        mobs.put("forestCarno", new Pair<>(EntityType.ZOMBIE, new SpawnableForestCarno()));
    }

    public static void initialize(File mobLocations) {
        mobLocationsFolder = mobLocations;
    }

    public static Pair<EntityType, SpawnableMob> getMobStructure(String name) {
        return mobs.get(name);
    }

    public static int getMobProperCount(String mobName) throws FileNotFoundException {
        System.out.print("properCount ");
        File file = new File(mobLocationsFolder, mobName + ".json");
        if (file.exists()) {
            JsonArray locations = gson.fromJson(new BufferedReader(new FileReader(file)), JsonArray.class);
            System.out.print(locations.size() + " --> ");
            System.out.println((int) (mobs.get(mobName).getValue().getSpawnableToRealPercentage() * locations.size()));
            return (int) (mobs.get(mobName).getValue().getSpawnableToRealPercentage() * locations.size());
        }
        System.out.println("oof");
        return 0;
    }

    public static Set<String> getMobSet() {
        return mobs.keySet();
    }

    public static float getMobMean(String mobName) {
        return mobs.get(mobName).getValue().getGroupMean();
    }

    public static int getMobCountInstance(String mobName) {
        return mobs.get(mobName).getValue().getGroupInstance();
    }
}
