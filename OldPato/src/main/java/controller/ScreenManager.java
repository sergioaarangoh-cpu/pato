package controller;

import model.GameScreen;

/**
 * @author sergioaarangoh-cpu
 * @version 1.1
 * Maneja la navegacion entre pantallas, esta clase se creó porque no había ningún lugar adecuado
 * para meter todo esto
 */
public class ScreenManager {

    // pantalla actual del juego
    private GameScreen currentScreen;

    // acción que se ejecuta cuando el juego termina
    private Runnable onGameOver;

    /**
     * Inicializa el ScreenManager en la pantalla del menú.
     */
    public ScreenManager() {
        currentScreen = GameScreen.MENU;
    }

    /**
     * Obtiene la pantalla actual.
     * @return pantalla actual
     */
    public GameScreen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Cambia a la pantalla de instrucciones.
     */
    public void goToInstructions() {
        currentScreen = GameScreen.INSTRUCTIONS;
    }

    /**
     * Cambia a la pantalla de juego.
     */
    public void goToPlaying() {
        currentScreen = GameScreen.PLAYING;
    }

    /**
     * Cambia a la pantalla de game over y ejecuta la acción registrada.
     */
    public void goToGameOver() {
        currentScreen = GameScreen.GAME_OVER;
        if (onGameOver != null) {
            onGameOver.run();
        }
    }

    /**
     * Vuelve a la pantalla del menú
     */
    public void goToMenu() {
        currentScreen = GameScreen.MENU;
    }

    /**
     * Verifica si el juego está activo
     * @return true si la pantalla actual es PLAYING
     */
    public boolean isPlaying() {
        return currentScreen == GameScreen.PLAYING;
    }

    /**
     * Registra la acción que se ejecuta cuando el juego termina.
     * @param onGameOver acción a ejecutar al llegar a game over
     */
    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }
}