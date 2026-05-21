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
    private static final String BACKGROUND_SOUND = "/sounds/background.wav";
    private static final String EVIL_DUCK_LEFT_IMAGE = "/images/evilduckleft.png";
    private static final String EVIL_DUCK_RIGHT_IMAGE = "/images/evilduckright.png";
    private static final String EXPLOSION_IMAGE = "/images/explosion.png";
    private static final int EXPLOSION_DURATION_MS = 200;
    private static final int EXPLOSION_SCALE_NUMERATOR = 3;
    private static final int EXPLOSION_SCALE_DIVISOR = 8;

    private Image fondo;
    private Image explosionImage;
    private List<Duck> ducks;
    private SoundManager soundManager;
    private Scope scope;
    private Font arcadeFont;
    private EvilDuck evilDuck;
    private int aimX;
    private int aimY;
    private int explosionX;
    private int explosionY;
    private boolean showExplosion;
    private Timer explosionTimer;

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
                    getClass().getResourceAsStream("/fonts/ARCADE_N.TTF")
            ).deriveFont(Font.PLAIN, 20f);
        } catch (Exception e) {
            System.out.println("Error cargando fuente: " + e.getMessage());
            arcadeFont = new Font("Arial", Font.BOLD, 20);
        }

        fondo = new ImageIcon(getClass().getResource(BACKGROUND_IMAGE)).getImage();
        explosionImage = new ImageIcon(getClass().getResource(EXPLOSION_IMAGE)).getImage();
        ducks = new ArrayList<>();
        scope = new Scope();
        explosionTimer = new Timer(EXPLOSION_DURATION_MS, e -> {
            showExplosion = false;
            repaint();
        });
        explosionTimer.setRepeats(false);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setAimPosition(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                setAimPosition(e.getX(), e.getY());
            }
        });

        // inicia la música de fondo
        soundManager.playMusic(BACKGROUND_SOUND);

        ducks.add(new Duck(100, 100, 900, 700, DUCK_LEFT_IMAGE, DUCK_RIGHT_IMAGE));
        ducks.add(new Duck(300, 200, 900, 700, DUCK_LEFT_IMAGE, DUCK_RIGHT_IMAGE));
        ducks.add(new Duck(500, 300, 900, 700, DUCK_LEFT_IMAGE, DUCK_RIGHT_IMAGE));
        ducks.add(new Duckencia(450, 350, 900, 700, DUCKENCIA_LEFT_IMAGE, DUCKENCIA_RIGHT_IMAGE));
        ducks.add(new Duckencia(700, 400, 900, 700, DUCKENCIA_LEFT_IMAGE, DUCKENCIA_RIGHT_IMAGE));
        ducks.add(new Duckencia(200, 500, 900, 700, DUCKENCIA_LEFT_IMAGE, DUCKENCIA_RIGHT_IMAGE));
        ducks.add(new Duckencia(600, 150, 900, 700, DUCKENCIA_LEFT_IMAGE, DUCKENCIA_RIGHT_IMAGE));
        evilDuck = new EvilDuck(200, 200, 900, 700, EVIL_DUCK_LEFT_IMAGE, EVIL_DUCK_RIGHT_IMAGE);

        for (Duck duck : ducks) {
            Thread hiloDuck = new Thread(duck);
            hiloDuck.start();
        }

        Thread evilThread = new Thread(evilDuck);
        evilThread.start();
        Timer evilDuckTimer = new Timer(5000, e -> {
            if (ducks.contains(evilDuck)) {
                ducks.remove(evilDuck);
            }
            ducks.add(evilDuck);
            repaint();

            Timer hideEvilDuckTimer = new Timer(2000, hideEvent -> {
                ducks.remove(evilDuck);
                repaint();
            });
            hideEvilDuckTimer.setRepeats(false);
            hideEvilDuckTimer.start();
        });
        evilDuckTimer.start();

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
        drawExplosion(graphics);
        scope.draw(graphics);
    }

    /**
     * Muestra la explosion durante un instante en la posicion indicada.
     *
     * @param x coordenada horizontal del disparo
     * @param y coordenada vertical del disparo
     */
    public void showExplosionAt(int x, int y) {
        explosionX = x;
        explosionY = y;
        showExplosion = true;
        explosionTimer.restart();
        repaint();
    }

    /**
     * Dibuja la explosion centrada en el ultimo disparo.
     *
     * @param graphics contexto grafico usado por Swing
     */
    private void drawExplosion(Graphics graphics) {
        if (!showExplosion || explosionImage == null) {
            return;
        }

        int width = explosionImage.getWidth(this);
        int height = explosionImage.getHeight(this);
        width = width * EXPLOSION_SCALE_NUMERATOR / EXPLOSION_SCALE_DIVISOR;
        height = height * EXPLOSION_SCALE_NUMERATOR / EXPLOSION_SCALE_DIVISOR;
        graphics.drawImage(explosionImage, explosionX - width / 2, explosionY - height / 2, width, height, this);
    }

    /**
     * Obtiene la lista de patos en pantalla.
     *
     * @return lista de patos
     */
    public List<Duck> getDucks() {
        return ducks;
    }

    /**
     * Mueve la mira al punto indicado y mantiene sus coordenadas centrales.
     *
     * @param x coordenada horizontal de apuntado
     * @param y coordenada vertical de apuntado
     */
    public void setAimPosition(int x, int y) {
        aimX = clamp(x, 0, Math.max(0, getWidth() - 1));
        aimY = clamp(y, 0, Math.max(0, getHeight() - 1));
        scope.setX(aimX - scope.getWidth() / 2);
        scope.setY(aimY - scope.getHeight() / 2);
        repaint();
    }

    /**
     * Obtiene la coordenada horizontal actual de la mira.
     *
     * @return coordenada x de la mira
     */
    public int getAimX() {
        return aimX;
    }

    /**
     * Obtiene la coordenada vertical actual de la mira.
     *
     * @return coordenada y de la mira
     */
    public int getAimY() {
        return aimY;
    }

    /**
     * Restringe un valor dentro de un rango.
     *
     * @param value valor a restringir
     * @param min   valor minimo
     * @param max   valor maximo
     * @return valor dentro del rango indicado
     */
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Detiene temporizadores internos del panel antes de abandonar la partida.
     */
    public void stopGame() {
        if (explosionTimer != null) {
            explosionTimer.stop();
        }
    }
}
