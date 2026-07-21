package com.example.navabattlebsj.models;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private String type;
    private String orientation;
    private int size;
    private List<Position> occupiedPositions;
    private int hits;

    public Ship(String type, String orientation) {
        this.type = type;
        this.orientation = orientation;
        this.size = ShipType.getCasillas(type);
        this.occupiedPositions = new ArrayList<>();
        this.hits = 0;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    // Calcula qué posiciones ocuparía el barco si se coloca en "start",
    // sin modificar el estado del barco todavía (solo un cálculo).
    public List<Position> computePositions(Position start) {
        List<Position> positions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int row = start.getRow() + (orientation.equals(Orientation.VERTICAL) ? i : 0);
            int column = start.getColumn() + (orientation.equals(Orientation.HORIZONTAL) ? i : 0);
            positions.add(new Position(row, column));
        }
        return positions;
    }

    // Confirma la colocación: guarda las posiciones ya calculadas y validadas.
    public void place(List<Position> positions) {
        this.occupiedPositions.clear();
        this.occupiedPositions.addAll(positions);
    }

    public void registerHit() {
        hits++;
    }

    public boolean isSunk() {
        return hits >= size;
    }

    public String getType() { return type; }
    public String getOrientation() { return orientation; }
    public int getSize() { return size; }
    public List<Position> getOccupiedPositions() { return occupiedPositions; }
    public int getHits() { return hits; }
}