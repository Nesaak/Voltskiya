package com.voltskiya.core.game.powertool;

import com.voltskiya.core.Voltskiya;
import org.bukkit.plugin.java.JavaPlugin;

public class PowerToolMain {
    public static void enable(JavaPlugin plugin) {
        Voltskiya.get().getCommandManager().registerCommand(new PowerToolCommand(plugin));
        new PowerToolListener(plugin);
        System.out.println("[VoltskiyaApple] [PowerTool] enabled");
    }
}
