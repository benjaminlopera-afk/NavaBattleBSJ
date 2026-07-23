# NavaBattle 🚢
Juego de estrategia **Batalla Naval** desarrollado en **Java** con **JavaFX** como proyecto universitario para el curso de **Fundamentos de Programación Orientada a Eventos - Universidad del Valle 2026-1**.

---

## Descripción
El objetivo del juego es hundir toda la flota del oponente antes de que este hunda la propia. Cada jugador (humano y máquina) cuenta con dos tableros de 10x10: un **tablero de posición**, donde distribuye su flota de 10 barcos antes de comenzar, y un **tablero principal**, donde ataca el territorio del oponente. En cada turno el jugador dispara sobre una celda del tablero principal: si es **agua**, pierde el turno; si es **tocado** o **hundido**, puede volver a disparar. Gana quien hunda primero toda la flota enemiga.

---

## Tecnologías

| Herramienta   | Versión |
| ------------- | ------- |
| Java          | 17+     |
| JavaFX        | 17+     |
| Scene Builder | -       |
| IntelliJ IDEA | -       |
| Git / GitHub  | -       |

---

## Funcionalidades

* Colocación de barcos por arrastre (drag & drop), con rotación horizontal/vertical desde clic derecho.
* Validación de colocación: sin superposiciones ni barcos fuera del tablero.
* Bloqueo del tablero de posición una vez iniciada la partida.
* Disparo interactivo sobre el tablero principal con marcas en tiempo real (agua, tocado, hundido).
* Cambio de turno automático según el resultado del disparo.
* Verificación previa a la batalla: visualización en modal de solo lectura del tablero de posición de la máquina.
* Inteligencia artificial de la máquina: colocación aleatoria de su flota y disparos aleatorios sin repetición sobre el tablero del jugador.
* Animación de los disparos de la máquina con pausas entre turnos.
* Detección automática de fin de partida (victoria/derrota) con alerta informativa.
* Interfaz gráfica con tema oscuro desarrollada en JavaFX y CSS, con paleta diferenciada para tablero y UI.

---

## Estructura del proyecto

```
src
├── main
│   ├── java
│   │   └── com/example/navabattlebsj
│   │       ├── applications
│   │       │   └── NavaBattleApplication.java
│   │       ├── controllers
│   │       │   ├── BoardController.java
│   │       │   ├── GameController.java
│   │       │   └── MenuController.java
│   │       ├── exceptions
│   │       │   ├── NavaBattleException.java
│   │       │   ├── InvalidShipPositionException.java
│   │       │   ├── InvalidMoveException.java
│   │       │   └── GameAlreadyFinishedException.java
│   │       ├── models
│   │       │   ├── Board.java
│   │       │   ├── Cell.java
│   │       │   ├── CellState.java
│   │       │   ├── Fleet.java
│   │       │   ├── Game.java
│   │       │   ├── HumanPlayer.java
│   │       │   ├── MachinePlayer.java
│   │       │   ├── Orientation.java
│   │       │   ├── Player.java
│   │       │   ├── Position.java
│   │       │   ├── Ship.java
│   │       │   ├── ShipType.java
│   │       │   ├── Shot.java
│   │       │   └── ShotResult.java
│   │       ├── patterns
│   │       │   ├── GameState.java
│   │       │   ├── SaveFacade.java
│   │       │   ├── ShipFactory.java
│   │       │   └── TurnStrategy.java
│   │       ├── persistences
│   │       │   ├── SaveManager.java
│   │       │   ├── SerializationManager.java
│   │       │   └── TextFileManager.java
│   │       ├── utils
│   │       │   └── Paths.java
│   │       ├── Launcher.java
│   │       └── module-info.java
│   └── resources
│       └── com/example/navabattlebsj
│           ├── BoardView.fxml
│           ├── GameView.fxml
│           ├── MenuView.fxml
│           └── Styles.css
│
└── test
    └── java
        └── com/example/navabattlebsj/models
            (pendiente)
```

---

## Cómo ejecutar
1. Clona el repositorio.
2. Abre el proyecto en IntelliJ IDEA con el SDK de JavaFX configurado.
3. Espera a que se resuelvan las dependencias del proyecto.
4. Ejecuta la clase `Launcher.java`.

---

## Características implementadas
* ✔ Historia de Usuario 1: Colocación de barcos.
* ✔ Historia de Usuario 2: Realización de disparos.
* ✔ Historia de Usuario 3: Visualización del tablero de posición del oponente.
* ✔ Historia de Usuario 4: Implementación de la inteligencia artificial de la máquina.
* ✔ Historia de Usuario 5: Guardado automático del juego.
* ✔ Historia de Usuario 6: Inicio del juego.
* ✔ Patrón de diseño estructural y de comportamiento (creacional ya implementado: `ShipFactory`).
* ✔ Pruebas unitarias con JUnit.

---

## Estado del proyecto

| Historia de Usuario | Estado      | Notas |
| --- |-------------| --- |
| HU-1 Colocación de barcos | ✔ Completa | Drag & drop, rotación, validación de posición |
| HU-2 Realización de disparos | ✔ Completa | Marcas en tiempo real, cambio de turno |
| HU-3 Visualización del tablero del oponente | ✔ Completa | Modal de solo lectura, disponible solo antes de la batalla |
| HU-4 IA de la máquina | ✔ Completa | Colocación y disparo aleatorio, animado, sin repetición |
| HU-5 Guardado automático | ✔ Completa | Requiere serialización de tableros + archivo plano |
| HU-6 Inicio del juego | ✔ Completa | Cargar partida guardada vs. nueva partida |

---

## Autores
* **Benjamín Lopera** - 2515144
* **Sebastián Martínez** - 2519817
* **Jhonny Alexander Moreno Florez** - 2522112
