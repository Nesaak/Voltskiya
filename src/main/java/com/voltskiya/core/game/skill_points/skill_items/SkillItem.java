package com.voltskiya.core.game.skill_points.skill_items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
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

    public void dealWithClick(Player player) {
        @NotNull PersistentDataContainer container = player.getPersistentDataContainer();
        int speed = getAttribute(player) + 1;
        container.set(getKey(), PersistentDataType.INTEGER, speed);
        final int xpCost = getXpCost();
        final int playerLevel = player.getLevel();

        if (playerLevel < xpCost) {
            player.sendMessage(getBadXpCost());
            return;
        }
        player.setLevel(playerLevel - xpCost);
        dealWithUpdate(player);

        player.sendMessage(ChatColor.GREEN + String.format("You've upgraded your %s to level %d!", getDisplayName(), speed));

    }

    protected abstract NamespacedKey getKey();

    public abstract void dealWithUpdate(Player player);

    public abstract int getXpCost();

    public abstract String getDisplayName();

    public abstract int getAttribute(Player player);

}
