package com.voltskiya.core.mobs.commands.paint;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import javax.swing.*;
import java.awt.*;

import static java.awt.Color.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PaintersGraphics extends JFrame {
    public static final int WIDTH = 420;
    PaintersWorld world;
    Map<String, Color> colors = new HashMap<>();
    Map<UUID, Location> mobs = new ConcurrentHashMap<>();

    public PaintersGraphics(PaintersWorld world) {
        super("Map");
        this.world = world;
        prepareBiomeColors();
        prepare();
        setVisible(true);
    }

    private void prepareBiomeColors() {
        colors.put("BEACH", new Color(209, 198, 102));
        colors.put("SAVANNA", new Color(173, 209, 102));
        colors.put("GIANT_TREE_TAIGA", new Color(26, 115, 32));
        colors.put("FROZEN_OCEAN", new Color(7, 64, 125));
        colors.put("BIRCH_FOREST", new Color(9, 74, 8));
        colors.put("RIVER", new Color(7, 64, 125));
        colors.put("DARK_FOREST_HILLS", new Color(9, 74, 8));
        colors.put("LUKEWARM_OCEAN", new Color(7, 64, 125));
        colors.put("NETHER", new Color(161, 8, 8));
        colors.put("MODIFIED_JUNGLE", new Color(17, 184, 2));
        colors.put("SNOWY_TUNDRA", new Color(218, 224, 223));
        colors.put("JUNGLE", new Color(17, 184, 2));
        colors.put("OCEAN", new Color(7, 64, 125));
        colors.put("ICE_SPIKES", new Color(218, 224, 223));
        colors.put("DEEP_OCEAN", new Color(7, 64, 125));
        colors.put("DESERT", new Color(140, 121, 24));
        colors.put("BAMBOO_JUNGLE_HILLS", new Color(17, 184, 2));
        colors.put("DARK_FOREST", new Color(9, 74, 8));
        colors.put("COLD_OCEAN", new Color(7, 64, 125));
        colors.put("WARM_OCEAN", new Color(7, 64, 125));
        colors.put("FOREST", new Color(9, 74, 8));
        colors.put("WOODED_MOUNTAINS", new Color(110, 110, 110));
        colors.put("MOUNTAINS", new Color(110, 110, 110));
        colors.put("SWAMP", new Color(25, 79, 6));
        colors.put("SNOWY_TAIGA", new Color(255, 255, 255));
        colors.put("FROZEN_RIVER", new Color(7, 64, 125));
        colors.put("BADLANDS", new Color(191, 123, 59));
        colors.put("DESERT_HILLS", new Color(140, 121, 24));
        colors.put("GIANT_SPRUCE_TAIGA", new Color(110, 161, 87));
        colors.put("SNOWY_TAIGA_HILLS", new Color(255, 255, 255));
        colors.put("BADLANDS_PLATEAU", new Color(191, 123, 59));
        colors.put("PLAINS", new Color(18, 201, 46));
        colors.put("TAIGA", new Color(18, 201, 46));
        colors.put("GIANT_TREE_TAIGA_HILLS", new Color(18, 201, 46));

    }

    public void putMob(Location loc, UUID name) {
        mobs.put(name, loc);
    }

    private void prepare() {
        //noinspection SuspiciousNameCombination
        setSize(WIDTH, WIDTH);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        int minX = 0;
        int maxX = 0;
        int minZ = 0;
        int maxZ = 0;
        for (PaintersChunk chunk : world.chunks) {
            minX = Math.min(chunk.xLoc, minX);
            maxX = Math.max(chunk.xLoc, maxX);
            minZ = Math.min(chunk.xLoc, minZ);
            maxZ = Math.max(chunk.xLoc, maxZ);
        }
        minX *= 16;
        maxX *= 16;
        minZ *= 16;
        maxZ *= 16;
        int rangeX = maxX - minX;
        int rangeZ = maxZ - minZ;
        rangeX /= WIDTH;
        rangeZ /= WIDTH;
        rangeX++;
        rangeZ++;
        final int range = Math.max(rangeX, rangeZ);

        Set<String> biomesNotDone = new HashSet<>();
        Biome[] biomes = Biome.values();
        Material[] materials = Material.values();
        for (PaintersChunk chunk : world.chunks) {
            int chunkX = chunk.xLoc;
            int chunkZ = chunk.zLoc;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    PaintersBlock block = chunk.blocks[x][z];
                    String biome = biomes[block.biome].toString();
                    Material blockType = materials[block.blockType];
                    int finalX = (chunkX * 16 + x - minX) / range;
                    int finalZ = (chunkZ * 16 + z - minZ) / range;
                    Color color = colors.getOrDefault(biome, black);
                    if (color.equals(black))
                        biomesNotDone.add(biome);
                    g.setColor(color);
                    System.out.println(finalX + " , " + finalZ);
                    g.drawLine(finalX, finalZ, finalX, finalZ);
                }
            }
        }

        for (UUID uid : mobs.keySet()) {
            Location location = mobs.get(uid);
            int x = (location.getBlockX() - minX) / range;
            int z = (location.getBlockZ() - minZ) / range;
            g.setColor(black);
            g.drawLine(x + 1, z - 1, x + 1, z + 1);
            g.drawLine(x, z - 1, x, z + 1);
            g.drawLine(x - 1, z - 1, x - 1, z + 1);
        }
        g.setColor(black);

        for (String biome : biomesNotDone) {
            System.out.println(String.format("colors.put(\"%s\", new Color(0, 0, 0));", biome));
        }
        System.out.println("--------------");
    }

}