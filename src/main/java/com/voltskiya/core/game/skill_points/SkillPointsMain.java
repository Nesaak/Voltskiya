package com.voltskiya.core.game.skill_points;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.game.skill_points.inventory.SkillPointClickListener;
import com.voltskiya.core.game.skill_points.listeners.SkillPointsPlayerJoin;
import com.voltskiya.core.game.skill_points.thirst.DirtyWaterRecipe;
import com.voltskiya.core.game.skill_points.thirst.ThirstDamageListener;
import com.voltskiya.core.game.skill_points.thirst.ThirstPlayerListener;
import com.voltskiya.core.game.skill_points.thirst.WaterFillListener;

public class SkillPointsMain {
    public static void enable() {
        Voltskiya plugin = Voltskiya.get();
        plugin.getCommandManager().registerCommand(new SkillPointsCommand());
        new SkillPointClickListener(plugin);
        new SkillPointsPlayerJoin(plugin);
        new ThirstDamageListener(plugin);
        new ThirstPlayerListener(plugin);
        new WaterFillListener(plugin);
        new DirtyWaterRecipe(plugin);
    }

}
