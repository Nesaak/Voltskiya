package com.voltskiya.core.mobs.scan.rules;

import com.voltskiya.core.mobs.scan.SpawningEnvironment;

public class RuleForestCarno extends SpawningRule {
    public static RuleForestCarno instance = new RuleForestCarno();

    public static RuleForestCarno get() {
        return instance;
    }

    @Override
    public boolean isSpawnable(String mob, SpawningEnvironment environment) {
        return false;
    }
}
