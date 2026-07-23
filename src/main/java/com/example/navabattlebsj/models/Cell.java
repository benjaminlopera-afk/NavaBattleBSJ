package com.example.navabattlebsj.models;

import java.io.Serializable;

/**
 * Representa una única casilla de un {@link Board}. Guarda su posición y su
 * estado actual, uno de los definidos en {@link CellState} (VACIA, BARCO,
 * AGUA, TOCADO, HUNDIDO).
 */
public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;

    private Position position;
    private String state;

    /**
     * Crea una celda vacía en la posición indicada.
     *
     * @param position posición de la celda dentro del tablero
     */
    public Cell(Position position) {
        this.position = position;
        this.state = CellState.VACIA;
    }

    /**
     * @return la posición de esta celda dentro del tablero
     */
    public Position getPosition() { return position; }
    /**
     * @return el estado actual de la celda (ver {@link CellState})
     */
    public String getState() { return state; }
    /**
     * Cambia el estado de la celda.
     *
     * @param state nuevo estado, uno de los definidos en {@link CellState}
     */
    public void setState(String state) { this.state = state; }
}