package com.voltskiya.core.game.skill_points.inventory;

import com.voltskiya.core.game.GameTagsNavigate;
import com.voltskiya.core.game.skill_points.UpdateSkills;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class WalkSpeedSkillItem extends SkillItem {
    private String displayName = "Walk Speed";
    private NamespacedKey key = GameTagsNavigate.SkillPointsTagsNavigate.skillSpeed;

    public WalkSpeedSkillItem(Material itemType) {
        super(itemType);
    }

    @Override
    public void dealWithClick(Player player) {

        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        int speed = container.getOrDefault(key, PersistentDataType.INTEGER, 0) + 1;
        container.set(key, PersistentDataType.INTEGER, speed);

        UpdateSkills.updateSpeed(player);
        player.sendMessage("Your speed has been updated");
    }

    @Override
    public int getXpCost() {
        return 1;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getAttribute(Player player) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 0);
    }
}
