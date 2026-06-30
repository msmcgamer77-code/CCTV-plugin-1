package com.cctv.gui;

import com.cctv.CCTVPlugin;
import com.cctv.managers.Camera;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class MonitorGUI {

    private final CCTVPlugin plugin;
    private final Camera camera;
    private final Inventory inventory;
    private BukkitTask updateTask;

    public MonitorGUI(CCTVPlugin plugin, Camera camera) {
        this.plugin = plugin;
        this.camera = camera;
        this.inventory = Bukkit.createInventory(null, 27,
                ChatColor.DARK_RED + "CCTV Monitor: " + ChatColor.YELLOW + camera.getName());
    }

    public Inventory open(Player viewer) {
        refresh();
        viewer.openInventory(inventory);
        startLiveUpdate(viewer);
        return inventory;
    }

    private void startLiveUpdate(Player viewer) {
        int interval = plugin.getConfig().getInt("scan-interval", 40);
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!viewer.isOnline() || !viewer.getOpenInventory().getTopInventory().equals(inventory)) {
                if (updateTask != null) updateTask.cancel();
                return;
            }
            refresh();
        }, 0L, interval);
    }

    private void refresh() {
        inventory.clear();
        double radius = plugin.getConfig().getDouble("detection-radius", 12);
        Location camLoc = camera.getLocation();

        List<Player> nearby = new ArrayList<>();
        for (Player p : camLoc.getWorld().getPlayers()) {
            if (p.getLocation().distance(camLoc) <= radius) {
                nearby.add(p);
            }
        }

        int slot = 0;
        for (Player p : nearby) {
            if (slot >= 27) break;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName(ChatColor.AQUA + p.getName());

            double dist = p.getLocation().distance(camLoc);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Distance: " + ChatColor.WHITE + String.format("%.1f", dist) + "m");
            lore.add(ChatColor.GRAY + "Location: " + ChatColor.WHITE
                    + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ());
            lore.add(ChatColor.GREEN + "● LIVE");
            meta.setLore(lore);

            head.setItemMeta(meta);
            inventory.setItem(slot, head);
            slot++;
        }

        if (nearby.isEmpty()) {
            ItemStack empty = new ItemStack(Material.BARRIER);
            var meta = empty.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "No players in range");
            empty.setItemMeta(meta);
            inventory.setItem(13, empty);
        }
    }
}
