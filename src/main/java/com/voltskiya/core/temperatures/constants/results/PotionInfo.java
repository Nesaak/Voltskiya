package com.voltskiya.core.temperatures.constants.results;

import org.bukkit.configuration.ConfigurationSection;

public class PotionInfo {
    public final int potionDuration;
    public final int potionLevel;
    public final int nextCheck;

    private static final String YML_DURATION = "duration";
    private static final String YML_LEVEL = "level";
    private static final String YML_IMMUNITY = "nextCheck";

    public PotionInfo(ConfigurationSection config) {
        potionDuration = config.getInt(YML_DURATION);
        potionLevel = config.getInt(YML_LEVEL);
        nextCheck = config.getInt(YML_IMMUNITY);
    }
}
