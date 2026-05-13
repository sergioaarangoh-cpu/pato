package main.java.controller;

/**
 * Controla el estado de toda la partida
 */
public class GameController implements Runnable {
    private volatile int remainingSeconds = 120;
    private boolean running = true;

    /**
     * Ejecuta la cuenta regresiva del juego mientras haya tiempo disponible.
     */
    @Override
    public void run() {
        while (running && remainingSeconds > 0) {

            try {

                Thread.sleep(1000);

                remainingSeconds--;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtiene los segundos restantes de la partida.
     *
     * @return cantidad de segundos disponibles antes de que termine el tiempo.
     */
    public int getRemainingSeconds() {
        return remainingSeconds;
    }
}
