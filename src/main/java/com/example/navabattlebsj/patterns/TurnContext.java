package com.example.navabattlebsj.patterns;

import com.example.navabattlebsj.models.Board;

import java.util.function.Consumer;

/**
 * Contexto que se pasa a una {@link TurnStrategy} con todo lo necesario para
 * ejecutar el turno de un jugador: el tablero objetivo y los "ganchos"
 * (callbacks) que deben dispararse ante cada evento relevante del disparo.
 * <p>
 * No todos los campos son usados por todas las estrategias: {@link
 * com.example.navabattlebsj.patterns.HumanTurnStrategy} solo necesita
 * {@code onEnableInput}, ya que el disparo en sí lo maneja el
 * BoardController por evento de clic; {@link
 * com.example.navabattlebsj.patterns.MachineTurnStrategy} usa el resto de
 * campos para ejecutar su ciclo autónomo de disparo.
 */
public class TurnContext {

    private final Board board;
    private final ShotResultHandler cellMarker;
    private final Runnable onEnableInput;
    private final Runnable onShotRegistered;
    private final Runnable onTurnEnd;
    private final Consumer<String> onVictory;

    public TurnContext(Board board, ShotResultHandler cellMarker, Runnable onEnableInput,
                        Runnable onShotRegistered, Runnable onTurnEnd, Consumer<String> onVictory) {
        this.board = board;
        this.cellMarker = cellMarker;
        this.onEnableInput = onEnableInput;
        this.onShotRegistered = onShotRegistered;
        this.onTurnEnd = onTurnEnd;
        this.onVictory = onVictory;
    }

    public Board getBoard() { return board; }
    public ShotResultHandler getCellMarker() { return cellMarker; }
    public Runnable getOnEnableInput() { return onEnableInput; }
    public Runnable getOnShotRegistered() { return onShotRegistered; }
    public Runnable getOnTurnEnd() { return onTurnEnd; }
    public Consumer<String> getOnVictory() { return onVictory; }
}
