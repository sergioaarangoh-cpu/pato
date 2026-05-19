package view;

import controller.GameController;
import controller.InputHandler;
import controller.MouseHandler;
import controller.ScreenManager;
import model.GameState;
import util.SoundManager;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de OldPato.
 * <p>
 * Controla el flujo inicial de la aplicacion mostrando primero el panel de
 * bienvenida y, despues de la interaccion del usuario, construye el panel de
 * juego con su HUD y sus manejadores de entrada.
 */
public class MainFrame extends JFrame {

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 700;
    private static final String WELCOME_SCREEN = "welcome";
    private static final String GAME_SCREEN = "game";
    private static final String GAME_OVER_SCREEN = "gameover";
    private static final String WELCOME_MUSIC = "OldPato\\src\\main\\resources\\sounds\\welcomeMusic.wav";
    private static final String START_SOUND = "OldPato\\src\\main\\resources\\sounds\\startsound.wav";
    private static final String LOST_SOUND = "OldPato\\src\\main\\resources\\sounds\\youlost.wav";
    
    private final SoundManager soundManager;
    private final CardLayout cardLayout;
    private final JPanel screens;
    private boolean gameStarted;
    private GameController gameController;
    private WelcomePanel welcomePanel;

    /**
     * Crea la ventana principal y registra la pantalla de bienvenida como
     * primera vista visible.
     */
    public MainFrame() {
        soundManager = new SoundManager();
        cardLayout = new CardLayout();
        screens = new JPanel(cardLayout);
        screens.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        welcomePanel = new WelcomePanel(this::showGamePanel);
        screens.add(welcomePanel, WELCOME_SCREEN);
        screens.add(new GameOverPanel(this::showWelcomePanel), GAME_OVER_SCREEN);

        add(screens);

        setTitle("OldPato");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // suena al abrir el programa y cuando termina arranca la música del menú
        soundManager.playSound(START_SOUND, () -> soundManager.playMusic(WELCOME_MUSIC));
        SwingUtilities.invokeLater(welcomePanel::requestNameFocus);
    }

    /**
     * Muestra el panel de bienvenida y reproduce su música.
     */
    private void showWelcomePanel() {
        if (gameController != null) {
            gameController.reset();
        }
        gameStarted = false;
        welcomePanel.reset();
        soundManager.playMusic(WELCOME_MUSIC);
        cardLayout.show(screens, WELCOME_SCREEN);
        SwingUtilities.invokeLater(welcomePanel::requestNameFocus);
    }

    /**
     * Construye y muestra el panel de juego despues de la bienvenida.
     * <p>
     * Este metodo solo se ejecuta una vez para evitar crear multiples
     * instancias de {@link GamePanel}, {@link HUD} o temporizadores de juego.
     *
     * @param playerName nombre ingresado por el jugador en la bienvenida
     */
    private void showGamePanel(String playerName) {
        if (gameStarted) {
            cardLayout.show(screens, GAME_SCREEN);
            return;
        }
        gameStarted = true;

        GameState gameState = new GameState();
        gameState.setPlayerName(playerName);
        ScreenManager screenManager = new ScreenManager();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        GamePanel gamePanel = new GamePanel(soundManager);
        gamePanel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        MouseHandler mouseHandler = new MouseHandler();
        gamePanel.addMouseListener(mouseHandler);
        gamePanel.addMouseMotionListener(mouseHandler);

        HUD hud = new HUD(gameState);
        hud.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        InputHandler inputHandler = new InputHandler();
        gamePanel.addKeyListener(inputHandler);
        gamePanel.setFocusable(true);

        gameController = new GameController(
                gameState, gamePanel, hud, mouseHandler, inputHandler, screenManager, soundManager
        );

        // conecta el clic directamente con checkShot
        mouseHandler.setOnShoot(gameController::checkShot);

        // cuando el juego termina muestra el panel de game over
        screenManager.setOnGameOver(() -> {
            soundManager.stopMusic();
            soundManager.playSound(LOST_SOUND);
            cardLayout.show(screens, GAME_OVER_SCREEN);
            SwingUtilities.invokeLater(() ->
                    screens.getComponent(screens.getComponentCount() - 1).requestFocusInWindow()
            );
        });

        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(hud, JLayeredPane.PALETTE_LAYER);

        screens.add(layeredPane, GAME_SCREEN);
        cardLayout.show(screens, GAME_SCREEN);
        revalidate();
        repaint();

        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
        gameController.startGame();
    }

    /**
     * Punto de entrada de la aplicacion.
     *
     * @param args argumentos de linea de comandos no utilizados
     */
    public static void main(String[] args) {
        new MainFrame();
    }
}