package com.example.navabattlebsj.models;

/**
 * Representa un disparo individual: la posición sobre la que se disparó y
 * el resultado obtenido (ver {@link ShotResult}). No forma parte del flujo
 * principal de {@link Board#receiveShot}, que devuelve el resultado
 * directamente como {@code String}; esta clase queda disponible como
 * estructura de apoyo para un futuro historial de disparos por tablero.
 */
public class Shot {
    private Position position;
    private String result;

    /**
     * Crea un disparo sobre la posición indicada, sin resultado aún.
     *
     * @param position posición sobre la que se dispara
     */
    public Shot(Position position) {
        this.position = position;
    }

    /**
     * @return la posición sobre la que se realizó este disparo
     */
    public Position getPosition() { return position; }
    /**
     * @return el resultado de este disparo (ver {@link ShotResult}), o
     *         {@code null} si aún no se ha establecido
     */
    public String getResult() { return result; }
    /**
     * Establece el resultado de este disparo.
     *
     * @param result uno de los valores definidos en {@link ShotResult}
     */
    public void setResult(String result) { this.result = result; }
}