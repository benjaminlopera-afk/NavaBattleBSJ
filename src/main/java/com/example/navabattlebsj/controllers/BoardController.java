package com.example.navabattlebsj.controllers;

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

import java.util.HashMap;
import java.util.Map;

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

    private Ship draggingShip;
    private Rectangle draggingShape;
    private double dragOffsetX;
    private double dragOffsetY;

    private Runnable onPlacementComplete;

    @FXML
    public void initialize() {
        shipShapes = new HashMap<>();
    }

    // Llamado desde GameController tras cargar esta vista, para
    // inyectarle el tablero y la flota del jugador que va a colocar barcos.
    public void setUpPlacement(Board board, Fleet fleet) {
        this.board = board;
        this.fleet = fleet;
        drawEmptyGrid();
        drawDraggableShips(fleet);
    }

    // Permite que el GameController se entere cuando el jugador termina de
    // colocar toda su flota y confirma con el botón "Listo".
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
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.DARKBLUE);
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
        shape.setFill(Color.GRAY);
        shape.setStroke(Color.BLACK);
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
        Rectangle shape = (Rectangle) event.getSource();
        draggingShape = shape;
        draggingShip = shipShapes.get(shape);

        dragOffsetX = event.getX();
        dragOffsetY = event.getY();

        // Se pasa el barco al Pane del tablero para poder moverlo libremente sobre él.
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
        shape.setFill(Color.DARKGRAY);
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
}