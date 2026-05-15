package controller;

import model.Duck;
import model.GameState;
import view.GamePanel;
import view.HUD;

import javax.swing.Timer;
import java.util.List;

/**
 * Controla el estado de toda la partida integrando todas sus partes: input, gamestate y lo propio del paquete view
 * @author sergioaarangoh-cpu
 * @version 1.1
 */
public class GameController {

    private GameState gameState;
    private GamePanel gamePanel;
    private HUD hud;
    private InputHandler inputHandler;
    private MouseHandler mouseHandler;
    private ScreenManager screenManager;
    private Timer countdownTimer;


    /**
     * Crea el controller con todo lo necesario
     * @param gameState estado
     * @param gamePanel panel principal do jogo
     * @param hud info de partida
     * @param mouseHandler manejador de mouse
     */
    public GameController(GameState gameState, GamePanel gamePanel, HUD hud, MouseHandler mouseHandler, InputHandler inputHandler, ScreenManager screenManager){
        this.gameState = gameState;
        this.gamePanel = gamePanel;
        this.hud = hud;
        this.mouseHandler= mouseHandler;
        this.inputHandler = inputHandler;
        this.screenManager = screenManager;

        // timer que se dispara cada 1 frame para revisar si el jugador presionó algo
        Timer inputTimer = new Timer(1000 / 60, e -> handleInput());

        // reduce el tiempo em 1 cada segundo.
        countdownTimer = new Timer(1000, e->{
            gameState.setRemainingTime(gameState.getRemainingTime() - 1);
            hud.repaint();

            if (gameState.getRemainingTime() <= 0){
                gameState.setGameOver(true);
                screenManager.goToGameOver();
                countdownTimer.stop();
            }
        });
    }

    /**
     * Maneja el input del teclado según la pantalla actual.
     */
    private void handleInput() {
        if (inputHandler.isEnterPressed()) {
            switch (screenManager.getCurrentScreen()) {
                case INSTRUCTIONS:
                    startGame();
                    break;
                case GAME_OVER:
                    gameState = new GameState();
                    screenManager.goToMenu();
                    break;
            }
        }

        if (inputHandler.isEscapePressed()) {
            screenManager.goToMenu();
            countdownTimer.stop();
        }
    }

    /**
     * Empieza el juego e inicia la cuenta regresiva.
     */
    public void startGame(){
        screenManager.goToPlaying();
        countdownTimer.start();
    }


    /**
     * Verifica si se diparó y si algún pato fue golpeado
     */
    public void checkShot(){
        //Escapempara si no se se está jugando y si no se está disparando.
        if (!screenManager.isPlaying()){
            return;
        }

        if (!mouseHandler.isShooting()){
            return;
        }

        int mx = mouseHandler.getMouseX();
        int my = mouseHandler.getMouseY();
        for (Duck duck: gamePanel.getDucks()){
            if (mx >= duck.getX() && mx <= duck.getX() + 80 &&
                    my >= duck.getY() && my <= duck.getY() + 80) {
                gameState.setScore(gameState.getScore() + 10);
                gameState.setRemainingTime(gameState.getRemainingTime() + 5);
            }
        }
    }
}
