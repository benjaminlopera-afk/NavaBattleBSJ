package com.example.navabattlebsj.models;

/**
 * Constantes que representan la orientación de un {@link Ship} sobre el
 * tablero: horizontal (crece en columnas) o vertical (crece en filas).
 */
public class Orientation {
    /** El barco ocupa varias columnas consecutivas en la misma fila. */
    public static final String HORIZONTAL = "HORIZONTAL";
    /** El barco ocupa varias filas consecutivas en la misma columna. */
    public static final String VERTICAL = "VERTICAL";

    private Orientation() {}
}