package view;

import controller.GameController;
import controller.InputHandler;
import controller.MouseHandler;
import controller.ScreenManager;
import model.GameState;
import view.HUD;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public  MainFrame(){
        GameState gameState = new GameState();
        ScreenManager screenManager = new ScreenManager();

        //Con layeredpane se pone el HUD encima del panel de juego
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(900, 700));

        GamePanel gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 900, 700);

        MouseHandler mouseHandler = new MouseHandler();
        gamePanel.addMouseListener(mouseHandler);
        gamePanel.addMouseMotionListener(mouseHandler);

        HUD hud = new HUD(gameState);
        hud.setBounds(0, 0, 900, 700);

        InputHandler inputHandler = new InputHandler();
        gamePanel.addKeyListener(inputHandler);
        // necesarios porque JPanel por defecto no recibe eventos de teclado.
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        GameController gameController = new GameController(gameState, gamePanel,
                hud, mouseHandler, inputHandler, screenManager);

        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(hud, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);

        setTitle("OldPato");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        gameController.startGame();

    }

    public static void main(String[] args){
        new MainFrame();

    }


}
