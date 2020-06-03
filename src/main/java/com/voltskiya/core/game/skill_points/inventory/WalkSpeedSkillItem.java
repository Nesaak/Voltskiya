package com.voltskiya.core.game.skill_points.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WalkSpeedSkillItem extends SkillItem {
    public WalkSpeedSkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    public void dealWithClick(Player player) {
        player.sendMessage("you clicked the walkspeed item");
    }
}
