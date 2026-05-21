package model;

/**
 * Enum es una clase especial como las interfaces para definir un conjunto fijo de ctes
 * Se hizo en un archivo separado por convenciones de java, aunque podría ir en GameState
 * Representa las 4 posibles pantallas del juego solicitadas en los requerimientos del programa
 * @author sergioaarangoh-cpu
 * @version 1.0
 */
public enum GameScreen {
    /**
     * Pantalla inicial de menu.
     */
    MENU,

    /**
     * Pantalla con instrucciones del juego.
     */
    INSTRUCTIONS,

    /**
     * Pantalla donde ocurre la partida.
     */
    PLAYING,

    /**
     * Pantalla final mostrada al perder.
     */
    GAME_OVER
}
