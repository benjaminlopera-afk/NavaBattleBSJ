package com.example.navabattlebsj.patterns;

/**
 * Estrategia de turno del jugador humano (HU-2). El disparo en sí está
 * dirigido por eventos de clic dentro de {@code BoardController}, que ya
 * tiene conectados los callbacks de autoguardado, fin de turno y victoria
 * mediante {@code setUpShooting(...)}. Esta estrategia solo se encarga de
 * habilitar la interacción del tablero cuando le corresponde disparar al
 * humano.
 */
public class HumanTurnStrategy implements TurnStrategy {

    @Override
    public void executeTurn(TurnContext context) {
        if (context.getOnEnableInput() != null) {
            context.getOnEnableInput().run();
        }
    }
}
