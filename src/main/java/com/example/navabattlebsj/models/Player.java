package com.example.navabattlebsj.models;

import com.example.navabattlebsj.patterns.ShipFactory;

import java.io.Serializable;

/**
 * Representa a un jugador de la partida (humano o máquina). Cada jugador
 * tiene un apodo, su propio tablero de posición y su propia flota completa,
 * creada automáticamente mediante {@link ShipFactory#createFleet()}.
 *
 * @see HumanPlayer
 * @see MachinePlayer
 */
public abstract class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname;
    private Board positionBoard;
    private Fleet fleet;

    /**
     * Crea un jugador con el apodo indicado, un tablero de posición vacío y
     * una flota reglamentaria recién generada.
     *
     * @param nickname nombre visible del jugador
     */
    public Player(String nickname) {
        this.nickname = nickname;
        this.positionBoard = new Board();
        this.fleet = new Fleet(ShipFactory.createFleet());
    }

    /**
     * @return el apodo de este jugador
     */
    public String getNickname() { return nickname; }
    /**
     * @return el tablero donde este jugador tiene colocada su flota
     */
    public Board getPositionBoard() { return positionBoard; }
    /**
     * @return la flota de este jugador
     */
    public Fleet getFleet() { return fleet; }
}