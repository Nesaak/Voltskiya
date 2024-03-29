package com.voltskiya.core.game.respawn;

import com.voltskiya.core.game.GameYMLNavigate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RespawnListener implements Listener {
    private JavaPlugin plugin;
    private List<Coord> spawnCoords = new ArrayList<>();
    private World spawnWorld;
    private Random random = new Random();
    private int invincibilityTicks;
    private int fallHeight;

    public RespawnListener(JavaPlugin plugin, File dataFolder) {
        this.plugin = plugin;
        File file = new File(String.format("%s%s%s%s%s%s", dataFolder, File.separator, GameYMLNavigate.RespawnYMLNavigate.RESPAWN_DIR, File.separator, GameYMLNavigate.RespawnYMLNavigate.SPAWN, ".yml"));
        YamlConfiguration configOrig = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection config = configOrig.getConfigurationSection(GameYMLNavigate.RespawnYMLNavigate.SPAWN);
        if (config == null) {
            System.err.println("[Volskiya] [Game] [RespawnRegion] could not load a spawn");
            return;
        }
        fallHeight = configOrig.getInt("fallHeight") + 1;
        invincibilityTicks = configOrig.getInt("invincibilityTicks");
        String worldString = configOrig.getString("world");
        if (worldString == null) {
            System.err.println("[Volskiya] [Game] [RespawnRegion] could not load a world for spawn");
            return;
        }
        spawnWorld = Bukkit.getWorld(worldString);
        if (spawnWorld == null) {
            System.err.println("[Volskiya] [Game] [RespawnRegion] could not load a world for spawn");
            return;
        }
        Set<String> keys = config.getKeys(false);
        for (String key : keys) {
            ConfigurationSection coordConfig = config.getConfigurationSection(key);
            if (coordConfig == null)
                continue;
            spawnCoords.add(new Coord(coordConfig));
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler(ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer().getBedSpawnLocation() == null) {
            int choose = (int) (spawnCoords.size() * random.nextDouble());
            Coord coord = spawnCoords.get(choose);
            Location spawnLocation = spawnWorld.getHighestBlockAt(coord.x, coord.z).getLocation().add(0, fallHeight, 0);
            event.setRespawnLocation(spawnLocation);
            // because it sometimes doesn't trigger, this might make it more likely to trigger because the invincibility might be happening before the death
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> event.getPlayer().addPotionEffect(
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, invincibilityTicks, 10)), 1);
        }
    }
}
