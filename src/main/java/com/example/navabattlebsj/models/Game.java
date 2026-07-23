package com.example.navabattlebsj.models;

import java.io.Serializable;

/**
 * Representa el estado global de una partida: los dos jugadores (humano y
 * máquina), si la partida ya terminó, y de quién es el turno actual.
 * Implementa {@link Serializable} para poder guardarse completa dentro de
 * un {@code GameState} (HU-5), incluyendo por herencia ambos tableros y
 * flotas.
 */
public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    private HumanPlayer human;
    private MachinePlayer machine;
    private boolean finished;
    private boolean humanTurn;

    /**
     * Crea una nueva partida entre el jugador humano y la máquina. El turno
     * inicial siempre es del humano.
     *
     * @param human   jugador humano
     * @param machine jugador máquina
     */
    public Game(HumanPlayer human, MachinePlayer machine) {
        this.human = human;
        this.machine = machine;
        this.finished = false;
        this.humanTurn = true;
    }

    /**
     * @return el jugador humano de esta partida
     */
    public HumanPlayer getHuman() { return human; }
    /**
     * @return el jugador máquina de esta partida
     */
    public MachinePlayer getMachine() { return machine; }
    /**
     * @return {@code true} si la partida ya terminó (alguna de las dos
     *         flotas fue hundida por completo)
     */
    public boolean isFinished() { return finished; }
    /**
     * Marca la partida como terminada o en curso.
     *
     * @param finished {@code true} si la partida ya terminó
     */
    public void setFinished(boolean finished) { this.finished = finished; }
    /**
     * @return {@code true} si le corresponde disparar al jugador humano;
     *         {@code false} si le corresponde a la máquina
     */
    public boolean isHumanTurn() { return humanTurn; }
    /**
     * Cambia de quién es el turno actual.
     *
     * @param humanTurn {@code true} para ceder el turno al humano,
     *                  {@code false} para cedérselo a la máquina
     */
    public void setHumanTurn(boolean humanTurn) { this.humanTurn = humanTurn; }
}