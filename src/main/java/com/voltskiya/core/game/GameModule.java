package com.voltskiya.core.game;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.game.bedTeleportScroll.BedTeleportMain;
import com.voltskiya.core.game.disabledCrafting.DisabledCraftingMain;
import com.voltskiya.core.game.enchantImmunity.ImmunityMain;
import com.voltskiya.core.game.mobGear.EquipMain;
import com.voltskiya.core.game.noDamage.NoDamageMain;
import com.voltskiya.core.game.noRegen.NoRegenMain;
import org.bukkit.plugin.java.JavaPlugin;

public class GameModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        JavaPlugin plugin = Voltskiya.get();
        BedTeleportMain.enable(plugin);
        DisabledCraftingMain.enable(plugin);
        ImmunityMain.enable(plugin);
        EquipMain.enable(plugin);
        NoDamageMain.enable(plugin);
        NoRegenMain.enable(plugin, getDataFolder());
    }

    @Override
    public String getName() {
        return "Game";
    }
}
