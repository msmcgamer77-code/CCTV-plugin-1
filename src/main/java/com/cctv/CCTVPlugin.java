package com.cctv;

import com.cctv.commands.CCTVCommand;
import com.cctv.listeners.CameraPlaceListener;
import com.cctv.listeners.TheftListener;
import com.cctv.managers.CameraManager;
import com.cctv.managers.Recorder;
import org.bukkit.plugin.java.JavaPlugin;

public class CCTVPlugin extends JavaPlugin {

    private CameraManager cameraManager;
    private Recorder recorder;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.cameraManager = new CameraManager(this);
        this.recorder = new Recorder(this);

        getServer().getPluginManager().registerEvents(new CameraPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new TheftListener(this), this);

        getCommand("cctv").setExecutor(new CCTVCommand(this));

        getLogger().info("CCTVPlugin enabled! " + cameraManager.getAllCameras().size() + " camera(s) loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CCTVPlugin disabled.");
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public Recorder getRecorder() {
        return recorder;
    }
}
