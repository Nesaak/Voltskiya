package com.voltskiya.core;

import co.aikar.commands.PaperCommandManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class Voltskiya extends JavaPlugin {

    private static Voltskiya instance;

    private LuckPerms luckPerms;
    private PaperCommandManager commandManager;
    private List<VoltskiyaModule> loadedModules = new ArrayList();
    private List<VoltskiyaModule> unloadedModules = new ArrayList();

    @Override
    public void onEnable() {
        instance = this;
        loadDependencies();
        setupLuckPerms();
        registerModules();
    }

    public static Voltskiya get() {
        return instance;
    }

    // Dynamically load dependencies start

    private void loadDependencies() {
        File dependencies = new File(getDataFolder(), "dependencies");
        if (!dependencies.exists()) dependencies.mkdirs();
        for (File child : dependencies.listFiles()) {
            if (!child.getName().endsWith(".jar")) return;
            try {
                System.out.println("attempting to load " + child.getName());
                loadDependency(child);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDependency(File file) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        URLClassLoader loader = (URLClassLoader) getClass().getClassLoader();
        method.invoke(loader, file.getAbsoluteFile().toURI().toURL());
    }

    //Dynamically load dependencies end


    // Module system start

    private void registerModules() {
        Reflections reflections = new Reflections("com.voltskiya.core", new SubTypesScanner(true));
        reflections.getSubTypesOf(VoltskiyaModule.class).forEach(moduleClass -> {
            VoltskiyaModule module;
            try {
                module = (VoltskiyaModule) moduleClass.getConstructors()[0].newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (module.shouldEnable()) {
                registerModule(module);
            } else {
                failedRegisterModule(module);
            }
        });
        getLogger().log(Level.INFO, "Loaded " + loadedModules.size() + " Voltskiya modules.");
        getLogger().log(Level.INFO, "Failed to load " + unloadedModules.size() + " Voltskiya modules.");
    }

    private void registerModule(VoltskiyaModule module) {
        module.startModule();
        loadedModules.add(module);
        getLogger().log(Level.INFO, "Registered Voltskiya Module: " + module.getName());
    }

    private void failedRegisterModule(VoltskiyaModule module) {
        unloadedModules.add(module);
        getLogger().log(Level.WARNING, "Voltskiya Module Did Not Load: " + module.getName());
    }

    private <T extends VoltskiyaModule> T getModule(Class<T> module) {
        for (VoltskiyaModule loadedModule : loadedModules) {
            if (loadedModule.getClass().isInstance(module)) {
                return (T) loadedModule;
            }
        }
        return null;
    }

    // Module system end


    // LuckPerms start

    private void setupLuckPerms() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            luckPerms = LuckPermsProvider.get();
        } else {
            getLogger().log(Level.WARNING, "LuckPerms is not enabled, some functions may be disabled.");
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
