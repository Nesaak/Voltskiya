package com.voltskiya.core.game;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.game.bedTeleportScroll.BedTeleportMain;
import org.bukkit.plugin.java.JavaPlugin;

public class VoltskiyaAppleMain extends VoltskiyaModule {
    @Override
    public void enabled() {
        JavaPlugin plugin = Voltskiya.get();
        BedTeleportMain.enable(plugin);
    }

    @Override
    public String getName() {
        return null;
    }
}
