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

/**
 * Controlador de un tablero individual (10x10). Soporta cuatro modos de uso:
 * <ul>
 *     <li><b>Colocación</b> (HU-1): permite arrastrar y rotar barcos sobre el tablero.</li>
 *     <li><b>Disparo</b> (HU-2): permite hacer clic en las celdas para disparar.</li>
 *     <li><b>Solo lectura</b> (HU-3): muestra el estado de un tablero sin permitir interacción,
 *     usado para verificar la colocación de la flota del oponente.</li>
 *     <li><b>Observación de impactos</b> (HU-4): permite pintar en el tablero de posición
 *     del jugador los disparos recibidos de la máquina, sin permitir interacción.</li>
 * </ul>
 */
public class BoardController {

    private static final int CELL_SIZE = 40;

    // ===================== PALETA DEL TABLERO =====================
    // Coincide con las variables definidas en Styles.css (-water-color,
    // -ship-color, -ship-placed-color, -border-color, -warning, -error,
    // -text-light, -text-secondary).
    private static final Color WATER_COLOR = Color.web("#0B2545");
    private static final Color SHIP_COLOR = Color.web("#64748B");
    private static final Color SHIP_PLACED_COLOR = Color.web("#475569");
    private static final Color GRID_BORDER_COLOR = Color.web("#334155");
    private static final Color HIT_COLOR = Color.web("#F59E0B");   // -warning
    private static final Color SUNK_COLOR = Color.web("#EF4444");  // -error
    private static final Color MISS_SYMBOL_COLOR = Color.web("#CBD5E1"); // -text-secondary
    private static final Color HIT_SYMBOL_COLOR = Color.web("#0F172A");  // -bg-color (contraste sobre ámbar)
    private static final Color SUNK_SYMBOL_COLOR = Color.web("#F8FAFC"); // -text-light

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
    private Runnable onShotRegistered;

    @FXML
    public void initialize() {
        shipShapes = new HashMap<>();
    }

    // ===================== MODO COLOCACIÓN (HU-1) =====================

    /**
     * Configura este tablero en modo colocación, permitiendo al jugador
     * arrastrar y rotar los barcos de su flota.
     *
     * @param board tablero de posición del jugador
     * @param fleet flota a colocar
     */
    public void setUpPlacement(Board board, Fleet fleet) {
        this.board = board;
        this.fleet = fleet;
        drawEmptyGrid();
        drawDraggableShips(fleet);
    }

    /**
     * Registra el callback a ejecutar cuando el jugador termina de colocar su flota.
     *
     * @param callback acción a ejecutar al finalizar la colocación
     */
    public void setOnPlacementComplete(Runnable callback) {
        this.onPlacementComplete = callback;
    }

    private void drawEmptyGrid() {
        boardPane.getChildren().clear();
        cellShapes = new Rectangle[Board.SIZE][Board.SIZE];
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setLayoutX(col * CELL_SIZE);
                cell.setLayoutY(row * CELL_SIZE);
                cell.setFill(WATER_COLOR);
                cell.setStroke(GRID_BORDER_COLOR);
                cellShapes[row][col] = cell;
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
        shape.setFill(SHIP_COLOR);
        shape.setStroke(GRID_BORDER_COLOR);
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
        shape.setFill(SHIP_PLACED_COLOR);
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

    /**
     * Configura este tablero en modo disparo sobre el tablero del oponente.
     *
     * @param enemyBoard tablero del oponente a atacar
     * @param onTurnEnd  callback ejecutado cuando el disparo es agua (termina el turno)
     * @param onVictory  callback ejecutado cuando se hunde toda la flota enemiga
     */
    public void setUpShooting(Board enemyBoard, Runnable onShotRegistered, Runnable onTurnEnd, Consumer<String> onVictory) {
        this.board = enemyBoard;
        this.onShotRegistered = onShotRegistered;
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
                cell.setFill(WATER_COLOR);
                cell.setStroke(GRID_BORDER_COLOR);

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
            markCell(row, col, result);

            if (onShotRegistered != null) onShotRegistered.run();

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

    // Habilita de nuevo el disparo cuando vuelve a ser el turno del jugador.
    public void enableShooting() {
        boardPane.setDisable(false);
    }

    // ===================== MODO SOLO LECTURA (HU-3) =====================

    /**
     * Configura este tablero en modo solo lectura, mostrando el estado actual
     * de un tablero (barcos incluidos) sin permitir ninguna interacción.
     * Se utiliza para verificar la colocación de la flota del oponente antes
     * de iniciar la partida.
     *
     * @param board tablero a mostrar (normalmente el de posición de la máquina)
     */
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
        cellShapes = new Rectangle[Board.SIZE][Board.SIZE];
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setLayoutX(col * CELL_SIZE);
                cell.setLayoutY(row * CELL_SIZE);

                String state = board.getCell(new Position(row, col)).getState();
                cell.setFill(state.equals(CellState.BARCO) ? SHIP_COLOR : WATER_COLOR);
                cell.setStroke(GRID_BORDER_COLOR);

                cellShapes[row][col] = cell;
                // Sin manejadores de eventos: el tablero es puramente informativo.
                boardPane.getChildren().add(cell);
            }
        }
    }

    // ===================== MARCADO DE DISPAROS (HU-2 / HU-4) =====================

    /**
     * Pinta en la celda (row, col) de este tablero el resultado de un disparo
     * (agua, tocado u hundido). Se usa tanto para los disparos del jugador sobre
     * el tablero principal (HU-2) como para los disparos de la máquina sobre el
     * tablero de posición del jugador (HU-4).
     *
     * @param row    fila de la celda impactada
     * @param col    columna de la celda impactada
     * @param result resultado del disparo ({@link ShotResult})
     */
    public void markCell(int row, int col, String result) {
        if (cellShapes == null) return;
        Rectangle cell = cellShapes[row][col];
        if (cell == null) return;

        String symbol;
        Color symbolColor;

        switch (result) {
            case ShotResult.AGUA -> {
                symbol = "X";
                symbolColor = MISS_SYMBOL_COLOR;
                cell.setFill(WATER_COLOR);
            }
            case ShotResult.TOCADO -> {
                symbol = "●";
                symbolColor = HIT_SYMBOL_COLOR;
                cell.setFill(HIT_COLOR);
            }
            case ShotResult.HUNDIDO -> {
                symbol = "🔥";
                symbolColor = SUNK_SYMBOL_COLOR;
                cell.setFill(SUNK_COLOR);
            }
            default -> {
                symbol = "";
                symbolColor = MISS_SYMBOL_COLOR;
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
}