package com.example.navabattlebsj.models;

import com.example.navabattlebsj.exceptions.GameAlreadyFinishedException;
import com.example.navabattlebsj.exceptions.InvalidMoveException;
import com.example.navabattlebsj.exceptions.InvalidShipPositionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un tablero de juego de 10x10 casillas. Guarda la grilla de
 * {@link Cell} y la lista de {@link Ship} colocados en él, y ofrece las
 * operaciones centrales del juego: colocar barcos (HU-1) y recibir disparos
 * (HU-2).
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Tamaño del tablero (10x10 casillas). */
    public static final int SIZE = 10;
    private Cell[][] grid;
    private List<Ship> ships;

    /**
     * Crea un tablero vacío de {@value #SIZE}x{@value #SIZE} casillas, todas
     * en estado {@link CellState#VACIA}.
     */
    public Board() {
        grid = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Cell(new Position(i, j));
            }
        }
        ships = new ArrayList<>();
    }

    /**
     * @return la grilla completa de celdas del tablero
     */
    public Cell[][] getGrid() { return grid; }
    /**
     * @return los barcos que han sido colocados en este tablero
     */
    public List<Ship> getShips() { return ships; }

    private boolean isInsideBoard(Position position) {
        return position.getRow() >= 0 && position.getRow() < SIZE
                && position.getColumn() >= 0 && position.getColumn() < SIZE;
    }

    /**
     * Obtiene la celda en la posición indicada.
     *
     * @param position posición a consultar
     * @return la celda correspondiente
     */
    public Cell getCell(Position position) {
        return grid[position.getRow()][position.getColumn()];
    }

    /**
     * Coloca un barco en el tablero a partir de la posición inicial dada
     * (HU-1). Valida que todas las casillas que ocuparía el barco estén
     * dentro del tablero y que ninguna esté ya ocupada por otro barco.
     *
     * @param ship  barco a colocar (ya con su orientación definida)
     * @param start posición inicial del barco
     * @throws InvalidShipPositionException si el barco se sale del tablero o
     *                                       se superpone con otro ya colocado
     */
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

    /**
     * Procesa un disparo sobre la posición indicada (HU-2/HU-4), actualiza
     * el estado de la celda (y del barco impactado, si aplica) y devuelve el
     * resultado.
     *
     * @param position posición sobre la que se dispara
     * @return el resultado del disparo (ver {@link ShotResult})
     * @throws InvalidMoveException          si ya se había disparado antes
     *                                        sobre esa misma posición
     * @throws GameAlreadyFinishedException  si la flota de este tablero ya
     *                                        estaba completamente hundida
     */
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

    /**
     * @return {@code true} si este tablero tiene al menos un barco colocado
     *         y todos ellos están hundidos (condición de fin de partida)
     */
    public boolean isFleetSunk() {
        return !ships.isEmpty() && ships.stream().allMatch(Ship::isSunk);
    }
}