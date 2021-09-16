package me.dummyperson.weathereffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        runnable();
        reloadConfig();
        this.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + " ]" +  ChatColor.GREEN + " ⁑ Enabled!");
        getCommand("weathereffect").setExecutor(this);
    }

    @Override
    public void onDisable() {
        this.getLogger().info(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + " ]" + ChatColor.RED + " ⁑ Disabled!");
        // Plugin shutdown logic
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("weathereffect")) {
            if(args.length == 0) {
                if(sender.hasPermission("weathereffect.player")) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + "] " +  ChatColor.RED + " ⁑ Unknown commands or No permissions...");
                    return true;
                }
            }
            if((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
                if(sender.hasPermission("weathereffect.reload")) {
                    reloadConfig();
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "[ " + ChatColor.AQUA + "WeathereEffect" + ChatColor.LIGHT_PURPLE + "] " +  ChatColor.GREEN + " ⁑ Reloaded!");
                    return true;
                }
            }
        }
        return true;
    }


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
