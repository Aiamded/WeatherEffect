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

public final class Main extends JavaPlugin implements CommandExecutor, Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadConfig();
        this.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Configuration" + ChatColor.AQUA + " ❖ " + ChatColor.GREEN + " Enabled!");
        getCommand("weathereffect").setExecutor(this);
        weatherList();
    }

    @Override
    public void onDisable() {
        this.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Configuration" + ChatColor.AQUA + " ❖ " + ChatColor.RED + " Disabled!");
        // Plugin shutdown logic
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("weathereffect")) {
            if(args.length == 0) {
                if(sender.hasPermission("weathereffect.player")) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Configuration" + ChatColor.AQUA + " ❖ " + ChatColor.RED + " Unknown commands or No permissions...");
                    return true;
                }
            }
            if((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
                if(sender.hasPermission("weathereffect.reload")) {
                    reloadConfig();
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.AQUA + " ❖ " + ChatColor.GRAY + " Configuration" + ChatColor.AQUA + " ❖ " + ChatColor.GREEN + " Reloaded!");
                    return true;
                }
            }
        }
        return true;
    }

    public void weatherList() {
        Configuration configuration = getConfig();
        ConfigurationSection conf = configuration.getConfigurationSection("enabled-list");
        for (String key : conf.getKeys(false)) {
            ConfigurationSection effect = conf.getConfigurationSection(key);
            runnable(key, effect, conf);
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
            }
        }.runTaskTimer(this, 0, effect.getInt("ticks"));
    }
}
