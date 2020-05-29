package com.voltskiya.core.temperatures;

import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.UUID;

public class IsWetCheck {
    private static HashSet<UUID> checks = new HashSet<>();
    private static JavaPlugin plugin;

    public static void initialize(JavaPlugin pl) {
        plugin = pl;
    }

    public static void addCauldronChecker(Player player) {
        if (!checks.contains(player.getUniqueId())) {
            inCauldronCheck(player);
        }
    }

    private static void inCauldronCheck(Player player) {
        Location location = player.getLocation();
        Block block = location.getBlock();
        Material blockType = block.getType();
        BlockData blockData = block.getBlockData();
        if (blockType == Material.CAULDRON && blockData instanceof Levelled) {
            // the block is a cauldron
            int level = ((Levelled) blockData).getLevel();
            if (level != 0) {
                PersistentDataContainer container = player.getPersistentDataContainer();
                container.set(NavigatePlayers.LAST_WET, PersistentDataType.LONG, System.currentTimeMillis());
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> inCauldronCheck(player), 40);
            }
        } else {
            checks.remove(player.getUniqueId());
        }
    }
}
