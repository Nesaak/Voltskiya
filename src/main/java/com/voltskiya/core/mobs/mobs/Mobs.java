package com.voltskiya.core.mobs.mobs;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.voltskiya.core.utils.Pair;
import org.bukkit.entity.EntityType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mobs {
    private static final Map<String, Pair<EntityType, SpawnableMob>> mobs = new HashMap<>();
    private static final Map<String, Float> mobsPercentage = new HashMap<>();
    public static final List<String> mobSet = new ArrayList<>();
    private static File mobLocationsFolder;
    private static final Gson gson = new Gson();

    static {
        mobs.put("forestCarno", new Pair<>(EntityType.ZOMBIE, new SpawnableForestCarno()));
        mobsPercentage.put("forestCarno", 0.15f);
        mobSet.add("forestCarno");
    }

    public static void initialize(File mobLocations) {
        mobLocationsFolder = mobLocations;
    }

    public static Pair<EntityType, SpawnableMob> getMobStructure(String name) {
        return mobs.get(name);
    }

    public static int getMobProperCount(String mobName) throws FileNotFoundException {
        System.out.print("properCount");
        File file = new File(mobLocationsFolder, mobName+".json");
        if (file.exists()) {
            JsonArray locations = gson.fromJson(new BufferedReader(new FileReader(file)), JsonArray.class);
            System.out.println((int) (mobsPercentage.get(mobName) * locations.size()));
            return (int) (mobsPercentage.get(mobName) * locations.size());
        }
        System.out.println("oof");
        return 0;
    }
}
