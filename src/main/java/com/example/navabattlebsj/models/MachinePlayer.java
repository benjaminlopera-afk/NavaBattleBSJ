package com.example.navabattlebsj.models;

/**
 * Jugador controlado automáticamente por el sistema (HU-4): coloca su flota
 * y dispara de forma aleatoria, sin intervención del usuario.
 */
public class MachinePlayer extends Player {
    /**
     * Crea el jugador máquina con el apodo fijo "Maquina".
     */
    public MachinePlayer() {
        super("Maquina");
    }
}