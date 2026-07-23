package com.example.navabattlebsj.models;

/**
 * Constantes que representan el resultado posible de un disparo sobre un
 * {@link Board} (ver {@link Board#receiveShot(Position)}). Comparten los
 * mismos valores que {@link CellState}, ya que un disparo deja la celda en
 * uno de esos tres estados finales.
 */
public class ShotResult {
    /** El disparo cayó en una celda sin barco. */
    public static final String AGUA = "AGUA";
    /** El disparo impactó un barco que aún tiene casillas sin tocar. */
    public static final String TOCADO = "TOCADO";
    /** El disparo impactó la última casilla sin tocar de un barco, hundiéndolo. */
    public static final String HUNDIDO = "HUNDIDO";

    private ShotResult() {}
}