package view;

import main.java.model.Duck;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel principal del juego encargado de dibujar el fondo, el titulo y el pato.
 */
public class GamePanel extends JPanel {
    private Image fondo;
    private List<Duck> ducks;

    /**
     * Crea el panel, carga las imagenes iniciales e inicia los hilos de los patos.
     */
    public GamePanel() {
        fondo = new ImageIcon("OldPato/src/main/resources/images/background.png").getImage();
        ducks = new ArrayList<>();

        ducks.add(new Duck(100, 100, 900, 700, "OldPato/src/main/resources/images/duck.png"));
        ducks.add(new Duck(300, 200, 900, 700, "OldPato/src/main/resources/images/duck.png"));
        ducks.add(new Duck(500, 300, 900, 700, "OldPato/src/main/resources/images/duck.png"));

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

        for (Duck duck : ducks) {
            duck.draw(graphics);
        }
    }
}
