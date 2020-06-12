package com.voltskiya.core.mobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.common.Permission;
import com.voltskiya.core.mobs.commands.paint.PaintWorld;
import com.voltskiya.core.mobs.scan.HardScan;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandAlias("test")
public class PaintWorldCommand extends BaseCommand {
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(Voltskiya.get(), () -> {
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

    @Subcommand("hardScan")
    public void hardScan(Player player) {
        World worldToScan = player.getWorld();
        @NotNull WorldBorder border = worldToScan.getWorldBorder();
        @NotNull Location borderCenter = border.getCenter();
        double size = border.getSize();
        short lowerX = (short) ((borderCenter.getX() - size) / 16);
        short lowerZ = (short) ((borderCenter.getZ() - size) / 16);
        short higherX = (short) ((borderCenter.getX() + size) / 16);
        short higherZ = (short) ((borderCenter.getZ() + size) / 16);

        for (short x = lowerX; x < higherX; x++) {
            for (short z = lowerZ; z < higherZ; z++) {
                ChunkSnapshot chunk = worldToScan.getChunkAt(x, z).getChunkSnapshot();
                HardScan.scan(chunk, lowerX, lowerZ);
            }
        }


    }
}
