package com.voltskiya.core.mobs.spawning;

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

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimpleDiskMob) {
            SimpleDiskMob other = (SimpleDiskMob) o;
            return other.name.equals(name) && other.x == x && other.y == y && other.z == z;
        }
        return false;
    }
}
