package com.voltskiya.core.mobs.scan.mechanics;


import com.voltskiya.core.mobs.scan.rules.RuleForestCarno;

import static com.voltskiya.core.mobs.scan.basedata.MobNames.FOREST_CARNO;


public class MechanicForestFloor extends SpawningMechanic {
    public MechanicForestFloor() {
        putRule(FOREST_CARNO, RuleForestCarno.get());
    }
}
