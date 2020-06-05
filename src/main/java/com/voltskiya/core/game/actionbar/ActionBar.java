package com.voltskiya.core.game.actionbar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class ActionBar {
    private static final int DEFAULT_TIME = 20 * 60 * 3;
    private static final int SKIP_TIME = 20 * 2;
    private static JavaPlugin plugin;
    private static HashMap<UUID, Integer> timeLeft;
    private static HashMap<UUID, TextComponent> messages;

    public static void initialize(JavaPlugin pl) {
        plugin = pl;
        timeLeft = new HashMap<>();
        messages = new HashMap<>();
    }

    public static void sendLongActionBar(Player player, TextComponent message) {
        UUID playerUID = player.getUniqueId();
        timeLeft.put(playerUID, DEFAULT_TIME);
        messages.put(playerUID, message);
        sendLongActionBar(player);
    }

    public static void sendLongActionBar(Player player) {
        UUID playerUID = player.getUniqueId();
        if (!messages.containsKey(playerUID) || !timeLeft.containsKey(playerUID)) {
            timeLeft.remove(playerUID);
            messages.remove(playerUID);
            return;
        }
        TextComponent message = messages.get(playerUID);
        int ticks = timeLeft.get(playerUID) - SKIP_TIME;
        timeLeft.put(playerUID, ticks);

        if (ticks <= 0) {
            timeLeft.remove(player.getUniqueId());
            messages.remove(playerUID);
            return;
        }
        // send an action bar and send another later
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> sendLongActionBar(player), SKIP_TIME);
    }

    public static void remove(Player player) {
        timeLeft.remove(player.getUniqueId());
        messages.remove(player.getUniqueId());
    }
}
