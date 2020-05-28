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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class Voltskiya extends JavaPlugin {

    private static Voltskiya instance;

    private LuckPerms luckPerms;
    private PaperCommandManager commandManager;

    private List<VoltskiyaModule> modules = new ArrayList();
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
            registerModule(module);
            if (module.shouldEnable()) {
                enableModule(module);
            }
        });
        getLogger().log(Level.INFO, "Loaded " + modules.stream().filter(module -> module.isEnabled()).collect(Collectors.toList()).size() + " Voltskiya modules.");
        getLogger().log(Level.INFO, "Failed to load " + modules.stream().filter(module -> !module.isEnabled()).collect(Collectors.toList()).size() + " Voltskiya modules.");
    }

    private void registerModule(VoltskiyaModule module) {
        modules.add(module);
        module.setEnabled(false);
        getLogger().log(Level.WARNING, "Registered Voltskiya Module " + module.getName());
    }

    public void enableModule(VoltskiyaModule module) {
        module.setEnabled(true);
        module.enabled();
        getLogger().log(Level.INFO, "Enabled Voltskiya Module: " + module.getName());
    }

    public <T extends VoltskiyaModule> T getModule(Class<T> moduleClass) {
        for (VoltskiyaModule module : modules) {
            if (module.getClass().isInstance(moduleClass)) {
                return (T) module;
            }
        }
        return null;
    }

    public VoltskiyaModule getModule(String name) {
        for (VoltskiyaModule module : modules) {
            if (module.getName().equalsIgnoreCase(name)) return module;
        }
        return null;
    }

    public List<VoltskiyaModule> getModules() {
        return modules;
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
            VoltskiyaModule module = getModule(context.popFirstArg());
            if (module != null) return module;
            throw new CommandException("Invalid Module Specified");
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("modules", context -> modules.stream().map(module -> module.getName()).collect(Collectors.toList()));
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    // ACF end
}
