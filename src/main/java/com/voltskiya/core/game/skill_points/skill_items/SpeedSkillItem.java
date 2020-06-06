package com.voltskiya.core.game.skill_points.skill_items;

import com.voltskiya.core.game.GameTagsNavigate;
import com.voltskiya.core.game.skill_points.UpdateSkills;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class SpeedSkillItem extends SkillItem {
    private String displayName = "Walk Speed";
    private NamespacedKey key = GameTagsNavigate.SkillPointsTagsNavigate.skillSpeed;

    public SpeedSkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    protected NamespacedKey getKey() {
        return key;
    }

    @Override
    public void dealWithUpdate(Player player) {
        UpdateSkills.updateSpeed(player);
    }

    @Override
    public int getXpCost(int newAttributeValue) {
        return (int) Math.pow(7+newAttributeValue,1.37);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
