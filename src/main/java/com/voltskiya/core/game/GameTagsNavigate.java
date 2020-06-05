package com.voltskiya.core.game;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class GameTagsNavigate {
    public static void initialize(JavaPlugin plugin) {
        SkillPointsTagsNavigate.initialize(plugin);
    }

    public static class RottingTagsNavigate {
        public static final String LAST_CHECKED = "time-last-checked";
        public static final String ROTTING_COUNTDOWN = "rotting-countdown";
        public static final String ROTTING_DIR = "rotting";
        public static final String ROTTING_CHART = "rottingTimesChart";
        public static final String ROT_INTO_CHART = "rotIntoChart";
        public static final String YML_CHART = "chart";
        public static final String VANILLA = "vanilla";
        public static final String COOLER = "cooler";
        public static final String COOLER_ITEM = "container.cooler";
        public static final String FREEZER_ITEM = "container.freezer";
    }

    public static class PowertoolTagsNavigate {
        public static final String POWER_TOOL_TAG = "power-tool";
    }

    public static class SkillPointsTagsNavigate {
        public static NamespacedKey skillSpeed;
        public static NamespacedKey skillMelee;
        public static NamespacedKey skillVitality;
        public static NamespacedKey skillThirst;
        public static NamespacedKey skillStamina;
        public static NamespacedKey currentThirst;
        public static NamespacedKey currentStamina;

        public static void initialize(JavaPlugin plugin) {
            skillSpeed = new NamespacedKey(plugin, "skillSpeed");
            skillMelee = new NamespacedKey(plugin, "skillMelee");
            skillVitality = new NamespacedKey(plugin, "skillVitality");
            skillThirst = new NamespacedKey(plugin, "skillThirst");
            skillStamina = new NamespacedKey(plugin, "skillStamina");
            currentThirst = new NamespacedKey(plugin, "currentThirst");
            currentStamina = new NamespacedKey(plugin, "currentStamina");
        }
    }
}
