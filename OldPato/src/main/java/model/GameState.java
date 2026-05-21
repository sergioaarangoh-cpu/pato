package model;

/**
 * @author sergioaarangoh-cpu
 * @version 1.0
 * Almacena el estado del juego
 */
public class GameState {

    // Condiciones iniciales de vidas y tiempo.
    private static final int INITIAL_LIVES = 5;
    private static final int INITIAL_TIMER_SECONDS = 120;

    // Variables para vidas, puntaje, tiempo restante, y booleana para game over
    private int lives;
    private int score;
    private int remainingTime;
    private int elapsedTime;
    private boolean gameOver;
    private String playerName;

    /**
     * Constructor que inicializa el gamestate con los valores por defecto de cada variable
     */
    public GameState(){
        lives = INITIAL_LIVES;
        score = 0;
        remainingTime = INITIAL_TIMER_SECONDS;
        elapsedTime = 0;
        gameOver = false;
        playerName = "";
    }

    /**
     * Reinicia el estado del juego a los valores iniciales.
     */
    public void reset() {
        lives = INITIAL_LIVES;
        score = 0;
        remainingTime = INITIAL_TIMER_SECONDS;
        elapsedTime = 0;
        gameOver = false;
        playerName = "";
    }
    /**
     * Verifica si el juego ha terminado
     * @return true si el jugador perdió
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Establece si el juego ha terminado.
     *
     * @param gameOver {@code true} si la partida debe marcarse como terminada
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }


    /**
     * Obtiene el nombre del jugador de la partida actual.
     *
     * @return nombre del jugador, o una cadena vacia si no se ha definido
     */
    public String getPlayerName() {
        return playerName == null ? "" : playerName;
    }

    /**
     * Define el nombre del jugador de la partida actual.
     *
     * @param playerName nombre ingresado por el usuario
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName == null ? "" : playerName.trim();
    }

    /**
     * Obtiene el tiempo restante de la partida.
     *
     * @return segundos restantes
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * Define el tiempo restante de la partida.
     *
     * @param remainingTime segundos restantes
     */
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    /**
     * Obtiene el puntaje acumulado.
     *
     * @return puntaje actual
     */
    public int getScore() {
        return score;
    }

    /**
     * Define el puntaje acumulado.
     *
     * @param score nuevo puntaje
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Obtiene la cantidad de vidas restantes.
     *
     * @return vidas actuales
     */
    public int getLives() {
        return lives;
    }

    /**
     * Define la cantidad de vidas restantes.
     *
     * @param lives nueva cantidad de vidas
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * Recupera una vida sin superar la cantidad inicial.
     */
    public void recoverLife() {
        if (lives < INITIAL_LIVES) {
            lives++;
        }
    }

    /**
     * Aumenta el tiempo total jugado en un segundo.
     */
    public void incrementElapsedTime() {
        elapsedTime++;
    }

    /**
     * Obtiene el tiempo total jugado en segundos.
     *
     * @return segundos jugados en la partida actual
     */
    public int getElapsedTime() {
        return elapsedTime;
    }
}
