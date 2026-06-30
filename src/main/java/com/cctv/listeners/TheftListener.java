package com.cctv.listeners;

import com.cctv.CCTVPlugin;
import com.cctv.managers.Camera;
import com.cctv.managers.CameraManager;
import com.cctv.managers.Recorder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.block.Container;

import java.util.Map;

public class TheftListener implements Listener {

    private final CCTVPlugin plugin;
    private final CameraManager cameraManager;
    private final Recorder recorder;

    public TheftListener(CCTVPlugin plugin) {
        this.plugin = plugin;
        this.cameraManager = plugin.getCameraManager();
        this.recorder = plugin.getRecorder();
    }

    private double radius() {
        return plugin.getConfig().getDouble("detection-radius", 12);
    }

    /** Finds the nearest camera within radius of a location, or null if none. */
    private Camera findNearbyCamera(Location loc) {
        Camera closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Map.Entry<String, Camera> entry : cameraManager.getAllCameras().entrySet()) {
            Camera cam = entry.getValue();
            if (!cam.getLocation().getWorld().equals(loc.getWorld())) continue;
            double dist = cam.getLocation().distance(loc);
            if (dist <= radius() && dist < closestDist) {
                closest = cam;
                closestDist = dist;
            }
        }
        return closest;
    }

    private void alert(Camera cam, Player player, String action, String details) {
        recorder.record(cam.getName(), player.getName(), action, details);

        if (!plugin.getConfig().getBoolean("alert-staff", true)) return;

        String msg = ChatColor.RED + "[CCTV] " + ChatColor.YELLOW + player.getName()
                + ChatColor.RED + " - " + action + ChatColor.GRAY + " (" + cam.getName() + ") "
                + ChatColor.WHITE + details;

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("cctv.admin")) {
                staff.sendMessage(msg);
                if (plugin.getConfig().getBoolean("alert-sound", true)) {
                    staff.playSound(staff.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.5f);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Camera cam = findNearbyCamera(block.getLocation());
        if (cam == null) return;
        alert(cam, event.getPlayer(), "BLOCK_BREAK",
                block.getType().name() + " at " + formatLoc(block.getLocation()));
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Container container)) return;

        Location loc = container.getLocation();
        Camera cam = findNearbyCamera(loc);
        if (cam == null) return;
        alert(cam, player, "CONTAINER_OPEN",
                container.getBlock().getType().name() + " at " + formatLoc(loc));
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Location loc = player.getLocation();
        Camera cam = findNearbyCamera(loc);
        if (cam == null) return;
        alert(cam, player, "ITEM_PICKUP",
                event.getItem().getItemStack().getType().name()
                        + " x" + event.getItem().getItemStack().getAmount());
    }

    private String formatLoc(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
    }
