package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author sergioaarangoh-cpu
 * @version 1.0 11/05/2026
 */
public class InputHandler extends KeyAdapter {

    //Indican si la tecla está siendo pulsada en ese momento
    public boolean enterPressed = false;
    public boolean escapePressed = false;

    /**
     *Método que se ejecuta automáticamente cuando el jugador presiona una tecla
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if (e.getKeyCode() == KeyEvent.VK_ENTER) enterPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) escapePressed = true;
    }

    /**
     * Método que se ejecuta automáticamente cuando el jugador suelta una tecla
     * @param e
     */
    @Override
    public void keyReleased (KeyEvent e) {
        super.keyPressed(e);
        if (e.getKeyCode() == KeyEvent.VK_ENTER) enterPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) escapePressed = false;
    }

    /**
     * Getter para enterPressed()
     * @return
     */
    public boolean isEnterPressed() {
        return enterPressed;
    }

    /**
     * Getter para escapePressed()
     * @return
     */
    public boolean isEscapePressed() {
        return escapePressed;
    }
}
