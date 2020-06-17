package com.voltskiya.core.mobs;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.mobs.paint.PaintWorld;
import com.voltskiya.core.mobs.scan.HardScan;
import com.voltskiya.core.mobs.scan.RefactorHardScan;

import java.io.File;

public class MobsModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        File dataFolder = getDataFolder();
        File worldDataFolder = new File(dataFolder, "world");
        File hardScanFolder = new File(dataFolder, "mobCountTemp");
        File hardScanRefactoredFolder = new File(dataFolder, "mobCount");
        if (!worldDataFolder.exists()) worldDataFolder.mkdir();
        if (!hardScanFolder.exists()) hardScanFolder.mkdir();
        if (!hardScanRefactoredFolder.exists()) hardScanRefactoredFolder.mkdir();
        Voltskiya plugin = Voltskiya.get();
        PaintWorld.initialize(worldDataFolder);
        HardScan.initialize(hardScanFolder);
        RefactorHardScan.initialize(plugin, hardScanFolder, hardScanRefactoredFolder);
        plugin.getCommandManager().registerCommand(new MobsCommand());

    }

    @Override
    public String getName() {
        return "Mobs";
    }
}
