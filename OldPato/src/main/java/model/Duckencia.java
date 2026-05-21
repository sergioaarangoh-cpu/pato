package model;

/**
 * pato bueno en el juego.
 * Si el jugador le dispara, pierde una vida.
 *
 * @author sergioaarangoh-cpu
 * @version 1.0
 */
public class Duckencia extends Duck {

    /**
     * Crea un Duckencia en una posición inicial.
     *
     * @param x           coordenada horizontal inicial
     * @param y           coordenada vertical inicial
     * @param panelWidth  ancho del panel
     * @param panelHeight alto del panel
     * @param leftImage   ruta del sprite mirando a la izquierda
     * @param rightImage  ruta del sprite mirando a la derecha
     */
    public Duckencia(int x, int y, int panelWidth, int panelHeight, String leftImage, String rightImage) {
        super(x, y, panelWidth, panelHeight, leftImage, rightImage);
        this.width = 120;
        this.height = 120;
    }
}
