package com.voltskiya.core.game.skill_points.thirst;

import com.voltskiya.core.Voltskiya;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;


public class ThirstDamageListener implements Listener {
    public ThirstDamageListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //I'm first because I want to change the modifier before other's might use it
    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDamageFirst(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player) entity;
            @Nullable final AttributeInstance genericArmor = player.getAttribute(Attribute.GENERIC_ARMOR);
            @Nullable final AttributeInstance genericToughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
            if (genericArmor == null || genericToughness == null) {
                // idk how this player doesn't have an armor attribute
                return;
            }
            boolean hadThirstArmor = ThirstBar.removeThirstModifier(genericArmor);
            if (!hadThirstArmor || event.getDamage(EntityDamageEvent.DamageModifier.ARMOR) == 0)
                return;

            double orignialDamage = event.getDamage();
            // recalculate the armor modifier  (this equation is what is used in nms when calculating the armor modifier
            double armorModifier = -(orignialDamage - CombatMath.a((float) orignialDamage, (float) genericArmor.getValue(), (float) genericToughness.getValue()));

            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, armorModifier); // I think I'm fine to use this deprecated method because I use nms to do the calculation for me

            // I don't immediately put it back in case someone else in this event tries to deal with generic armor
            Bukkit.getScheduler().scheduleSyncDelayedTask(Voltskiya.get(), () -> ThirstBar.calc(player), 0);

        }
    }
}
