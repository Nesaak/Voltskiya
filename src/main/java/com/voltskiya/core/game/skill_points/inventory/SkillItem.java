package com.voltskiya.core.game.skill_points.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class SkillItem {
    public Material itemType;
    private static String badXpCostMessage = ChatColor.RED + "You don't have enough xp to upgrade that skill point";

    public SkillItem(Material itemType) {
        this.itemType = itemType;
    }

    public String getBadXpCost() {
        return badXpCostMessage;
    }

    public abstract void dealWithClick(Player player);

    public abstract int getXpCost();

    public abstract String getDisplayName();

    public abstract int getAttribute(Player player);

}
