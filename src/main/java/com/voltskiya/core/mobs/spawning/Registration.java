package com.voltskiya.core.mobs.spawning;

import com.voltskiya.core.Voltskiya;

import java.io.File;

public class Registration {
    private static Voltskiya plugin;
    private static File registeredMobsFolder;

    public static void initialize(Voltskiya pl, File registeredMobs) {
        plugin = pl;
        registeredMobsFolder = registeredMobs;
    }
}
