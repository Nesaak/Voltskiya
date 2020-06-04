package com.voltskiya.core.game.skill_points.stamina;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.game.GameTagsNavigate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerAirObject {
    private static final short INCREMENT = 5;
    private static final float TICKS_PER_DECREMENT = 100F;
    private static final float SLEEPING_MODIFIER = 5F;
    private static final float SNEAKING_MODIFIER = 1.5F;
    private static final float WALKING_MOIDIFIER = 1.1F;
    private static final float SPRINTING_MODIFIER = -2F;
    private static final float SWIMMING_MODIFIER = -0.75F;

    private List<Double> airToAttribute = new ArrayList<>(5);

    // might be useful for the future?
    private void giveObjectAir(Player player, double amount) {
        this.airToAttribute.add(amount);
    }

    public void doObjectAirTick(Player player) {
        if (!player.isOnline())
            return;
        // time to do air
        doAirChange(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Voltskiya.get(), () -> doObjectAirTick(player), INCREMENT);
    }

    private void doAirChange(Player player) {
        // reset the counter
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        double currentAir = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.currentStamina, PersistentDataType.DOUBLE, 10D);
        double maxStamina = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.skillStamina, PersistentDataType.DOUBLE, 0D) + 10;


        while (!airToAttribute.isEmpty()) {
            currentAir += airToAttribute.remove(0);
        }
        float timeWaited = INCREMENT / TICKS_PER_DECREMENT;
        if (player.isSleeping()) {
            currentAir += SLEEPING_MODIFIER * timeWaited;
        } else if (player.isSneaking()) {
            currentAir += SNEAKING_MODIFIER * timeWaited;
        } else if (!player.isSprinting()) {
            currentAir += WALKING_MOIDIFIER * timeWaited;
        } else if (player.isSwimming()) {
            currentAir += SWIMMING_MODIFIER * timeWaited;
        } else {
            currentAir += SPRINTING_MODIFIER * timeWaited;
        }

        // normalize the air
        if (currentAir < 0)
            currentAir = 0D;
        else if (currentAir > maxStamina) {
            currentAir = maxStamina;
        }
        System.out.println(currentAir);
        container.set(GameTagsNavigate.SkillPointsTagsNavigate.currentStamina, PersistentDataType.DOUBLE, currentAir);
    }
}
