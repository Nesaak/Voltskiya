package com.voltskiya.core.temperatures.constants;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class NavigatePlayers {

    public static final Double INITIAL_TEMPERATURE = 0.0;
    public static int BLOCK_IMPACT_DISTANCE = 7;
    public static long CHECK_TIME = 50;
    public static double TEMPERATURE_CHANGE_RATE = 0.035;
    public static long LOGIN_DELAY = 20;
    private static final String CHECK_TIME_PATH = "defaultTimeToUpdate";
    private static final String BLOCK_IMPACT_DISTANCE_PATH = "blockImpactDistance";
    private static final String LOGIN_DELAY_PATH = "loginDelayBeforeTemp";
    private static final String TEMPERATURE_CHANGE_RATE_PATH = "temperatureChangeRate";
    public static final String PLUGIN_NAME = "[Temperatures]";
    public static NamespacedKey LAST_WET;
    public static NamespacedKey TEMPERATURE; // it's pretty much final so we're using final's naming convention
    private static final String CONFIG_PATH = "config.yml";

    public static TextComponent veryHotMessage;
    public static TextComponent hotMessage;
    public static TextComponent normalMessage;
    public static TextComponent coldMessage;
    public static TextComponent veryColdMessage;
    public static TextComponent normalContinuedMessage;

    public static ChatColor veryHotColor = ChatColor.DARK_RED;
    public static ChatColor hotColor = ChatColor.RED;
    public static ChatColor normalColor = ChatColor.WHITE;
    public static ChatColor coldColor = ChatColor.DARK_AQUA;
    public static ChatColor veryColdColor = ChatColor.DARK_BLUE;

    public static void initialize(JavaPlugin plugin, File dataFolder) {
        TEMPERATURE = new NamespacedKey(plugin, "current-temperature");
        LAST_WET = new NamespacedKey(plugin, "last-wet");

        veryHotMessage = new TextComponent();
        veryHotMessage.setColor(ChatColor.DARK_RED);
        veryHotMessage.setText("You're burning up! Cool down, quickly! (%d\u00B0C)");

        hotMessage = new TextComponent();
        hotMessage.setColor(ChatColor.RED);
        hotMessage.setText("You're hot. You should find some place to cool down soon. (%d\u00B0C)");

        normalMessage = new TextComponent();
        normalMessage.setColor(ChatColor.WHITE);
        normalMessage.setText("You're returning to a normal temperature. (%d\u00B0C)");

        normalContinuedMessage = new TextComponent();
        normalContinuedMessage.setColor(ChatColor.WHITE);
        normalContinuedMessage.setText("You're at a normal temperature. (%d\u00B0C)");

        coldMessage = new TextComponent();
        coldMessage.setColor(ChatColor.DARK_AQUA);
        coldMessage.setText("You're cold. You should find some warmth soon. (%d\u00B0C)");

        veryColdMessage = new TextComponent();
        veryColdMessage.setColor(ChatColor.DARK_BLUE);
        veryColdMessage.setText("You're freezing! Find some warmth, quickly! (%d\u00B0C)");

        File file = new File(dataFolder + File.separator + CONFIG_PATH);
        if (!file.exists()) {
            try {
                boolean success = file.createNewFile();
                if (!success) {
                    System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, CONFIG_PATH));
                    return;
                }

            } catch (IOException e) {
                System.err.println(String.format("%s There was an error making the file \"%s\"", NavigatePlayers.PLUGIN_NAME, CONFIG_PATH));
                return;
            }
        }
        YamlConfiguration configOrig = YamlConfiguration.loadConfiguration(file);

        TEMPERATURE_CHANGE_RATE = configOrig.getDouble(TEMPERATURE_CHANGE_RATE_PATH);
        CHECK_TIME = Math.max(5, configOrig.getInt(CHECK_TIME_PATH)); // make it so you can't crash your server if this is empty
        BLOCK_IMPACT_DISTANCE = configOrig.getInt(BLOCK_IMPACT_DISTANCE_PATH);
        LOGIN_DELAY = configOrig.getInt(LOGIN_DELAY_PATH);
    }
}
