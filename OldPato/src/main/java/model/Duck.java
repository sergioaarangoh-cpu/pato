package model;

import javax.swing.*;
import java.awt.*;

/**
 * Representa el pato del juego, incluyendo su posicion, sprite y movimiento.
 */
public class Duck implements Runnable {
    private int x;
    private int y;
    private int speedX;
    //private int speedY;
    private int panelWidth;
    private int panelHeight;
    private Image sprite;

    /**
     * Crea un pato en una posicion inicial y carga su imagen.
     *
     * @param x coordenada horizontal inicial.
     * @param y coordenada vertical inicial.
     * @param panelWidth ancho del panel donde se mueve.
     * @param panelHeight alto del panel donde se mueve.
     * @param imagePath ruta de la imagen del pato.
     */
    public Duck(int x, int y, int panelWidth, int panelHeight, String imagePath) {
        this.x = x;
        this.y = y;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        speedX = (int) (Math.random() * 8) - 3;
        //speedY = (int) (Math.random() * 8) - 3;

        if (speedX == 0) {
            speedX = 1;
        }

        /**if (speedY == 0) {
            speedY = 1;
        }
         **/
        sprite = new ImageIcon(imagePath).getImage();
    }

    /**
     * Obtiene la coordenada horizontal actual del pato.
     *
     * @return posicion horizontal.
     */
    public int getX() {
        return x;
    }

    /**
     * Obtiene la coordenada vertical actual del pato.
     *
     * @return posicion vertical.
     */
    public int getY() {
        return y;
    }

    /**
     * Mueve el pato de forma fluida dentro de los limites del panel.
     *
     * @param panelWidth ancho actual del panel.
     * @param panelHeight alto actual del panel.
     */
    public void move(int panelWidth, int panelHeight) {
        x += speedX;
        //y += speedY;

        if (x <= 0 || x >= panelWidth ) {
            speedX *= -1;
        }

        /**if (y <= 0 || y >= panelHeight  ){
            speedY *= -1;
        }
         **/
    }

    /**
     * Actualiza los limites donde el pato puede moverse.
     *
     * @param panelWidth ancho actual del panel.
     * @param panelHeight alto actual del panel.
     */
    public void setPanelSize(int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    /**
     * Ejecuta el movimiento aleatorio del pato en un hilo.
     */
    @Override
    public void run() {
        while (true) {
            move(panelWidth, panelHeight);

            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * Dibuja el pato en su posicion actual.
     *
     * @param graphics contexto grafico donde se dibuja el sprite.
     */
    public void draw(Graphics graphics) {
        if (sprite != null) {
            graphics.drawImage(sprite, x, y, 80, 80, null);
        }
    }
}
