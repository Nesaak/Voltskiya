package com.voltskiya.core.mobs;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.mobs.paint.PaintWorld;
import com.voltskiya.core.mobs.scan.HardScan;
import com.voltskiya.core.mobs.scan.RefactorHardScan;
import com.voltskiya.core.mobs.scan.SoftScan;

import java.io.File;

public class MobsModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        File dataFolder = getDataFolder();
        File worldDataFolder = new File(dataFolder, "world");
        if (!worldDataFolder.exists()) worldDataFolder.mkdir();

        File hardScanFolder = new File(dataFolder, "mobCountTemp");
        File hardScanRefactoredFolder = new File(dataFolder, "mobCount");
        File mobLocationsTempFolder = new File(dataFolder, "mobLocationsTemp");
        File mobLocationsFolder = new File(dataFolder, "mobLocations");
        File mobLocationsChunkFolder = new File(dataFolder, "mobLocationsChunk");

        if (!hardScanFolder.exists()) hardScanFolder.mkdir();
        if (!hardScanRefactoredFolder.exists()) hardScanRefactoredFolder.mkdir();
        if (!mobLocationsTempFolder.exists()) mobLocationsTempFolder.mkdir();
        if (!mobLocationsFolder.exists()) mobLocationsFolder.mkdir();
        if (!mobLocationsChunkFolder.exists()) mobLocationsChunkFolder.mkdir();

        Voltskiya plugin = Voltskiya.get();
        MobsCommand.initialize(plugin);
        PaintWorld.initialize(worldDataFolder);
        HardScan.initialize(hardScanFolder);
        RefactorHardScan.initialize(plugin, hardScanFolder, hardScanRefactoredFolder);
        SoftScan.initialize(plugin, mobLocationsFolder, hardScanRefactoredFolder, mobLocationsTempFolder, mobLocationsChunkFolder);
        plugin.getCommandManager().registerCommand(new MobsCommand());

    }

    @Override
    public String getName() {
        return "Mobs";
    }
}
