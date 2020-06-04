package com.voltskiya.core.game.skill_points;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.voltskiya.core.game.skill_points.inventory.SkillPointsGUI;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@CommandAlias("skillpoints|sp")
public class SkillPointsCommand extends BaseCommand {
    private static final AttributeModifier thirst = new AttributeModifier("thirst", 100, AttributeModifier.Operation.ADD_NUMBER);

    @Default
    public void skillPoints(Player player) {
        new SkillPointsGUI(player);
    }

    @Subcommand("armor")
    public void armor(Player player) {
        @NotNull Collection<AttributeModifier> modifiers = player.getAttribute(Attribute.GENERIC_ARMOR).getModifiers();
        for (AttributeModifier modifier : modifiers)
            if (modifier.getName().equals("thirst"))
                player.getAttribute(Attribute.GENERIC_ARMOR).removeModifier(modifier);
        player.getAttribute(Attribute.GENERIC_ARMOR).addModifier(thirst);
    }

    @Subcommand("undo")
    public void undo(Player player) {
        @NotNull Collection<AttributeModifier> modifiers = player.getAttribute(Attribute.GENERIC_ARMOR).getModifiers();
        for (AttributeModifier modifier : modifiers)
            if (modifier.getName().equals("thirst"))
                player.getAttribute(Attribute.GENERIC_ARMOR).removeModifier(modifier);
    }
}
