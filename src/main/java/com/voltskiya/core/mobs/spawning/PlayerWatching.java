package com.voltskiya.core.mobs.spawning;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.mobs.mobs.Mobs;
import com.voltskiya.core.mobs.mobs.SpawnableMob;
import com.voltskiya.core.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class PlayerWatching implements @NotNull Listener {
    private static final int TIME_BETWEEN_WATCH_PLAYER = 10 * 20;
    private static final byte PLAYER_VIEW_DISTANCE = 6;
    private static final byte PLAYER_PROTECTION_DISTANCE = 2;
    private Voltskiya plugin;
    private final File registeredMobsFolder;
    private final File activeMobsFolder;
    private LinkedList<Player> playersToWatch = new LinkedList<>();
    private final Object playersToWatchSync = new Object();
    private final Gson gson = new Gson();

    public PlayerWatching(Voltskiya pl, File registeredMobs, File activeMobs) {
        plugin = pl;
        registeredMobsFolder = registeredMobs;
        activeMobsFolder = activeMobs;
        Bukkit.getPluginManager().registerEvents(this, pl);
        synchronized (playersToWatchSync) {
            playersToWatch.addAll(Bukkit.getOnlinePlayers());
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::watchPlayers, TIME_BETWEEN_WATCH_PLAYER);
    }

    private void watchPlayers() {
        String worldName = "world";
        Collection<Pair<Integer, Integer>> chunksToLoad;
        Collection<Pair<Integer, Integer>> chunksToRemain = new ArrayList<>();
        synchronized (playersToWatchSync) {
            playersToWatch.removeIf(playerToWatch -> !playerToWatch.isOnline());
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
            // todo unload the chunk
            chunksLoaded.remove(chunkToUnload);
        }
    }

    private void loadChunk(Pair<Integer, Integer> chunkToLoad, String worldName) throws IOException {
        System.out.println("load chunk " + chunkToLoad.toString());
        World world = Objects.requireNonNull(Bukkit.getWorld(worldName)); // this really should not be null

        int x = chunkToLoad.getKey();
        int z = chunkToLoad.getValue();
        File fileChunkToLoad = new File(registeredMobsFolder, String.format("%s,%d,%d.json", worldName, x, z));
        if (fileChunkToLoad.exists()) {
            // we have a record of this chunk in this world
            BufferedReader read = new BufferedReader(new FileReader(fileChunkToLoad));
            JsonArray mobsToLoad = gson.fromJson(read, JsonArray.class);
            for (JsonElement mobToLoadJson : mobsToLoad) {
                SimpleDiskMob mobToLoad = gson.fromJson(mobToLoadJson, SimpleDiskMob.class);
                // load the mob
                Pair<EntityType, SpawnableMob> mobStructure = Mobs.getMobStructure(mobToLoad.name);
                Entity spawned = world.spawnEntity(new Location(world, x * 16 + mobToLoad.x, mobToLoad.y, z * 16 + mobToLoad.z), mobStructure.getKey());
                ((LivingEntity) spawned).setRemoveWhenFarAway(false); // this is my mob D:
                mobStructure.getValue().spawn(spawned);
                System.out.println(spawned.getName() + " was spawned at " + (x * 16 + mobToLoad.x) + ", " + (z * 16 + mobToLoad.z));
            }
            read.close();
            fileChunkToLoad.delete();
        }
    }

    @EventHandler
    public void watchPlayerJoin(PlayerJoinEvent event) {
        synchronized (playersToWatchSync) {
            playersToWatch.add(event.getPlayer());
        }
    }

    @EventHandler
    public void watchPlayerLeave(PlayerQuitEvent event) {
        synchronized (playersToWatchSync) {
            playersToWatch.remove(event.getPlayer());
        }
    }

}
