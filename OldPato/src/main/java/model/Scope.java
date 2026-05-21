package model;

/**
 * Mira que representa el punto de apuntado del jugador.
 */
public class Scope extends Entity {
    private static final String SCOPE_IMAGE = "OldPato\\src\\main\\resources\\images\\scope.png";
    private static final int SCOPE_SIZE = 50;

    /**
     * Crea una mira en la esquina superior izquierda.
     */
    public Scope() {
        this(0, 0);
    }

    /**
     * Crea una mira en la posicion indicada.
     *
     * @param x coordenada horizontal inicial
     * @param y coordenada vertical inicial
     */
    public Scope(int x, int y) {
        this(x, y, SCOPE_IMAGE);
    }

    /**
     * Crea una mira con una imagen personalizada.
     *
     * @param x coordenada horizontal inicial
     * @param y coordenada vertical inicial
     * @param imagePath ruta de la imagen de la mira
     */
    public Scope(int x, int y, String imagePath) {
        super(x, y, SCOPE_SIZE, SCOPE_SIZE, imagePath);
    }
}
