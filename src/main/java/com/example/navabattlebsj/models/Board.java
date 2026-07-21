package com.example.navabattlebsj.models;

import com.example.navabattlebsj.exceptions.InvalidShipPositionException;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int SIZE = 10;
    private Cell[][] grid;
    private List<Ship> ships;

    public Board() {
        grid = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Cell(new Position(i, j));
            }
        }
        ships = new ArrayList<>();
    }

    public Cell[][] getGrid() { return grid; }
    public List<Ship> getShips() { return ships; }

    private boolean isInsideBoard(Position position) {
        return position.getRow() >= 0 && position.getRow() < SIZE
                && position.getColumn() >= 0 && position.getColumn() < SIZE;
    }

    public Cell getCell(Position position) {
        return grid[position.getRow()][position.getColumn()];
    }

    public void placeShip(Ship ship, Position start) throws InvalidShipPositionException {
        List<Position> positions = ship.computePositions(start);

        // 1. Validar que todas las casillas estén dentro del tablero
        for (Position p : positions) {
            if (!isInsideBoard(p)) {
                throw new InvalidShipPositionException(
                        "El barco se sale del tablero en la posición (" + p.getRow() + ", " + p.getColumn() + ")");
            }
        }

        // 2. Validar que ninguna casilla esté ocupada por otro barco
        for (Position p : positions) {
            if (getCell(p).getState().equals(CellState.BARCO)) {
                throw new InvalidShipPositionException(
                        "Ya hay un barco en la posición (" + p.getRow() + ", " + p.getColumn() + ")");
            }
        }

        // 3. Si todo es válido, se coloca el barco
        ship.place(positions);
        for (Position p : positions) {
            getCell(p).setState(CellState.BARCO);
        }
        ships.add(ship);
    }
}