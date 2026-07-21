package com.example.navabattlebsj.controllers;

import com.example.navabattlebsj.applications.NavaBattleApplication;
import com.example.navabattlebsj.utils.Paths;
import javafx.fxml.FXML;

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
}