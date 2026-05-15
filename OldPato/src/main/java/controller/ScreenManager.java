package controller;

import model.GameScreen;

/**
 * @author sergioaarangoh-cpu
 * @version 1.0
 * Maneja la navegacion entre pantallas, esta clase se creó porque no había ningún lugar adecuado
 * para meter todo esto
 */
public class ScreenManager {
    // pantalla actual del juego
    private GameScreen currentScreen;

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
     * Cambia a la pantalla de game over.
     */
    public void goToGameOver() {
        currentScreen = GameScreen.GAME_OVER;
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
}

