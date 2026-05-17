package view;

import controller.GameController;
import controller.InputHandler;
import controller.MouseHandler;
import controller.ScreenManager;
import model.GameState;

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

    private final CardLayout cardLayout;
    private final JPanel screens;
    private boolean gameStarted;

    /**
     * Crea la ventana principal y registra la pantalla de bienvenida como
     * primera vista visible.
     */
    public MainFrame() {
        cardLayout = new CardLayout();
        screens = new JPanel(cardLayout);
        screens.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        screens.add(new WelcomePanel(this::showGamePanel), WELCOME_SCREEN);

        add(screens);

        setTitle("OldPato");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Construye y muestra el panel de juego despues de la bienvenida.
     * <p>
     * Este metodo solo se ejecuta una vez para evitar crear multiples
     * instancias de {@link GamePanel}, {@link HUD} o temporizadores de juego.
     */
    private void showGamePanel() {
        if (gameStarted) {
            return;
        }
        gameStarted = true;

        GameState gameState = new GameState();
        ScreenManager screenManager = new ScreenManager();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        GamePanel gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        MouseHandler mouseHandler = new MouseHandler();
        gamePanel.addMouseListener(mouseHandler);
        gamePanel.addMouseMotionListener(mouseHandler);

        HUD hud = new HUD(gameState);
        hud.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        InputHandler inputHandler = new InputHandler();
        gamePanel.addKeyListener(inputHandler);
        gamePanel.setFocusable(true);

        GameController gameController = new GameController(gameState, gamePanel,
                hud, mouseHandler, inputHandler, screenManager);

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