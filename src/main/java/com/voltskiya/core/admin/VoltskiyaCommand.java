package com.voltskiya.core.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.voltskiya.core.Voltskiya;
import com.voltskiya.core.VoltskiyaModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

@CommandPermission("voltskiya.admin")
@CommandAlias("voltskiya|volt")
public class VoltskiyaCommand extends BaseCommand {

    @Subcommand("dependencies")
    public void dependencies(CommandSender sender) {
        StringBuilder builder = new StringBuilder();
        Voltskiya.get().getLoadedJars().forEach(jar -> builder.append(", " + jar));
        String prettyList = builder.toString().replaceFirst(",", "");
        sender.sendMessage(ChatColor.GREEN + "Currently loaded dependencies:" + prettyList);
    }

    @Subcommand("modules")
    public class ModuleCommand {

        @Subcommand("list")
        public void list(CommandSender sender) {
            StringBuilder builder = new StringBuilder();
            Voltskiya.get().getLoadedJars().forEach(jar -> builder.append(", " + jar));
            String prettyList = builder.toString().replaceFirst(",", "");
            sender.sendMessage(ChatColor.GREEN + "Currently loaded modules:" + prettyList);
        }

        @CommandCompletion("@modules")
        @Subcommand("enable")
        public void enable(CommandSender sender, VoltskiyaModule module) {
            if (Voltskiya.get().isLoaded(module)) {
                throw new CommandException("That module is already enabled.");
            }
            Voltskiya.get().loadModule(module);
        }

    }
}
