package com.voltskiya.core.admin;

import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;

public class AdminModule extends VoltskiyaModule {

    @Override
    public void enabled() {
        Voltskiya.get().getCommandManager().registerCommand(new VoltskiyaCommand());
    }

    @Override
    public String getName() {
        return "Admin";
    }
}
