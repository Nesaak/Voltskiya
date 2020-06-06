package com.voltskiya.core.game.skill_points.skill_items;

import com.voltskiya.core.game.GameTagsNavigate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class StaminaSkillItem extends SkillItem {
    private static final String DISPLAY_NAME = "Stamina";
    private NamespacedKey key = GameTagsNavigate.SkillPointsTagsNavigate.skillStamina;

    public StaminaSkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    protected NamespacedKey getKey() {
        return key;
    }

    @Override
    public void dealWithUpdate(Player player) {
    }

    @Override
    public int getXpCost(int newAttributeValue) {
        return (int) Math.pow(7+newAttributeValue,1.37);
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }
}
