package com.voltskiya.core.game.skill_points.inventory;

import com.voltskiya.core.game.GameTagsNavigate;
import com.voltskiya.core.game.skill_points.UpdateSkills;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class WalkSpeedSkillItem extends SkillItem {
    private String displayName = "Walk Speed";

    public WalkSpeedSkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    public void dealWithClick(Player player) {
        player.sendMessage("you clicked the walkspeed item");

        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        int speed = container.getOrDefault(GameTagsNavigate.SkillPointsTagsNavigate.skillSpeed, PersistentDataType.INTEGER, 0) + 1;
        container.set(GameTagsNavigate.SkillPointsTagsNavigate.skillSpeed, PersistentDataType.INTEGER, speed);

        UpdateSkills.updateSpeed(player);
    }

    @Override
    public int getXpCost() {
        return 1;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
