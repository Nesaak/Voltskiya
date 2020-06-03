package com.voltskiya.core.game.skill_points.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NothingSkillItem extends SkillItem {
    private String displayName = "nothing";

    public NothingSkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    public void dealWithClick(Player player) {
        // do nothing
    }

    @Override
    public int getXpCost() {
        return 0;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
