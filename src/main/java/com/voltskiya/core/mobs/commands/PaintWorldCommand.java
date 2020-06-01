package com.voltskiya.core.mobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.voltskiya.core.mobs.commands.paint.PaintWorld;
import org.bukkit.ChunkSnapshot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@CommandAlias("test")
public class PaintWorldCommand extends BaseCommand {
    @Subcommand("paint world")
    public void paintWorld() {
        PaintWorld.paintWorld();
    }

    @Subcommand("load world")
    public void loadWorld(Player player) {
        @NotNull ChunkSnapshot chunk = player.getLocation().getChunk().getChunkSnapshot(true,true,false);
//        new Thread(() ->
//        {
        try {
            PaintWorld.loadWorld(chunk);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
//        );
    }
}
