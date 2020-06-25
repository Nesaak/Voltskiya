package com.voltskiya.core.mobs.paint;

import com.google.gson.Gson;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.mobs.MobsModule;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

import java.io.*;
import java.util.*;
import java.util.List;

public class PaintWorld {
    private static File worldDataFolder;
    private static PaintersGraphics graphics;
    private static Map<String, PaintersWorld> namesToWorld;

    public static void initialize(File folder) {
        worldDataFolder = folder;
    }

    public static void main(String[] args) throws FileNotFoundException {
        worldDataFolder = new File("world");
        namesToWorld = readWorld();
        runGraphics();
    }


    public static void runGraphics() {
        PaintersWorld world = namesToWorld.get(MobsModule.worldToMoniter);
        graphics = new PaintersGraphics(world);

    }

    public static void readWorldBukkit() {
        String[] chunkFiles = worldDataFolder.list();
        if (chunkFiles == null) {
            //todo
            return;
        }
        Map<String, List<PaintersChunk>> worldToChunks = new HashMap<>();
        int i = 0;
        for (String chunkFileName : chunkFiles) {
            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Voltskiya.get(), () -> {
                ChunkContainer chunks = null;
                try {
                    chunks = (new Gson()).fromJson(new BufferedReader(new FileReader(new File(worldDataFolder, chunkFileName))), ChunkContainer.class);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String name = chunkFileName.split("#", 2)[0];
                if (!worldToChunks.containsKey(name)) {
                    worldToChunks.put(name, new ArrayList<>());
                }
                worldToChunks.get(name).addAll(chunks.chunkList);
                if (finalI % 20 == 0)
                    Bukkit.broadcastMessage("reading " + chunkFileName);
            }, i++);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Voltskiya.get(), () -> {
            Map<String, PaintersWorld> nameToWorld = new HashMap<>();
            for (String name : worldToChunks.keySet()) {
                nameToWorld.put(name, new PaintersWorld(worldToChunks.get(name)));
            }
            PaintWorld.namesToWorld = nameToWorld;
            Bukkit.broadcastMessage("Done loading!");
        }, i + 40);
    }

    private static Map<String, PaintersWorld> readWorld() throws FileNotFoundException {
        String[] chunkFiles = worldDataFolder.list();
        if (chunkFiles == null) {
            //todo
            return new HashMap<>();
        }
        Map<String, List<PaintersChunk>> worldToChunks = new HashMap<>();
        int i = 0;
        for (String chunkFileName : chunkFiles) {
            ChunkContainer chunks = (new Gson()).fromJson(new BufferedReader(new FileReader(new File(worldDataFolder, chunkFileName))), ChunkContainer.class);
            String name = chunkFileName.split("#", 2)[0];
            if (!worldToChunks.containsKey(name)) {
                worldToChunks.put(name, new ArrayList<>());
            }
            worldToChunks.get(name).addAll(chunks.chunkList);
            if (i++ % 20 == 0)
                System.out.println(chunkFileName);
        }
        Map<String, PaintersWorld> nameToWorld = new HashMap<>();
        for (String name : worldToChunks.keySet()) {
            nameToWorld.put(name, new PaintersWorld(worldToChunks.get(name)));
        }
        return nameToWorld;

    }

    public static void loadWorld(List<ChunkSnapshot> chunks) throws IOException {
        Gson gson = new Gson();
        List<PaintersChunk> myChunks = new ArrayList<>(16);
        for (ChunkSnapshot chunk : chunks)
            myChunks.add(new PaintersChunk(chunk));
        String json = gson.toJson(new ChunkContainer(myChunks));
        ChunkSnapshot originChunk = chunks.get(0);
        File chunkFile = new File(worldDataFolder.getPath() + File.separator + originChunk.getWorldName() + '#' + originChunk.getX() + '#' + originChunk.getZ() + ".json");
        BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFile));
        writer.write(json);
        writer.flush();
        writer.close();
    }

    public static void drawMob(Location loc, UUID uid) {
        graphics.putMob(loc,uid);
        graphics.repaint();
    }
}
