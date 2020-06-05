package com.voltskiya.core.game.skill_points;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.voltskiya.core.game.skill_points.inventory.SkillPointsGUI;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

@CommandAlias("skillpoints|sp")
public class SkillPointsCommand extends BaseCommand {

    @Default
    public void skillPoints(Player player) {
        new SkillPointsGUI(player);
    }
}
