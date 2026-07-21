package com.example.navabattlebsj.utils;

public final class Paths {

    // ===================== VISTAS FXML (classpath) =====================
    public static final String MENU_VIEW = "/com/example/navabattlebsj/MenuView.fxml";
    public static final String GAME_VIEW = "/com/example/navabattlebsj/GameView.fxml";
    public static final String BOARD_VIEW = "/com/example/navabattlebsj/BoardView.fxml";

    // ===================== ARCHIVOS DE GUARDADO (disco) =====================
    // Carpeta donde se guardan las partidas, relativa al directorio de ejecución.
    public static final String SAVE_DIRECTORY = "saves/";
    public static final String SAVE_FILE = SAVE_DIRECTORY + "savegame.dat";
    public static final String SAVE_TEXT_FILE = SAVE_DIRECTORY + "savegame.txt";

    private Paths() {}
}