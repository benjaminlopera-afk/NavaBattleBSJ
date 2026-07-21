package com.example.navabattlebsj.model;

public class Board {
    public static final int SIZE = 10;
    private Cell[][] grid;

    public Board() {
        grid = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Cell(new Position(i, j));
            }
        }
    }

    public Cell[][] getGrid() { return grid; }
}