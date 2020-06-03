package com.voltskiya.core.game.skill_points.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SkillPointsGUI implements InventoryHolder {
    private Inventory myInventory;
    private final static SkillItem[] clickableItems = new SkillItem[9]; // this needs to be a multiple of 9

    static {
        /*
        0 Endurance: (Max Stamina)
        2 Hunger: (Max Hunger)
        4 Thirst: (Max Thirst) TBA
        5 Vitality: (Max HP)
        7 Strength: (Melee Damage)
        9 Walkspeed: (Max Walkspeed)
         */
        SkillItem nothingItem = new NothingSkillItem(Material.AIR);
        clickableItems[0] = nothingItem;
        clickableItems[1] = nothingItem;
        clickableItems[2] = nothingItem;
        clickableItems[3] = nothingItem;
        clickableItems[4] = nothingItem;
        clickableItems[5] = nothingItem;
        clickableItems[6] = nothingItem;
        clickableItems[7] = nothingItem;
        clickableItems[8] = new WalkSpeedSkillItem(Material.FEATHER);
    }

    public SkillPointsGUI() {
        myInventory = Bukkit.createInventory(this, clickableItems.length, "Skill Points");
        // 0 1 2 3 4 5 6 7 8 - item slots
        for (int i = 0; i < clickableItems.length; i++) {
            myInventory.setItem(i, new ItemStack(clickableItems[i].itemType));
        }
    }

    public static void dealWithClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (slot > 0 && slot < clickableItems.length) {
            event.setCancelled(true);
            HumanEntity whoClicked = event.getWhoClicked();
            if (whoClicked instanceof Player)
                clickableItems[slot].dealWithClick((Player) whoClicked);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return myInventory;
    }

    public void openInventory(Player player) {
        player.openInventory(myInventory);
    }
}
