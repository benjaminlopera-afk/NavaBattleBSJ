package com.example.navabattlebsj.controllers;

import com.example.navabattlebsj.applications.NavaBattleApplication;
import com.example.navabattlebsj.utils.Paths;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class MenuController {

    @FXML
    private void handleNewGame() {
        GameController controller = NavaBattleApplication.setScene(Paths.GAME_VIEW, "NavaBattle - Colocación de barcos");
        controller.startNewGame();
    }

    @FXML
    private void handleLoadGame() {
        // Se implementa en el HU-6
    }

    @FXML
    private void handleRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reglas del Juego");
        alert.setHeaderText("Batalla Naval - NavaBattle");
        alert.setContentText("""
                Cada jugador cuenta con una flota de 10 barcos:
                • 1 Portaaviones (4 casillas)
                • 2 Submarinos (3 casillas c/u)
                • 3 Destructores (2 casillas c/u)
                • 4 Fragatas (1 casilla c/u)

                Coloca tu flota en el Tablero de Posición, de forma
                horizontal o vertical, sin superponer barcos.

                Luego, dispara sobre el Tablero Principal para intentar
                hundir la flota enemiga:
                • Agua: la casilla no tiene barco. Pierdes el turno.
                • Tocado: impactaste parte de un barco. Disparas de nuevo.
                • Hundido: destruiste el barco completo. Disparas de nuevo.

                Gana quien hunda toda la flota del oponente primero.
                """);
        alert.showAndWait();
    }
}