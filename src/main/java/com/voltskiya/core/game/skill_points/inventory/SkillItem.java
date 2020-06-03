package com.voltskiya.core.game.skill_points.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class SkillItem {
    public Material itemType;

    public SkillItem(Material itemType) {
        this.itemType = itemType;
    }

    public abstract void dealWithClick(Player player);

    public abstract int getXpCost();

    public abstract String getDisplayName();

    public abstract int getAttribute(Player player);
}
