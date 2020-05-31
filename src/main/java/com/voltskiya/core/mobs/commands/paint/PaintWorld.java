package com.voltskiya.core.mobs.commands.paint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class PaintWorld {
    private static File worldDataFolder;

    public static void initialize(File folder) {
        worldDataFolder = folder;
    }

    public static void paintWorld() {
        readWorld();

    }

    private static void readWorld() throws FileNotFoundException {
        String[] chunkFiles = worldDataFolder.list();
        if (chunkFiles == null) {
            //todo
            return;
        }
        for (String chunkFileName : chunkFiles) {
            JsonObject chunkJsonObject = new JsonParser().parse(new JsonReader(new BufferedReader(new FileReader(new File(worldDataFolder, chunkFileName))))).getAsJsonObject();
            PaintersChunk chunk = new PaintersChunk(chunkJsonObject);


        }


    }
}
