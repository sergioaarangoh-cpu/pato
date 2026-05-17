package model;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Represents a duck in the game.
 */
public class Duck implements Runnable {

    private int x;

    private int y;

    private int speedX;

    private int speedY;

    private int panelWidth;

    private int panelHeight;

    private Image sprite;

    /**
     * Creates a duck.
     */
    public Duck(
            int x,
            int y,
            int panelWidth,
            int panelHeight,
            String imagePath
    ) {

        this.x = x;

        this.y = y;

        this.panelWidth = panelWidth;

        this.panelHeight = panelHeight;

        speedX = 4;

        speedY = 2;

        loadSprite(imagePath);
    }

    /**
     * Loads duck sprite.
     */
    private void loadSprite(String imagePath) {

        URL url = getClass().getResource(imagePath);

        if (url == null) {

            System.out.println(
                    "No se encontro la imagen: " + imagePath
            );

            return;
        }

        sprite = new ImageIcon(url).getImage();
    }

    @Override
    public void run() {

        while (true) {

            move();

            try {

                Thread.sleep(16);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Handles duck movement.
     */
    private void move() {

        x += speedX;

        y += speedY;

        if (x <= 0 || x >= panelWidth - 120) {

            speedX *= -1;
        }

        if (y <= 0 || y >= panelHeight - 120) {

            speedY *= -1;
        }
    }

    /**
     * Draws the duck.
     */
    public void draw(Graphics graphics) {

        if (sprite != null) {

            graphics.drawImage(
                    sprite,
                    x,
                    y,
                    120,
                    120,
                    null
            );
        }
    }

    public void setPanelSize(
            int panelWidth,
            int panelHeight
    ) {

        this.panelWidth = panelWidth;

        this.panelHeight = panelHeight;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}