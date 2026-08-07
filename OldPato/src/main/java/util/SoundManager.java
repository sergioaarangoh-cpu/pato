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
    private volatile String currentMusicPath;
    private volatile long musicRequestId;

    /**
     * Reproduce una música en loop de forma continua.
     * Detiene cualquier música anterior antes de iniciar la nueva.
     *
     * @param path ruta del archivo de audio en resources
     */
    public void playMusic(String path) {
        currentMusicPath = path;
        playMusicAtSpeed(path, 1.0f);
    }

    /**
     * Cambia la velocidad de reproduccion de la musica actual, reinterpretando
     * el sample rate del audio (sube tempo y tono a la vez, sin necesitar otro clip).
     *
     * @param speedFactor factor de velocidad, 1.0 es la velocidad normal
     */
    public void setMusicSpeed(float speedFactor) {
        if (currentMusicPath != null) {
            playMusicAtSpeed(currentMusicPath, speedFactor);
        }
    }

    /**
     * Reproduce en loop el audio indicado a la velocidad dada. La carga del
     * archivo (potencialmente pesada) se hace en un hilo aparte para no
     * congelar la interfaz, y descarta el resultado si mientras tanto se pidió
     * reproducir otra pista distinta.
     *
     * @param path        ruta del archivo de audio en resources
     * @param speedFactor factor de velocidad, 1.0 es la velocidad normal
     */
    private void playMusicAtSpeed(String path, float speedFactor) {
        long requestId = ++musicRequestId;
        stopClip();
        new Thread(() -> loadAndPlayMusic(path, speedFactor, requestId), "music-loader").start();
    }

    private synchronized void loadAndPlayMusic(String path, float speedFactor, long requestId) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.out.println("No se encontró el audio: " + path);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            AudioFormat baseFormat = audioIn.getFormat();
            byte[] audioBytes = audioIn.readAllBytes();

            AudioFormat playbackFormat = new AudioFormat(
                    baseFormat.getSampleRate() * speedFactor,
                    baseFormat.getSampleSizeInBits(),
                    baseFormat.getChannels(),
                    baseFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.isBigEndian()
            );

            // si mientras se cargaba se pidió otra música (o se detuvo), se descarta esta
            if (requestId != musicRequestId) {
                return;
            }

            Clip clip = AudioSystem.getClip();
            clip.open(playbackFormat, audioBytes, 0, audioBytes.length);

            if (requestId != musicRequestId) {
                clip.close();
                return;
            }

            musicClip = clip;
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Detiene la música actual e invalida cualquier carga pendiente.
     */
    public synchronized void stopMusic() {
        musicRequestId++;
        stopClip();
    }

    /**
     * Detiene y libera el clip en reproducción, sin invalidar solicitudes pendientes.
     */
    private synchronized void stopClip() {
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
            URL url = getClass().getResource(path);
            if (url == null) {
                System.out.println("No se encontró el audio: " + path);
                return;
            }
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
