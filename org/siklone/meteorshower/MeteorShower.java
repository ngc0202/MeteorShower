package org.siklone.meteorshower;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class MeteorShower extends JavaPlugin {

    private Configuration YAMLcon;
    protected Map<World, Integer> counterTimers = new HashMap();
    public MeteorShowerTask meteors = new MeteorShowerTask(this);
    protected Map<World, Timer> genericTimers = new HashMap();
    public Map<World, Boolean> activeShowers = new HashMap();
    public ArrayList<Fireball> BALLS;
    private final MeteorShowerEntityListener entityListener = new MeteorShowerEntityListener(this);

    public void onEnable() {
        try {
            new File("plugins" + File.separator + "MeteorShower").mkdir();
        } catch (Exception e) {
            return;
        }
        File file = new File("plugins" + File.separator + "MeteorShower" + File.separator + "config.yml");
        this.YAMLcon = load();
        if (!file.exists()) {
            try {
                file.createNewFile();
                this.YAMLcon.setProperty("shower.duration", Integer.valueOf(900));
                this.YAMLcon.setProperty("shower.meteors", Integer.valueOf(3600));
                this.YAMLcon.setProperty("shower.checkrate", Integer.valueOf(30));
                this.YAMLcon.setProperty("shower.blastradii", Integer.valueOf(10));
                this.YAMLcon.setProperty("shower.setfire", Boolean.valueOf(true));
                this.YAMLcon.setProperty("shower.chance", Integer.valueOf(150));
                this.YAMLcon.setProperty("shower.orientation", "random");
                this.YAMLcon.setProperty("shower.worldsToExclude", "");
                this.YAMLcon.setProperty("shower.warningMsgs", Boolean.valueOf(true));
                this.YAMLcon.setProperty("shower.slimySurprise", Boolean.valueOf(true));
                this.YAMLcon.save();
            } catch (Exception e) {
                return;
            }
        }
        System.out.println("[MeteorShower] version " + getDescription().getVersion() + " has been enabled!");
        World w;
        for (Iterator localIterator = getServer().getWorlds().iterator(); localIterator.hasNext(); this.genericTimers.put(w, new Timer(true))) {
            w = (World) localIterator.next();
            this.counterTimers.put(w, Integer.valueOf(0));
            this.activeShowers.put(w, Boolean.valueOf(false));
        }
        if (((Integer) read("shower.checkrate")).intValue() * 60 >= ((Integer) read("shower.duration")).intValue()) {
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, this.meteors, ((Integer) read("shower.checkrate")).intValue() * 60 * 20, ((Integer) read("shower.checkrate")).intValue() * 60 * 20);
        } else {
            System.out.println("[MeteorShower] No tasks were scheduled, because your duration is longer than, or equal to, your check-rate variable in your config.yml file! Please fix this immediately!");
            getPluginLoader().disablePlugin(this);
            return;
        }
//        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_EXPLODE, this.entityListener, Event.Priority.Normal, this);
//        getServer().getPluginManager().registerEvent(Event.Type.EXPLOSION_PRIME, this.entityListener, Event.Priority.Normal, this);
//        getServer().getPluginManager().registerEvent(Event.Type.WORLD_LOAD, new MeteorShowerWorldListener(this), Event.Priority.Normal, this);
        this.getServer().getPluginManager().registerEvents(entityListener, this);
        getServer().getPluginCommand("mshower").setExecutor(new MeteorShowerCommandExecutor(this));
        BALLS = new ArrayList<Fireball>();
    }

    private Configuration load() {
        try {
            Configuration config = getConfiguration();
            config.load();
            return config;
        } catch (Exception e) {
        }
        return null;
    }

    public Object read(String root) {
        return this.YAMLcon.getProperty(root);
    }

    public void onDisable() {
        System.out.println("[MeteorShower] has been disabled!");
    }
}