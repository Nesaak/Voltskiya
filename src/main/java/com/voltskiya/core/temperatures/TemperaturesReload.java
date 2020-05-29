package com.voltskiya.core.temperatures;

import com.voltskiya.core.temperatures.constants.NavigateArmor;
import com.voltskiya.core.temperatures.constants.NavigateBlocks;
import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import com.voltskiya.core.temperatures.constants.NavigatePotions;
import com.voltskiya.core.temperatures.constants.biomes.NavigateBiomes;
import com.voltskiya.core.temperatures.constants.results.NavigateResults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class TemperaturesReload implements CommandExecutor {
    private final JavaPlugin plugin;

    public TemperaturesReload(JavaPlugin plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("temperature_reload");
        if (command == null) {
            System.err.println(NavigatePlayers.PLUGIN_NAME + " could not get command: temperature_reload");
            return;
        }
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        NavigatePlayers.initialize(plugin);
        NavigateBiomes.initialize(plugin);
        NavigateBlocks.initialize(plugin);
        NavigateArmor.initialize(plugin);
        NavigatePotions.initialize(plugin);
        NavigateResults.initialize(plugin);
        return true;
    }
}
