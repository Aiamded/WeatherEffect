package me.dummyperson.weathereffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public final class Main extends JavaPlugin implements CommandExecutor, Listener {

    HashMap<String, Boolean> taskRunning = new HashMap<String, Boolean>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        this.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Loader" + ChatColor.AQUA + " ❖ " + ChatColor.GREEN + " Enabled!");
        getCommand("weathereffect").setExecutor(this);
        weatherList();
    }

    @Override
    public void onDisable() {
        this.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Loader" + ChatColor.AQUA + " ❖ " + ChatColor.RED + " Disabled!");
        // Plugin shutdown logic
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("weathereffect")) {
            if(args.length == 0) {
                if(sender.hasPermission("weathereffect.player")) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Configuration" + ChatColor.AQUA + " ❖ " + ChatColor.RED + " Unknown commands or No permissions...");
                    return true;
                }
            }
            if((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
                if(sender.hasPermission("weathereffect.reload")) {
                    reloader();
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Configuration" + ChatColor.AQUA + " ❖ " + ChatColor.DARK_PURPLE + " Tasks unloading!! Check console for more information!!");
                    return true;
                }
            }
        }
        return true;
    }

    public void reloader() {
        reloadConfig();
        Configuration configuration = getConfig();
        ConfigurationSection conf = configuration.getConfigurationSection("enabled-list");
        for (String key : conf.getKeys(false)) {
            while (taskRunning.get(key)) {
                taskRunning.put(key, false);
                Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Reloading " + key + ChatColor.AQUA + " ❖ " + ChatColor.RED + " Please wait!");
            }
        }

    }
    public void weatherList() {
        Configuration configuration = getConfig();
        ConfigurationSection conf = configuration.getConfigurationSection("enabled-list");
        for (String key : conf.getKeys(false)) {
            while (!taskRunning.containsKey(key)) {
                taskRunning.put(key, true);
                Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Task Loading: " + key + ChatColor.AQUA + " ❖ " + ChatColor.DARK_PURPLE + " Loaded!");
                runnable(key);
            }
        }
    }

    public void runnable(String key) {
        Configuration configuration = getConfig();
        ConfigurationSection conf = configuration.getConfigurationSection("enabled-list");
        ConfigurationSection effect = conf.getConfigurationSection(key);

        new BukkitRunnable() {
            private WeatherEffectTasks weathertask;

            @Override
            public void run() {
                this.weathertask = new WeatherEffectTasks();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    for (int i = 0; i < effect.getInt("attempts"); i++) {
                        weathertask.criteriaChecker(player, conf, key);
                    }
                }
                if (!taskRunning.get(key)) {
                    taskRunning.remove(key);
                    Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Effect Name: " + key + ChatColor.AQUA + " ❖ " + ChatColor.RED + " Stopping!");
                }
                if (!taskRunning.containsKey(key)) {
                    cancel();
                    weatherList();
                    Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Restarted: " + key + ChatColor.AQUA + " ❖ " + ChatColor.GREEN + " Successful!");
                }
            }
        }.runTaskTimer(this, 0, effect.getInt("ticks"));
    }
}