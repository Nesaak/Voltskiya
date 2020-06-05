package com.voltskiya.core.custom_mobs.araneo;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WebProjectile {
    private static final Random random = new Random();
    private static final long CHECK_COLLISION_COOLDOWN = 2;
    private List<ArmorStand> projectiles;
    private boolean allDead = false;
    private static JavaPlugin plugin;
    private Vector direction;

    private int checksDone = 0;
    private static final int checksToDeath = 20;
    private static Collection<PotionEffect> stunEffects = new LinkedList<>();

    static {
        stunEffects.add(new PotionEffect(PotionEffectType.SLOW, 200, 2));
        stunEffects.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 9));
        stunEffects.add(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
    }

    public static void initialize(JavaPlugin pl) {
        plugin = pl;
    }

    public WebProjectile(Location majorProjectileLoc, Vector direction) {
        this.direction = direction;
        World world = majorProjectileLoc.getWorld();
        if (world == null) {
            return;
        }
        List<ArmorStand> projectiles = new LinkedList<>();
        for (int i = 0; i < 15; i++) {
            double x = random.nextDouble() * 10 - 5;
            double y = random.nextDouble() * 360;
            double z = random.nextDouble() * 10 - 5;
            if (x < 0)
                x += 360;
            if (z < 0)
                z += 360;
            x = Math.toRadians(x);
            y = Math.toRadians(y);
            z = Math.toRadians(z);
            Location minorProjectileLoc = majorProjectileLoc.clone();
            minorProjectileLoc.setDirection(new Vector(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1));
            Entity entity = world.spawnEntity(minorProjectileLoc, EntityType.ARMOR_STAND);
            if (!(entity instanceof ArmorStand)) {
                entity.remove();
                continue;
            }
            ArmorStand proj = (ArmorStand) entity;
            proj.setGravity(false);
            proj.setInvulnerable(true);
            proj.setVisible(false);
            proj.setHeadPose(new EulerAngle(x, y, z));
            proj.setCanPickupItems(false);
            @Nullable EntityEquipment inventory = proj.getEquipment();
            if (inventory == null) {
                proj.remove();
                continue;
            }
            inventory.setHelmet(new ItemStack(Material.COBWEB));
            projectiles.add(proj);
        }
        this.projectiles = projectiles;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doVelocity, 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doParticles, 1);
        checkDead();
        checkCollisioin();
    }

    private void doParticles() {
        if (allDead) return;
        for (int i = 0; i < 5; i++) {
            double x = random.nextDouble() * 1 - .5;
            double y = random.nextDouble() * 1 - .5;
            double z = random.nextDouble() * 1 - .5;
            Location location = projectiles.get(0).getEyeLocation().add(x + 0, y + 0.5, z + 0);
            World world = location.getWorld();
            if (world == null)
                return;
            world.spawnParticle(Particle.SNOWBALL, location, 0);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doParticles, 1);

    }

    private void doVelocity() {
        if (checksDone++ > checksToDeath) {
            killAll();
            return;
        }
        if (allDead)
            return;
        for (ArmorStand projectile : projectiles) {
            projectile.teleport(projectile.getLocation().add(direction));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::doVelocity, 2);
    }

    private void checkCollisioin() {
        if (allDead)
            return;
        Location location = projectiles.get(0).getEyeLocation().add(0, 0.5, 0);
        World world = location.getWorld();
        if (world == null) {
            killAll();
            return;
        }
        if (!world.getBlockAt(location).getType().isAir()) {
            killAll();
            return;
        }

        @NotNull Collection<Entity> nearbyEntities = world.getNearbyEntities(location, 1, 1, 1);
        for (Entity nearby : nearbyEntities) {
            if (nearby instanceof Player) {
                final GameMode gameMode = ((Player) nearby).getGameMode();
                if (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE) {
                    stun((Player) nearby);
                    killAll();
                    return;
                }
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::checkCollisioin, CHECK_COLLISION_COOLDOWN);

    }

    private static void stun(Player entity) {
        entity.addPotionEffects(stunEffects);
        entity.playSound(entity.getLocation(), Sound.ENTITY_LLAMA_SPIT, SoundCategory.HOSTILE, 5, (float) 0.5);
    }

    private void killAll() {
        for (ArmorStand projectile : projectiles) {
            projectile.remove();
        }
        this.allDead = true;
    }

    private void checkDead() {
        boolean allDead = false;
        for (ArmorStand projectile : projectiles) {
            if (!projectile.isValid()) {
                allDead = true;
                break;
            }
        }
        if (allDead)
            killAll();
    }

}
