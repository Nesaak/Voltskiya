package com.voltskiya.core.temperatures;

import com.voltskiya.core.temperatures.constants.*;
import com.voltskiya.core.temperatures.constants.biomes.BiomeWithWeatherMap;
import com.voltskiya.core.temperatures.constants.biomes.NavigateBiomes;
import com.voltskiya.core.temperatures.constants.results.NavigateResults;
import com.voltskiya.core.temperatures.constants.results.PotionInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.NBTBase;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WatchPlayer {
    private static final String CUSTOM_ARMOR = "armor.temperature";
    private JavaPlugin plugin;

    public boolean done;

    private UUID playerUUID;
    private double currentPlayerTemperature;
    private long lastChecked = 0;
    private Integer oldPlayerThreshold = (int) (double) NavigatePlayers.INITIAL_TEMPERATURE;

    public WatchPlayer(@NotNull UUID playerUUID, JavaPlugin plugin) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            this.playerUUID = playerUUID;
            PersistentDataContainer container = player.getPersistentDataContainer();
            Double temperature = container.get(NavigatePlayers.TEMPERATURE, PersistentDataType.DOUBLE);
            if (temperature == null) {
                container.set(NavigatePlayers.TEMPERATURE, PersistentDataType.DOUBLE, NavigatePlayers.INITIAL_TEMPERATURE);
                this.currentPlayerTemperature = NavigatePlayers.INITIAL_TEMPERATURE;
            } else {
                this.currentPlayerTemperature = temperature;
            }
            this.plugin = plugin;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doTemperature, NavigatePlayers.LOGIN_DELAY);

        }

    }

    private void doTemperature() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            // the player doesn't exist so we're done watching
            done = true;
            return;
        }
        if (player.getGameMode() != GameMode.SURVIVAL) {
            // the player isn't in the correct gamemode
            lastChecked = System.currentTimeMillis();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doTemperature, NavigatePlayers.CHECK_TIME);
            return;
        }
        PersistentDataContainer container = player.getPersistentDataContainer();
        @Nullable Double temperature = container.get(NavigatePlayers.TEMPERATURE, PersistentDataType.DOUBLE);
        if (temperature == null) {
            // idk how this happened, but lets deal with it
            container.set(NavigatePlayers.TEMPERATURE, PersistentDataType.DOUBLE, NavigatePlayers.INITIAL_TEMPERATURE);
            this.currentPlayerTemperature = NavigatePlayers.INITIAL_TEMPERATURE;
        } else {
            this.currentPlayerTemperature = temperature;
        }

        // do the temperature change
        Location playerLocation = player.getLocation();
        World playerWorld = playerLocation.getWorld();
        if (playerWorld == null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doTemperature, NavigatePlayers.CHECK_TIME);
            return;
        }

        double temperatureGoal = blockifyTemperature(playerLocation, playerWorld);

        temperatureGoal = specialArmorifyTemperature(temperatureGoal, player);

        temperatureGoal = biomeifyTemperature(temperatureGoal, playerLocation, playerWorld.hasStorm(), playerWorld.getTime());

        temperatureGoal = potionifyTemperature(temperatureGoal, player.getActivePotionEffects());

        temperatureGoal = armorifyTemperature(player.getInventory().getArmorContents(), temperatureGoal);

        temperatureGoal = burnifyTemperature(player.getFireTicks(), temperatureGoal);

        long lastWet = container.getOrDefault(NavigatePlayers.LAST_WET, PersistentDataType.LONG, (long) -1);
        temperatureGoal = wetifyTemperature(lastWet, temperatureGoal);

        long now = System.currentTimeMillis();
        if (lastChecked == 0)
            lastChecked = now;
        int timePassed = (int) ((now - lastChecked) / 1000);
        lastChecked = now;

        double difference = temperatureGoal - currentPlayerTemperature;
        double change = difference * NavigatePlayers.TEMPERATURE_CHANGE_RATE * timePassed;
        currentPlayerTemperature += change;
        // if player is wet, make freezing 5x faster
        if (now - lastWet < 2000) {
            // the player is wet
            if (change < 0) {
                for (int i = 0; i < 2; i++) {
                    difference = temperatureGoal - currentPlayerTemperature;
                    change = difference * NavigatePlayers.TEMPERATURE_CHANGE_RATE * timePassed;
                    currentPlayerTemperature += change;
                }
            }
        }


        container.set(NavigatePlayers.TEMPERATURE, PersistentDataType.DOUBLE, currentPlayerTemperature);

        int nextCheck = doResults(player, currentPlayerTemperature);
        if (nextCheck < 5) {
            nextCheck = (int) NavigatePlayers.CHECK_TIME;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doTemperature, nextCheck);
    }

    private double specialArmorifyTemperature(double temperatureGoal, Player player) {

        double constantModifier = 0;
        @NotNull ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack armorPiece : armor) {
            if (armorPiece == null || armorPiece.getType().isAir())
                continue;

            // the item is good
            net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(armorPiece);
            NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
            if (compound == null)
                continue;

            if (compound.hasKey(CUSTOM_ARMOR)) {
                NBTBase itemTags = compound.get(CUSTOM_ARMOR);
                if (itemTags == null)
                    continue;
                try {
                    constantModifier += Double.parseDouble(itemTags.asString());
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return temperatureGoal + constantModifier;
    }

    private int doResults(Player player, double currentPlayerTemperature) {
        int nextCheck = -1;
        ArrayList<Integer> thresholds = new ArrayList<>(NavigateResults.tempToResult.keySet());
        thresholds.sort(Comparator.comparingInt(o -> o));
        for (Integer threshold : thresholds) {
            if (currentPlayerTemperature < threshold) {
                if (currentPlayerTemperature < -110) {
                    TextComponent message = new TextComponent(NavigatePlayers.veryColdMessage);
                    message.setText(String.format(message.getText(), ((int) currentPlayerTemperature / 3)));
                    ActionBar.sendLongActionBar(player, message);
                } else if (currentPlayerTemperature < -25) {
                    TextComponent message = new TextComponent(NavigatePlayers.coldMessage);
                    message.setText(String.format(message.getText(), ((int) currentPlayerTemperature / 3)));
                    ActionBar.sendLongActionBar(player, message);
                } else if (currentPlayerTemperature < 40) {
                    if (!oldPlayerThreshold.equals(threshold)) {
                        ActionBar.remove(player);
                        TextComponent message = new TextComponent(NavigatePlayers.normalMessage);
                        message.setText(String.format(message.getText(), ((int) currentPlayerTemperature / 3)));
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
                    } else {
                        TextComponent message = new TextComponent(NavigatePlayers.normalContinuedMessage);
                        message.setText(String.format(message.getText(), ((int) currentPlayerTemperature / 3)));
                        ActionBar.sendLongActionBar(player, message);
                    }
                } else if (currentPlayerTemperature < 120) {
                    TextComponent message = new TextComponent(NavigatePlayers.hotMessage);
                    message.setText(String.format(message.getText(), ((int) currentPlayerTemperature / 3)));
                    ActionBar.sendLongActionBar(player, message);
                } else {
                    TextComponent message = new TextComponent(NavigatePlayers.veryHotMessage);
                    message.setText(String.format(message.getText(), ((int) currentPlayerTemperature / 3)));
                    ActionBar.sendLongActionBar(player, message);

                }

                oldPlayerThreshold = threshold;

                // this is the effect to give to the player
                Map<PotionEffectType, PotionInfo> results = NavigateResults.tempToResult.get(threshold).results;
                for (PotionEffectType effect : results.keySet()) {
                    PotionInfo potionInfo = results.get(effect);
                    player.addPotionEffect(effect.createEffect(potionInfo.potionDuration, potionInfo.potionLevel));
                    if (nextCheck == -1)
                        nextCheck = potionInfo.nextCheck;
                    else if (potionInfo.nextCheck > nextCheck)
                        nextCheck = potionInfo.nextCheck;
                }
                break;
            }
        }
        return nextCheck;
    }

    private double burnifyTemperature(int fireTicks, double temperatureGoal) {
        if (fireTicks <= 0)
            return temperatureGoal;
        return temperatureGoal + 15;
    }

    private double wetifyTemperature(Long lastWet, double temperatureGoal) {
        if (lastWet == -1)
            return temperatureGoal;
        long now = System.currentTimeMillis();
        long difference = now - lastWet;
        difference = Math.max(1000, difference);
        return temperatureGoal - 1000.0 / Math.pow(difference, 0.9) * 10;

    }

    private double potionifyTemperature(double temperatureGoal, Collection<PotionEffect> potions) {
        double modifier = 1;
        // the higher the modifier, the more impact
        if (temperatureGoal < 0) {
            // deal with cold resistance
            for (PotionEffect potion : potions) {
                modifier += NavigatePotions.potionToModifierCold.getOrDefault(potion.getType(), 0.0);
            }
        } else {
            // deal with heat resistance
            for (PotionEffect potion : potions) {
                modifier += NavigatePotions.potionToModifierHeat.getOrDefault(potion.getType(), 0.0);
            }
        }
        return temperatureGoal / modifier;
    }

    private double biomeifyTemperature(double temperatureGoal, Location playerLocation, boolean isWeather, long time) {
        Biome biome = playerLocation.getBlock().getBiome();
        if (isWeather) {
            World world = playerLocation.getWorld();
            if (world != null && world.getHighestBlockAt(playerLocation).getY() > playerLocation.getY()) {
                isWeather = false;
            }
        }
        TimeOfDay timeOfDay;
        if (time >= 1500 && time <= 10500)
            timeOfDay = TimeOfDay.DAY;
        else if (time >= 13500 && time <= 22500)
            timeOfDay = TimeOfDay.NIGHT;
        else if (time > 10500 && time < 13500)
            timeOfDay = TimeOfDay.EVENING;
        else
            timeOfDay = TimeOfDay.MORNING;
        BiomeWithWeatherMap biomeMap = NavigateBiomes.biomeModifiers.get(biome);
        if (biomeMap == null)
            return temperatureGoal;
        double biomeModifier = biomeMap.biomeModifiers.get(isWeather).biomeModifiers.getOrDefault(timeOfDay, 0.0);
        World playerWorld = playerLocation.getWorld();
        if (playerWorld == null) {
            return temperatureGoal + biomeModifier;
        }
        int x = playerLocation.getBlockX();
        int oldY = playerLocation.getBlockY();
        int y = oldY;
        int z = playerLocation.getBlockZ();
        for (int i = 0; i < 35; i++) {
            y = oldY + i;
            Block blockAbove = playerWorld.getBlockAt(x, y, z);
            if (blockAbove != null && !blockAbove.getType().isAir() && blockAbove.getType().isOccluding()) {
                // then decrease temperature
                return temperatureGoal + biomeModifier * (1.25 - (-(Math.sqrt(Math.max(0, y - oldY))) / Math.sqrt(36.0) * .75 + 1));
            }
        }

        return temperatureGoal + biomeModifier;
    }

    private double blockifyTemperature(Location playerLocation, World playerWorld) {
        int blockImpactDistance = NavigatePlayers.BLOCK_IMPACT_DISTANCE;
        int oldX = playerLocation.getBlockX();
        int oldY = playerLocation.getBlockY();
        int oldZ = playerLocation.getBlockZ();

        double temperatureGoal = 0;
        // check every block around the player
        for (int xi = -blockImpactDistance; xi < blockImpactDistance; xi++) {
            for (int yi = -blockImpactDistance; yi < blockImpactDistance; yi++) {
                for (int zi = -blockImpactDistance; zi < blockImpactDistance; zi++) {
                    Block block = playerWorld.getBlockAt(oldX + xi, oldY + yi, oldZ + zi);
                    if (!block.isEmpty()) {
                        // then add this block to the players temperature
                        Material blockType = block.getType();
                        Double rawBlockTemp = NavigateBlocks.blockToTemp.getOrDefault(blockType, null);
                        if (rawBlockTemp != null) {
                            temperatureGoal += refineBlockTemp(xi, yi, zi, rawBlockTemp);
                        }
                    }
                }
            }
        }

        return temperatureGoal;
    }

    private double armorifyTemperature(ItemStack[] armor, double temperatureGoal) {
        double modifier = 1;
        // the higher the modifier, the more impact
        if (temperatureGoal < 0) {
            // deal with cold resistance
            for (ItemStack armorPiece : armor) {
                if (armorPiece != null) {
                    modifier += NavigateArmor.coldArmorToModifier.getOrDefault(armorPiece.getType(), 0.0);
                }
            }
        } else {
            // deal with heat resistance
            for (ItemStack armorPiece : armor) {
                if (armorPiece != null)
                    modifier += NavigateArmor.heatArmorToModifier.getOrDefault(armorPiece.getType(), 0.0);
            }
        }
        return temperatureGoal / modifier;
    }

    private double refineBlockTemp(int xi, int yi, int zi, Double rawBlockTemp) {
        double distance = Math.sqrt(xi * xi + yi * yi + zi * zi);
        if (distance <= 1)
            return rawBlockTemp;
        return rawBlockTemp / distance / distance;
    }
}
