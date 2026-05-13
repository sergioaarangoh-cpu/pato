package main.java.view;

import main.java.controller.GameController;
import main.java.model.Duck;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel principal del juego encargado de dibujar el fondo, el titulo y el pato.
 */
public class GamePanel extends JPanel {
    // "/" porque Java lo interpreta bien tanto en Linux como en Windows.
    private static final String BACKGROUND_IMAGE = "OldPato/src/main/resources/images/background.png";
    private static final String DUCK_LEFT_IMAGE = "OldPato/src/main/resources/images/duckleft.png";
    private static final String DUCK_RIGHT_IMAGE = "OldPato/src/main/resources/images/duckright.png";

    private Image fondo;
    private List<Duck> ducks;
    private GameController controller;

    /**
     * Crea el panel, carga las imagenes iniciales e inicia los hilos de los patos.
     */
    public GamePanel() {
        fondo = new ImageIcon(BACKGROUND_IMAGE).getImage();
        ducks = new ArrayList<>();

        controller = new GameController();
        Thread controllerThread = new Thread(controller);
        controllerThread.start();

        ducks.add(new Duck(100, 100, 900, 700, DUCK_LEFT_IMAGE));
        ducks.add(new Duck(300, 200, 900, 700, DUCK_LEFT_IMAGE));
        ducks.add(new Duck(500, 300, 900, 700, DUCK_RIGHT_IMAGE));

        for (Duck duck : ducks) {
            Thread hiloDuck = new Thread(duck);
            hiloDuck.start();
        }

        Timer timer = new Timer(1000 / 60, e -> {
            for (Duck duck : ducks) {
                duck.setPanelSize(getWidth(), getHeight());
            }
            repaint();
        });
        timer.start();
    }

    /**
     * Dibuja todos los elementos visibles del juego.
     *
     * @param graphics contexto grafico usado por Swing para pintar el panel.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (fondo != null) {
            graphics.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }

        Color color = new Color(37, 111, 186);
        graphics.setColor(color);
        graphics.setFont(new Font("Arial", Font.BOLD, 24));
        graphics.drawString("Duck Hunt", 100, 50);
        int minutes = controller.getRemainingSeconds() / 60;
        int seconds = controller.getRemainingSeconds() % 60;

        String timeText = String.format("%02d:%02d", minutes, seconds);

        graphics.drawString(timeText, 700, 50);

        for (Duck duck : ducks) {
            duck.draw(graphics);
        }
    }
}
