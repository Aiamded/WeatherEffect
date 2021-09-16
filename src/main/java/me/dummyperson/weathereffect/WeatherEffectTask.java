package me.dummyperson.weathereffect;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;


import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

public class WeatherEffectTask {

    private Checker checker;

    public void criteriaChecker(Player player, Configuration configuration) {
        this.checker = new Checker();
        String biome = String.valueOf(checker.getBiomeKey(player.getLocation()));
        ConfigurationSection conf = configuration.getConfigurationSection("enabled-weather");
        for (String key : conf.getKeys(false)){
            ConfigurationSection effect = conf.getConfigurationSection(key);
            String weather = effect.getString("weathertype");
            boolean biomesCheck = effect.getBoolean("biomes-check");
            ConfigurationSection particle = effect.getConfigurationSection("particle");
            ConfigurationSection sound = effect.getConfigurationSection("sound");
            ConfigurationSection summon = effect.getConfigurationSection("summon");
            ConfigurationSection effects = effect.getConfigurationSection("effects");
            List<String> biomes = effect.getStringList("biomes");
            if (checker.weatherCheck(player).equals(weather) & biomes.contains(biome) & biomesCheck){
                spawnParticles(particle, player.getLocation(), player);
                soundGenerator(sound, player.getLocation(), player);
                effectgiver(effects, player.getLocation(), player);
                entitygenerator(summon, player.getLocation(), player);
            }
        }
    }

    interface Blockabovecheck {
        void checker();
    }

    public void spawnParticles(ConfigurationSection particle, Location location, Player player){
        double max = particle.getInt("radius");
        double min = -particle.getInt("radius");
        double x = (rando(min, max));
        double y = (rando(min, max));
        double z = (rando(min, max));
        Location randoLoc = location.add(x, y, z);
        Blockabovecheck check = new Blockabovecheck() {
            public void checker() {
                if (particle.getBoolean("blockabovecheck")) {
                    blockAboveChecking();
                } else if (!particle.getBoolean("blockabovecheck")) {
                    if (checker.skylightChance(randoLoc, particle.getInt("chances"))) {
                        spawnParticlesPassed(particle, randoLoc, player);
                    } else {
                        // Bruh Attempt Fails
                        retryOnFail();
                    }
                } else {
                    retryOnFail();
                }
            }

            public void blockAboveChecking() {
                if (!checker.blockAbove(randoLoc)) {
                    spawnParticlesPassed(particle, randoLoc, player);
                } else if (checker.blockAbove(randoLoc)){
                    //Do not attempt to spawn particle again if there's a block above
                    //retryOnFail();
                } else {

                }
            }

            public void retryOnFail() {
                double max = particle.getInt("radius");
                double min = -particle.getInt("radius");
                double x = (rando(min, max));
                double y = (rando(min, max));
                double z = (rando(min, max));
                Location randoLoc = location.add(x, y, z);
                airChecking(randoLoc);
            }
            public void airChecking(Location randoLoc) {
                if (particle.getBoolean("aircheck")) {
                    if (particle.getList("blocktypes").contains(checker.airCheck(randoLoc))) {
                        checker();
                    } else {
                        //If it returned other material type it will fail
                        //check.checker();
                    }
                } else {
                    checker();
                }
            }
        };

        if (particle.getBoolean("aircheck")) {
            if (particle.getList("blocktypes").contains(checker.airCheck(randoLoc))) {
                check.checker();
            } else {
                //If it returned other material type it will fail
                //check.checker();
            }
        } else if (!particle.getBoolean("aircheck")) {
            check.checker();
        } else {
            check.checker();
        }
    }

    public void spawnParticlesPassed(ConfigurationSection particle, Location location, Player player) {
        int count = particle.getInt("count");
        double deltax = particle.getDouble("delta-x");
        double deltay = particle.getDouble("delta-y");
        double deltaz = particle.getDouble("delta-z");
        ParticleBuilder pb = new ParticleBuilder(Particle.valueOf(particle.getString("name")));
        pb.receivers(player);
        pb.count(count);
        pb.location(location);
        pb.offset(deltax, deltay, deltaz);
        pb.data(Bukkit.createBlockData(Material.valueOf(particle.getString("particledata"))));
        pb.extra(particle.getDouble("speed"));
        pb.force(particle.getBoolean("force"));
        pb.spawn();
    }

    public double rando (double min, double max) {
        return (double)Math.floor(Math.random()*(max-min+1)+min);
    }

    public void soundGenerator(ConfigurationSection sound, @NotNull Location location, Player player) {
        double max = sound.getInt("radius");
        double min = -sound.getInt("radius");
        double x = (rando(min, max));
        double y = (rando(min, max));
        double z = (rando(min, max));
        Float volume = Float.parseFloat(sound.getString("pitch"));
        Float pitch = Float.parseFloat(sound.getString("pitch"));
        String category = sound.getString("category");
        String name = sound.getString("name");
        Location randoLoc = location.add(x, y, z);

        Blockabovecheck check = new Blockabovecheck() {
            @Override
            public void checker() {
                if (sound.getBoolean("blockabovecheck")) {
                    if (!checker.blockAbove(randoLoc)) {
                        int a = new Random().nextInt(100);
                        if (a <= sound.getInt("chance")) {
                            player.playSound(randoLoc, Sound.valueOf(name), volume, pitch);
                        } else { }
                    } else if (checker.blockAbove(randoLoc)) { }
                } else {
                    int a = new Random().nextInt(100);
                    if (a <= sound.getInt("chance")) {
                        player.playSound(randoLoc, Sound.valueOf(name), volume, pitch);
                    }
                }
            }
        };
        check.checker();
    }

    public void entitygenerator(ConfigurationSection summon, Location location, Player player) {

    }

    public void effectgiver(ConfigurationSection effects, Location location, Player player) {
        int duration = effects.getInt("duration");
        int amplifier= effects.getInt("amplifier");
        Boolean ambient = effects.getBoolean("isambient");
        Boolean particles = effects.getBoolean("effectparticle");
        Enumeration<String> e = Collections.enumeration(effects.getStringList("types"));
        while(e.hasMoreElements()) {
            int type = (Integer.parseInt(e.nextElement())) -1;
            int a = new Random().nextInt(100);
            if (a <= effects.getInt("chance")) {
                PotionEffect potion = new PotionEffect(PotionEffectType.values()[type], duration, amplifier, ambient, particles);
                player.addPotionEffect(potion);
            }
        }
    }

}
