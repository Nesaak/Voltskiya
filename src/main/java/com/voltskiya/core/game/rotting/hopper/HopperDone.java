package com.voltskiya.core.game.rotting.hopper;

import com.voltskiya.core.game.rotting.IsRottable;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import java.util.ArrayList;
import java.util.List;

public class HopperDone {
    public boolean hopperDone;
    public List<InventoryMoveItemEvent> firstWaveCheckEvents= new ArrayList<>();
    public List<InventoryMoveItemEvent> secondWaveMergeEvents= new ArrayList<>();


    public HopperDone(InventoryMoveItemEvent event) {
        hopperDone = false;
        this.firstWaveCheckEvents.add(event);
        this.secondWaveMergeEvents.add(event);
    }

    public void add(InventoryMoveItemEvent event) {
        hopperDone = false;
        this.firstWaveCheckEvents.add(event);
        if (IsRottable.isRottable(event.getItem().getType()))
            this.secondWaveMergeEvents.add(event);
    }

    public void clear() {
        firstWaveCheckEvents = new ArrayList<>();
        secondWaveMergeEvents = new ArrayList<>();
    }
    public void clearFirst() {
        firstWaveCheckEvents = new ArrayList<>();
    }
}
