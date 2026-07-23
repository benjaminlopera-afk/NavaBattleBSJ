package com.example.navabattlebsj.models;

/**
 * Constantes que representan los posibles estados de una {@link Cell} en un
 * {@link Board}.
 */
public class CellState {
    /** La celda no tiene barco ni ha recibido disparos. */
    public static final String VACIA = "VACIA";
    /** La celda tiene parte de un barco colocado, sin haber sido disparada. */
    public static final String BARCO = "BARCO";
    /** Se disparó sobre la celda y no había barco (fallo). */
    public static final String AGUA = "AGUA";
    /** Se disparó sobre la celda y había parte de un barco no hundido. */
    public static final String TOCADO = "TOCADO";
    /** Se disparó sobre la celda y el barco al que pertenece quedó hundido. */
    public static final String HUNDIDO = "HUNDIDO";

    private CellState() {}
}