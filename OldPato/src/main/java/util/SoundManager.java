package util;

import javax.sound.sampled.*;
import javax.swing.*;
import java.net.URL;

/**
 * Maneja todo el audio del juego: música en loop y efectos de sonido.
 *
 * @author sergioaarangoh-cpu
 * @version 1.0
 */
public class SoundManager {

    // clips de música en loop
    private Clip musicClip;

    /**
     * Reproduce una música en loop de forma continua.
     * Detiene cualquier música anterior antes de iniciar la nueva.
     *
     * @param path ruta del archivo de audio en resources
     */
    public void playMusic(String path) {
        stopMusic();
        try {
            URL url = new java.io.File(path).toURI().toURL();
            if (url == null) {
                System.out.println("No se encontró el audio: " + path);
                return;
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            musicClip = AudioSystem.getClip();
            musicClip.open(audio);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Detiene la música actual.
     */
    public void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }

    /**
     * Reproduce un efecto de sonido corto sin interrumpir la música.
     *
     * @param path ruta del archivo de audio en resources
     */
    /**
     * Reproduce un efecto de sonido corto y ejecuta una acción al terminar.
     *
     * @param path     ruta del archivo de audio
     * @param onFinish acción a ejecutar cuando el sonido termine, puede ser null
     */
    public void playSound(String path, Runnable onFinish) {
        try {
            URL url = new java.io.File(path).toURI().toURL();
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    // ejecuta la acción al terminar
                    if (onFinish != null) {
                        SwingUtilities.invokeLater(onFinish);
                    }
                }
            });
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reproduce un efecto de sonido corto sin acción al terminar.
     *
     * @param path ruta del archivo de audio
     */
    public void playSound(String path) {
        playSound(path, null);
    }
}