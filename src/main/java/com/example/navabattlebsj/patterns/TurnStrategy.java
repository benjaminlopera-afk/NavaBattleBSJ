package com.example.navabattlebsj.patterns;

/**
 * Patrón de comportamiento (Strategy) que encapsula cómo se ejecuta el turno
 * de un jugador. Permite que {@code GameController} alterne entre el turno
 * del humano ({@link HumanTurnStrategy}) y el de la máquina
 * ({@link MachineTurnStrategy}) sin condicionales explícitos dispersos por
 * el controlador, y sin duplicar la lógica compartida de autoguardado, fin
 * de turno y victoria (que vive en los callbacks de {@link TurnContext}).
 */

public interface TurnStrategy {
    void executeTurn(TurnContext context);
}