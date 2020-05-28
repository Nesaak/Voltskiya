package com.voltskiya.core.chat;

import com.voltskiya.core.common.Permission;
import com.voltskiya.core.common.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.ChatColor.*;

public class ChatEvents implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        chat(event.getPlayer(), event.getMessage());
    }

    public void chat(Player sent, String message) {
        final String finalMessage = UserUtil.getDisplayName(sent) + GRAY + " >> " + WHITE + (sent.hasPermission(Permission.ColorChat) ? translateAlternateColorCodes('&', message) : message);

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(finalMessage);
        });
        Bukkit.getConsoleSender().sendMessage(finalMessage);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(DARK_GREEN + "[" + GREEN + "+" + DARK_GREEN + "] " + RESET + UserUtil.getDisplayName(event.getPlayer()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(DARK_RED + "[" + RED + "+" + DARK_RED+ "] " + RESET + UserUtil.getDisplayName(event.getPlayer()));
    }
}
