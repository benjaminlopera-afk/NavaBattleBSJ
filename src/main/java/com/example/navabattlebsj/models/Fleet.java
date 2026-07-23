package com.example.navabattlebsj.models;

import java.io.Serializable;
import java.util.List;

/**
 * Representa la flota completa de un jugador: 1 portaaviones, 2 submarinos,
 * 3 destructores y 4 fragatas (10 barcos en total, ver {@link ShipType}).
 * Normalmente se construye con {@link com.example.navabattlebsj.patterns.ShipFactory#createFleet()}.
 */
public class Fleet implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Ship> ships;

    /**
     * Crea una flota a partir de una lista de barcos ya construidos.
     *
     * @param ships los 10 barcos reglamentarios de la flota
     */
    public Fleet(List<Ship> ships) {
        this.ships = ships;
    }

    /**
     * @return la lista de barcos de esta flota
     */
    public List<Ship> getShips() {
        return ships;
    }

    /**
     * @return {@code true} si todos los barcos de la flota están hundidos
     */
    public boolean isFullySunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }
}