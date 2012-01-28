package org.siklone.meteorshower;

import java.util.Map;
import java.util.Timer;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

public class MeteorShowerWorldListener extends WorldListener {

    private static MeteorShower plugin;

    public MeteorShowerWorldListener(MeteorShower instance) {
        plugin = instance;
    }

    public void onWorldLoad(WorldLoadEvent event) {
        if (plugin.activeShowers.get(event.getWorld()) == null) {
            plugin.activeShowers.put(event.getWorld(), Boolean.valueOf(false));
        }
        if (plugin.genericTimers.get(event.getWorld()) == null) {
            plugin.genericTimers.put(event.getWorld(), new Timer(true));
        }
        if (plugin.counterTimers.get(event.getWorld()) == null) {
            plugin.counterTimers.put(event.getWorld(), Integer.valueOf(0));
        }
    }
}
