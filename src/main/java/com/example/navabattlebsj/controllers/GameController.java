package com.example.navabattlebsj.controllers;

import com.example.navabattlebsj.exceptions.InvalidShipPositionException;
import com.example.navabattlebsj.models.*;
import com.example.navabattlebsj.patterns.HumanTurnStrategy;
import com.example.navabattlebsj.patterns.MachineTurnStrategy;
import com.example.navabattlebsj.patterns.SaveFacade;
import com.example.navabattlebsj.patterns.TurnContext;
import com.example.navabattlebsj.patterns.TurnStrategy;
import com.example.navabattlebsj.utils.Paths;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

/**
 * Controlador principal de la vista de juego. Coordina el tablero de posición
 * del jugador, el tablero principal (disparo sobre la máquina), la opción de
 * verificación del tablero del oponente (HU-3), el autoguardado de la
 * partida (HU-5) y la restauración de partidas guardadas (HU-6).
 * <p>
 * La alternancia de turnos entre el jugador humano y la máquina (HU-4) se
 * delega al patrón de comportamiento Strategy: {@link HumanTurnStrategy} y
 * {@link MachineTurnStrategy} comparten la misma interfaz {@link
 * TurnStrategy}, de modo que este controlador no necesita condicionales
 * dispersos para saber "qué hacer" en cada turno, solo cuál estrategia
 * invocar según {@code game.isHumanTurn()}.
 */
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
    private final Random random = new Random();
    private final SaveFacade saveFacade = new SaveFacade();

    // Patrón de comportamiento (Strategy): una instancia por cada tipo de turno.
    private final TurnStrategy humanTurnStrategy = new HumanTurnStrategy();
    private final TurnStrategy machineTurnStrategy = new MachineTurnStrategy();

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

        String prefix = "Partida restaurada. ";
        if (game.isHumanTurn()) {
            statusLabel.setText(prefix + "Tu turno: dispara en el tablero principal.");
        } else {
            mainBoardController.disableShooting();
            statusLabel.setText(prefix + "Turno de la máquina...");
        }
        startTurn();
    }

    // Recorre un tablero y repinta en el controlador dado las marcas de todos
    // los disparos ya registrados (AGUA/TOCADO/HUNDIDO), usado al restaurar
    // una partida guardada (HU-6).
    private void redrawShots(BoardController boardController, Board board) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                String state = board.getCell(new Position(row, col)).getState();
                if (state.equals(CellState.AGUA) || state.equals(CellState.TOCADO) || state.equals(CellState.HUNDIDO)) {
                    boardController.markCell(row, col, state);
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

        autoSave(); // guarda el estado inicial de la partida ya iniciada
        startTurn(); // el humano empieza (game.isHumanTurn() == true por defecto)
    }

    /**
     * Punto único de arranque de turno (HU-4): consulta de quién es el turno
     * actual y delega en la {@link TurnStrategy} correspondiente. Se usa
     * tanto al iniciar la batalla como al ceder el turno entre jugador y
     * máquina, y al restaurar una partida guardada (HU-6).
     */
    private void startTurn() {
        if (game.isFinished()) return;

        if (game.isHumanTurn()) {
            statusLabel.setText("Tu turno: dispara en el tablero principal");
            humanTurnStrategy.executeTurn(buildHumanContext());
        } else {
            statusLabel.setText("Turno de la máquina...");
            machineTurnStrategy.executeTurn(buildMachineContext());
        }
    }

    /**
     * HU-4: se ejecuta cuando el jugador humano falla un disparo (agua) y por
     * tanto pierde el turno. A partir de aquí la máquina juega de forma
     * autónoma sobre el tablero de posición del jugador.
     */
    private void onHumanTurnEnded() {
        game.setHumanTurn(false);
        startTurn();
    }

    /**
     * Se ejecuta cuando la máquina falla un disparo (agua) y por tanto cede
     * el turno de vuelta al jugador humano.
     */
    private void onMachineTurnEnded() {
        game.setHumanTurn(true);
        startTurn(); // HumanTurnStrategy se encarga de rehabilitar el tablero
    }

    /**
     * Contexto de turno para el jugador humano: el disparo lo maneja
     * directamente {@code BoardController} por evento de clic (ya conectado
     * en {@code setUpShooting}), así que aquí solo se necesita el callback
     * para habilitar la interacción del tablero.
     */
    private TurnContext buildHumanContext() {
        return new TurnContext(
                game.getMachine().getPositionBoard(),
                null,
                mainBoardController::enableShooting,
                null,
                null,
                null
        );
    }

    /**
     * Contexto de turno para la máquina: {@link MachineTurnStrategy} necesita
     * el tablero objetivo, cómo pintar cada resultado, y los tres ganchos
     * compartidos (autoguardado, fin de turno y victoria).
     */
    private TurnContext buildMachineContext() {
        return new TurnContext(
                game.getHuman().getPositionBoard(),
                positionBoardController::markCell,
                null,
                this::autoSave,
                this::onMachineTurnEnded,
                this::onMachineVictory
        );
    }

    private void onHumanVictory(String message) {
        game.setFinished(true);
        autoSave();
        statusLabel.setText(message);
        showGameOverAlert("¡Victoria!", message);
    }

    private void onMachineVictory(String message) {
        game.setFinished(true);
        autoSave();
        statusLabel.setText(message);
        showGameOverAlert("Fin de la partida", message);
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
