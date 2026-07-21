package com.example.navabattlebsj.model;

import java.util.ArrayList;
import java.util.List;

public class Fleet {
    private List<Ship> ships;

    public Fleet() {
        this.ships = new ArrayList<>();
    }

    public List<Ship> getShips() { return ships; }
}