package com.voltskiya.core.mobs;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.mobs.commands.PaintWorldCommand;
import com.voltskiya.core.mobs.commands.paint.PaintWorld;

import java.io.File;

public class MobsModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        File dataFolder = getDataFolder();
        File worldDataFolder = new File(dataFolder, "world");
        if (!worldDataFolder.exists()) worldDataFolder.mkdir();
        Voltskiya plugin = Voltskiya.get();
        PaintWorld.initialize(worldDataFolder);
        plugin.getCommandManager().registerCommand(new PaintWorldCommand());

    }

    @Override
    public String getName() {
        return "Mobs";
    }
}
