package com.voltskiya.core.mobs.commands.paint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.bukkit.ChunkSnapshot;

import java.io.*;

public class PaintWorld {
    private static File worldDataFolder;

    public static void initialize(File folder) {
        worldDataFolder = folder;
    }

    public static void paintWorld() {
        try {
            readWorld();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

    public static void loadWorld(ChunkSnapshot chunk) throws IOException {
        Gson gson = new Gson();
        PaintersChunk myChunk = new PaintersChunk(chunk);
        String json = gson.toJson(myChunk);
        File chunkFile = new File(worldDataFolder.getPath() + File.separator + chunk.getWorldName() + '-' + chunk.getX() + '-' + chunk.getZ() + ".json");
        BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFile));
        writer.write(json);
        writer.flush();
        writer.close();
    }
}
