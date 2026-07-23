package com.example.navabattlebsj.models;

import com.example.navabattlebsj.exceptions.GameAlreadyFinishedException;
import com.example.navabattlebsj.exceptions.InvalidMoveException;
import com.example.navabattlebsj.exceptions.InvalidShipPositionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

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

        for (Position p : positions) {
            if (!isInsideBoard(p)) {
                throw new InvalidShipPositionException(
                        "El barco se sale del tablero en la posición (" + p.getRow() + ", " + p.getColumn() + ")");
            }
        }

        for (Position p : positions) {
            if (getCell(p).getState().equals(CellState.BARCO)) {
                throw new InvalidShipPositionException(
                        "Ya hay un barco en la posición (" + p.getRow() + ", " + p.getColumn() + ")");
            }
        }

        ship.place(positions);
        for (Position p : positions) {
            getCell(p).setState(CellState.BARCO);
        }
        ships.add(ship);
    }

    public String receiveShot(Position position) throws InvalidMoveException, GameAlreadyFinishedException {
        if (isFleetSunk()) {
            throw new GameAlreadyFinishedException("La flota ya fue hundida por completo, la partida terminó.");
        }

        Cell cell = getCell(position);
        String state = cell.getState();

        if (state.equals(CellState.AGUA) || state.equals(CellState.TOCADO) || state.equals(CellState.HUNDIDO)) {
            throw new InvalidMoveException("Ya se disparó anteriormente en esta posición.");
        }

        if (state.equals(CellState.VACIA)) {
            cell.setState(CellState.AGUA);
            return ShotResult.AGUA;
        }

        Ship ship = findShipAt(position);
        ship.registerHit();

        if (ship.isSunk()) {
            markShipCells(ship, CellState.HUNDIDO);
            return ShotResult.HUNDIDO;
        } else {
            cell.setState(CellState.TOCADO);
            return ShotResult.TOCADO;
        }
    }

    private Ship findShipAt(Position position) {
        for (Ship ship : ships) {
            if (ship.getOccupiedPositions().contains(position)) {
                return ship;
            }
        }
        return null;
    }

    private void markShipCells(Ship ship, String state) {
        for (Position p : ship.getOccupiedPositions()) {
            getCell(p).setState(state);
        }
    }

    public boolean isFleetSunk() {
        return !ships.isEmpty() && ships.stream().allMatch(Ship::isSunk);
    }
}