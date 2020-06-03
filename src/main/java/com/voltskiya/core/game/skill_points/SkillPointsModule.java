package com.voltskiya.core.game.skill_points;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import com.voltskiya.core.game.skill_points.inventory.SkillPointClickListener;

import java.io.File;

public class SkillPointsModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        Voltskiya plugin = Voltskiya.get();
        File dataFolder = getDataFolder();

        plugin.getCommandManager().registerCommand(new SkillPointsCommand());
        new SkillPointClickListener(plugin);
    }

    @Override
    public String getName() {
        return "SkillPoints";
    }
}
