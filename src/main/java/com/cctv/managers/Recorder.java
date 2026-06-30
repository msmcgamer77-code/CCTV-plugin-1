package com.cctv.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Recorder {

    private final JavaPlugin plugin;
    private final File recordingsFolder;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public Recorder(JavaPlugin plugin) {
        this.plugin = plugin;
        this.recordingsFolder = new File(plugin.getDataFolder(), "recordings");
        if (!recordingsFolder.exists()) recordingsFolder.mkdirs();
    }

    /**
     * Writes one line to the camera's recording file for today.
     * Format: [HH:mm:ss] PlayerName - ACTION - details
     */
    public void record(String cameraName, String playerName, String action, String details) {
        File camFolder = new File(recordingsFolder, cameraName);
        if (!camFolder.exists()) camFolder.mkdirs();

        String fileName = dateFormat.format(new Date()) + ".log";
        File logFile = new File(camFolder, fileName);

        String line = "[" + timeFormat.format(new Date()) + "] " + playerName
                + " - " + action + " - " + details;

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(line + System.lineSeparator());
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write recording for camera " + cameraName + ": " + e.getMessage());
        }
    }

    /**
     * Returns the last N lines from today's log file for a camera.
     */
    public List<String> getRecentLines(String cameraName, int maxLines) {
        File camFolder = new File(recordingsFolder, cameraName);
        String fileName = dateFormat.format(new Date()) + ".log";
        File logFile = new File(camFolder, fileName);

        if (!logFile.exists()) return List.of();

        try {
            List<String> allLines = Files.readAllLines(logFile.toPath());
            int from = Math.max(0, allLines.size() - maxLines);
            return allLines.subList(from, allLines.size());
        } catch (IOException e) {
            return List.of("Error reading log: " + e.getMessage());
        }
    }
          }
