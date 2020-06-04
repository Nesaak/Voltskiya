package com.voltskiya.core.game.skill_points;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.game.skill_points.inventory.SkillPointClickListener;
import com.voltskiya.core.game.skill_points.listeners.SkillPointsPlayerJoin;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillPointsMain {
    public static void enable() {
        Voltskiya plugin = Voltskiya.get();
        plugin.getCommandManager().registerCommand(new SkillPointsCommand());
        new SkillPointClickListener(plugin);
        new SkillPointsPlayerJoin(plugin);
    }

}
