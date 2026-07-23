package com.example.navabattlebsj.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un barco de la flota (portaaviones, submarino, destructor o
 * fragata). Conoce su tamaño, orientación, las casillas que ocupa una vez
 * colocado, y cuántos impactos ha recibido.
 */
public class Ship implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private String orientation;
    private int size;
    private List<Position> occupiedPositions;
    private int hits;

    /**
     * Crea un barco del tipo y orientación indicados. El tamaño se calcula
     * automáticamente a partir del tipo (ver {@link ShipType#getCasillas}).
     * El barco se crea sin posiciones asignadas; hay que colocarlo con
     * {@link #place(List)} (normalmente vía {@link Board#placeShip}).
     *
     * @param type        uno de los valores de {@link ShipType}
     * @param orientation uno de los valores de {@link Orientation}
     */
    public Ship(String type, String orientation) {
        this.type = type;
        this.orientation = orientation;
        this.size = ShipType.getCasillas(type);
        this.occupiedPositions = new ArrayList<>();
        this.hits = 0;
    }

    /**
     * Cambia la orientación del barco (usado al rotarlo antes de colocarlo).
     *
     * @param orientation uno de los valores de {@link Orientation}
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    /**
     * Calcula qué posiciones ocuparía el barco si se colocara a partir de
     * {@code start}, según su tamaño y orientación actual. No modifica el
     * estado del barco; es un cálculo previo a la validación en
     * {@link Board#placeShip}.
     *
     * @param start posición inicial (esquina superior/izquierda del barco)
     * @return la lista de posiciones que ocuparía el barco
     */
    public List<Position> computePositions(Position start) {
        List<Position> positions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int row = start.getRow() + (orientation.equals(Orientation.VERTICAL) ? i : 0);
            int column = start.getColumn() + (orientation.equals(Orientation.HORIZONTAL) ? i : 0);
            positions.add(new Position(row, column));
        }
        return positions;
    }

    /**
     * Confirma la colocación del barco, guardando las posiciones ya
     * calculadas y validadas por {@link Board#placeShip}.
     *
     * @param positions posiciones finales que ocupa el barco en el tablero
     */
    public void place(List<Position> positions) {
        this.occupiedPositions.clear();
        this.occupiedPositions.addAll(positions);
    }

    /**
     * Registra un impacto recibido en alguna de las casillas del barco.
     */
    public void registerHit() {
        hits++;
    }

    /**
     * @return {@code true} si el barco ha recibido tantos impactos como
     *         casillas ocupa (es decir, si está hundido)
     */
    public boolean isSunk() {
        return hits >= size;
    }

    /**
     * @return el tipo de barco (ver {@link ShipType})
     */
    public String getType() { return type; }
    /**
     * @return la orientación actual del barco (ver {@link Orientation})
     */
    public String getOrientation() { return orientation; }
    /**
     * @return el tamaño del barco en casillas
     */
    public int getSize() { return size; }
    /**
     * @return las posiciones que ocupa actualmente el barco en el tablero
     */

    public List<Position> getOccupiedPositions() { return occupiedPositions; }
    /**
     * @return la cantidad de impactos recibidos hasta ahora
     */
    public int getHits() { return hits; }
}