package com.voltskiya.core.game.skill_points.no_sprint;

public class NoSprintMemory {
    protected int foodLevel;
    protected NoSprintReason reason;

    public NoSprintMemory(int foodLevel, NoSprintReason reason) {
        this.foodLevel = foodLevel;
        this.reason = reason;
    }
}
