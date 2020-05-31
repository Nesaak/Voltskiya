package com.voltskiya.core.temperatures;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.common.Permission;
import com.voltskiya.core.temperatures.constants.NavigateArmor;
import com.voltskiya.core.temperatures.constants.NavigateBlocks;
import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import com.voltskiya.core.temperatures.constants.NavigatePotions;
import com.voltskiya.core.temperatures.constants.biomes.NavigateBiomes;
import com.voltskiya.core.temperatures.constants.results.NavigateResults;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@CommandAlias("temperature")
@CommandPermission(Permission.TEMPERATURES)
public class TemperatureCommands extends BaseCommand {
    private File dataFolder;

    public TemperatureCommands(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Subcommand("remove")
    @CommandCompletion("@players")
    public boolean remove(@NotNull CommandSender commandSender, @Single Player player) {
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        container.remove(NavigatePlayers.TEMPERATURE);
        container.remove(NavigatePlayers.LAST_WET);
        commandSender.sendMessage(ChatColor.GREEN + "You removed the temperatures from that player");
        return true;
    }

    @Subcommand("reload")
    public boolean reload(@NotNull CommandSender commandSender) {
        JavaPlugin plugin = Voltskiya.get();
        NavigatePlayers.initialize(plugin, dataFolder);
        NavigateBiomes.initialize(dataFolder);
        NavigateBlocks.initialize(dataFolder);
        NavigateArmor.initialize(dataFolder);
        NavigatePotions.initialize(dataFolder);
        NavigateResults.initialize(dataFolder);
        return true;
    }
}
