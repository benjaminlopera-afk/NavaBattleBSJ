package com.example.navabattlebsj.model;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private String type;
    private String orientation;
    private List<Position> occupiedPositions;
    private int hits;

    public Ship(String type, String orientation) {
        this.type = type;
        this.orientation = orientation;
        this.occupiedPositions = new ArrayList<>();
        this.hits = 0;
    }

    public String getType() { return type; }
    public String getOrientation() { return orientation; }
    public List<Position> getOccupiedPositions() { return occupiedPositions; }
    public int getHits() { return hits; }
}