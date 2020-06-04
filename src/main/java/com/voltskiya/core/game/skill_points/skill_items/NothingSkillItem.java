package com.voltskiya.core.game.skill_points.skill_items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class NothingSkillItem extends SkillItem {
    private String displayName = "nothing";

    public NothingSkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    protected NamespacedKey getKey() {
        return null;
    }

    @Override
    public void dealWithUpdate(Player player) {
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

    @Override
    public int getAttribute(Player player) {
        return 0;
    }
}
