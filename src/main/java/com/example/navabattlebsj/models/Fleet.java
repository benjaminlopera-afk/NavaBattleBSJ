package com.example.navabattlebsj.models;

import java.util.List;

public class Fleet {
    private List<Ship> ships;

    public Fleet(List<Ship> ships) {
        this.ships = ships;
    }

    public List<Ship> getShips() {
        return ships;
    }

    // La flota está completamente hundida cuando todos sus barcos lo están.
    // Esto lo vamos a necesitar en el HU-2/HU-4 para saber cuándo termina la partida.
    public boolean isFullySunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }
}