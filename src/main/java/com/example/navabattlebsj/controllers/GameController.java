package com.example.navabattlebsj.controllers;

import com.example.navabattlebsj.exceptions.GameAlreadyFinishedException;
import com.example.navabattlebsj.exceptions.InvalidMoveException;
import com.example.navabattlebsj.exceptions.InvalidShipPositionException;
import com.example.navabattlebsj.models.*;
import com.example.navabattlebsj.patterns.SaveFacade;
import com.example.navabattlebsj.utils.Paths;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controlador principal de la vista de juego. Coordina el tablero de posición
 * del jugador, el tablero principal (disparo sobre la máquina), la opción de
 * verificación del tablero del oponente (HU-3) y el turno autónomo de la
 * máquina (HU-4).
 */
public class GameController {

    /** Pausa entre cada disparo de la máquina, para que el jugador pueda seguir la jugada. */
    private static final Duration MACHINE_SHOT_DELAY = Duration.seconds(0.6);

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
    private final Random random = new Random();
    private final SaveFacade saveFacade = new SaveFacade();

    /**
     * Inicia una nueva partida: crea el jugador humano y la máquina,
     * y deja el tablero de posición listo para que el jugador coloque su flota.
     */
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

    /**
     * HU-6: restaura una partida previamente guardada y reconstruye la
     * pantalla de juego en el punto exacto donde había quedado: ambos
     * tableros con sus barcos y disparos ya registrados, respetando de
     * quién era el turno al momento de guardar.
     *
     * @param savedGame partida restaurada desde {@link SaveFacade#loadGame()}
     */
    public void resumeGame(Game savedGame) {
        this.game = savedGame;

        if (viewOpponentButton != null) {
            viewOpponentButton.setDisable(true);
            viewOpponentButton.setVisible(false);
        }
        if (startBattleButton != null) {
            startBattleButton.setDisable(true);
            startBattleButton.setVisible(false);
        }

        // Tablero de posición del jugador: solo lectura, mostrando su flota
        // y los disparos que ya había recibido de la máquina.
        positionBoardController.setUpReadOnly(game.getHuman().getPositionBoard());
        redrawShots(positionBoardController, game.getHuman().getPositionBoard());

        // Tablero principal: modo disparo sobre la máquina, mostrando los
        // disparos que el jugador ya había hecho antes de guardar.
        mainBoardController.setUpShooting(
                game.getMachine().getPositionBoard(),
                this::autoSave,
                this::onHumanTurnEnded,
                this::onHumanVictory
        );
        redrawShots(mainBoardController, game.getMachine().getPositionBoard());

        if  (game.isHumanTurn()) {
            mainBoardController.enableShooting();
            statusLabel.setText("Partida restaurada. Tu turno: dispara en el tablero principal.");
        } else {
            mainBoardController.disableShooting();
            statusLabel.setText("Partida restaurada. Turno de la máquina...");
            startMachineTurn();
        }
    }

    // Recorre un tablero y repinta en el controlador dado las marcas de todos
    // los disparos ya registrados (AGUA/TOCADO/HUNDIDO), usado al restaurar
    // una partida guardada (HU-6).
    private void redrawShots(BoardController controller, Board board) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                String state = board.getCell(new Position(row, col)).getState();
                if (state.equals(CellState.AGUA) || state.equals(CellState.TOCADO) || state.equals(CellState.HUNDIDO)) {
                    controller.markCell(row, col, state);
                }
            }
        }
    }

    /**
     * Se ejecuta cuando el jugador humano termina de colocar su flota.
     * Coloca la flota de la máquina de forma aleatoria (HU-4) y habilita las
     * opciones de verificación (HU-3) antes de comenzar la batalla.
     */
    private void onHumanFleetReady() {
        placeMachineFleetRandomly(game.getMachine().getPositionBoard(), game.getMachine().getFleet());

        viewOpponentButton.setDisable(false);
        startBattleButton.setDisable(false);

        statusLabel.setText("Flota lista. Puedes verificar el tablero del oponente antes de comenzar.");
    }

    /**
     * HU-4: coloca la flota de la máquina de manera aleatoria, respetando las
     * mismas reglas de validación que el jugador humano (sin superposiciones
     * ni salirse del tablero).
     */
    private void placeMachineFleetRandomly(Board board, Fleet fleet) {
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

    /**
     * HU-3: abre una ventana modal de solo lectura mostrando el tablero de
     * posición del oponente (máquina), únicamente disponible antes de iniciar
     * la fase de disparo.
     */
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

    /**
     * Cierra la fase de verificación (HU-3) y da inicio a la fase de disparo (HU-2),
     * deshabilitando la opción de ver el tablero del oponente.
     */
    @FXML
    private void handleStartBattle() {
        viewOpponentButton.setDisable(true);
        startBattleButton.setDisable(true);
        viewOpponentButton.setVisible(false);
        startBattleButton.setVisible(false);

        mainBoardController.setUpShooting(
                game.getMachine().getPositionBoard(),
                this::autoSave,
                this::onHumanTurnEnded,
                this::onHumanVictory
        );

        statusLabel.setText("Tu turno: dispara en el tablero principal.");
        autoSave();
    }

    /**
     * HU-4: se ejecuta cuando el jugador humano falla un disparo (agua) y por
     * tanto pierde el turno. A partir de aquí la máquina juega de forma
     * autónoma sobre el tablero de posición del jugador.
     */
    private void onHumanTurnEnded() {
        game.setHumanTurn(false);
        if (game.isFinished()) return;
        statusLabel.setText("Turno de la máquina...");
        startMachineTurn();
    }

    // Programa el primer disparo de la máquina tras una breve pausa. Se usa
    // tanto tras un turno normal del humano (onHumanTurnEnded) como al
    // restaurar una partida guardada donde ya le tocaba jugar a la máquina (HU-6).
    private void startMachineTurn() {
        PauseTransition initialPause = new PauseTransition(MACHINE_SHOT_DELAY);
        initialPause.setOnFinished((e -> fireMachineShot()));
        initialPause.play();
    }

    /**
     * HU-4: dispara una posición aleatoria (sin repetir) sobre el tablero de
     * posición del jugador humano y pinta el resultado en tiempo real. Si la
     * máquina acierta (tocado u hundido) programa un nuevo disparo tras una
     * breve pausa; si falla (agua) o hunde toda la flota, termina el turno.
     */
    private void fireMachineShot() {
        Board humanBoard = game.getHuman().getPositionBoard();
        Position target = pickRandomUnshotPosition(humanBoard);

        try {
            String result = humanBoard.receiveShot(target);
            positionBoardController.markCell(target.getRow(), target.getColumn(), result);
            autoSave();

            if (humanBoard.isFleetSunk()) {
                game.setFinished(true);
                autoSave();
                String message = "La máquina ha hundido toda tu flota. ¡Perdiste!";
                statusLabel.setText(message);
                showGameOverAlert("Fin de la partida", message);
                return;
            }

            if (result.equals(ShotResult.AGUA)) {
                game.setHumanTurn(true);
                statusLabel.setText("Turno de la máquina completado. Tu turno de nuevo.");
                mainBoardController.enableShooting();
            } else {
                PauseTransition pause = new PauseTransition(MACHINE_SHOT_DELAY);
                pause.setOnFinished(e -> fireMachineShot());
                pause.play();
            }

        } catch (InvalidMoveException | GameAlreadyFinishedException e) {
            // No debería ocurrir porque solo elegimos celdas no disparadas,
            // pero por seguridad se reintenta con otra posición.
            fireMachineShot();
        }
    }

    /**
     * Selecciona al azar una posición del tablero que todavía no haya recibido
     * disparo (celda VACIA o BARCO, es decir, distinta de AGUA/TOCADO/HUNDIDO).
     */
    private Position pickRandomUnshotPosition(Board board) {
        List<Position> candidates = new ArrayList<>();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Position p = new Position(row, col);
                String state = board.getCell(p).getState();
                if (state.equals(CellState.VACIA) || state.equals(CellState.BARCO)) {
                    candidates.add(p);
                }
            }
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private void onHumanVictory(String message) {
        game.setFinished(true);
        autoSave();
        statusLabel.setText(message);
        showGameOverAlert("¡Victoria!", message);
    }

    /**
     * Muestra un cuadro de diálogo informativo al finalizar la partida,
     * ya sea por victoria del jugador o por derrota ante la máquina.
     */
    private void showGameOverAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * HU-5: guarda automáticamente el estado completo de la partida
     * (tableros, flotas y turno actual) cada vez que se registra una jugada,
     * ya sea del jugador humano o de la máquina.
     */
    private void autoSave() {
        saveFacade.saveGame(game);
    }
}