package com.voltskiya.core.mobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.voltskiya.core.mobs.commands.paint.PaintWorld;

@CommandAlias("test")
public class PaintWorldCommand extends BaseCommand {
    @Subcommand("paint world")
    public void paintWorld(){
        PaintWorld.paintWorld();
    }
}
