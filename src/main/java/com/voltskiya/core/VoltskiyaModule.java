package com.voltskiya.core;

import java.io.File;

public abstract class VoltskiyaModule {

    private boolean isEnabled;

    public abstract void enabled();

    public abstract String getName();

    protected void setEnabled(boolean enabled) {
        isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    boolean shouldEnable() {
        return true;
    }

    public File getDataFolder() {
        return new File(Voltskiya.get().getDataFolder(), getName().toLowerCase());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VoltskiyaModule) return obj.hashCode() == hashCode();
        return false;
    }
}
