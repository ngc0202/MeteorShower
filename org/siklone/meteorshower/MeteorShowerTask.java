package org.siklone.meteorshower;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MeteorShowerTask extends Thread
        implements Runnable {

    Random rand = new Random();
    ArrayList<Double> playerX = new ArrayList();
    ArrayList<Double> playerZ = new ArrayList();
    private static MeteorShower plugin;

    public MeteorShowerTask(MeteorShower instance) {
        plugin = instance;
    }

    @Override
    public void run() {
        String[] exclude = ((String) plugin.read("shower.worldsToExclude")).split(",");
        for (String str : exclude) {
            str = str.replaceAll(",", "").trim();
        }
        List<World> serverWorlds = plugin.getServer().getWorlds();
        for (World w : serverWorlds) {
            plugin.counterTimers.put(w, Integer.valueOf(0));
            plugin.genericTimers.put(w, new Timer(true));
            for (String str : exclude) {
                if (!w.getName().equals(str)) {
                    continue;
                }
                System.out.println("[MeteorShower] World \"" + w.getName() + "\" excluded from meteor-showers!");
            }
            double worldX = Double.valueOf(0.0D).doubleValue();
            double worldZ = Double.valueOf(0.0D).doubleValue();
            List<Player> players = w.getPlayers();
            if (players.isEmpty()) {
                continue;
            }
            for (Player p : players) {
                this.playerX.add(Double.valueOf(p.getLocation().getX()));
                this.playerZ.add(Double.valueOf(p.getLocation().getZ()));
            }
            if ((this.playerX.isEmpty()) || (this.playerZ.isEmpty())) {
                continue;
            }
            double finalVal = Double.valueOf(0.0D).doubleValue();
            int amnt = 0;
            for (; amnt < this.playerX.toArray().length; amnt++) {
                finalVal += ((Double) this.playerX.get(amnt)).doubleValue();
            }
            worldX = Double.valueOf(finalVal / amnt).doubleValue();
            amnt = 0;
            finalVal = Double.valueOf(0.0D).doubleValue();
            for (; amnt < this.playerZ.toArray().length; amnt++) {
                finalVal += ((Double) this.playerZ.get(amnt)).doubleValue();
            }
            worldZ = Double.valueOf(finalVal / amnt).doubleValue();

            if ((((Integer) plugin.read("shower.chance")).intValue() >= this.rand.nextInt(10000) + 1) && (w.getEnvironment() != World.Environment.NETHER)) {
                if (((Boolean) plugin.activeShowers.get(w)).booleanValue()) {
                    System.out.println("[MeteorShower] A shower is already active in world \"" + w.getName() + "\". Skipping...");
                } else {
                    try {
                        if (((Boolean) plugin.read("shower.warningMsgs")).booleanValue()) {
                            plugin.getServer().broadcastMessage(ChatColor.RED + "WARNING: " + ChatColor.GREEN + "A meteor shower is imminent for world \"" + w.getName() + "\"!");
                            plugin.getServer().broadcastMessage(ChatColor.GREEN + "An estimated " + ChatColor.RED + plugin.read("shower.meteors") + ChatColor.GREEN + " meteors are expected to drop within " + ChatColor.RED + plugin.read("shower.duration") + ChatColor.GREEN + " seconds!");
                        }
                    } catch (Exception e) {
                        System.out.println("[MeteorShower] There is a problem with your config.yml file! The 'shower.warningMsgs' node contains an incorrect boolean value! Fix this immediately!");
                    }
                    System.out.println("[MeteorShower] A meteor shower event has been initiated!");
                    if ((!isInt(plugin.read("shower.duration"))) || (!isInt(plugin.read("shower.meteors")))) {
                        System.out.println("[MeteorShower] Failed to generate meteor shower. Bad config.yml value!");
                    } else {
                        int numBall = ((Integer) plugin.read("shower.meteors")).intValue();
                        int numBalls = numBall;
                        int durationSec = ((Integer) plugin.read("shower.duration")).intValue();
                        int durationMil = durationSec * 1000;
                        plugin.activeShowers.put(w, Boolean.valueOf(true));
                        ((Timer) plugin.genericTimers.get(w)).schedule(new MeteorShowerMake(plugin, worldX, worldZ, w, this.rand, numBalls, (Timer) plugin.genericTimers.get(w)), durationMil / numBalls, durationMil / numBalls);
                    }
                }
            } else {
                this.playerX.clear();
                this.playerZ.clear();
            }
        }
    }

    private boolean isInt(Object value) {
        if ((value instanceof String)) {
            try {
                Integer.parseInt((String) value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
