package model;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Representa un pato en el juego que se mueve rebotando en los bordes.
 *
 * @author sergioaarangoh-cpu, juancho-2006, victorm
 * @version 2.0
 */
public class Duck extends Entity implements Runnable {

    private static final int DUCK_SIZE = 90;
    private static final int DEFAULT_LIFETIME_MS = 6000;
    private static final int BLINK_WARNING_MS = 2000;
    private static final int BLINK_INTERVAL_MS = 150;

    private int speedX;
    private int speedY;
    private int panelWidth;
    private int panelHeight;
    private String leftImagePath;
    private String rightImagePath;
    private volatile boolean running;
    private final long spawnTimestamp;
    private int lifetimeMs;
    protected boolean blinkEnabled;
    private double speedMultiplier = 1.0;

    /**
     * Crea un pato en una posición inicial.
     *
     * @param x           coordenada horizontal inicial
     * @param y           coordenada vertical inicial
     * @param panelWidth  ancho del panel
     * @param panelHeight alto del panel
     * @param leftImage   ruta del sprite mirando a la izquierda
     * @param rightImage  ruta del sprite mirando a la derecha
     */
    public Duck(int x, int y, int panelWidth, int panelHeight, String leftImage, String rightImage) {
        super(x, y, DUCK_SIZE, DUCK_SIZE, leftImage);
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.leftImagePath = leftImage;
        this.rightImagePath = rightImage;
        this.speedX = 4;
        this.speedY = 3;
        this.spawnTimestamp = System.currentTimeMillis();
        this.lifetimeMs = DEFAULT_LIFETIME_MS;
        this.blinkEnabled = true;
    }

    /**
     * Carga un sprite desde el classpath.
     *
     * @param imagePath ruta de la imagen
     * @return imagen cargada o null si no se encontró
     */
    private Image loadSprite(String imagePath) {
        URL url = getClass().getResource(imagePath);
        if (url == null) {
            System.out.println("No se encontró la imagen: " + imagePath);
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    /**
     * Ejecuta el movimiento del pato en su propio hilo.
     */
    @Override
    public void run() {
        running = true;
        while (running) {
            move();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /**
     * Mueve el pato y cambia su sprite según la dirección horizontal.
     */
    protected void move() {
        x += (int) Math.round(speedX * speedMultiplier);
        y += (int) Math.round(speedY * speedMultiplier);

        if (x <= 0) {
            speedX *= -1;
            sprite = loadSprite(rightImagePath);
        }

        if (x >= panelWidth - width) {
            speedX *= -1;
            sprite = loadSprite(leftImagePath);
        }

        if (y <= 0 || y >= panelHeight - height) {
            speedY *= -1;
        }
    }

    /**
     * Actualiza el tamaño del panel donde se mueve el pato.
     *
     * @param panelWidth  nuevo ancho
     * @param panelHeight nuevo alto
     */
    public void setPanelSize(int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    /**
     * Define la velocidad inicial del pato al entrar en pantalla.
     *
     * @param speedX velocidad horizontal
     * @param speedY velocidad vertical
     */
    public void setSpeed(int speedX, int speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    /**
     * Escala la velocidad de movimiento sin alterar la logica de rebote.
     * Usado por el frenzy mode para acelerar temporalmente a los patos.
     *
     * @param speedMultiplier factor de velocidad, 1.0 es la velocidad normal
     */
    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    /**
     * Define cuanto tiempo permanece el pato en pantalla antes de desaparecer.
     *
     * @param lifetimeMs tiempo de vida en milisegundos
     */
    public void setLifetime(int lifetimeMs) {
        this.lifetimeMs = lifetimeMs;
    }

    /**
     * Verifica si ya se cumplio el tiempo de vida del pato.
     *
     * @return true si el pato debe desaparecer
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTimestamp >= lifetimeMs;
    }

    /**
     * Dibuja el pato, aplicando titileo durante los ultimos instantes de su vida.
     *
     * @param graphics contexto grafico usado por Swing
     */
    @Override
    public void draw(Graphics graphics) {
        if (blinkEnabled && isBlinkedOut()) {
            return;
        }
        super.draw(graphics);
    }

    /**
     * Determina si, en este instante, el pato debe omitirse del dibujo para simular titileo.
     *
     * @return true si el pato esta en su fase "apagada" del titileo
     */
    private boolean isBlinkedOut() {
        long elapsed = System.currentTimeMillis() - spawnTimestamp;
        long remaining = lifetimeMs - elapsed;
        if (remaining > BLINK_WARNING_MS || remaining <= 0) {
            return false;
        }
        return (elapsed / BLINK_INTERVAL_MS) % 2 != 0;
    }

    /**
     * Detiene el ciclo de movimiento del pato.
     */
    public void stop() {
        running = false;
    }
}
