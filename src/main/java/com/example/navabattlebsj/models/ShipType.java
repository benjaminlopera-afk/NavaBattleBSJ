package com.example.navabattlebsj.models;

/**
 * Constantes con los cuatro tipos de barco reglamentarios de la flota y la
 * cantidad de casillas que ocupa cada uno.
 */
public class ShipType {
    /** Portaaviones: ocupa 4 casillas. Hay 1 por flota. */
    public static final String PORTAAVIONES = "PORTAAVIONES";
    /** Submarino: ocupa 3 casillas. Hay 2 por flota. */
    public static final String SUBMARINO = "SUBMARINO";
    /** Destructor: ocupa 2 casillas. Hay 3 por flota. */
    public static final String DESTRUCTOR = "DESTRUCTOR";
    /** Fragata: ocupa 1 casilla. Hay 4 por flota. */
    public static final String FRAGATA = "FRAGATA";

    private ShipType() {
        // Clase de constantes, no se instancia
    }

    /**
     * Devuelve cuántas casillas ocupa un barco según su tipo.
     *
     * @param tipo uno de los valores definidos en esta clase
     * @return la cantidad de casillas que ocupa ese tipo de barco
     * @throws IllegalArgumentException si {@code tipo} no es un tipo válido
     */
    public static int getCasillas(String tipo) {
        switch (tipo) {
            case PORTAAVIONES: return 4;
            case SUBMARINO: return 3;
            case DESTRUCTOR: return 2;
            case FRAGATA: return 1;
            default: throw new IllegalArgumentException("Tipo de barco no válido: " + tipo);
        }
    }
}