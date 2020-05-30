package com.voltskiya.core.game.powertool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandAlias("power_tool")
public class PowerToolCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public PowerToolCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Subcommand("")
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = Bukkit.getPlayer(commandSender.getName());
        if (player == null) {
            commandSender.sendMessage("nope. you're not a player.");
            return false;
        }
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Nothing is in your hand.");
            return false;
        }
        ItemMeta mainMeta = mainHand.getItemMeta();
        if (mainMeta == null) {
            player.sendMessage(ChatColor.RED + "Could not get the item meta of that item.");
            return false;
        }

        int i = 0;
        NamespacedKey key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + i);
        String comamndToExecute = mainMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        while (comamndToExecute != null) {
            key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + ++i);
            comamndToExecute = mainMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }
        mainMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, String.join(" ", args));
        mainHand.setItemMeta(mainMeta);
        return true;
    }
}
