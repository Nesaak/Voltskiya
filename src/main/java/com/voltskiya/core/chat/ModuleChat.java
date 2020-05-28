package com.voltskiya.core.chat;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import org.bukkit.Bukkit;

public class ModuleChat implements VoltskiyaModule {

    @Override
    public void startModule() {
        Bukkit.getPluginManager().registerEvents(new ChatEvents(), Voltskiya.get());
    }

    @Override
    public String getName() {
        return "Chat";
    }
}
