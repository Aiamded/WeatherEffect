package me.dummyperson.weathereffect;

import com.destroystokyo.paper.ParticleBuilder;
import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;


import java.util.*;

public class WeatherEffectTasks {

    private Checker checker;

    interface Starting {
        void starter();
    }

    public void criteriaChecker(Player player, ConfigurationSection conf, String key) {
        this.checker = new Checker();
        ConfigurationSection effect = conf.getConfigurationSection(key);
        assert effect != null;
        ConfigurationSection particle = effect.getConfigurationSection("particle");
        ConfigurationSection sound = effect.getConfigurationSection("sound");
        ConfigurationSection summon = effect.getConfigurationSection("summon");
        ConfigurationSection effects = effect.getConfigurationSection("effects");
        List<String> biomes = effect.getStringList("biomes");
        List<String> weather = effect.getStringList("weathertypes");
        Starting start = new Starting() {
            public void starter() {
                weather();
            }

            public void weather() {
                if (!weather.isEmpty()) {
                    if (weather.contains(checker.weatherCheck(player))) {
                        biomes();
                    }
                } else if (weather.isEmpty()) {
                    biomes();
                } else {
                    Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖" + ChatColor.GRAY + " Configuration" + ChatColor.AQUA + " ❖" + ChatColor.RED + " Missing Weather Types list Key!");
                }
            }
            public void biomes() {
                if (effect.getBoolean("biomescheck") & !biomes.isEmpty()) {
                    if (biomes.contains(String.valueOf(checker.getBiomeKey(player.getLocation())))) {
                        runModules();
                    }
                } else if (effect.getBoolean("biomescheck") & biomes.isEmpty()){
                    runModules();
                } else if (effect.getBoolean("biomescheck") & !biomes.isEmpty()) {
                } else if (effect.getBoolean("biomescheck")) {
                    runModules();
                }
                else {
                    Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖" + ChatColor.GRAY + " Configuration" + ChatColor.AQUA + " ❖" + ChatColor.RED + " Missing Biomes list Key!");
                }
            }

            public void runModules () {
                particles();
                sounds();
                effects();
                summon();
            }

            public void particles() {
                if (particle.getBoolean("enabled")) {
                    spawnParticles(particle, player.getLocation(), player);
                }
            }

            public void sounds() {
                if (sound.getBoolean("enabled")) {
                    soundGenerator(sound, player.getLocation(), player);
                }
            }

            public void effects() {
                if (effects.getBoolean("enabled")) {
                    effectGiver(effects, player.getLocation(), player);
                }
            }

            public void summon() {
                if (summon.getBoolean("enabled")) {
                    entityGenerator(summon, player.getLocation());
                }
            }
        };
        start.starter();
    }

    interface Checking {
        void checker();
    }

    public void spawnParticles(ConfigurationSection particle, Location location, Player player){
        double max = particle.getInt("radius");
        double min = -particle.getInt("radius");
        double x = (rando(min, max));
        double y = (rando(min, max));
        double z = (rando(min, max));
        Location randoLoc = location.add(x, y, z);
        Checking check = new Checking() {
            public void checker() {
                if (particle.getBoolean("aircheck")) {
                    checkinging();
                } else if (!particle.getBoolean("aircheck")) {
                    if (checker.skylightChance(randoLoc, particle.getInt("chances"))) {
                        spawnParticlesPassed(particle, randoLoc, player);
                    }
                } else {
                    retryOnFail();
                }
            }

            public void checkinging() {
                if (!checker.blockAbove(randoLoc)) {
                    spawnParticlesPassed(particle, randoLoc, player);
                } else if (checker.blockAbove(randoLoc)){
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
                    if (particle.getList("blocktypes").contains(checker.blockCheck(randoLoc))) {
                        checker();
                    }
                } else {
                    checker();
                }
            }
        };

        if (particle.getBoolean("aircheck")) {
            if (particle.getList("blocktypes").contains(checker.blockCheck(randoLoc))) {
                check.checker();
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
        if (Objects.equals(particle.getString("name"), "FALLING_DUST")) {
            pb.data(Bukkit.createBlockData(Material.valueOf(particle.getString("particledata"))));
        }
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
        String name = sound.getString("name");
        Location randoLoc = location.add(x, y, z);

        Checking check = new Checking() {
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

    public void entityGenerator(ConfigurationSection summon, Location location) {
        double max = summon.getInt("radius");
        double min = -summon.getInt("radius");
        double x = (rando(min, max));
        double y = (rando(min, max));
        double z = (rando(min, max));
        Location randoLoc = location.add(x, y, z);
        Checking check = new Checking() {
            @Override
            public void checker() {
                if (!summon.getStringList("entities").isEmpty()) {
                    chance();
                }
            }

            public void chance() {
                if (new Random().nextInt(100) < summon.getInt("chance")) {
                    blockAbove();
                }
            }

            public void blockAbove() {
                if (summon.getBoolean("blockabovecheck")) {
                    if (!checker.blockAbove(randoLoc)) {
                        groundCheck();
                    }
                } else {
                    groundCheck();
                }
            }

            public void groundCheck() {
                if (summon.getBoolean("groundcheck")) {
                    lowestGroundAt();
                } else {
                 airCheck(null);
                }
            }
            public void lowestGroundAt () {
                for (int i = 0; i < summon.getInt("groundcheck.depth"); i++) {
                    Location checking = randoLoc;
                    if (!summon.getStringList("blocktypes").isEmpty() && summon.getStringList("blocktypes").contains(checker.blockCheck(checking)) && checker.blockAbove(checking.add(0,1,0))) {
                        checking.add(0, -1,0);
                    } else if (!summon.getStringList("blocktypes").isEmpty() && summon.getStringList("blocktypes").contains(checker.blockCheck(checking)) && checker.blockAbove(checking.add(0,1,0))) {
                        lowestBlockVerify(i, checking);
                    }
                }
            }

            public void lowestBlockVerify(int i, Location checking) {
                if (i <= (summon.getInt("aircheck.height") - 1)) {
                    airCheck(checking);
                }
            }

            public void airCheck(Location checking) {
                if (summon.getBoolean("aircheck.enabled")) {
                    if (checking == null) {
                        if (summon.getStringList("blocktypes").contains(checker.blockCheck(randoLoc))) {
                            Location airChecking = randoLoc;
                            for (int i = 0; i < summon.getInt("aircheck.height"); i++) {
                                if (!summon.getStringList("blocktypes").isEmpty() && summon.getStringList("blocktypes").contains(checker.blockCheck(airChecking))) {
                                    airChecking.add(0, 1,0);
                                    radiusVerify(i, airChecking);
                                } else if (summon.getStringList("blocktypes").isEmpty()) {
                                    summoning(randoLoc);
                                }
                            }
                        }
                    } else if (checking != null) {
                        if (summon.getStringList("blocktypes").contains(checker.blockCheck(checking))) {
                            Location airChecking = checking;
                            for (int i = 0; i < summon.getInt("aircheck.height"); i++) {
                                if (!summon.getStringList("blocktypes").isEmpty() && summon.getStringList("blocktypes").contains(checker.blockCheck(airChecking))) {
                                    airChecking.add(0, 1, 0);
                                    radiusVerify(i, checking);
                                } else if (summon.getStringList("blocktypes").isEmpty()) {
                                    summoning(checking);
                                }
                            }
                        }
                    }
                } else {
                    summoning(randoLoc);
                }
            }

            public void radiusVerify (int i, Location newLoc) {
                if (i == (summon.getInt("aircheck.height") - 1)) {
                    summoning(newLoc);
                }
            }

            public void summoning(Location newLoc) {
                List<String> list = summon.getStringList("entities");
                Random rand = new Random();
                String entityname = list.get(rand.nextInt(list.size()));
                org.bukkit.entity.Entity entity = randoLoc.getWorld().spawnEntity(randoLoc, EntityType.valueOf(entityname));
                //nbtTags(entity, newLoc);
                NBTEntity nbtent = new NBTEntity(entity);
                nbtent.setByte("CanPickUpLoot", (byte) 1);
                if (summon.getBoolean("entities-spawned-loggings")) {
                    Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖  " + ChatColor.GRAY + entity.getName() + " spawned at x: " + newLoc.getBlockX() + " y: " + newLoc.getBlockY() + " z: " + newLoc.getBlockZ());
                }
            }

            public void nbtTags(org.bukkit.entity.Entity entity,  Location newLoc) {
                //Bukkit.getLogger().info(String.valueOf(nbtent));

            }

            public void attributes() {

            }
        };
        check.checker();
    }

    public void effectGiver(ConfigurationSection effects, Location location, Player player) {
        int duration = effects.getInt("duration");
        int amplifier= effects.getInt("amplifier");
        Boolean ambient = effects.getBoolean("isambient");
        Boolean particles = effects.getBoolean("effectparticle");
        Checking check = new Checking() {
            @Override
            public void checker() {
                blockAboveCheck();
            }

            public void blockAboveCheck() {
                if (effects.getBoolean("blockabovecheck")) {
                    if (!checker.blockAbove(location)) {
                        int a = new Random().nextInt(100);
                        if (a <= effects.getInt("chance")) {
                            giveEffects();
                        } else { }
                    } else if (checker.blockAbove(location)) { }
                } else {
                    int a = new Random().nextInt(100);
                    if (a <= effects.getInt("chance")) {
                        giveEffects();
                    }
                }
            }

            public void giveEffects() {
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
        };
        check.checker();
    }
}
