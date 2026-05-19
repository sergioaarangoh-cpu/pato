package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Panel de game over que se muestra cuando el jugador pierde.
 *
 * @author sergioaarangoh-cpu
 * @version 1.0
 */
public class GameOverPanel extends JPanel {

    private final Image gameOverImage;
    private final Runnable onReturn;
    private boolean returned;

    /**
     * Crea el panel de game over.
     *
     * @param onReturn acción ejecutada cuando el jugador vuelve al menú
     */
    public GameOverPanel(Runnable onReturn) {
        this.onReturn = onReturn;
        this.gameOverImage = loadImage();
        setFocusable(true);
        configureReturnActions();
    }

    /**
     * Configura las entradas para volver al menú.
     */
    private void configureReturnActions() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                returnToMenu();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "returnToMenu");
        getActionMap().put("returnToMenu", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                returnToMenu();
            }
        });
    }

    /**
     * Vuelve al menú una sola vez.
     */
    private void returnToMenu() {
        if (returned) return;
        returned = true;
        onReturn.run();
    }

    /**
     * Carga la imagen de game over desde el classpath.
     */
    private Image loadImage() {
        URL url = getClass().getResource("/images/gameoverscreenfinal.png");
        if (url == null) {
            System.out.println("No se encontró la imagen de game over");
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    /**
     * Dibuja la imagen de game over ocupando todo el panel.
     *
     * @param graphics contexto gráfico usado por Swing
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (gameOverImage != null) {
            graphics.drawImage(gameOverImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}