package com.example.navabattlebsj.models;

public class Game {
    private HumanPlayer human;
    private MachinePlayer machine;
    private boolean finished;

    public Game(HumanPlayer human, MachinePlayer machine) {
        this.human = human;
        this.machine = machine;
        this.finished = false;
    }

    public HumanPlayer getHuman() { return human; }
    public MachinePlayer getMachine() { return machine; }
    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }
}