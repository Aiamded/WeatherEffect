package me.dummyperson.weathereffect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        // Plugin startup logic
        getConfig().options().copyDefaults();
        File configFile = new File(getDataFolder().toString() + "/config.yml");
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void  runnable() {
        new BukkitRunnable() {
            private WeatherEffectTask weathertask;

            @Override
            public void run() {
                this.weathertask = new WeatherEffectTask();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    weathertask.criteriaChecker(player, getConfig());
                }
            }
        }.runTaskTimer(this, 0, getConfig().getInt("ticks"));
    }
}
