package com.voltskiya.core.admin;

import co.aikar.commands.InvalidCommandArgument;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;

import java.util.stream.Collectors;

public class AdminModule extends VoltskiyaModule {

    @Override
    public void enabled() {
        setupACFContexts();
        Voltskiya.get().getCommandManager().registerCommand(new VoltskiyaCommand());
    }

    @Override
    public String getName() {
        return "Admin";
    }

    public void setupACFContexts() {
        Voltskiya.get().getCommandManager().getCommandContexts().registerContext(VoltskiyaModule.class, context -> {
            VoltskiyaModule module = Voltskiya.get().getModule(context.popFirstArg());
            if (module != null) return module;
            throw new InvalidCommandArgument("Invalid Module Specified");
        });
        Voltskiya.get().getCommandManager().getCommandCompletions().registerAsyncCompletion("modules", context -> Voltskiya.get().getModules().stream().map(module -> module.getName()).collect(Collectors.toList()));
    }
}
