package com.voltskiya.core;

import co.aikar.commands.PaperCommandManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.logging.Level;

public final class Voltskiya extends JavaPlugin {

    private static Voltskiya instance;

    private LuckPerms luckPerms;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        setupLuckPerms();
        setupACF();
        registerModules();
    }

    public static Voltskiya get() {
        return instance;
    }

    // Module system start

    public void registerModules() {
        Reflections reflections = new Reflections("com.voltskiya.core", new SubTypesScanner(true));
        reflections.getSubTypesOf(VoltskiyaModule.class).forEach(moduleClass -> {
            VoltskiyaModule module;
            try {
                module = (VoltskiyaModule) moduleClass.getConstructors()[0].newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            registerModule(module);
        });
    }

    public void registerModule(VoltskiyaModule module) {
        getLogger().log(Level.INFO, "Registered Voltskiya Module: " + module.getName());
    }

    // Module system end


    // LuckPerms start

    private void setupLuckPerms() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            luckPerms = LuckPermsProvider.get();
        } else {
            getLogger().log(Level.INFO, "LuckPerms is not enabled, some functions may be disabled.");
        }
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    // LuckPerms end


    // ACF start

    private void setupACF() {
        commandManager = new PaperCommandManager(this);
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    // ACF end
}
