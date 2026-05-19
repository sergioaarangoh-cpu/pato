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
    private boolean gameOver;
    private String playerName;

    /**
     * Constructor que inicializa el gamestate con los valores por defecto de cada variable
     */
    public GameState(){
        lives = INITIAL_LIVES;
        score = 0;
        remainingTime = INITIAL_TIMER_SECONDS;
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
     * Establece si el juego ha terminada
     * @param gameOver
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

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
}
