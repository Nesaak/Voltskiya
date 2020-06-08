package com.voltskiya.core.game.skill_points;

import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

import static com.voltskiya.core.game.skill_points.UpdateSkills.SKILL_MODIFIER_NAME;

public class SkillPointsUtils {
    protected static void removeModifier(AttributeInstance attribute) {
        for (AttributeModifier modifier : attribute.getModifiers()) {
            if (modifier.getName().equals(SKILL_MODIFIER_NAME)) {
                attribute.removeModifier(modifier);
            }
        }
    }
}
