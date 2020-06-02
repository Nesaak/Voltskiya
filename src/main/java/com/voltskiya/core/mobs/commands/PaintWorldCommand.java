package com.voltskiya.core.mobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.common.Permission;
import com.voltskiya.core.mobs.commands.paint.PaintWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CommandAlias("test")
public class PaintWorldCommand extends BaseCommand {
    @CommandPermission(Permission.WORLD_READING)
    @Subcommand("load everything")
    public void loadEverything() {
        World world = Bukkit.getWorld("world");
        int i = 0;
        for (int x = -125; x < 105; x += 4) {
            for (int z = -78; z < 123; z += 4) {
                int finalX = x;
                int finalZ = z;
                Bukkit.getScheduler().scheduleSyncDelayedTask(Voltskiya.get(), () -> {
                    if (finalZ == -78) Bukkit.broadcastMessage(String.valueOf(finalX));
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
}
