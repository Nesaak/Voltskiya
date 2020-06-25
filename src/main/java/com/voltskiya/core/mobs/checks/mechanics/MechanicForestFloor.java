package com.voltskiya.core.mobs.checks.mechanics;


import com.voltskiya.core.mobs.checks.rules.RuleForestCarno;

import static com.voltskiya.core.mobs.checks.basedata.MobNames.FOREST_CARNO;


public class MechanicForestFloor extends SpawningMechanic {
    public MechanicForestFloor() {
        putRule(FOREST_CARNO, RuleForestCarno.get());
    }
}
