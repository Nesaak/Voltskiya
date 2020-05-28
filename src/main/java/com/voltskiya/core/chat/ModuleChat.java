package com.voltskiya.core.chat;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import org.bukkit.Bukkit;

public class ModuleChat extends VoltskiyaModule {

    @Override
    public void enabled() {
        Bukkit.getPluginManager().registerEvents(new ChatEvents(), Voltskiya.get());
    }

    @Override
    public String getName() {
        return "Chat";
    }
}
