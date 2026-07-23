package com.example.navabattlebsj.models;

import java.io.Serializable;

public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    private HumanPlayer human;
    private MachinePlayer machine;
    private boolean finished;
    private boolean humanTurn;

    public Game(HumanPlayer human, MachinePlayer machine) {
        this.human = human;
        this.machine = machine;
        this.finished = false;
        this.humanTurn = true;
    }

    public HumanPlayer getHuman() { return human; }
    public MachinePlayer getMachine() { return machine; }
    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }
    public boolean isHumanTurn() { return humanTurn; }
    public void setHumanTurn(boolean humanTurn) { this.humanTurn = humanTurn; }
}