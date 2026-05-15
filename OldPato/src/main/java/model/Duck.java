package model;

import java.awt.*;

/**
 * Representa el pato del juego, incluyendo su posicion, sprite y movimiento.
 */
public class Duck extends Entity implements Runnable {
    private int speedX;
    private int speedY;
    private int panelWidth;
    private int panelHeight;

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
        super(x, y, 80, 80, imagePath);
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        speedX = (int) (Math.random() * 8) - 3;
        speedY = (int) (Math.random() * 8) - 3;

        // Aqui podremos obtener de manera aleatoria la velocidad para despues ser utilizada individualmente en cada pato.
        speedX = (int) (Math.random() * 7) - 3;
        speedY = (int) (Math.random() * 7) - 3;

        // Esto es por si el pato queda en 0 no se sienta interrumpida la animacion y se vea fluido ( mejor dicho que no pare)
        if (speedX == 0) {
            speedX = 1;
        }

        if (speedY == 0) {
            speedY = 1;
        }
    }

    /**
     * Mueve el pato de forma fluida dentro de los limites del panel.
     *
     * @param panelWidth ancho actual del panel.
     * @param panelHeight alto actual del panel.
     */
    public void move(int panelWidth, int panelHeight) {
        x += speedX;
        y += speedY;

        if (x <= 0 || x >= panelWidth - width) {
            speedX *= -1;
        }


        if (y <= 0 || y >= panelHeight - height) {
            speedY *= -1;
        }
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
}
