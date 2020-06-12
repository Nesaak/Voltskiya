package com.voltskiya.core.mobs.scan.mechanics;


import com.voltskiya.core.mobs.scan.rules.RuleForestCarno;

import static com.voltskiya.core.mobs.scan.basedata.MobNames.*;

public class MechanicRedWoodsFloor extends SpawningMechanic {
    public MechanicRedWoodsFloor() {
        addRule(FOREST_CARNO, RuleForestCarno.get());
    }
}
