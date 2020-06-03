package com.voltskiya.core.game.skill_points;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.game.skill_points.inventory.SkillPointClickListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillPointsMain {
    public static void enable(JavaPlugin plugin) {

        Voltskiya.get().getCommandManager().registerCommand(new SkillPointsCommand());
        new SkillPointClickListener(plugin);
    }

}
