package com.voltskiya.core.mobs;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.mobs.commands.PaintWorldCommand;
import com.voltskiya.core.mobs.commands.paint.PaintWorld;
import com.voltskiya.core.mobs.scan.HardScan;
import com.voltskiya.core.mobs.scan.SoftScan;

import java.io.File;

public class MobsModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        File dataFolder = getDataFolder();
        File worldDataFolder = new File(dataFolder, "world");
        File hardScanFolder = new File(dataFolder, "mobCount");
        File mobLocationsFolder = new File(dataFolder, "mobLocations");
        if (!worldDataFolder.exists()) worldDataFolder.mkdir();
        if (!hardScanFolder.exists()) worldDataFolder.mkdir();
        Voltskiya plugin = Voltskiya.get();
        PaintWorld.initialize(worldDataFolder);
        HardScan.initialize(hardScanFolder);
        SoftScan.initialize( plugin, hardScanFolder, mobLocationsFolder);
        plugin.getCommandManager().registerCommand(new PaintWorldCommand());

    }

    @Override
    public String getName() {
        return "Mobs";
    }
}
