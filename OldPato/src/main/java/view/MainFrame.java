package main.java.view;

import main.java.view.GamePanel;

import model.GameState;
import view.HUD;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public  MainFrame(){
        GameState gameState = new GameState();

        //Con layeredpane se pone el HUD encima del panel de juego
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(900, 700));

        GamePanel gamePanel = new GamePanel();
        gamePanel.setBounds(0, 0, 900, 700);

        HUD hud = new HUD(gameState);
        hud.setBounds(0, 0, 900, 700);

        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(hud, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);

        setTitle("OldPato");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

    }

    public static void main(String[] args){
        new MainFrame();

    }


}
