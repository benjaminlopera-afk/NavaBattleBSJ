package com.example.navabattlebsj.patterns;

/**
 * Interfaz funcional usada por {@link TurnContext} para notificar el
 * resultado de un disparo (fila, columna y resultado) a quien deba
 * pintarlo en el tablero correspondiente, sin acoplar el patrón de
 * comportamiento (Strategy) directamente a JavaFX.
 */
@FunctionalInterface
public interface ShotResultHandler {
    void onShotResult(int row, int col, String result);
}
