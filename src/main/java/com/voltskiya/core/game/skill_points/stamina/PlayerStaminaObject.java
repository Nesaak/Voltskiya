package com.voltskiya.core.game.skill_points.stamina;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.game.GameTagsNavigate;
import com.voltskiya.core.game.actionbar.ActionBarRun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PlayerStaminaObject {
    private static final short INCREMENT = 5;
    private static final float TICKS_PER_DECREMENT = 100F;
    private static final float SLEEPING_MODIFIER = 5F;
    private static final float SNEAKING_MODIFIER = 1.5F;
    private static final float WALKING_MOIDIFIER = 1.1F;
    private static final float SPRINTING_MODIFIER = -2F;
    private static final float SWIMMING_MODIFIER = -0.75F;

    private List<Double> airToAttribute = new ArrayList<>(5);
    private short lastStaminaSize = -1;

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
        double currentStamina = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.currentStamina, PersistentDataType.DOUBLE, 10D);
        double maxStamina = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.skillStamina, PersistentDataType.DOUBLE, 0D) + 10;


        while (!airToAttribute.isEmpty()) {
            currentStamina += airToAttribute.remove(0);
        }
        float timeWaited = INCREMENT / TICKS_PER_DECREMENT;
        if (player.isSleeping()) {
            currentStamina += SLEEPING_MODIFIER * timeWaited;
        } else if (player.isSneaking()) {
            currentStamina += SNEAKING_MODIFIER * timeWaited;
        } else if (!player.isSprinting()) {
            currentStamina += WALKING_MOIDIFIER * timeWaited;
        } else if (player.isSwimming()) {
            currentStamina += SWIMMING_MODIFIER * timeWaited;
        } else {
            currentStamina += SPRINTING_MODIFIER * timeWaited;
        }

        // normalize the air
        if (currentStamina < 0)
            currentStamina = 0D;
        else if (currentStamina > maxStamina) {
            currentStamina = maxStamina;
        }
        container.set(GameTagsNavigate.SkillPointsTagsNavigate.currentStamina, PersistentDataType.DOUBLE, currentStamina);
        double percentageStamina = currentStamina / maxStamina;
        short staminaSize = (short) (ActionBarRun.STAMINA_BAR_SIZE - percentageStamina * ActionBarRun.STAMINA_BAR_SIZE);
        if (staminaSize != lastStaminaSize) {
            ActionBarRun.updateOnce(player);
        }
        lastStaminaSize = staminaSize;
    }
}
