package com.voltskiya.core;

import co.aikar.commands.PaperCommandManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class Voltskiya extends JavaPlugin {

    private static Voltskiya instance;

    private LuckPerms luckPerms;
    private PaperCommandManager commandManager;

    private Map<VoltskiyaModule, Boolean> modules = new HashMap<>();
    private List<String> loadedJars = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        loadDependencies();
        setupACF();
        setupLuckPerms();
        registerModules();
    }

    public static Voltskiya get() {
        return instance;
    }

    // Dynamically load dependencies start

    private void loadDependencies() {
        getLogger().log(Level.INFO, "Starting dynamic dependency loading");
        File dependencies = new File(getDataFolder(), "dependencies");
        if (!dependencies.exists()) dependencies.mkdirs();
        for (File child : dependencies.listFiles()) {
            if (!child.getName().endsWith(".jar")) return;
            String depend = child.getName().replace(".jar", "");
            try {
                loadDependency(child);
                loadedJars.add(depend);
                getLogger().log(Level.INFO, "Loaded dependency: " + depend);
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Failed to load dependency: " + depend);
            }
        }
        getLogger().log(Level.INFO, "Finished dynamic dependency loading, loaded " + loadedJars.size() + " jars.");
    }

    private void loadDependency(File file) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        if (!method.isAccessible()) method.setAccessible(true);
        URLClassLoader loader = (URLClassLoader) getClass().getClassLoader();
        method.invoke(loader, file.getAbsoluteFile().toURI().toURL());
    }

    public boolean isLoaded(String dependency) {
        return loadedJars.contains(dependency);
    }

    public List<String> getLoadedJars() {
        return loadedJars;
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
                loadModule(module);
            } else {
                failedLoadModule(module);
            }
        });
        getLogger().log(Level.INFO, "Loaded " + modules.values().stream().filter(bool -> bool).collect(Collectors.toList()).size() + " Voltskiya modules.");
        getLogger().log(Level.INFO, "Failed to load " + modules.values().stream().filter(bool -> !bool).collect(Collectors.toList()).size() + " Voltskiya modules.");
    }

    private void failedLoadModule(VoltskiyaModule module) {
        modules.put(module, false);
        getLogger().log(Level.WARNING, "Voltskiya Module did not load: " + module.getName());
    }

    public void loadModule(VoltskiyaModule module) {
        module.startModule();
        modules.put(module, true);
        getLogger().log(Level.INFO, "Loaded Voltskiya Module: " + module.getName());
    }

    public <T extends VoltskiyaModule> T getModule(Class<T> moduleClass) {
        for (VoltskiyaModule module : modules.keySet()) {
            if (module.getClass().isInstance(moduleClass)) {
                return (T) module;
            }
        }
        return null;
    }

    public VoltskiyaModule getModule(String name) {
        for (VoltskiyaModule module : modules.keySet()) {
            if (module.getName().equalsIgnoreCase(name)) return module;
        }
        return null;
    }

    public boolean isLoaded(VoltskiyaModule module) {
        return modules.get(module);
    }

    public <T extends VoltskiyaModule> boolean isLoaded(Class<T> moduleClass) {
        return isLoaded(getModule(moduleClass));
    }

    public Set<VoltskiyaModule> getModules() {
        return modules.keySet();
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
        commandManager.getCommandContexts().registerContext(VoltskiyaModule.class, context -> {
            String name = context.popFirstArg();
            for (VoltskiyaModule module : modules.keySet()) {
                if (module.getName().equalsIgnoreCase(name)) return module;
            }
            throw new CommandException("Invalid Module Specified, " + name);
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("modules", context -> modules.keySet().stream().map(module -> module.getName()).collect(Collectors.toList()));
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    // ACF end
}
