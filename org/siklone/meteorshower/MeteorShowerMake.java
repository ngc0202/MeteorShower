package org.siklone.meteorshower;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

public class MeteorShowerMake extends TimerTask {

    private double worldX;
    private double worldZ;
    private int duration;
    private Timer timer;
    private MeteorShower plugin;
    private Object w;
    private Random rand;

    public MeteorShowerMake(MeteorShower instance, double X, double Z, World world, Random randGen, int numBalls, Timer t) {
        this.worldX = X;
        this.worldZ = Z;
        this.plugin = instance;
        this.w = world;
        this.rand = randGen;
        this.timer = t;
        this.duration = numBalls;
    }

    public void run() {
        try {
            if (((Boolean) this.plugin.activeShowers.get((World) this.w)).booleanValue()) {
                if (((Integer) this.plugin.counterTimers.get((World) this.w)).intValue() <= this.duration) {
                    Location location = new Location((World) this.w, this.worldX + (this.rand.nextInt(240) - 120), 190.0D, this.worldZ + (this.rand.nextInt(240) - 120));
                    if (((String) this.plugin.read("shower.orientation")).equals("random")) {
                        location.setPitch(this.rand.nextInt(90) + 45);
                    } else if ((String) this.plugin.read("shower.orientation") == "straight") {
                        location.setPitch(80.0F);
                    } else {
                        location.setPitch(90.0F);
                    }
                    location.setYaw(this.rand.nextFloat());
                    Fireball fireball = (Fireball) ((World) this.w).spawn(location, Fireball.class);
                    synchronized (this.plugin.BALLS) {
                        this.plugin.BALLS.add(fireball);
                    }
                    fireball.setVelocity(new Vector(2.0D, 0.0D, 0.0D));
                    fireball.setYield(0.0F);
                } else {
                    this.timer.cancel();
                    this.plugin.BALLS.clear();
                    this.plugin.activeShowers.put((World) this.w, Boolean.valueOf(false));
                    System.out.println("[MeteorShower] The shower has ended!");
                    if (((Boolean) this.plugin.read("shower.warningMsgs")).booleanValue()) {
                        this.plugin.getServer().broadcastMessage(ChatColor.GREEN + "The meteor shower has passed!");
                    }
                }
            } else {
                this.timer.cancel();
                this.plugin.BALLS.clear();
                System.out.println("[MeteorShower] A meteor shower in world \"" + ((World) this.w).getName() + "\" has been stopped by an external event!");
                if (((Boolean) this.plugin.read("shower.warningMsgs")).booleanValue()) {
                    this.plugin.getServer().broadcastMessage(ChatColor.GREEN + "The meteor shower has passed!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
