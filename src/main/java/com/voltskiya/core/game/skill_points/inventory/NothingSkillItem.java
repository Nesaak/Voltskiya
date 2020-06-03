package com.voltskiya.core.game.skill_points.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NothingSkillItem extends SkillItem{
    public NothingSkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    public void dealWithClick(Player player) {
        // do nothing
    }
}
