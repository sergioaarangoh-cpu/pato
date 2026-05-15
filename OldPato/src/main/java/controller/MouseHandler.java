package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Maneja los eventos del mouse para el disparinho y movimiento de mira
 */
public class MouseHandler extends MouseAdapter {

    // booleana que indica si el jugador está disparinhando
    private boolean shooting = false;

    // posicion del mouse en x y y
    private int mouseX = 0;
    private int mouseY = 0;

    /**
     * Se ejecuta cuando el jugador clickea
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        shooting = true;
    }

    /**
     * Cuando el jugador suelta el clic
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        shooting = false;
    }

    /**
     * Mouse movido sin clic
     * @param e the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Mouse movido arrastrado con clic
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public boolean isShooting() {
        return shooting;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
