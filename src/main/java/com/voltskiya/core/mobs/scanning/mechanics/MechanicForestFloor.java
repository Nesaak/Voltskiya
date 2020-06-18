package com.voltskiya.core.mobs.scanning.mechanics;


import com.voltskiya.core.mobs.scanning.rules.RuleForestCarno;

import static com.voltskiya.core.mobs.scanning.basedata.MobNames.FOREST_CARNO;


public class MechanicForestFloor extends SpawningMechanic {
    public MechanicForestFloor() {
        putRule(FOREST_CARNO, RuleForestCarno.get());
    }
}
