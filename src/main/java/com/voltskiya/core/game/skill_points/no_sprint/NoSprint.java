package com.voltskiya.core.game.skill_points.no_sprint;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.voltskiya.core.game.skill_points.no_sprint.NoSprintReason.*;

public class NoSprint {
    private static Map<UUID, NoSprintMemory> playerHunger = new ConcurrentHashMap<>();
    private static PotionEffect lowThirstEffect = new PotionEffect(PotionEffectType.HUNGER, 140, 0, false, false);
    private static PotionEffect criticalThirstEffect = new PotionEffect(PotionEffectType.WITHER, 140, 0, false, false);

    // if more reasons before thirst and sprint are made, NoSprintMemory.reason is going to need to be a list
    public static void noSprint(Player player, NoSprintReason reason) {
        NoSprintMemory currentMemory = playerHunger.get(player.getUniqueId());
        if (currentMemory == null) {
            playerHunger.put(player.getUniqueId(), new NoSprintMemory(player.getFoodLevel(), reason));
            player.setFoodLevel(6);
            player.setSaturation(0); // I remove saturation every time the player runs out of breath
        } else {
            if (currentMemory.reason != reason) {
                playerHunger.put(player.getUniqueId(), new NoSprintMemory(player.getFoodLevel(), STAMINA_AND_THIRST));
            }
        }
    }

    public static void yesSprint(Player player) {
        final UUID uuid = player.getUniqueId();
        NoSprintMemory memory = playerHunger.remove(uuid);
        if (memory != null) {
            player.setFoodLevel(memory.foodLevel);
        }

    }

    /**
     * updates the player's hunger if food was lost
     *
     * @param player  the player whose food level changed
     * @param newFood what the new food is
     * @return true if we know the player, otherwise false
     */
    public static boolean updateHunger(Player player, int newFood) {
        final UUID uuid = player.getUniqueId();
        NoSprintMemory memory = playerHunger.get(uuid);
        if (memory == null)
            // we don't know the player
            return false;
        int oldFood = memory.foodLevel;
        if (oldFood <= newFood) {
            // the player ate food and we know the player
            return true;
        } else {
            newFood = oldFood + newFood - 6;
            if (newFood < 6) {
                playerHunger.remove(uuid);
                // we no longer know the player
                return false;
            }
            memory.foodLevel = newFood;
            // we don't re set the player hunger because the event is probably cancelled
            // the player lost food and we know the player
            return true;
        }
    }

    public static void sprint(Player player, double currentStamina) {
        final UUID uuid = player.getUniqueId();
        NoSprintMemory memory = playerHunger.get(uuid);
        if (memory == null) {
            if (currentStamina == 0) {
                noSprint(player, STAMINA);
            }
        } else {
            if (currentStamina == 0) {
                memory.reason = STAMINA_AND_THIRST;
            } else if (currentStamina > 2.5) {
                switch (memory.reason) { // is a switch for possible future changes
                    case STAMINA_AND_THIRST:
                        memory.reason = THIRST;
                        break;
                    case STAMINA:
                        yesSprint(player);
                        break;
                }
            }
        }
    }

    public static void thirst(Player player, double currentThirst) {
        final UUID uuid = player.getUniqueId();
        NoSprintMemory memory = playerHunger.get(uuid);
        if (currentThirst <= 3) {
            if (currentThirst == 0) {
                player.addPotionEffect(criticalThirstEffect);
            }
            // the player can't run
            player.addPotionEffect(lowThirstEffect);
            if (memory == null) {
                // the player is new to not running
                noSprint(player, THIRST);
            } else if (memory.reason == STAMINA) {
                memory.reason = STAMINA_AND_THIRST;
            }
        } else {
            if (memory != null) {
                if (memory.reason == STAMINA_AND_THIRST)
                    memory.reason = STAMINA;
                else if (memory.reason == THIRST)
                    yesSprint(player);
            }
        }
    }

    public static boolean shouldEat(UUID uuid) {
        return !playerHunger.containsKey(uuid);
    }
}
