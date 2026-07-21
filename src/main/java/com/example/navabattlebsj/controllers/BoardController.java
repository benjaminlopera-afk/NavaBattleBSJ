package com.example.navabattlebsj.controllers;

import com.example.navabattlebsj.exceptions.GameAlreadyFinishedException;
import com.example.navabattlebsj.exceptions.InvalidMoveException;
import com.example.navabattlebsj.exceptions.InvalidShipPositionException;
import com.example.navabattlebsj.models.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BoardController {

    private static final int CELL_SIZE = 40;

    @FXML
    private Pane boardPane;

    @FXML
    private HBox shipsContainer;

    @FXML
    private Button readyButton;

    private Board board;
    private Fleet fleet;
    private Map<Rectangle, Ship> shipShapes;
    private Rectangle[][] cellShapes;

    private Ship draggingShip;
    private Rectangle draggingShape;
    private double dragOffsetX;
    private double dragOffsetY;

    private Runnable onPlacementComplete;
    private Runnable onTurnEnd;
    private Consumer<String> onVictory;

    @FXML
    public void initialize() {
        shipShapes = new HashMap<>();
    }

    // ===================== MODO COLOCACIÓN (HU-1) =====================

    public void setUpPlacement(Board board, Fleet fleet) {
        this.board = board;
        this.fleet = fleet;
        drawEmptyGrid();
        drawDraggableShips(fleet);
    }

    public void setOnPlacementComplete(Runnable callback) {
        this.onPlacementComplete = callback;
    }

    private void drawEmptyGrid() {
        boardPane.getChildren().clear();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setLayoutX(col * CELL_SIZE);
                cell.setLayoutY(row * CELL_SIZE);
                cell.setFill(Color.web("#1b263b"));
                cell.setStroke(Color.web("#00b4d8"));
                boardPane.getChildren().add(cell);
            }
        }
    }

    private void drawDraggableShips(Fleet fleet) {
        shipsContainer.getChildren().clear();
        for (Ship ship : fleet.getShips()) {
            Rectangle shape = createShipShape(ship);
            shipShapes.put(shape, ship);
            attachHandlers(shape);
            shipsContainer.getChildren().add(shape);
        }
    }

    private Rectangle createShipShape(Ship ship) {
        boolean horizontal = ship.getOrientation().equals(Orientation.HORIZONTAL);
        double width = horizontal ? ship.getSize() * CELL_SIZE : CELL_SIZE;
        double height = horizontal ? CELL_SIZE : ship.getSize() * CELL_SIZE;

        Rectangle shape = new Rectangle(width, height);
        shape.setFill(Color.web("#415a77"));
        shape.setStroke(Color.web("#00b4d8"));
        return shape;
    }

    private void attachHandlers(Rectangle shape) {
        shape.setOnMousePressed(this::handleMousePressed);
        shape.setOnMouseDragged(this::handleMouseDragged);
        shape.setOnMouseReleased(this::handleMouseReleased);
        shape.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                rotateShip(shape);
            }
        });
    }

    private void handleMousePressed(MouseEvent event) {
        // Solo el click izquierdo inicia el arrastre; el derecho queda libre para rotar.
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        Rectangle shape = (Rectangle) event.getSource();
        draggingShape = shape;
        draggingShip = shipShapes.get(shape);

        dragOffsetX = event.getX();
        dragOffsetY = event.getY();

        if (shipsContainer.getChildren().contains(shape)) {
            shipsContainer.getChildren().remove(shape);
            boardPane.getChildren().add(shape);
            shape.setLayoutX(event.getSceneX() - boardPane.localToScene(0, 0).getX());
            shape.setLayoutY(event.getSceneY() - boardPane.localToScene(0, 0).getY());
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (draggingShape == null) return;
        double x = event.getSceneX() - boardPane.localToScene(0, 0).getX() - dragOffsetX;
        double y = event.getSceneY() - boardPane.localToScene(0, 0).getY() - dragOffsetY;
        draggingShape.setLayoutX(x);
        draggingShape.setLayoutY(y);
    }

    private void handleMouseReleased(MouseEvent event) {
        if (draggingShape == null) return;

        int col = (int) Math.round(draggingShape.getLayoutX() / CELL_SIZE);
        int row = (int) Math.round(draggingShape.getLayoutY() / CELL_SIZE);
        Position target = new Position(row, col);

        try {
            board.placeShip(draggingShip, target);
            snapShipToBoard(draggingShape, target);
            disableDragging(draggingShape);
            checkAllPlaced();
        } catch (InvalidShipPositionException e) {
            returnShipToContainer(draggingShape);
        }

        draggingShape = null;
        draggingShip = null;
    }

    private void snapShipToBoard(Rectangle shape, Position position) {
        shape.setLayoutX(position.getColumn() * CELL_SIZE);
        shape.setLayoutY(position.getRow() * CELL_SIZE);
        shape.setFill(Color.web("#2c3e50"));
    }

    private void disableDragging(Rectangle shape) {
        shape.setOnMousePressed(null);
        shape.setOnMouseDragged(null);
        shape.setOnMouseReleased(null);
        shape.setOnMouseClicked(null);
    }

    private void returnShipToContainer(Rectangle shape) {
        boardPane.getChildren().remove(shape);
        shape.setLayoutX(0);
        shape.setLayoutY(0);
        shipsContainer.getChildren().add(shape);
    }

    private void rotateShip(Rectangle shape) {
        Ship ship = shipShapes.get(shape);
        String newOrientation = ship.getOrientation().equals(Orientation.HORIZONTAL)
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;
        ship.setOrientation(newOrientation);

        double oldWidth = shape.getWidth();
        double oldHeight = shape.getHeight();
        shape.setWidth(oldHeight);
        shape.setHeight(oldWidth);
    }

    private void checkAllPlaced() {
        boolean allPlaced = board.getShips().size() == fleet.getShips().size();
        readyButton.setDisable(!allPlaced);
    }

    @FXML
    private void handleReady() {
        lockBoard();
        if (onPlacementComplete != null) {
            onPlacementComplete.run();
        }
    }

    private void lockBoard() {
        readyButton.setDisable(true);
        boardPane.setDisable(true);
        shipsContainer.setDisable(true);
    }

    // ===================== MODO DISPARO (HU-2) =====================

    public void setUpShooting(Board enemyBoard, Runnable onTurnEnd, Consumer<String> onVictory) {
        this.board = enemyBoard;
        this.onTurnEnd = onTurnEnd;
        this.onVictory = onVictory;
        drawShootableGrid();
    }

    private void drawShootableGrid() {
        boardPane.getChildren().clear();
        cellShapes = new Rectangle[Board.SIZE][Board.SIZE];

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setLayoutX(col * CELL_SIZE);
                cell.setLayoutY(row * CELL_SIZE);
                cell.setFill(Color.web("#0f3460"));
                cell.setStroke(Color.web("#1a1a2e"));

                int r = row;
                int c = col;
                cell.setOnMouseClicked(event -> handleShot(r, c));

                cellShapes[row][col] = cell;
                boardPane.getChildren().add(cell);
            }
        }
    }

    private void handleShot(int row, int col) {
        try {
            String result = board.receiveShot(new Position(row, col));
            paintShotResult(row, col, result);

            if (board.isFleetSunk()) {
                if (onVictory != null) onVictory.accept("¡Has hundido toda la flota enemiga!");
                return;
            }

            if (result.equals(ShotResult.AGUA)) {
                boardPane.setDisable(true); // el turno termina: se bloquea hasta que vuelva a ser tu turno
                if (onTurnEnd != null) onTurnEnd.run();
            }

        } catch (InvalidMoveException e) {
            // Ya se disparó en esa celda; se ignora el clic.
        } catch (GameAlreadyFinishedException e) {
            // La partida ya terminó; se ignora el clic.
        }
    }

    private void paintShotResult(int row, int col, String result) {
        Rectangle cell = cellShapes[row][col];
        String symbol;
        Color symbolColor;

        switch (result) {
            case ShotResult.AGUA -> {
                symbol = "X";
                symbolColor = Color.web("#e0e1dd");
                cell.setFill(Color.web("#1b263b"));
            }
            case ShotResult.TOCADO -> {
                symbol = "●";
                symbolColor = Color.BLACK;
                cell.setFill(Color.web("#f4a261"));
            }
            case ShotResult.HUNDIDO -> {
                symbol = "🔥";
                symbolColor = Color.WHITE;
                cell.setFill(Color.web("#ff6b6b"));
            }
            default -> {
                symbol = "";
                symbolColor = Color.BLACK;
            }
        }

        Text mark = new Text(symbol);
        mark.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        mark.setFill(symbolColor);
        mark.setLayoutX(cell.getLayoutX() + CELL_SIZE / 2.0 - 8);
        mark.setLayoutY(cell.getLayoutY() + CELL_SIZE / 2.0 + 6);
        boardPane.getChildren().add(mark);

        cell.setOnMouseClicked(null);
    }

    // Habilita de nuevo el disparo cuando vuelve a ser el turno del jugador.
    // Se llamará desde GameController al terminar el turno de la máquina (HU-4).
    public void enableShooting() {
        boardPane.setDisable(false);
    }

    // ===================== MODO SOLO LECTURA (HU-3) =====================

    public void setUpReadOnly(Board board) {
        this.board = board;

        if (shipsContainer != null) {
            shipsContainer.setVisible(false);
            shipsContainer.setManaged(false);
        }
        if (readyButton != null) {
            readyButton.setVisible(false);
            readyButton.setManaged(false);
        }

        drawReadOnlyGrid();
    }

    private void drawReadOnlyGrid() {
        boardPane.getChildren().clear();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setLayoutX(col * CELL_SIZE);
                cell.setLayoutY(row * CELL_SIZE);

                String state = board.getCell(new Position(row, col)).getState();
                cell.setFill(state.equals(CellState.BARCO)
                        ? Color.web("#415a77")   // celda con barco: se ve la flota del oponente
                        : Color.web("#1b263b")); // celda vacía
                cell.setStroke(Color.web("#00b4d8"));

                // Sin manejadores de eventos: el tablero es puramente informativo.
                boardPane.getChildren().add(cell);
            }
        }
    }
}