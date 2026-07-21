package com.example.navabattlebsj.patterns;

import com.example.navabattlebsj.models.Orientation;
import com.example.navabattlebsj.models.Ship;
import com.example.navabattlebsj.models.ShipType;

import java.util.ArrayList;
import java.util.List;

public class ShipFactory {

    public static Ship createShip(String type, String orientation) {
        return new Ship(type, orientation);
    }

    // Crea la flota completa reglamentaria: 1 portaaviones, 2 submarinos,
    // 3 destructores y 4 fragatas, todos en orientación horizontal por defecto
    // (el jugador podrá rotarlos luego desde la interfaz).
    public static List<Ship> createFleet() {
        List<Ship> ships = new ArrayList<>();

        ships.add(createShip(ShipType.PORTAAVIONES, Orientation.HORIZONTAL));

        for (int i = 0; i < 2; i++) {
            ships.add(createShip(ShipType.SUBMARINO, Orientation.HORIZONTAL));
        }
        for (int i = 0; i < 3; i++) {
            ships.add(createShip(ShipType.DESTRUCTOR, Orientation.HORIZONTAL));
        }
        for (int i = 0; i < 4; i++) {
            ships.add(createShip(ShipType.FRAGATA, Orientation.HORIZONTAL));
        }

        return ships;
    }
}