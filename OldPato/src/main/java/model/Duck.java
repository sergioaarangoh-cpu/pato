package model;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Representa un pato en el juego que se mueve rebotando en los bordes.
 *
 * @author sergioaarangoh-cpu, juancho-2006
 * @version 2.0
 */
public class Duck extends Entity implements Runnable {

    private static final int DUCK_SIZE = 120;

    private int speedX;
    private int speedY;
    private int panelWidth;
    private int panelHeight;
    private String leftImagePath;
    private String rightImagePath;

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
        this.speedY = 2;
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
        while (true) {
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
        x += speedX;
        y += speedY;

        if (x <= 0) {
            speedX *= -1;
            sprite = loadSprite(rightImagePath);
        }

        if (x >= panelWidth - DUCK_SIZE) {
            speedX *= -1;
            sprite = loadSprite(leftImagePath);
        }

        if (y <= 0 || y >= panelHeight - DUCK_SIZE) {
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
}