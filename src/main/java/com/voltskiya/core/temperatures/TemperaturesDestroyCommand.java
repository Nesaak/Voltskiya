package com.voltskiya.core.temperatures;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

@CommandAlias("temperature")
public class TemperaturesDestroyCommand extends BaseCommand {
    @Subcommand("remove")
    public boolean removeTemp(@NotNull CommandSender commandSender, @Single String playerName) {
        Player player = Bukkit.getPlayer(playerName);
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
