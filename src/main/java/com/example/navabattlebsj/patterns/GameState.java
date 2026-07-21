package com.example.navabattlebsj.patterns;

import com.example.navabattlebsj.models.Game;

import java.io.Serializable;

/**
 * Representa una fotografía completa y serializable del estado de la partida
 * en un momento dado (HU-5). Envuelve el objeto {@link Game}, que a su vez
 * contiene ambos tableros, ambas flotas y el turno actual.
 */

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Game game;

    public GameState(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}