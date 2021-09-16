package me.dummyperson.weathereffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        runnable();
        reloadConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player player = (Player) sender;
        if (!sender.hasPermission("weathereffect.reload") || !sender.isOp()) {
            if (alias.equalsIgnoreCase("weathereffect")) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.GREEN + "To reload do /weathereffect reload");
                } else if (args[0].equalsIgnoreCase("reload")) {
                    saveDefaultConfig();
                    reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "WeatherEffect reloaded!");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Unknown Command");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Permission Denied!");
        }
        return false;
    };

    public void  runnable() {
        new BukkitRunnable() {
            private WeatherEffectTask weathertask;

            @Override
            public void run() {
                this.weathertask = new WeatherEffectTask();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    for (int i = 0; i < getConfig().getInt("attempts"); i++) {
                        weathertask.criteriaChecker(player, getConfig());
                    }
                }
            }
        }.runTaskTimer(this, 0, getConfig().getInt("ticks"));
    }
}
