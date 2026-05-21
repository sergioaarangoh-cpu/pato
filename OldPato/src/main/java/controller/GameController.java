package controller;

import model.Duck;
import model.Duckencia;
import model.EvilDuck;
import model.GameState;
import util.ScoreManager;
import util.SoundManager;
import view.GamePanel;
import view.HUD;

import javax.swing.*;

/**
 * Controla el estado de toda la partida integrando todas sus partes: input, gamestate y lo propio del paquete view
 *
 * @author sergioaarangoh-cpu
 * @version 1.2
 */
public class GameController {

    private static final String GUNSHOT_SOUND = "/sounds/gunshot.wav";
    private static final String DUCK_SOUND = "/sounds/duck1.wav";
    private static final String DUCKENCIA_SOUND = "/sounds/duckencia.wav";
    private static final String EVIL_DUCK_SOUND = "/sounds/evilduck.wav";

    private GameState gameState;
    private GamePanel gamePanel;
    private HUD hud;
    private InputHandler inputHandler;
    private MouseHandler mouseHandler;
    private GamepadHandler gamepadHandler;
    private ScreenManager screenManager;
    private SoundManager soundManager;
    private Timer countdownTimer;
    private Timer inputTimer;
    private int evilDuckHits;
    private boolean scoreSaved;

    /**
     * Crea el controller con todo lo necesario.
     *
     * @param gameState     estado del juego
     * @param gamePanel     panel principal del juego
     * @param hud           info de partida
     * @param mouseHandler  manejador de mouse
     * @param inputHandler  manejador de teclado
     * @param screenManager manejador de pantallas
     * @param soundManager  manejador de audio
     */
    public GameController(GameState gameState, GamePanel gamePanel, HUD hud,
                          MouseHandler mouseHandler, InputHandler inputHandler,
                          ScreenManager screenManager, SoundManager soundManager) {
        this.gameState = gameState;
        this.gamePanel = gamePanel;
        this.hud = hud;
        this.mouseHandler = mouseHandler;
        this.gamepadHandler = new GamepadHandler();
        this.inputHandler = inputHandler;
        this.screenManager = screenManager;
        this.soundManager = soundManager;

        // timer que se dispara cada frame para revisar si el jugador presionó algo
        inputTimer = new Timer(1000 / 60, e -> handleInput());
        inputTimer.start();

        // reduce el tiempo en 1 cada segundo
        countdownTimer = new Timer(1000, e -> {
            gameState.incrementElapsedTime();
            gameState.setRemainingTime(gameState.getRemainingTime() - 1);
            hud.repaint();

            if (gameState.getRemainingTime() <= 0) {
                finishGame();
            }
        });
    }

    /**
     * Maneja el input del teclado según la pantalla actual.
     */
    private void handleInput() {
        handleGamepadInput();

        if (inputHandler.isEnterPressed()) {
            switch (screenManager.getCurrentScreen()) {
                case INSTRUCTIONS:
                    startGame();
                    break;
                case GAME_OVER:
                    gameState.reset();
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
     * Actualiza el mando y aplica sus acciones sobre el juego.
     */
    private void handleGamepadInput() {
        if (!screenManager.isPlaying()) {
            return;
        }

        gamepadHandler.update(gamePanel.getWidth(), gamePanel.getHeight());
        if (!gamepadHandler.isAvailable()) {
            return;
        }
        gamePanel.setAimPosition(gamepadHandler.getAimX(), gamepadHandler.getAimY());

        if (gamepadHandler.wasShootPressed()) {
            checkShotAt(gamepadHandler.getAimX(), gamepadHandler.getAimY());
        }
    }

    /**
     * Empieza el juego e inicia la cuenta regresiva.
     */
    public void startGame() {
        screenManager.goToPlaying();
        gamepadHandler.centerAim(gamePanel.getWidth(), gamePanel.getHeight());
        countdownTimer.start();
    }

    /**
     * Reinicia el juego a su estado inicial.
     */
    public void reset() {
        inputTimer.stop();
        countdownTimer.stop();
        gamepadHandler.dispose();
        gamePanel.stopGame();
        gameState.reset();
        evilDuckHits = 0;
        scoreSaved = false;
        screenManager.goToMenu();
    }

    /**
     * Verifica si se disparó y si algún pato fue golpeado.
     */
    public void checkShot() {
        if (!screenManager.isPlaying()) return;
        if (!mouseHandler.isShooting()) return;

        checkShotAt(gamePanel.getAimX(), gamePanel.getAimY());
    }

    /**
     * Verifica si un disparo realizado en las coordenadas indicadas golpea algun pato.
     *
     * @param mx coordenada horizontal del disparo
     * @param my coordenada vertical del disparo
     */
    private void checkShotAt(int mx, int my) {
        // sonido de disparo
        soundManager.playSound(GUNSHOT_SOUND);
        gamePanel.showExplosionAt(mx, my);

        for (Duck duck : gamePanel.getDucks()) {
            if (mx >= duck.getX() && mx <= duck.getX() + 120 &&
                    my >= duck.getY() && my <= duck.getY() + 120) {

                if (duck instanceof Duckencia) {
                    // disparar a un Duckencia quita una vida
                    soundManager.playSound(DUCKENCIA_SOUND);
                    gameState.setLives(gameState.getLives() - 1);
                    if (gameState.getLives() <= 0) {
                        finishGame();
                    }
                } else if (duck instanceof EvilDuck) {
                    soundManager.playSound(EVIL_DUCK_SOUND);
                    gameState.setRemainingTime(gameState.getRemainingTime() + 20);
                    evilDuckHits++;
                    if (evilDuckHits == 3) {
                        gameState.recoverLife();
                        evilDuckHits = 0;
                    }
                } else {
                    // disparar a un Duck normal suma puntos y tiempo
                    soundManager.playSound(DUCK_SOUND);
                    gameState.setScore(gameState.getScore() + 10);
                    gameState.setRemainingTime(gameState.getRemainingTime() + 5);
                }
            }
        }
    }

    /**
     * Termina la partida, guarda el puntaje y muestra la pantalla de game over.
     */
    private void finishGame() {
        gameState.setGameOver(true);
        countdownTimer.stop();
        if (!scoreSaved) {
            ScoreManager.saveScore(gameState.getPlayerName(), gameState.getScore());
            scoreSaved = true;
        }
        hud.repaint();
        screenManager.goToGameOver();
    }
}
