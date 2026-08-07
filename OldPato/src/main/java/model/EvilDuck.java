package model;

/**
 * Representa un pato malvado en el juego.
 * Si el jugador le dispara, gana 20 segundos extra.
 *
 * @author sergioaarangoh-cpu
 * @version 1.0
 */
public class EvilDuck extends Duck {

    /**
     * Crea un EvilDuck en una posición inicial.
     *
     * @param x           coordenada horizontal inicial
     * @param y           coordenada vertical inicial
     * @param panelWidth  ancho del panel
     * @param panelHeight alto del panel
     * @param leftImage   ruta del sprite mirando a la izquierda
     * @param rightImage  ruta del sprite mirando a la derecha
     */
    public EvilDuck(int x, int y, int panelWidth, int panelHeight, String leftImage, String rightImage) {
        super(x, y, panelWidth, panelHeight, leftImage, rightImage);
        this.width = 70;
        this.height = 70;
        this.blinkEnabled = false;
    }
}