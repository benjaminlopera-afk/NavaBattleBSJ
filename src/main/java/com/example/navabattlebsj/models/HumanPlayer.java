package com.example.navabattlebsj.models;

/**
 * Jugador controlado por una persona a través de la interfaz gráfica.
 */
public class HumanPlayer extends Player {
    /**
     * Crea el jugador humano con el apodo indicado.
     *
     * @param nickname nombre visible del jugador
     */
    public HumanPlayer(String nickname) {
        super(nickname);
    }
}