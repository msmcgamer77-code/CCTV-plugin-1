package com.cctv.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CameraManager {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;
    private final Map<String, Camera> cameras = new LinkedHashMap<>();

    public CameraManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "cameras.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create cameras.yml: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        loadAll();
    }

    private void loadAll() {
        if (config.getConfigurationSection("cameras") == null) return;
        for (String name : config.getConfigurationSection("cameras").getKeys(false)) {
            String path = "cameras." + name + ".";
            World world = Bukkit.getWorld(config.getString(path + "world"));
            if (world == null) continue;
            double x = config.getDouble(path + "x");
            double y = config.getDouble(path + "y");
            double z = config.getDouble(path + "z");
            String placedBy = config.getString(path + "placedBy", "unknown");
            Location loc = new Location(world, x, y, z);
            cameras.put(name.toLowerCase(), new Camera(name, loc, placedBy));
        }
    }

    public boolean addCamera(String name, Location loc, String placedBy) {
        String key = name.toLowerCase();
        if (cameras.containsKey(key)) return false;
        cameras.put(key, new Camera(name, loc, placedBy));
        String path = "cameras." + name + ".";
        config.set(path + "world", loc.getWorld().getName());
        config.set(path + "x", loc.getX());
        config.set(path + "y", loc.getY());
        config.set(path + "z", loc.getZ());
        config.set(path + "placedBy", placedBy);
        save();
        return true;
    }

    public boolean removeCamera(String name) {
        String key = name.toLowerCase();
        if (!cameras.containsKey(key)) return false;
        Camera cam = cameras.get(key);
        cameras.remove(key);
        config.set("cameras." + cam.getName(), null);
        save();
        return true;
    }

    public Camera getCamera(String name) {
        return cameras.get(name.toLowerCase());
    }

    public Map<String, Camera> getAllCameras() {
        return cameras;
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save cameras.yml: " + e.getMessage());
        }
    }
      }
