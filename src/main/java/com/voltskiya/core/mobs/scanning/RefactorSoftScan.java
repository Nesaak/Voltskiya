package com.voltskiya.core.mobs.scanning;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.voltskiya.core.Voltskiya;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


public class RefactorSoftScan {
    private static Voltskiya plugin;
    private static File mobLocationsFolder, mobLocationsChunkFolder;

    private static Gson gson = new Gson();

    public static void initialize(Voltskiya pl, File mobLocations, File mobLocationsChunk) {
        plugin = pl;
        mobLocationsChunkFolder = mobLocationsChunk;
        mobLocationsFolder = mobLocations;
    }

    public static void scan() throws IOException {
        System.out.println("Stage 4!");
        cleanNewFolder();

        Map<String, JsonArray> finalTotal = new HashMap<>();
        String[] mobLocationsChunkPaths = mobLocationsChunkFolder.list();
        for (String mobLocationsChunkPath : mobLocationsChunkPaths) {
            final BufferedReader reader = new BufferedReader(new FileReader(new File(mobLocationsChunkFolder, mobLocationsChunkPath)));
            JsonObject total = gson.fromJson(reader, JsonObject.class);
            reader.close();
            for (Map.Entry<String, JsonElement> mobToLocations : total.entrySet()) {
                JsonArray finalArray = finalTotal.get(mobToLocations.getKey());
                if (finalArray == null) {
                    finalTotal.put(mobToLocations.getKey(), finalArray = new JsonArray());
                }
                for (JsonElement oldEntry : mobToLocations.getValue().getAsJsonArray()) {
                    if (!oldEntry.isJsonPrimitive()) {
                        finalArray.add(oldEntry);
                    }
                }

            }
        }
        for (Map.Entry<String, JsonArray> mobEntry : finalTotal.entrySet()) {
            File fileToWrite = new File(mobLocationsFolder, mobEntry.getKey() + ".json");
            if (!fileToWrite.exists()) fileToWrite.createNewFile();
            final BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite));
            gson.toJson(mobEntry.getValue(), writer);
            writer.close();
        }
        cleanOldFolder();
        System.out.println("done.");
    }

    private static void cleanNewFolder() {
        String[] mobLocationPaths = mobLocationsFolder.list();
        if (mobLocationPaths != null) {
            // clean the folder (just in case people changed the name of a mob and there is a lingering file)
            for (String mobLocationPath : mobLocationPaths) {
                File mobLocationFile = new File(mobLocationsFolder, mobLocationPath);
                if (mobLocationFile.exists()) mobLocationFile.delete();
            }
        } else {
            plugin.getLogger().log(Level.SEVERE, String.format("The file %s should be a folder", mobLocationsFolder.toString()));
        }
    }

    private static void cleanOldFolder() {
        String[] mobLocationsChunkPaths = mobLocationsChunkFolder.list();
        if (mobLocationsChunkPaths != null) {
            // clean the folder (just in case people changed the name of a mob and there is a lingering file)
            for (String mobLocationsChunkPath : mobLocationsChunkPaths) {
                File mobLocationsChunkFile = new File(mobLocationsChunkFolder, mobLocationsChunkPath);
                if (mobLocationsChunkFile.exists()) mobLocationsChunkFile.delete();
            }
        } else {
            plugin.getLogger().log(Level.SEVERE, String.format("The file %s should be a folder", mobLocationsChunkFolder.toString()));
        }
    }
}
