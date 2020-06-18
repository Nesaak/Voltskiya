package com.voltskiya.core.mobs;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.common.Permission;
import com.voltskiya.core.mobs.paint.PaintWorld;
import com.voltskiya.core.mobs.scan.HardScan;
import com.voltskiya.core.mobs.scan.RefactorHardScan;
import com.voltskiya.core.mobs.scan.SoftScan;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandAlias("mobs")
public class MobsCommand extends BaseCommand {
    public static final byte CHUNK_SCAN_INCREMENT = 5;

    private static Voltskiya plugin;

    public static void initialize(Voltskiya pl) {
        plugin = pl;
    }

    @CommandPermission(Permission.WORLD_READING)
    @Subcommand("load everything")
    public void loadEverything(Player player) {
        World world = player.getWorld();
        @NotNull WorldBorder border = world.getWorldBorder();
        @NotNull Location borderCenter = border.getCenter();
        double size = border.getSize();
        final int lowerX = (int) (borderCenter.getX() - size) / 16;
        final int lowerZ = (int) (borderCenter.getZ() - size) / 16;
        final int higherX = (int) (borderCenter.getX() + size) / 16;
        final int higherZ = (int) (borderCenter.getZ() + size) / 16;
        int i = 0;
        for (int x = lowerX; x < higherX; x += 4) {
            for (int z = lowerZ; z < higherZ; z += 4) {
                int finalX = x;
                int finalZ = z;
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (finalZ == lowerZ) Bukkit.broadcastMessage(String.valueOf(finalX));
                    List<ChunkSnapshot> chunks = new ArrayList<>(17);
                    for (int xi = 0; xi < 4; xi++) {
                        for (int zi = 0; zi < 4; zi++) {
                            chunks.add(world.getChunkAt(finalX + xi, finalZ + zi).getChunkSnapshot(true, true, false));
                        }
                    }
                    try {
                        PaintWorld.loadWorld(chunks);
                    } catch (IOException e) {
                    }
                }, i += 6);
            }
        }
    }

    @CommandPermission(Permission.WORLD_READING)
    @Subcommand("graphics run")
    public void runGraphics() {
        PaintWorld.runGraphics();
    }

    @CommandPermission(Permission.WORLD_READING)
    @Subcommand("graphics loadFromDisk")
    public void loadFromDisk() {
        PaintWorld.readWorldBukkit();
    }

    @CommandPermission(Permission.WORLD_READING)
    @Subcommand("spawn mob")
    public void spawnMob(Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();
        Entity spawned = world.spawnEntity(location, EntityType.ARMOR_STAND);
        UUID uid = spawned.getUniqueId();
        PaintWorld.drawMob(location, uid);

    }

    @Subcommand("scan")
    public class Scan extends BaseCommand {
        private static final int CHUNK_SCAN_INCREMENT_TIME = 10;

        @Subcommand("hard")
        public void hardScan(Player player) {
            World worldToScan = player.getWorld();
            @NotNull WorldBorder border = worldToScan.getWorldBorder();
            @NotNull Location borderCenter = border.getCenter();
            double size = border.getSize();
            short lowerX = (short) ((borderCenter.getX() - size) / 16);
            short lowerZ = (short) ((borderCenter.getZ() - size) / 16);
            short higherX = (short) ((borderCenter.getX() + size) / 16);
            short higherZ = (short) ((borderCenter.getZ() + size) / 16);

            int delayCounter = 0;
            for (short x = lowerX; x < higherX; x += CHUNK_SCAN_INCREMENT) {
                for (short z = lowerZ; z < higherZ; z += CHUNK_SCAN_INCREMENT) {
                    short finalX = x;
                    short finalZ = z;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                final ChunkSnapshot[][] chunks = new ChunkSnapshot[CHUNK_SCAN_INCREMENT][CHUNK_SCAN_INCREMENT];
                                for (byte xi = 0; xi < CHUNK_SCAN_INCREMENT; xi++) {
                                    for (byte zi = 0; zi < CHUNK_SCAN_INCREMENT; zi++) {
                                        chunks[xi][zi] = worldToScan.getChunkAt(finalX + xi, finalZ + zi).getChunkSnapshot(true, true, false);

                                    }
                                }
                                HardScan.scan(chunks, CHUNK_SCAN_INCREMENT);
                                if (finalZ == lowerZ) System.out.println(finalX + "/" + higherX);
                            }
                            , delayCounter += CHUNK_SCAN_INCREMENT_TIME
                    );
                }
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, RefactorHardScan::scan, delayCounter + 1); // this scan will call the next scan when it's finished
        }

        @Subcommand("soft")
        public void softScan() {
            try {
                SoftScan.scan();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
