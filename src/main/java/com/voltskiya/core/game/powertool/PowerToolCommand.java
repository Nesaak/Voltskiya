package com.voltskiya.core.game.powertool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

@CommandAlias("power_tool")
public class PowerToolCommand extends BaseCommand {
    private JavaPlugin plugin;

    public PowerToolCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void powerTool(Player player, String args) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Nothing is in your hand.");
            return;
        }
        ItemMeta mainMeta = mainHand.getItemMeta();
        if (mainMeta == null) {
            player.sendMessage(ChatColor.RED + "Could not get the item meta of that item.");
            return;
        }

        int i = 0;
        NamespacedKey key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + i);
        String comamndToExecute = mainMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        while (comamndToExecute != null) {
            key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + ++i);
            comamndToExecute = mainMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }
        mainMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, args);
        mainHand.setItemMeta(mainMeta);
    }

    @Subcommand("list")
    public void list(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Nothing is in your hand.");
            return;
        }
        ItemMeta mainMeta = mainHand.getItemMeta();
        if (mainMeta == null) {
            player.sendMessage(ChatColor.RED + "Could not get the item meta of that item.");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Here is a list of all the commands on the powertool in your hand:");
        int i = 0;
        NamespacedKey key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + i);
        String comamndToExecute = mainMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        while (comamndToExecute != null) {
            player.sendMessage(ChatColor.DARK_GREEN + comamndToExecute);
            key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + ++i);
            comamndToExecute = mainMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        }
    }

    @Subcommand("clear")
    public void clear(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Nothing is in your hand.");
            return;
        }
        ItemMeta mainMeta = mainHand.getItemMeta();
        if (mainMeta == null) {
            player.sendMessage(ChatColor.RED + "Could not get the item meta of that item.");
            return;
        }

        int i = 0;
        NamespacedKey key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + i);
        while (mainMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            mainMeta.getPersistentDataContainer().remove(key);
            key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + ++i);
        }
        mainHand.setItemMeta(mainMeta);

        player.sendMessage(ChatColor.GREEN + "This tool is now cleared of any power.");
    }

    @Subcommand("clear all")
    public void clearAll(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            // THIS CAN BE NULL
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            ItemMeta mainMeta = item.getItemMeta();
            if (mainMeta == null) {
                continue;
            }
            int i = 0;
            NamespacedKey key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + i);
            while (mainMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                mainMeta.getPersistentDataContainer().remove(key);
                key = new NamespacedKey(plugin, TagsNavigate.POWER_TOOL_TAG + ++i);
            }
            item.setItemMeta(mainMeta);
        }
        player.sendMessage(ChatColor.GREEN + "Your inventory is now cleared of any power.");
    }
}
