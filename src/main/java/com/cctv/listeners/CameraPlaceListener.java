package com.cctv.listeners;

import com.cctv.CCTVPlugin;
import com.cctv.managers.CameraManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CameraPlaceListener implements Listener {

    private final CCTVPlugin plugin;
    private final CameraManager cameraManager;

    public CameraPlaceListener(CCTVPlugin plugin) {
        this.plugin = plugin;
        this.cameraManager = plugin.getCameraManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null || !meta.getDisplayName().contains("CCTV Camera")) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        Location loc = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);

        String camName = "cam" + (cameraManager.getAllCameras().size() + 1);
        boolean added = cameraManager.addCamera(camName, loc, player.getName());

        if (added) {
            player.sendMessage(ChatColor.GREEN + "[CCTV] Camera placed: " + ChatColor.YELLOW + camName);
            item.setAmount(item.getAmount() - 1);
        } else {
            player.sendMessage(ChatColor.RED + "[CCTV] Failed to place camera (name conflict). Try again.");
        }
    }
                           }
