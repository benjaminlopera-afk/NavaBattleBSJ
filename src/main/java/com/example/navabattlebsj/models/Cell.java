package com.example.navabattlebsj.models;

import java.io.Serializable;

public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;

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