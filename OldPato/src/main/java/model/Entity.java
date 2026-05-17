package model;

import javax.swing.*;
import java.awt.*;

/**
 * Base comun para los elementos del juego que tienen posicion, tamano y sprite.
 */
public abstract class Entity {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image sprite;

    protected Entity(int x, int y, int width, int height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = new ImageIcon(imagePath).getImage();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics graphics) {
        if (sprite != null) {
            graphics.drawImage(sprite, x, y, width, height, null);
        }
    }
}
