package com.voltskiya.core.game.skill_points;

import com.voltskiya.core.game.GameTagsNavigate;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * make sure no other methods in this class exist that return void
 * besides update methods
 */
public class UpdateSkills {
    /**
     * calls all other methods in this class that are not this method
     *
     * @param player the player who's skill needs to be updated
     */
    public static void updateAll(Player player) {
        Method[] methods = UpdateSkills.class.getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getReturnType().equals(void.class) && !method.getName().equals("updateAll")) {
                    method.invoke(null, player);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace(); // complain about the error the programmer made
            }
        }
    }

    public static void updateSpeed(Player player) {
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        int speed = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.skillSpeed, PersistentDataType.INTEGER, 0);
        @Nullable AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attribute != null)
            attribute.setBaseValue(getSpeed(speed));
    }

    public static void updateMelee(Player player) {
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        int melee = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.skillMelee, PersistentDataType.INTEGER, 0);
        @Nullable AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attribute != null)
            attribute.setBaseValue(getMelee(melee));
    }

    public static void updateVitality(Player player) {
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        int vitality = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.skillVitality, PersistentDataType.INTEGER, 0);
        @Nullable AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null)
            attribute.setBaseValue(getVitality(vitality));
    }


    private static double getSpeed(int speedSkill) {
        return Math.max(2, ((double) speedSkill) / 25); // normal is ???
    }

    private static double getMelee(int melee) {
        return ((double) melee) / 5; // normal is ???
    }

    private static double getVitality(int vitality) {
        return ((double) vitality) / 5 + 20; // normal is 20
    }
}
