package com.voltskiya.core.game.skill_points.thirst;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.game.GameTagsNavigate;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ThirstBar {
    private static final String THIRST_MODIFIER = "thirst";
    private static final long CHECK_INCREMENT = 1; //in ticks
    private static final int TICKS_PER_THIRST = 100;

    private static void calcAndDecrement(@NotNull Player player) {
        if (!player.isOnline())
            return;
        PersistentDataContainer container = player.getPersistentDataContainer();
        double thirst = getCurrentThirst(container);
        double totalThirst = getTotalThirst(container);
        double newThirst = thirst - CHECK_INCREMENT / (double) TICKS_PER_THIRST;
        if (newThirst < 0)
            newThirst = 0;
        container.set(GameTagsNavigate.SkillPointsTagsNavigate.currentThirst, PersistentDataType.DOUBLE, newThirst);
        setThirstBar(player, newThirst / totalThirst);
        scheduleDecrement(player);
    }

    public static void scheduleDecrement(@NotNull Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Voltskiya.get(), () -> calcAndDecrement(player), CHECK_INCREMENT);
    }

    private static void setThirstBar(Player player, double thirstPercentage) {
        @Nullable AttributeInstance genericArmor = player.getAttribute(Attribute.GENERIC_ARMOR);
        if (genericArmor == null)
            // idk what to do
            return;
        removeThirstModifier(genericArmor);
        genericArmor.addModifier(new AttributeModifier(THIRST_MODIFIER,
                thirstPercentage * 20,
                AttributeModifier.Operation.ADD_NUMBER));
    }


    public static void calc(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        double thirst = getCurrentThirst(container);
        double totalThirst = getTotalThirst(container);
        setThirstBar(player, thirst / totalThirst);
    }

    private static double getTotalThirst(PersistentDataContainer container) {
        return container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.skillThirst, PersistentDataType.INTEGER, 0) + 20;
    }

    private static double getCurrentThirst(PersistentDataContainer container) {
        double currentThirst = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.currentThirst, PersistentDataType.DOUBLE, -1D);
        if (currentThirst == -1) {
            container.set(GameTagsNavigate.SkillPointsTagsNavigate.currentThirst, PersistentDataType.DOUBLE, 20D);
            return 20;
        }
        return currentThirst;
    }

    public static boolean removeThirstModifier(AttributeInstance genericArmor) {
        boolean hadThirstArmor = false;
        @NotNull Collection<AttributeModifier> modifiers = genericArmor.getModifiers();
        for (AttributeModifier modifier : modifiers)
            if (modifier.getName().equals(THIRST_MODIFIER)) {
                genericArmor.removeModifier(modifier);
                hadThirstArmor = true;
            }
        return hadThirstArmor;
    }
}
