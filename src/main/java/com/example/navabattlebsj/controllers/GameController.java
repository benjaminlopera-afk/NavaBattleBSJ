package com.example.navabattlebsj.controllers;

import com.example.navabattlebsj.exceptions.InvalidShipPositionException;
import com.example.navabattlebsj.models.*;
import com.example.navabattlebsj.utils.Paths;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class GameController {

    @FXML
    private Label statusLabel;

    @FXML
    private BoardController positionBoardController;

    @FXML
    private BoardController mainBoardController;

    @FXML
    private Button viewOpponentButton;

    @FXML
    private Button startBattleButton;

    private Game game;

    public void startNewGame() {
        HumanPlayer human = new HumanPlayer("Jugador");
        MachinePlayer machine = new MachinePlayer();
        game = new Game(human, machine);

        positionBoardController.setUpPlacement(human.getPositionBoard(), human.getFleet());
        positionBoardController.setOnPlacementComplete(this::onHumanFleetReady);

        if (viewOpponentButton != null) viewOpponentButton.setDisable(true);
        if (startBattleButton != null) startBattleButton.setDisable(true);

        statusLabel.setText("Coloca tu flota para comenzar.");
    }

    private void onHumanFleetReady() {
        // TEMPORAL: colocación aleatoria básica solo para poder probar el HU-2.
        // Se reemplaza formalmente por la IA de colocación en el HU-4.
        placeMachineFleetRandomly(game.getMachine().getPositionBoard(), game.getMachine().getFleet());

        viewOpponentButton.setDisable(false);
        startBattleButton.setDisable(false);

        statusLabel.setText("Flota lista. Puedes verificar el tablero del oponente antes de comenzar.");
    }

    private void placeMachineFleetRandomly(Board board, Fleet fleet) {
        Random random = new Random();
        for (Ship ship : fleet.getShips()) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(Board.SIZE);
                int col = random.nextInt(Board.SIZE);
                ship.setOrientation(random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL);
                try {
                    board.placeShip(ship, new Position(row, col));
                    placed = true;
                } catch (InvalidShipPositionException e) {
                    // Posición inválida, se reintenta con otra al azar.
                }
            }
        }
    }

    @FXML
    private void handleViewOpponentBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.BOARD_VIEW));
            Parent root = loader.load();

            BoardController readOnlyController = loader.getController();
            readOnlyController.setUpReadOnly(game.getMachine().getPositionBoard());

            Stage popup = new Stage();
            popup.setTitle("Verificación - Tablero del Oponente");
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setScene(new Scene(root));
            popup.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el tablero del oponente", e);
        }
    }

    @FXML
    private void handleStartBattle() {
        viewOpponentButton.setDisable(true);
        startBattleButton.setDisable(true);
        viewOpponentButton.setVisible(false);
        startBattleButton.setVisible(false);

        mainBoardController.setUpShooting(
                game.getMachine().getPositionBoard(),
                this::onHumanMissedShot,
                this::onVictory
        );

        statusLabel.setText("Tu turno: dispara en el tablero principal.");
    }

    private void onHumanMissedShot() {
        // TEMPORAL: como el HU-4 (turno real de la máquina) no existe todavía,
        // simulamos que la máquina "ya jugó" y devolvemos el turno de inmediato,
        // solo para poder seguir probando el HU-2. Esto se reemplaza en el HU-4
        // por un disparo real de la máquina al tablero de posición del humano.
        statusLabel.setText("Agua. (Simulado) Turno de la máquina completado. Tu turno de nuevo.");
        mainBoardController.enableShooting();
    }

    private void onVictory(String message) {
        statusLabel.setText(message);
    }
}