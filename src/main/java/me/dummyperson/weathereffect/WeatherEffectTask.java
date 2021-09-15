package me.dummyperson.weathereffect;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;


import java.util.List;

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
            List<String> biomes = effect.getStringList("biomes");
            if (checker.weatherCheck(player).equals(weather) & biomes.contains(biome) & biomesCheck){
                spawnParticles(particle, player.getLocation(), player);
                //run spawnParticles n amount of times
            }
        }
    }

    interface blockabovecheck {
        void checker();
    }

    public void spawnParticles(ConfigurationSection particle, Location location, Player player){
        double max = particle.getInt("radius");
        double min = -particle.getInt("radius");
        double x = (rando(min, max));
        double y = (rando(min, max));
        double z = (rando(min, max));
        Location randoLoc = location.add(x, y, z);
        blockabovecheck check = new blockabovecheck() {
            public void checker() {
                if (particle.getBoolean("blockabovecheck")) {
                    blockabove();
                } else if (!particle.getBoolean("blockabovecheck")) {
                    if (checker.skylightChance(randoLoc, particle.getInt("chances"))) {
                        spawnParticlesPassed(particle, randoLoc, player);
                    } else {
                        // Bruh Attempt Fails
                    }
                } else {

                }
            }

            public void blockabove() {
                if (!checker.blockAbove(randoLoc)) {
                    spawnParticlesPassed(particle, randoLoc, player);
                } else if (checker.blockAbove(randoLoc)){
                    //repeat Spawnparticles
                } else {

                }
            }
        };

        if (particle.getBoolean("aircheck")) {
            if (checker.airCheck(randoLoc).contains("air")) {
                check.checker();
            } else if (checker.airCheck(randoLoc).contains("cave")) {
                check.checker();
            } else if (checker.airCheck(randoLoc).contains("other")) {
                //check.checker();
            } else {
                check.checker();
            }
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
        pb.extra(particle.getDouble("speed"));
        pb.force(particle.getBoolean("force"));
        pb.spawn();
    }

    public double rando (double min, double max) {
        return (double)Math.floor(Math.random()*(max-min+1)+min);
    }

}
