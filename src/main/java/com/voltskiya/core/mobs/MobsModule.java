package com.voltskiya.core.mobs;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.mobs.mobs.Mobs;
import com.voltskiya.core.mobs.paint.PaintWorld;
import com.voltskiya.core.mobs.scanning.HardScan;
import com.voltskiya.core.mobs.scanning.RefactorHardScan;
import com.voltskiya.core.mobs.scanning.RefactorSoftScan;
import com.voltskiya.core.mobs.scanning.SoftScan;
import com.voltskiya.core.mobs.spawning.PlayerWatching;
import com.voltskiya.core.mobs.spawning.Spawning;

import java.io.File;

public class MobsModule extends VoltskiyaModule {
    public static final String worldToMoniter = "world";

    @Override
    public void enabled() {
        File dataFolder = getDataFolder();
        File worldDataFolder = new File(dataFolder, MobsModule.worldToMoniter);
        if (!worldDataFolder.exists()) worldDataFolder.mkdir();

        File hardScanFolder = new File(dataFolder, "mobCountTemp");
        File hardScanRefactoredFolder = new File(dataFolder, "mobCount");
        File mobLocationsTempFolder = new File(dataFolder, "mobLocationsTemp");
        File mobLocationsFolder = new File(dataFolder, "mobLocations");
        File mobLocationsChunkFolder = new File(dataFolder, "mobLocationsChunk");
        File registeredMobsFolder = new File(dataFolder, "registeredMobs");
        File activeMobsFolder = new File(dataFolder, "activeMobs");

        if (!hardScanFolder.exists()) hardScanFolder.mkdir();
        if (!hardScanRefactoredFolder.exists()) hardScanRefactoredFolder.mkdir();
        if (!mobLocationsTempFolder.exists()) mobLocationsTempFolder.mkdir();
        if (!mobLocationsFolder.exists()) mobLocationsFolder.mkdir();
        if (!mobLocationsChunkFolder.exists()) mobLocationsChunkFolder.mkdir();
        if (!registeredMobsFolder.exists()) registeredMobsFolder.mkdir();
        if (!activeMobsFolder.exists()) activeMobsFolder.mkdir();

        Voltskiya plugin = Voltskiya.get();
        MobsCommand.initialize(plugin);
        PaintWorld.initialize(worldDataFolder);

        // scanning
        HardScan.initialize(hardScanFolder);
        RefactorHardScan.initialize(plugin, hardScanFolder, hardScanRefactoredFolder);
        SoftScan.initialize(plugin, hardScanRefactoredFolder, mobLocationsTempFolder, mobLocationsChunkFolder);
        RefactorSoftScan.initialize(plugin, mobLocationsFolder, mobLocationsChunkFolder);

        // spawning
        Mobs.initialize(mobLocationsFolder);
        Spawning.initialize(plugin, mobLocationsFolder, registeredMobsFolder);
        new PlayerWatching(plugin, registeredMobsFolder, activeMobsFolder);
        plugin.getCommandManager().registerCommand(new MobsCommand());

    }

    @Override
    public String getName() {
        return "Mobs";
    }
}
