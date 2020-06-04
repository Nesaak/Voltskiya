package com.voltskiya.core.game.skill_points.skill_items;

import com.voltskiya.core.game.GameTagsNavigate;
import com.voltskiya.core.game.skill_points.UpdateSkills;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class ThirstSkillItem extends SkillItem {
    private NamespacedKey key = GameTagsNavigate.SkillPointsTagsNavigate.skillMelee;
    private static String displayName = "Thirst";

    public ThirstSkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    protected NamespacedKey getKey() {
        return key;
    }

    @Override
    public void dealWithUpdate(Player player) {
        UpdateSkills.updateThirst(player);
    }

    @Override
    public int getXpCost(int newAttributeValue) {
        return newAttributeValue;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
