package com.voltskiya.core.game.skill_points;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.voltskiya.core.game.GameTagsNavigate;
import com.voltskiya.core.game.skill_points.inventory.SkillPointsGUI;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

@CommandAlias("skillpoints|sp")
public class SkillPointsCommand extends BaseCommand {

    @Default
    public void skillPoints(Player player) {
        new SkillPointsGUI(player);
    }

    @Subcommand("remove")
    @CommandPermission("skillpoints.temp")
    public void remove(Player player) {
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        container.remove(GameTagsNavigate.SkillPointsTagsNavigate.skillStamina);
        container.remove(GameTagsNavigate.SkillPointsTagsNavigate.skillThirst);
        container.remove(GameTagsNavigate.SkillPointsTagsNavigate.skillMelee);
        container.remove(GameTagsNavigate.SkillPointsTagsNavigate.skillSpeed);
        container.remove(GameTagsNavigate.SkillPointsTagsNavigate.skillVitality);
    }
}
