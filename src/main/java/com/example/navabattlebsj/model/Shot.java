package com.example.navabattlebsj.model;

public class Shot {
    private Position position;
    private String result;

    public Shot(Position position) {
        this.position = position;
    }

    public Position getPosition() { return position; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}