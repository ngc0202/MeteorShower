package org.siklone.meteorshower;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class MeteorShowerEntityListener implements Listener {

    protected static MeteorShower plugin;

    public MeteorShowerEntityListener(MeteorShower plugin){
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (((event.getEntity() instanceof Fireball))
                && (event.getEntity().getLocation().getWorld().getEnvironment() != World.Environment.NETHER)
                && (plugin.BALLS.contains((Fireball) event.getEntity()))) {
            boolean isFire = false;
            try {
                isFire = ((Boolean) plugin.read("shower.setfire")).booleanValue();
            } catch (Exception localException) {
            }
            event.setFire(isFire);
            int rad = ((Integer) plugin.read("shower.blastradii")).intValue();
            int radii = rad;
            event.setRadius(radii);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (((event.getEntity() instanceof Fireball))
                && (event.getLocation().getWorld().getEnvironment() != World.Environment.NETHER)
                && (plugin.BALLS.contains((Fireball) event.getEntity()))) {
            event.setYield(0.0F);
            if (((Boolean) plugin.read("shower.slimySurprise")).booleanValue()) {
                Location loc = event.getLocation();
                Random rand = new Random();
                if ((5 > rand.nextInt(100000))
                        && (((Integer) plugin.read("shower.blastradii")).intValue() >= 5)) {
                    loc.setY(loc.getY() + 5.0D);
                    double originalZ = loc.getZ();
                    double originalX = loc.getX();
                    int howManySlimes = rand.nextInt(20);
                    int slimesSpawned = 0;
                    do {
                        loc.setX(rand.nextInt(5) - 3 + originalX);
                        loc.setZ(rand.nextInt(5) - 3 + originalZ);
                        event.getEntity().getWorld().spawn(loc, Slime.class);
                        slimesSpawned++;
                    } while (slimesSpawned < howManySlimes);
                }
            }

            plugin.counterTimers.put(event.getEntity().getWorld(), Integer.valueOf(((Integer) plugin.counterTimers.get(event.getEntity().getWorld())).intValue() + 1));
        }
    }
}
