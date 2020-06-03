package com.voltskiya.core.game.skill_points;

import com.voltskiya.core.game.GameTagsNavigate;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class UpdateSkills {
    public static void updateSpeed(Player player) {
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        int speed = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.skillSpeed, PersistentDataType.INTEGER, 0);
        player.setWalkSpeed(getSpeed(speed));

    }

    private static float getSpeed(int speedSkill) {
        return 1;
    }
}
