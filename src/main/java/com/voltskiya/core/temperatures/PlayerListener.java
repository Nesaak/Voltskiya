package com.voltskiya.core.temperatures;

import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {
    private final JavaPlugin plugin;

    private final Map<UUID, WatchPlayer> watching = new ConcurrentHashMap<>();

    public PlayerListener(JavaPlugin plugin) {
        this.plugin = plugin;
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uid = player.getUniqueId();
            if (watching.containsKey(uid)) {
                if (watching.get(uid).done) {
                    watching.put(uid, new WatchPlayer(uid, plugin));
                }
            } else {
                watching.put(uid, new WatchPlayer(uid, plugin));
            }
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uid = event.getPlayer().getUniqueId();
        if (watching.containsKey(uid)) {
            if (watching.get(uid).done) {
                watching.put(uid, new WatchPlayer(uid, plugin));
            }
        } else {
            watching.put(uid, new WatchPlayer(uid, plugin));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(NavigatePlayers.TEMPERATURE, PersistentDataType.DOUBLE, 0.0);
    }

    // wet handler
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        PersistentDataContainer container = event.getPlayer().getPersistentDataContainer();
        @Nullable Location to = event.getTo();
        if (to != null) {
            Block block = to.getBlock();
            @NotNull Material blockType = block.getType();
            if (blockType == Material.WATER) {
                if (!event.getPlayer().isInsideVehicle())
                    container.set(NavigatePlayers.LAST_WET, PersistentDataType.LONG, System.currentTimeMillis());
            } else if (blockType == Material.CAULDRON) {
                IsWetCheck.addCauldronChecker(event.getPlayer());
            }
        }
    }
}
