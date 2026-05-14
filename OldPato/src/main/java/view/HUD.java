package view;

import javax.swing.*;
import model.GameState;

import java.awt.*;

/**
 * @author sergioaarangoh-cpu
 * @version 1.0
 *
 * Clase que permite al jugador ver la información de su partida, como vidas, timer y puntaje
 */
public class HUD extends JPanel {
    private GameState gameState;
    private Font arcadeFont;

    /**
     * Crea el HUD con el Game state (estado actual del juego)
     * @param gameState
     */
    public HUD(GameState gameState) {
        this.gameState = gameState;
        setOpaque(false);

        try {
            arcadeFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new java.io.File("src\\main\\resources\\fonts\\ARCADE_N.TTF")
            ).deriveFont(Font.PLAIN, 20f);
        } catch (Exception e) {
            System.out.println("Error cargando fuente: " + e.getMessage());
            arcadeFont = new Font("Arial", Font.BOLD, 20); // fuente de respaldo
        }
    }


    /**
     * Dibuja el HUD encima del juego con graphics
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(arcadeFont);
        g.setColor(Color.WHITE);


        //graphics.getFontMetrics() obtiene las medidas de la fuente actual
        //.stringWidth(score) devuelve el ancho en píxeles que ocuparía el texto
        //(getWidth() - anchoTexto) / 2 → centra el texto restando su ancho al ancho total del panel y dividiendo entre 2

        // vidas arriba a la derecha
        String vidas = "Lives: " + gameState.getLives();
        int vidasX = getWidth() - g.getFontMetrics().stringWidth(vidas) - 20;
        g.drawString(vidas, vidasX, 30);

        // timer arriba a la derecha debajo de las vidas
        String tiempo = "Time:" + gameState.getRemainingTime() + " s";
        int tiempoX = getWidth() - g.getFontMetrics().stringWidth(tiempo) - 20;
        g.drawString(tiempo, tiempoX, 60);

        // puntuación abajo en el medio
        String score = "Score: " + gameState.getScore();
        int scoreX = (getWidth() - g.getFontMetrics().stringWidth(score)) / 2;
        g.drawString(score, scoreX, getHeight() - 50);
    }

}
