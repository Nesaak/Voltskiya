package com.voltskiya.core.game;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.game.bedTeleportScroll.BedTeleportMain;
import com.voltskiya.core.game.disabledCrafting.DisabledCraftingMain;
import com.voltskiya.core.game.enchantImmunity.ImmunityMain;
import com.voltskiya.core.game.mobGear.EquipMain;
import com.voltskiya.core.game.noDamage.NoDamageMain;
import com.voltskiya.core.game.noRegen.NoRegenMain;
import com.voltskiya.core.game.powertool.PowerToolMain;
import com.voltskiya.core.game.respawn.RespawnMain;
import com.voltskiya.core.game.rotting.RottingMain;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class GameModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        final File dataFolder = getDataFolder();
        JavaPlugin plugin = Voltskiya.get();

        BedTeleportMain.enable(plugin);
        DisabledCraftingMain.enable(plugin);
        ImmunityMain.enable(plugin);
        EquipMain.enable(plugin);
        NoDamageMain.enable(plugin);
        NoRegenMain.enable(plugin, dataFolder);
        PowerToolMain.enable(plugin);
        RespawnMain.enable(plugin, dataFolder);
        RottingMain.enable(plugin, dataFolder);
    }

    @Override
    public String getName() {
        return "Game";
    }
}
