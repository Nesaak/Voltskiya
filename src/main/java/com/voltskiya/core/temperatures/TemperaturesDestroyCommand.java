package com.voltskiya.core.temperatures;

import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class TemperaturesDestroyCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public TemperaturesDestroyCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("temperature_remove");
        if (command == null) {
            System.err.println(NavigatePlayers.PLUGIN_NAME + " could not get command: temperature_remove");
            return;
        }
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length != 1) {
            commandSender.sendMessage("Include only the player's name in your command arguments.");
            return false;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage("That player does not exist right now.");
            return false;
        }
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        container.remove(NavigatePlayers.TEMPERATURE);
        container.remove(NavigatePlayers.LAST_WET);
        commandSender.sendMessage(ChatColor.GREEN + "You removed the temperatures from that player");
        return true;
    }
}
