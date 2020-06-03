package com.voltskiya.core.game.skill_points;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;

import java.io.File;

public class SkillPointsModule extends VoltskiyaModule {
    @Override
    public void enabled() {
        Voltskiya plugin = Voltskiya.get();
        File dataFolder = getDataFolder();

        plugin.getCommandManager().registerCommand(new SkillPointsCommand());

    }

    @Override
    public String getName() {
        return "SkillPoints";
    }
}
