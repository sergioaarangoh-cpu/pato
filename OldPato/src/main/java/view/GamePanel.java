package view;

import model.Duck;
import model.Musica;
import model.Scope;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel principal del juego encargado de dibujar el fondo, el titulo y el pato.
 */
public class GamePanel extends JPanel {
    // "/" porque Java lo interpreta bien tanto en Linux como en Windows.
    private static final String BACKGROUND_IMAGE =
            "/images/background.png";

    private static final String DUCK_LEFT_IMAGE =
            "/images/duckleft.png";

    private static final String DUCK_RIGHT_IMAGE =
            "/images/duckright.png";

    private static final String BACKGROUND_SOUND =
            "/sounds/background.wav";

    private Image fondo;
    private List<Duck> ducks;
    private Musica musica;
    private Scope scope;

    /**
     * Crea el panel, carga las imagenes iniciales e inicia los hilos de los patos.
     */
    public GamePanel() {
        fondo = new ImageIcon(getClass().getResource(BACKGROUND_IMAGE)).getImage();
        ducks = new ArrayList<>();
        scope = new Scope();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                scope.setX(e.getX() - 40);
                scope.setY(e.getY() - 40);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                scope.setX(e.getX() - 40);
                scope.setY(e.getY() - 40);
                repaint();
            }
        });

        musica = new Musica();

        musica.play(BACKGROUND_SOUND);
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

        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", Font.BOLD, 24));
        graphics.drawString("OldPato", 100, 50);

        for (Duck duck : ducks) {
            duck.draw(graphics);
        }
        scope.draw(graphics);
    }

    public List<Duck> getDucks() {
        return ducks;
    }
}


