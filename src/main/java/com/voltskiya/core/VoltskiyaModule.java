package com.voltskiya.core;

public interface VoltskiyaModule {

    void startModule();

    String getName();

    default boolean shouldEnable() {
        return true;
    }

}
