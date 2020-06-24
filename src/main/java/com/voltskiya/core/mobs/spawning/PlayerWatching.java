package com.voltskiya.core.mobs.spawning;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.mobs.mobs.Mobs;
import com.voltskiya.core.mobs.mobs.SpawnableMob;
import com.voltskiya.core.utils.Pair;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class PlayerWatching {
    private static final int TIME_BETWEEN_WATCH_PLAYER = 1 * 20;
    private static final byte PLAYER_VIEW_DISTANCE = 6;
    private static final byte PLAYER_PROTECTION_DISTANCE = 2;
    public static final String MOB_SYSTEM_TAG = "mobSystem";
    private final Voltskiya plugin;
    private final File registeredMobsFolder;
    private final File activeMobsFolder;
    private final Gson gson = new Gson();
    private final Collection<GameMode> immuneGameModes = Arrays.asList(GameMode.CREATIVE, GameMode.SPECTATOR);
    private final NamespacedKey MOB_NAME_TAG;

    public PlayerWatching(Voltskiya pl, File registeredMobs, File activeMobs) {
        plugin = pl;
        registeredMobsFolder = registeredMobs;
        activeMobsFolder = activeMobs;
        MOB_NAME_TAG = new NamespacedKey(pl, "system_name");
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::watchPlayers, TIME_BETWEEN_WATCH_PLAYER);
    }

    private void watchPlayers() {
        String worldName = "world";
        Collection<? extends Player> playersToWatch = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collection<Pair<Integer, Integer>> chunksToLoad;
        Collection<Pair<Integer, Integer>> chunksToRemain = new ArrayList<>();
        playersToWatch.removeIf(playerToWatch -> !playerToWatch.isOnline() || immuneGameModes.contains(playerToWatch.getGameMode()));
        chunksToLoad = getChunksLoaded(playersToWatch);
        for (Player player : playersToWatch) {
            Location playerLocation = player.getLocation();
            World world = playerLocation.getWorld();
            if (world != null)
                if (world.getName().equals("world")) {
                    int x = (int) playerLocation.getX() / 16;
                    int z = (int) playerLocation.getZ() / 16;
                    for (byte xi = -PLAYER_PROTECTION_DISTANCE; xi < PLAYER_PROTECTION_DISTANCE; xi++) {
                        for (byte zi = -PLAYER_PROTECTION_DISTANCE; zi < PLAYER_PROTECTION_DISTANCE; zi++) {
                            chunksToRemain.add(new Pair<>(x + xi, z + zi));
                        }
                    }
                }
        }
        correctLoadedChunks(chunksToLoad, chunksToRemain, worldName);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::watchPlayers, TIME_BETWEEN_WATCH_PLAYER);
    }

    @NotNull
    public static Collection<Pair<Integer, Integer>> getChunksLoaded(Collection<? extends Player> players) {
        Collection<Pair<Integer, Integer>> playerLocations = new ArrayList<>();
        for (Player player : players) {
            Location playerLocation = player.getLocation();
            World world = playerLocation.getWorld();
            if (world != null)
                if (world.getName().equals("world")) {
                    int x = (int) playerLocation.getX() / 16;
                    int z = (int) playerLocation.getZ() / 16;
                    playerLocations.add(new Pair<>(x, z));
                }
        }

        Collection<Pair<Integer, Integer>> chunksToLoad = new ArrayList<>();
        for (Pair<Integer, Integer> playerLocation : playerLocations) {
            for (byte xi = -PLAYER_VIEW_DISTANCE; xi < PLAYER_VIEW_DISTANCE; xi++) {
                for (byte zi = -PLAYER_VIEW_DISTANCE; zi < PLAYER_VIEW_DISTANCE; zi++) {
                    chunksToLoad.add(new Pair<>(playerLocation.getKey() + xi, playerLocation.getValue() + zi));
                }
            }
        }
        for (Pair<Integer, Integer> playerLocation : playerLocations) {
            for (byte xi = -PLAYER_PROTECTION_DISTANCE; xi < PLAYER_PROTECTION_DISTANCE; xi++) {
                for (byte zi = -PLAYER_PROTECTION_DISTANCE; zi < PLAYER_PROTECTION_DISTANCE; zi++) {
                    chunksToLoad.remove(new Pair<>(playerLocation.getKey() + xi, playerLocation.getValue() + zi));
                }
            }
        }
        return chunksToLoad;
    }

    private void correctLoadedChunks(Collection<Pair<Integer, Integer>> chunksToLoad, Collection<Pair<Integer, Integer>> chunksToRemain, String worldName) {
        String[] chunksLoadedStrings = activeMobsFolder.list();
        HashSet<Pair<Integer, Integer>> chunksLoaded = new HashSet<>();
        for (String chunkLoadedString : chunksLoadedStrings) {
            String[] chunkLoadedStringSplit = chunkLoadedString.split(",");
            if (chunkLoadedStringSplit[0].equals(worldName))
                try {
                    int x = Integer.parseInt(chunkLoadedStringSplit[1]);
                    int z = Integer.parseInt(chunkLoadedStringSplit[2].substring(0, chunkLoadedStringSplit[2].length() - 5));
                    chunksLoaded.add(new Pair<>(x, z));
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    plugin.getLogger().log(Level.SEVERE, chunkLoadedString + " in the folder " + registeredMobsFolder.getName() + " has an improper name");
                }
        }
        // find the chunks that are not loaded and load them
        for (Pair<Integer, Integer> chunkToLoad : chunksToLoad) {
            if (!chunksLoaded.remove(chunkToLoad)) {
                // the chunk is not currently loaded
                try {
                    loadChunk(chunkToLoad, worldName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Pair<Integer, Integer> chunkToStay : chunksToRemain) {
            chunksLoaded.remove(chunkToStay);
        }

        // remove the chunks that are loaded that shouldn't be loaded
        for (Pair<Integer, Integer> chunkToUnload : chunksLoaded) {
            try {
                unloadChunk(chunkToUnload, worldName);
                File fileActiveChunk = new File(activeMobsFolder, String.format("%s,%d,%d.json", worldName, chunkToUnload.getKey(), chunkToUnload.getValue()));
                if (fileActiveChunk.exists()) fileActiveChunk.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void unloadChunk(Pair<Integer, Integer> chunkToUnloadCoords, String worldName) throws IOException {
        World world = Objects.requireNonNull(Bukkit.getWorld(worldName)); // this really should not be null
        int x = chunkToUnloadCoords.getKey();
        int z = chunkToUnloadCoords.getValue();
        @NotNull Chunk chunkToUnload = world.getChunkAt(x, z);

        JsonArray mobsToSave = new JsonArray();
        @NotNull Entity[] entitiesInChunk = chunkToUnload.getEntities();
        for (Entity entityInChunk : entitiesInChunk) {
            if (entityInChunk.getScoreboardTags().contains(MOB_SYSTEM_TAG)) {
                // this is my mob and should be despawned
                String mobName = entityInChunk.getPersistentDataContainer().get(MOB_NAME_TAG, PersistentDataType.STRING);
                Location entityLocation = entityInChunk.getLocation();
                mobsToSave.add(gson.toJsonTree(new SimpleDiskMob(mobName, entityLocation.getBlockX() % 16, entityLocation.getBlockY(), entityLocation.getBlockZ() % 16)));
                System.out.println(entityInChunk.getName() + " was despawneed at " + entityLocation.getBlockX() + ", " + entityLocation.getBlockZ());
                entityInChunk.remove();
            }
        }
        if (mobsToSave.size() == 0) {
            return;
        }
        File fileChunkToSave = new File(registeredMobsFolder, String.format("%s,%d,%d.json", worldName, x, z));
        if (!fileChunkToSave.exists()) fileChunkToSave.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileChunkToSave));
        gson.toJson(mobsToSave, writer);
        writer.close();

    }

    private void loadChunk(Pair<Integer, Integer> chunkToLoad, String worldName) throws IOException {
        World world = Objects.requireNonNull(Bukkit.getWorld(worldName)); // this really should not be null

        int x = chunkToLoad.getKey();
        int z = chunkToLoad.getValue();
        String fileName = String.format("%s,%d,%d.json", worldName, x, z);
        File fileChunkToLoad = new File(registeredMobsFolder, fileName);
        if (fileChunkToLoad.exists()) {
            System.out.println("load chunk " + chunkToLoad.toString());
            // we have a record of this chunk in this world
            BufferedReader read = new BufferedReader(new FileReader(fileChunkToLoad));
            JsonArray mobsToLoad = gson.fromJson(read, JsonArray.class);
            for (JsonElement mobToLoadJson : mobsToLoad) {
                SimpleDiskMob mobToLoad = gson.fromJson(mobToLoadJson, SimpleDiskMob.class);
                // load the mob
                Pair<EntityType, SpawnableMob> mobStructure = Mobs.getMobStructure(mobToLoad.name);
                System.out.println(String.format("full: %d, x: %d, sub: %d", x * 16 + mobToLoad.x, x, mobToLoad.x));
                Entity spawned = world.spawnEntity(new Location(world, x * 16 + mobToLoad.x, mobToLoad.y, z * 16 + mobToLoad.z), mobStructure.getKey());
                ((LivingEntity) spawned).setRemoveWhenFarAway(false); // this is my mob D:
                spawned.addScoreboardTag(MOB_SYSTEM_TAG);
                spawned.getPersistentDataContainer().set(MOB_NAME_TAG, PersistentDataType.STRING, mobToLoad.name);
                mobStructure.getValue().spawn(spawned);
                System.out.println(spawned.getName() + " was spawned at " + (x * 16 + mobToLoad.x) + ", " + (z * 16 + mobToLoad.z));
            }
            read.close();
            System.out.println("killing chunk soon");
            System.out.println(fileChunkToLoad.delete());
        }
        File fileActiveChunk = new File(activeMobsFolder, fileName);
        if (!fileActiveChunk.exists()) fileActiveChunk.createNewFile();
        // these are empty files rather than a file in case i want to put information about the chunk somewhere.
    }
}
