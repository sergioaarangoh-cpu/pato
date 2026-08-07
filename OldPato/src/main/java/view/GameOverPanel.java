package view;

import model.GameState;
import util.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de game over que se muestra cuando el jugador pierde.
 *
 * @author sergioaarangoh-cpu
 * @version 1.0
 */
public class GameOverPanel extends JPanel {

    private final Image gameOverImage;
    private final Runnable onReturn;
    private final Font arcadeFont;
    private final JButton returnButton;
    private List<ScoreManager.ScoreEntry> topScores;
    private String playerName;
    private int score;
    private int elapsedTime;
    private boolean returned;

    /**
     * Crea el panel de game over.
     *
     * @param onReturn acción ejecutada cuando el jugador vuelve al menú
     */
    public GameOverPanel(Runnable onReturn) {
        this.onReturn = onReturn;
        this.gameOverImage = loadImage();
        this.arcadeFont = loadArcadeFont();
        this.returnButton = new JButton("Volver al menu");
        this.topScores = new ArrayList<>();
        this.playerName = "";
        setFocusable(true);
        setLayout(null);
        configureReturnActions();
    }

    /**
     * Configura las entradas para volver al menú.
     */
    private void configureReturnActions() {
        returnButton.setBounds(30, 450, 260, 40);
        returnButton.setFont(arcadeFont);
        returnButton.addActionListener(e -> returnToMenu());
        add(returnButton);

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
     * Permite que el panel vuelva a aceptar la accion de retorno al menu.
     */
    public void reset() {
        returned = false;
    }

    /**
     * Actualiza la informacion que se muestra al terminar una partida.
     *
     * @param gameState estado final de la partida
     * @param topScores mejores puntajes guardados
     */
    public void setGameResult(GameState gameState, List<ScoreManager.ScoreEntry> topScores) {
        playerName = gameState.getPlayerName();
        score = gameState.getScore();
        elapsedTime = gameState.getElapsedTime();
        this.topScores = new ArrayList<>(topScores);
        repaint();
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
     * Carga la fuente arcade usada para los textos de resultado.
     *
     * @return fuente arcade, o una fuente de respaldo si no se puede cargar
     */
    private Font loadArcadeFont() {
        try {
            return Font.createFont(
                    Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/fonts/ARCADE_N.TTF")
            ).deriveFont(Font.PLAIN, 12f);
        } catch (Exception e) {
            System.out.println("Error cargando fuente: " + e.getMessage());
            return new Font("Arial", Font.BOLD, 12);
        }
    }

    /**
     * Convierte segundos a formato minutos:segundos.
     *
     * @param totalSeconds segundos totales
     * @return tiempo formateado
     */
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
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

        graphics.setFont(arcadeFont);
        graphics.setColor(Color.WHITE);
        graphics.drawString(playerName, 40, 560);
        graphics.drawString("Score: " + score, 750, 575);
        graphics.drawString("Time: " + formatTime(elapsedTime), 40, 580);
        graphics.drawString("Top 3", 740, 70);

        int y = 90;
        for (int i = 0; i < topScores.size(); i++) {
            ScoreManager.ScoreEntry entry = topScores.get(i);
            graphics.drawString((i + 1) + ". " + entry.getPlayerName() + " " + entry.getScore(), 740, y);
            y += 20;
        }
    }
}
