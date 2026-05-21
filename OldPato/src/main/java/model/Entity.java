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

    /**
     * Crea una entidad con posicion, tamano y sprite inicial.
     *
     * @param x coordenada horizontal inicial
     * @param y coordenada vertical inicial
     * @param width ancho de la entidad
     * @param height alto de la entidad
     * @param imagePath ruta del sprite asociado
     */
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

    /**
     * Obtiene la coordenada horizontal de la entidad.
     *
     * @return coordenada x actual
     */
    public int getX() {
        return x;
    }

    /**
     * Obtiene la coordenada vertical de la entidad.
     *
     * @return coordenada y actual
     */
    public int getY() {
        return y;
    }

    /**
     * Actualiza la coordenada horizontal de la entidad.
     *
     * @param x nueva coordenada horizontal
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Actualiza la coordenada vertical de la entidad.
     *
     * @param y nueva coordenada vertical
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Obtiene el ancho usado para dibujar la entidad.
     *
     * @return ancho de la entidad
     */
    public int getWidth() {
        return width;
    }

    /**
     * Obtiene el alto usado para dibujar la entidad.
     *
     * @return alto de la entidad
     */
    public int getHeight() {
        return height;
    }

    /**
     * Actualiza la posicion completa de la entidad.
     *
     * @param x nueva coordenada horizontal
     * @param y nueva coordenada vertical
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Dibuja el sprite de la entidad si esta disponible.
     *
     * @param graphics contexto grafico usado por Swing
     */
    public void draw(Graphics graphics) {
        if (sprite != null) {
            graphics.drawImage(sprite, x, y, width, height, null);
        }
    }
}
