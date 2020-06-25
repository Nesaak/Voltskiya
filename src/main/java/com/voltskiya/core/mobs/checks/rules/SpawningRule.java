package com.voltskiya.core.mobs.checks.rules;

import com.voltskiya.core.mobs.scanning.SpawningEnvironment;

/**
 * is a rule for if a certain mob can spawn
 */
public abstract class SpawningRule {
    /**
     * The biome nor the mob is verified in this method
     *
     * @param environment the environment that is in question of whether it can spawn something
     * @return whether the mob would be allowed to spawn in this environment
     */
    public abstract boolean isSpawnable(SpawningEnvironment environment);
}
