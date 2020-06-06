package com.voltskiya.core.game.skill_points.skill_items;

import com.voltskiya.core.game.skill_points.inventory.SkillPointsGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public abstract class SkillItem {
    public Material itemType;
    private static String badXpCostMessage = ChatColor.RED + "You don't have enough xp to upgrade that skill point";

    public SkillItem(Material itemType) {
        this.itemType = itemType;
    }

    public String getBadXpCost() {
        return badXpCostMessage;
    }

    public void dealWithClick(Player player, Inventory clickedInventory) {
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        int newAttributeValue = getAttribute(player) + 1;
        final int xpCost = getXpCost(newAttributeValue - 1);
        final int playerLevel = player.getLevel();

        if (playerLevel < xpCost) {
            player.sendMessage(getBadXpCost());
            return;
        }
        player.setLevel(playerLevel - xpCost);
        dealWithUpdate(player);
        container.set(getKey(), PersistentDataType.INTEGER, newAttributeValue);
        SkillPointsGUI.setGUI(player,clickedInventory);

        player.sendMessage(ChatColor.GREEN + String.format("You've upgraded your %s to level %d!", getDisplayName(), newAttributeValue));

    }

    protected abstract NamespacedKey getKey();

    public abstract void dealWithUpdate(Player player);

    public abstract int getXpCost(int newAttributeValue);

    public abstract String getDisplayName();

    public int getAttribute(Player player) {
        return player.getPersistentDataContainer().getOrDefault(getKey(), PersistentDataType.INTEGER, 0);
    }

}
