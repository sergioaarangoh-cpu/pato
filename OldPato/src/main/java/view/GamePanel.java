package view;

import model.Duck;
import model.Duckencia;
import model.EvilDuck;
import model.Scope;
import util.SoundManager;

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

    private static final String BACKGROUND_IMAGE = "/images/background.png";
    private static final String DUCK_LEFT_IMAGE = "/images/duckleft.png";
    private static final String DUCK_RIGHT_IMAGE = "/images/duckright.png";
    private static final String DUCKENCIA_LEFT_IMAGE = "/images/duckencialeft.png";
    private static final String DUCKENCIA_RIGHT_IMAGE = "/images/duckenciaright.png";
    private static final String BACKGROUND_SOUND = "OldPato\\src\\main\\resources\\sounds\\background.wav";
    private static final String EVIL_DUCK_LEFT_IMAGE = "/images/evilduckleft.png";
    private static final String EVIL_DUCK_RIGHT_IMAGE = "/images/evilduckright.png";

    private Image fondo;
    private List<Duck> ducks;
    private SoundManager soundManager;
    private Scope scope;
    private Font arcadeFont;

    /**
     * Crea el panel, carga las imagenes iniciales e inicia los hilos de los patos.
     *
     * @param soundManager manejador de audio compartido
     */
    public GamePanel(SoundManager soundManager) {
        this.soundManager = soundManager;

        try {
            arcadeFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new java.io.File("OldPato\\src\\main\\resources\\fonts\\ARCADE_N.TTF")
            ).deriveFont(Font.PLAIN, 20f);
        } catch (Exception e) {
            System.out.println("Error cargando fuente: " + e.getMessage());
            arcadeFont = new Font("Arial", Font.BOLD, 20);
        }

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

        // inicia la música de fondo
        soundManager.playMusic(BACKGROUND_SOUND);

        ducks.add(new Duck(100, 100, 900, 700, DUCK_LEFT_IMAGE, DUCK_RIGHT_IMAGE));
        ducks.add(new Duck(300, 200, 900, 700, DUCK_LEFT_IMAGE, DUCK_RIGHT_IMAGE));
        ducks.add(new Duck(500, 300, 900, 700, DUCK_LEFT_IMAGE, DUCK_RIGHT_IMAGE));
        ducks.add(new Duckencia(450, 350, 900, 700, DUCKENCIA_LEFT_IMAGE, DUCKENCIA_RIGHT_IMAGE));
        ducks.add(new Duckencia(700, 400, 900, 700, DUCKENCIA_LEFT_IMAGE, DUCKENCIA_RIGHT_IMAGE));
        ducks.add(new EvilDuck(200, 200, 900, 700, EVIL_DUCK_LEFT_IMAGE, EVIL_DUCK_RIGHT_IMAGE));

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
        graphics.setFont(arcadeFont);
        graphics.drawString("OldPato", 10, 20);

        for (Duck duck : ducks) {
            duck.draw(graphics);
        }
        scope.draw(graphics);
    }

    /**
     * Obtiene la lista de patos en pantalla.
     *
     * @return lista de patos
     */
    public List<Duck> getDucks() {
        return ducks;
    }
}