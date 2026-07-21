package com.example.navabattlebsj.model;

public class Cell {
    private Position position;
    private String state;

    public Cell(Position position) {
        this.position = position;
        this.state = CellState.VACIA;
    }

    public Position getPosition() { return position; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}