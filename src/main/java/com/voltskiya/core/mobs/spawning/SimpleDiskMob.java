package com.voltskiya.core.mobs.spawning;

import com.voltskiya.core.mobs.scanning.CheapLocation;

public class SimpleDiskMob {
    public String name;
    public int x;
    public int y;
    public int z;

    public SimpleDiskMob(String name, int x, int y, int z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SimpleDiskMob(String name, CheapLocation spawnableLocation) {
        this.name = name;
        this.x = spawnableLocation.x;
        this.y = spawnableLocation.y;
        this.z = spawnableLocation.z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimpleDiskMob) {
            SimpleDiskMob other = (SimpleDiskMob) o;
            return other.name.equals(name) && other.x == x && other.y == y && other.z == z;
        }
        return false;
    }
}
