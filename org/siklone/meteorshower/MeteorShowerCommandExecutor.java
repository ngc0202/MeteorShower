package org.siklone.meteorshower;

import java.util.Random;
import java.util.Timer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MeteorShowerCommandExecutor
        implements CommandExecutor {

    private static MeteorShower plugin;

    public MeteorShowerCommandExecutor(MeteorShower instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("mshower")) {
            if (args.length <= 0) {
                return false;
            }
            if (!((Player) sender).isOp()) {
                sender.sendMessage(ChatColor.RED + "You must be an operator to perform this command!");
                return true;
            }
            if (args[0].equalsIgnoreCase("start")) {
                if (args.length != 6) {
                    return false;
                }
                try {
                    Integer.parseInt(args[1]);
                    Integer.parseInt(args[2]);
                    Integer.parseInt(args[3]);
                    Integer.parseInt(args[4]);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "One of your integer-value arguments is invalid!");
                    return false;
                }

                for (String s : args) {
                    s = s.trim();
                }
                if (plugin.getServer().getWorld(args[5]) == null) {
                    sender.sendMessage(ChatColor.RED + "That world either does not exist or is not loaded by the server!");
                    return true;
                }
                World wrld = plugin.getServer().getWorld(args[5]);
                if (!((Boolean) plugin.activeShowers.get(wrld)).booleanValue()) {
                    plugin.genericTimers.put(wrld, new Timer(true));
                    int numBalls = Integer.parseInt(args[1]);
                    int durationMil = Integer.parseInt(args[2]);
                    if (((Boolean) plugin.read("shower.warningMsgs")).booleanValue()) {
                        plugin.getServer().broadcastMessage(ChatColor.GREEN + ((Player) sender).getDisplayName() + " has initiated a meteor-shower event!");
                    }
                    plugin.activeShowers.put(wrld, Boolean.valueOf(true));
                    ((Timer) plugin.genericTimers.get(wrld)).schedule(new MeteorShowerMake(plugin, Integer.parseInt(args[3]), Integer.parseInt(args[4]), wrld, new Random(), numBalls, (Timer) plugin.genericTimers.get(wrld)), durationMil / numBalls, durationMil / numBalls);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("stop")) {
                if (args.length != 2) {
                    return false;
                }
                if (plugin.getServer().getWorld(args[1]) == null) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid world!");
                    return true;
                }
                try {
                    if (((Boolean) plugin.activeShowers.get(plugin.getServer().getWorld(args[1]))).booleanValue()) {
                        ((Timer) plugin.genericTimers.get(plugin.getServer().getWorld(args[1]))).cancel();
                        plugin.activeShowers.put(plugin.getServer().getWorld(args[1]), Boolean.valueOf(false));
                        sender.sendMessage(ChatColor.RED + "Meteor shower successfully stopped!");
                        System.out.println("[MeteorShower] The shower has ended!");
                        if (((Boolean) plugin.read("shower.warningMsgs")).booleanValue()) {
                            plugin.getServer().broadcastMessage(ChatColor.GREEN + "The meteor shower has passed!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "No meteor-shower event is currently active in that world!");
                    }
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Unable to halt the shower!");
                }
                return true;
            }
        }
        return false;
    }
}
