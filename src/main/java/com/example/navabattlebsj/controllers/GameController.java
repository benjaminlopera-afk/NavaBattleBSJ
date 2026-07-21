package com.example.navabattlebsj.controllers;

import com.example.navabattlebsj.models.Game;
import com.example.navabattlebsj.models.HumanPlayer;
import com.example.navabattlebsj.models.MachinePlayer;
import javafx.fxml.FXML;

public class GameController {

    // El sufijo "Controller" es obligatorio: debe coincidir con el fx:id
    // del <fx:include> en GameView.fxml (positionBoard -> positionBoardController).
    @FXML
    private BoardController positionBoardController;

    @FXML
    private BoardController mainBoardController;

    private Game game;

    public void startNewGame() {
        HumanPlayer human = new HumanPlayer("Jugador");
        MachinePlayer machine = new MachinePlayer();
        game = new Game(human, machine);

        positionBoardController.setUpPlacement(human.getPositionBoard(), human.getFleet());
        positionBoardController.setOnPlacementComplete(this::onHumanFleetReady);
    }

    private void onHumanFleetReady() {
        // Aquí, en el HU-4, colocaremos la flota de la máquina automáticamente
        // y arrancará el HU-2 (disparos). Por ahora, solo confirmamos que funciona:
        System.out.println("Flota del jugador colocada. Partida lista para comenzar.");
    }
}