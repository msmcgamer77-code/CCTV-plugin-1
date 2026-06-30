package com.cctv.commands;

import com.cctv.CCTVPlugin;
import com.cctv.gui.MonitorGUI;
import com.cctv.managers.Camera;
import com.cctv.managers.CameraManager;
import com.cctv.managers.Recorder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CCTVCommand implements CommandExecutor {

    private final CCTVPlugin plugin;
    private final CameraManager cameraManager;
    private final Recorder recorder;

    public CCTVCommand(CCTVPlugin plugin) {
        this.plugin = plugin;
        this.cameraManager = plugin.getCameraManager();
        this.recorder = plugin.getRecorder();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("cctv.use")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /cctv <give|list|monitor|remove|log> [name]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give" -> handleGive(sender);
            case "list" -> handleList(sender);
            case "monitor" -> handleMonitor(sender, args);
            case "remove" -> handleRemove(sender, args);
            case "log" -> handleLog(sender, args);
            default -> sender.sendMessage(ChatColor.YELLOW + "Usage: /cctv <give|list|monitor|remove|log> [name]");
        }
        return true;
    }

    private void handleGive(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this.");
            return;
        }
        ItemStack item = new ItemStack(Material.OBSERVER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "CCTV Camera");
        meta.setLore(List.of(ChatColor.GRAY + "Right-click a block to place a camera"));
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        player.sendMessage(ChatColor.GREEN + "[CCTV] You received a camera item.");
    }

    private void handleList(CommandSender sender) {
        if (cameraManager.getAllCameras().isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No cameras placed yet.");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "=== CCTV Cameras ===");
        for (Camera cam : cameraManager.getAllCameras().values()) {
            sender.sendMessage(ChatColor.YELLOW + "- " + cam.getName() + ChatColor.GRAY
                    + " @ " + cam.getLocation().getWorld().getName() + " "
                    + cam.getLocation().getBlockX() + "," + cam.getLocation().getBlockY() + "," +
