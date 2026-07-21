package com.example.navabattlebsj.model;

public class ShipType {
    public static final String PORTAAVIONES = "PORTAAVIONES";
    public static final String SUBMARINO = "SUBMARINO";
    public static final String DESTRUCTOR = "DESTRUCTOR";
    public static final String FRAGATA = "FRAGATA";

    private ShipType() {
        // Clase de constantes, no se instancia
    }

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