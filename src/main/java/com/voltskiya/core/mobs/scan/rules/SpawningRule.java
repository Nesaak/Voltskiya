package com.voltskiya.core.mobs.scan.rules;

import com.voltskiya.core.mobs.scan.SpawningEnvironment;

/**
 * is a rule for if a certain mob can spawn
 */
public abstract class SpawningRule {
    public abstract boolean isSpawnable(String mob, SpawningEnvironment environment);
}
