package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Maneja los eventos del mouse para el disparo y movimiento de mira.
 */
public class MouseHandler extends MouseAdapter {

    // booleana que indica si el jugador está disparando
    private boolean shooting = false;

    // posicion del mouse en x y y
    private int mouseX = 0;
    private int mouseY = 0;

    // acción que se ejecuta inmediatamente cuando el jugador hace clic
    private Runnable onShoot;

    /**
     * Registra la acción que se ejecuta al disparar.
     * @param onShoot acción a ejecutar al hacer clic
     */
    public void setOnShoot(Runnable onShoot) {
        this.onShoot = onShoot;
    }

    /**
     * Se ejecuta cuando el jugador clickea.
     * @param e evento del mouse
     */
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        shooting = true;
        // notifica inmediatamente en lugar de esperar al timer
        if (onShoot != null) {
            onShoot.run();
        }
    }

    /**
     * Cuando el jugador suelta el clic.
     * @param e evento del mouse
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        shooting = false;
    }

    /**
     * Mouse movido sin clic.
     * @param e evento del mouse
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Mouse movido arrastrado con clic.
     * @param e evento del mouse
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Indica si el jugador mantiene presionado el disparo.
     *
     * @return {@code true} si el mouse esta disparando
     */
    public boolean isShooting() { return shooting; }

    /**
     * Obtiene la coordenada horizontal actual del mouse.
     *
     * @return coordenada x del mouse
     */
    public int getMouseX() { return mouseX; }

    /**
     * Obtiene la coordenada vertical actual del mouse.
     *
     * @return coordenada y del mouse
     */
    public int getMouseY() { return mouseY; }
}
