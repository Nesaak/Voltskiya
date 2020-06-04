package com.voltskiya.core.game.skill_points.inventory;

import com.voltskiya.core.game.skill_points.skill_items.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SkillPointsGUI implements InventoryHolder {
    private Inventory myInventory;
    private final static SkillItem[] clickableItems = new SkillItem[9]; // this needs to be a multiple of 9

    static {
        /*
        0 Endurance: (Max Stamina)
        2 Hunger: (Max Hunger)
        3 Thirst: (Max Thirst) TBA
        5 Vitality: (Max HP)
        6 Strength: (Melee Damage)
        8 Walkspeed: (Max Walkspeed)
         */
        SkillItem nothingItem = new NothingSkillItem(Material.AIR);
        clickableItems[0] = new VitalitySkillItem(Material.BEEF);
        clickableItems[1] = nothingItem;
        clickableItems[2] = nothingItem;
        clickableItems[3] = new MeleeSkillItem(Material.IRON_SWORD);
        clickableItems[4] = nothingItem;
        clickableItems[5] = new ThirstSkillItem(Material.POTION);
        clickableItems[6] = nothingItem;
        clickableItems[7] = nothingItem;
        clickableItems[8] = new SpeedSkillItem(Material.FEATHER);
    }

    public SkillPointsGUI(Player player) {
        myInventory = Bukkit.createInventory(this, clickableItems.length, "Skill Points");
        // 0 1 2 3 4 5 6 7 8 - item slots
        for (int i = 0; i < clickableItems.length; i++) {
            final SkillItem clickableItem = clickableItems[i];

            if (clickableItem instanceof NothingSkillItem)
                continue; // skip nothing just to save resources

            int points = clickableItem.getAttribute(player);
            if (points < 1)
                points = 1;

            ItemStack item = new ItemStack(clickableItem.itemType);
            item.setAmount(points);

            ItemMeta im = item.getItemMeta();
            if (im == null)
                continue;

            // set the name
            im.setDisplayName(clickableItem.getDisplayName());

            // set the lore
            List<String> lore = new ArrayList<>(2);
            lore.add(String.format(ChatColor.DARK_GREEN + "It costs %d xp to increase %s", clickableItem.getXpCost(clickableItem.getAttribute(player)), clickableItem.getDisplayName()));
            im.setLore(lore);
            item.setItemMeta(im);

            // the item has been made
            myInventory.setItem(i, item);
        }
        player.openInventory(myInventory);

    }

    public static void dealWithClick(InventoryClickEvent event) {
        event.setCancelled(true); // cancel all clicks if this inventory is open
        final HumanEntity whoClicked = event.getWhoClicked();
        final int slot = event.getRawSlot();
        if (slot >= 0 && slot < clickableItems.length) {
            if (whoClicked instanceof Player) {
                final SkillItem clickableItem = clickableItems[slot];
                if (!(clickableItem instanceof NothingSkillItem)) {
                    clickableItem.dealWithClick((Player) whoClicked);
                }
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return myInventory;
    }
}
