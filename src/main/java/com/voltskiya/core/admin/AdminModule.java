package com.voltskiya.core.admin;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;

public class AdminModule implements VoltskiyaModule {

    @Override
    public void startModule() {
        Voltskiya.get().getCommandManager().registerCommand(new VoltskiyaCommand());
    }

    @Override
    public String getName() {
        return "Admin";
    }
}
