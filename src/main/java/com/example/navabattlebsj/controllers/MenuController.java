package com.example.navabattlebsj.controllers;

import com.example.navabattlebsj.applications.NavaBattleApplication;
import com.example.navabattlebsj.models.Game;
import com.example.navabattlebsj.patterns.SaveFacade;
import com.example.navabattlebsj.utils.Paths;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.Optional;

/**
 * Controlador del menú principal. Además de las opciones de siempre
 * (nueva partida, cargar partida, reglas), implementa HU-6: al iniciar la
 * aplicación consulta automáticamente si existe una partida guardada sin
 * terminar y le da al jugador la opción de continuarla o empezar de cero.
 */

public class MenuController {

    private final SaveFacade saveFacade = new SaveFacade();

    @FXML
    public void initialize() {
        checkForSavedGame();
    }

    /**
     * HU-6: si existe una partida guardada y no ha terminado, se le pregunta
     * al jugador si desea continuarla o iniciar una nueva. Si la partida
     * guardada ya había terminado, se descarta automáticamente sin preguntar
     * y el flujo normal de "Nueva Partida" queda disponible.
     */

    private void checkForSavedGame() {
        if (!saveFacade.hasSaveGame()) {
            return;
        }

        Game savedGame;
        try {
            savedGame = saveFacade.loadGame();
        } catch (IOException | ClassNotFoundException e) {
            //archivo de guardado corrupto o inaccesible: se descarta y se
            // continúa como si no hubiera guardado previo.
            saveFacade.deleteSave();
            return;
        }

        if (savedGame.isFinished()) {
            // la partida guardada ya terminó: no tiene sentido ofrece continuarla.
            saveFacade.deleteSave();
            return;
        }

        Optional<ButtonType> choice = askContinueOrNewGame();

        if (choice.isPresent() && choice.get().getText().equals("Continuar partida.")) {
            GameController controller = NavaBattleApplication.setScene(Paths.GAME_VIEW, "NavaBatlle - Batalla en curso");
            controller.resumeGame(savedGame);
        } else {
            // el jugador eligió empezar de nuevo: se descarta el guardado
            // anterior para que no se le vuelva a preguntar la próxima vez
            saveFacade.deleteSave();
        }
    }

    private Optional<ButtonType> askContinueOrNewGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Partida guardada encontrada");
        alert.setHeaderText("Tienes una partida en curso");
        alert.setContentText("¿Deseas continuar tu partida guardada o iniciar una nueva?");

        ButtonType continueButton = new ButtonType("Continuar partida");
        ButtonType newGameButton = new ButtonType("Nueva partida");
        alert.getButtonTypes().setAll(continueButton, newGameButton);

        return alert.showAndWait();
    }

    @FXML
    private void handleNewGame() {
        GameController controller = NavaBattleApplication.setScene(Paths.GAME_VIEW, "NavaBattle - Colocación de barcos");
        controller.startNewGame();
    }

    /**
     * HU-6: permite cargar manualmente la última partida guardada desde el
     * menú, sin esperar a la consulta automática de {@link #initialize()}
     * (útil si el jugador cerró el diálogo inicial eligiendo "Nueva partida"
     * por error, o si vuelve al menú desde otra pantalla).
     */

    @FXML
    private void handleLoadGame() {
        if (!saveFacade.hasSaveGame()) {
            showInfo("Sin partida guardada", "No hay ninguna partida guardada para cargar");
            return;
        }

        try {
            Game savedGame = saveFacade.loadGame();

            if (savedGame.isFinished()) {
                saveFacade.deleteSave();
                showInfo("Partida finalizada", "La última partida guardada ya había terminado. Inicia una nueva.");
                return;
            }

            GameController controller = NavaBattleApplication.setScene(Paths.GAME_VIEW, "NavaBattle - Batalla en curso");
            controller.resumeGame(savedGame);
        } catch (IOException | ClassNotFoundException e) {
            showInfo("Error al cargar", "No se pudo cargar la partida guardada.");
        }
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

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}