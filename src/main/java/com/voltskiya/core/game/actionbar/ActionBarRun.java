package com.voltskiya.core.game.actionbar;

import com.voltskiya.core.game.GameTagsNavigate;
import com.voltskiya.core.temperatures.constants.NavigatePlayers;
import com.voltskiya.core.temperatures.constants.results.NavigateResults;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Comparator;


public class ActionBarRun implements Listener {
    private JavaPlugin plugin;
    private static ActionBarRun instance;
    private static TextComponent seperator = new TextComponent();
    public static final short STAMINA_BAR_SIZE = 30;

    static {
        seperator.setText("          ");
        seperator.setColor(ChatColor.RESET);
    }

    public ActionBarRun(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void updateOnce(Player player) {
        TextComponent message = instance.getMessage(player);
        ActionBar.sendLongActionBar(player, message);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> updateActionBar(player), 50); // let the player log in
    }

    private void updateActionBar(Player player) {
        // stop when the player logs off
        if (player.isOnline()) {
            TextComponent message = getMessage(player);
            ActionBar.sendLongActionBar(player, message);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> updateActionBar(player), 80);
        }
    }

    private TextComponent getMessage(Player player) {
        double currentPlayerTemperature = player.getPersistentDataContainer().getOrDefault(NavigatePlayers.TEMPERATURE, PersistentDataType.DOUBLE, 0D);

        ArrayList<Integer> thresholds = new ArrayList<>(NavigateResults.tempToResult.keySet());
        thresholds.sort(Comparator.comparingInt(o -> o));
        TextComponent temperatureMessage = new TextComponent();
        for (Integer threshold : thresholds) {
            if (currentPlayerTemperature < threshold) {
                if (currentPlayerTemperature < -110) {
                    temperatureMessage.setColor(NavigatePlayers.veryColdColor);
                } else if (currentPlayerTemperature < -25) {
                    temperatureMessage.setColor(NavigatePlayers.coldColor);
                } else if (currentPlayerTemperature < 40) {
                    temperatureMessage.setColor(NavigatePlayers.normalColor);
                } else if (currentPlayerTemperature < 120) {
                    temperatureMessage.setColor(NavigatePlayers.hotColor);
                } else {
                    temperatureMessage.setColor(NavigatePlayers.veryHotColor);
                }
            }
        }
        temperatureMessage.setText(String.format("(%s\u00B0C)", ((int) currentPlayerTemperature / 3)));

        double currentPlayerStamina = player.getPersistentDataContainer().getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.currentStamina, PersistentDataType.DOUBLE, 10D);
        double maxPlayerStamina = player.getPersistentDataContainer().getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.skillStamina, PersistentDataType.INTEGER, 0) + 10;
        double percentageStamina = currentPlayerStamina / maxPlayerStamina;

        short staminaSize = (short) (STAMINA_BAR_SIZE - percentageStamina * STAMINA_BAR_SIZE);
        StringBuilder emptyStaminaString = new StringBuilder();
        for (int i = 0; i < staminaSize; i++) {
            emptyStaminaString.append('|');
        }
        staminaSize = (short) Math.ceil(percentageStamina * STAMINA_BAR_SIZE);
        StringBuilder fullStaminaString = new StringBuilder();
        for (int i = 0; i < staminaSize; i++) {
            fullStaminaString.append('|');
        }


        TextComponent emptyStamina = new TextComponent();
        emptyStamina.setColor(ChatColor.RED);
        emptyStamina.setText(emptyStaminaString.toString());

        TextComponent fullStamina = new TextComponent();
        fullStamina.setColor(ChatColor.GREEN);
        fullStamina.setText(fullStaminaString.toString());


        return new TextComponent(temperatureMessage, seperator, emptyStamina, fullStamina);
    }
}