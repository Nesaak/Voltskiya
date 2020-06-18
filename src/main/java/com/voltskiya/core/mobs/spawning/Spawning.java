package com.voltskiya.core.mobs.spawning;

import com.voltskiya.core.Voltskiya;

import java.io.File;

public class Spawning {

    private static Voltskiya plugin;
    private static File registeredMobsFolder;
    private static File mobLocationsFolder;

    public static void initialize(Voltskiya pl, File mobLocations, File registeredMobs) {
        plugin = pl;
        registeredMobsFolder = registeredMobs;
        mobLocationsFolder = mobLocations;
    }
}
