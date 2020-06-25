package com.voltskiya.core.mobs.checks.mechanics;

import com.voltskiya.core.mobs.scanning.SpawningEnvironment;
import com.voltskiya.core.mobs.checks.rules.SpawningRule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * is a list of spawning rules
 * there should be one of these for every region
 */
public abstract class SpawningMechanic {
    /**
     * If a new rule needs to be added for the same mob, make a new mob and a new rule
     * exampe: carno:forestRule --> forestCarno:forestCarnoRule, mountainCarno:mountainCarnoRule
     */
    private Map<String, SpawningRule> spawningRules = new HashMap<>();

    /**
     * determines if a mob could spawn given the environment and the spawning mechanics for this region
     *
     * @param mob         the mob that is being spawned
     * @param environment the environment around where we are questioning the spawning
     * @return true if the mob would be allowed to spawn, false otherwise
     */
    public boolean isSpawnable(String mob, SpawningEnvironment environment) {
        SpawningRule rule = spawningRules.get(mob);
        return rule != null && rule.isSpawnable(environment);
    }

    protected void putRule(String mob, SpawningRule rule) {
        spawningRules.put(mob, rule);
    }

    public Set<String> getSpawnableMobs() {
        return spawningRules.keySet();
    }
}
