package com.voltskiya.core.game.skill_points.skill_items;

import com.voltskiya.core.game.GameTagsNavigate;
import com.voltskiya.core.game.skill_points.UpdateSkills;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class VitalitySkillItem extends SkillItem {

    private static NamespacedKey key = GameTagsNavigate.SkillPointsTagsNavigate.skillVitality;
    private static String displayName = "Vitality";

    public VitalitySkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    protected NamespacedKey getKey() {
        return key;
    }

    @Override
    public void dealWithUpdate(Player player) {
        UpdateSkills.updateVitality(player);
    }

    @Override
    public int getXpCost(int newAttributeValue) {
        return 1;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
