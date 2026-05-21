package model;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

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
        this.sprite = loadSprite(imagePath);
    }

    /**
     * Carga el sprite desde el classpath o desde una ruta de archivo local.
     *
     * @param imagePath ruta del sprite a cargar
     * @return imagen cargada, o {@code null} si no se encuentra
     */
    private Image loadSprite(String imagePath) {
        URL resource = getClass().getResource(imagePath);
        if (resource != null) {
            return new ImageIcon(resource).getImage();
        }

        File file = new File(imagePath);
        if (file.exists()) {
            return new ImageIcon(file.getPath()).getImage();
        }

        System.out.println("No se encontro la imagen: " + imagePath);
        return null;
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
