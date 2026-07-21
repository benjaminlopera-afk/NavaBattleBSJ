package com.example.navabattlebsj.patterns;

import com.example.navabattlebsj.models.Game;
import com.example.navabattlebsj.models.Ship;
import com.example.navabattlebsj.persistences.SerializationManager;
import com.example.navabattlebsj.persistences.TextFileManager;
import com.example.navabattlebsj.utils.Paths;

import java.io.File;
import java.io.IOException;

/**
 * Fachada (patrón estructural) que unifica el guardado/carga de partida,
 * ocultando al resto de la aplicación el hecho de que internamente se usan
 * dos mecanismos distintos: serialización binaria (tablero completo) y
 * archivo plano (nickname + barcos hundidos).
 */

public class SaveFacade {

    private final SerializationManager serializationManager = new SerializationManager();
    private final TextFileManager textFileManager = new TextFileManager();

    public SaveFacade() {
        File dir = new File(Paths.SAVE_DIRECTORY);
        if (!dir.exists()) dir.mkdirs();
    }

    public void saveGame(Game game) {
        try {
            serializationManager.save(new GameState(game), Paths.SAVE_FILE);

            long humanSunk = countSunk(game.getHuman().getPositionBoard().getShips());
            long machineSunk = countSunk(game.getMachine().getPositionBoard().getShips());

            textFileManager.save(Paths.SAVE_TEXT_FILE, game.getHuman().getNickname(),
                    (int) humanSunk, (int) machineSunk);
        } catch (IOException e) {
            System.err.println("No se pudo guardar la partida: " + e.getMessage());
        }
    }

    public boolean hasSaveGame() {
        return serializationManager.exists(Paths.SAVE_FILE);
    }

    public Game loadGame() throws IOException, ClassNotFoundException {
        GameState state = serializationManager.load(Paths.SAVE_FILE);
        return state.getGame();
    }

    public void deleteSave() {
        serializationManager.delete(Paths.SAVE_FILE);
        new File(Paths.SAVE_TEXT_FILE).delete();
    }

    private long countSunk(java.util.List<Ship> ships) {
        return ships.stream().filter(Ship::isSunk).count();
    }
}
