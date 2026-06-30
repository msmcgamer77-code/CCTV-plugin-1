package com.cctv.managers;

import org.bukkit.Location;

public class Camera {

    private final String name;
    private final Location location;
    private final String placedBy;

    public Camera(String name, Location location, String placedBy) {
        this.name = name;
        this.location = location;
        this.placedBy = placedBy;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public String getPlacedBy() {
        return placedBy;
    }
}
