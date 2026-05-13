package main.java.controller;

public class GameController implements Runnable {
    private volatile int remainingSeconds = 120;
    private boolean running = true;

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

    public int getRemainingSeconds() {
        return remainingSeconds;
    }
}
