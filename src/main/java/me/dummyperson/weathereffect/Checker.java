package me.dummyperson.weathereffect;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Random;

public class Checker {
    public BiomeBase getBiomeBase(Location location) {
        BlockPosition pos = new BlockPosition(location.getBlockX(), 0, location.getBlockZ());
        Chunk nmsChunk = ((CraftWorld)location.getWorld()).getHandle().getChunkAtWorldCoords(pos);
        if (nmsChunk != null) {
            return nmsChunk.getBiomeIndex().getBiome(pos.getX(), 0, pos.getZ());
        }
        return null;
    }

    public MinecraftKey getBiomeKey(Location location) {
        DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();
        IRegistryWritable<BiomeBase> registry = dedicatedServer.getCustomRegistry().b(IRegistry.aO);
        return registry.getKey(getBiomeBase(location));
    }

    public String weatherCheck (Player player) {
        boolean clear = player.getWorld().isClearWeather();
        boolean rain = Objects.requireNonNull(player.getPlayer()).getWorld().hasStorm();
        boolean thunder = player.getWorld().isThundering();
        if (clear){
            return "clear";
        } else if (rain){
            return "rain";
        } else if (thunder) {
            return "thunder";
        }else {
            return null;
        }
    }

    public boolean blockAbove (Location location) {
        Bukkit.getLogger().info("Block above checking");
        int solid = location.getWorld().getHighestBlockYAt(location);
        int yspawn = location.getBlockY();
        if (solid >= yspawn) {
            return true;
        } else {
            return false;
        }
    }

    public boolean skylightChance (Location location, int chances) {
        int lightLevel = location.getBlock().getLightLevel();
        int a = new Random().nextInt(15);
        if (a <= lightLevel) {
           if (new Random().nextInt(100) < chances) {
               return true;
            }
           else return false;
        } else return  false;
    }

    public String airCheck (Location location) {
        String airchecker = location.getBlock().getBlockData().toString();
        if (airchecker.equals("AIR")) {
            return airchecker;
        } else if (airchecker.equals("CAVE_AIR")) {
            return airchecker;
        } else {
            return  airchecker;
        }
    }
}
