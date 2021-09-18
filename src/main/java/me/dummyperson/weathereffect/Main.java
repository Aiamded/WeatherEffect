package me.dummyperson.weathereffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public final class Main extends JavaPlugin implements CommandExecutor, Listener {

    HashMap<String, Boolean> taskCancel = new HashMap<String, Boolean>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();
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
                    reloadTasks();
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Configuration" + ChatColor.AQUA + " ❖ " + ChatColor.GREEN + " Tasks unloading!! Check console for more information!!");
                    return true;
                }
            }
        }
        return true;
    }

    public void reloadTasks() {
        reloadConfig();
        Configuration configuration = getConfig();
        ConfigurationSection conf = configuration.getConfigurationSection("enabled-list");
        for (String key : conf.getKeys(false)) {
            if (!taskCancel.get(key)) {
                ConfigurationSection effect = conf.getConfigurationSection(key);
                runnable(key, effect, conf);
                taskCancel.put(key, true);
                Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Unloading " + key + ChatColor.AQUA + " ❖ " + ChatColor.RED + " Please wait!");
            } else if (!taskCancel.containsKey(key)) {
                Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Loaded New Task: " + key + ChatColor.AQUA + " ❖ " + ChatColor.GREEN + " Successful!");
                ConfigurationSection effect = conf.getConfigurationSection(key);
                taskCancel.put(key, false);
                runnable(key, effect, conf);
            }
        }
    }


    public void weatherList() {
        Configuration configuration = getConfig();
        ConfigurationSection conf = configuration.getConfigurationSection("enabled-list");
        for (String key : conf.getKeys(false)) {
            if (!taskCancel.containsKey(key)) {
                Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Loading Task: " + key + ChatColor.AQUA + " ❖ " + ChatColor.GREEN + " Successful!");
                ConfigurationSection effect = conf.getConfigurationSection(key);
                taskCancel.put(key, false);
                runnable(key, effect, conf);
            }
        }
    }

    public void  runnable(String key, ConfigurationSection effect, ConfigurationSection conf) {
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
                while (taskCancel.get(key)) {
                    cancel();
                    taskCancel.put(key, false);
                    Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeatherEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Reloading " + key + ChatColor.AQUA + " ❖ " + ChatColor.GREEN + " Successful!");
                }
            }
        }.runTaskTimer(this, 0, effect.getInt("ticks"));
    }
}
