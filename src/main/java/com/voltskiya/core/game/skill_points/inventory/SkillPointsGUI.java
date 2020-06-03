package com.voltskiya.core.game.skill_points.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SkillPointsGUI implements InventoryHolder {
    private Inventory myInventory;

    public SkillPointsGUI() {
        myInventory = Bukkit.createInventory(this, 9);

    }

    @Override
    public @NotNull Inventory getInventory() {
        return myInventory;
    }

    public void openInventory(Player player) {
        player.openInventory(myInventory);
    }

}
