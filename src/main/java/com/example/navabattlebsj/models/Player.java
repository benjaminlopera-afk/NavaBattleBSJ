package com.example.navabattlebsj.models;

import com.example.navabattlebsj.patterns.ShipFactory;

public abstract class Player {
    private String nickname;
    private Board positionBoard;
    private Fleet fleet;

    public Player(String nickname) {
        this.nickname = nickname;
        this.positionBoard = new Board();
        this.fleet = new Fleet(ShipFactory.createFleet());
    }

    public String getNickname() { return nickname; }
    public Board getPositionBoard() { return positionBoard; }
    public Fleet getFleet() { return fleet; }
}