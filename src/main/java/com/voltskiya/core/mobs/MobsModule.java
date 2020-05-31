package com.voltskiya.core.mobs;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.mobs.commands.PaintWorldCommand;

import java.io.File;

public class MobsModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        File dataFolder = getDataFolder();
        Voltskiya plugin = Voltskiya.get();
        plugin.getCommandManager().registerCommand(new PaintWorldCommand());

    }

    @Override
    public String getName() {
        return "Mobs";
    }
}
