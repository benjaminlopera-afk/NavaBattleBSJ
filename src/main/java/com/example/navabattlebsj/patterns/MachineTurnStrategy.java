package com.example.navabattlebsj.patterns;

import com.example.navabattlebsj.exceptions.GameAlreadyFinishedException;
import com.example.navabattlebsj.exceptions.InvalidMoveException;
import com.example.navabattlebsj.models.Board;
import com.example.navabattlebsj.models.CellState;
import com.example.navabattlebsj.models.Position;
import com.example.navabattlebsj.models.ShotResult;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Estrategia de turno de la máquina (HU-4): selecciona casillas de disparo
 * de forma aleatoria (sin repetir) sobre el tablero del jugador humano,
 * encadenando disparos mientras acierte (tocado/hundido), y cede el turno
 * al fallar (agua) o termina la partida si hunde toda la flota.
 */
public class MachineTurnStrategy implements TurnStrategy {

    private static final Duration SHOT_DELAY = Duration.seconds(0.6);
    private final Random random = new Random();

    @Override
    public void executeTurn(TurnContext context) {
        PauseTransition pause = new PauseTransition(SHOT_DELAY);
        pause.setOnFinished(e -> fireShot(context));
        pause.play();
    }

    private void fireShot(TurnContext context) {
        Board board = context.getBoard();
        Position target = pickRandomUnshotPosition(board);

        try {
            String result = board.receiveShot(target);
            context.getCellMarker().onShotResult(target.getRow(), target.getColumn(), result);

            if (context.getOnShotRegistered() != null) {
                context.getOnShotRegistered().run();
            }

            if (board.isFleetSunk()) {
                if (context.getOnVictory() != null) {
                    context.getOnVictory().accept("La máquina ha hundido toda tu flota. ¡Perdiste!");
                }
                return;
            }

            if (result.equals(ShotResult.AGUA)) {
                if (context.getOnTurnEnd() != null) {
                    context.getOnTurnEnd().run();
                }
            } else {
                // Tocado o hundido: la máquina dispara de nuevo tras otra pausa.
                executeTurn(context);
            }

        } catch (InvalidMoveException | GameAlreadyFinishedException e) {
            // No debería ocurrir porque solo se eligen celdas no disparadas,
            // pero por seguridad se reintenta con otra posición.
            fireShot(context);
        }
    }

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
}
