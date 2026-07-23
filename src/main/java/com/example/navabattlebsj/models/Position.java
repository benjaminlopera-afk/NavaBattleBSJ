package com.example.navabattlebsj.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa una coordenada (fila, columna) dentro de un {@link Board} de
 * 10x10. Es un objeto de valor inmutable: una vez creado, su fila y columna
 * no cambian.
 */
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;

    private int row;
    private int column;

    /**
     * Crea una nueva posición.
     *
     * @param row    fila (0 a {@link Board#SIZE} - 1)
     * @param column columna (0 a {@link Board#SIZE} - 1)
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * @return la fila de esta posición
     */
    public int getRow() { return row; }
    /**
     * @return la columna de esta posición
     */
    public int getColumn() { return column; }

    /**
     * Dos posiciones son iguales si tienen la misma fila y la misma columna.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}