package model;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/**
 * Handles background music playback.
 */
public class Musica {

    private Clip clip;

    /**
     * Plays audio in loop mode.
     *
     * @param ruta audio path inside resources
     */
    public void play(String ruta) {

        stop();

        try {

            URL url = getClass().getResource(ruta);

            System.out.println(url);

            if (url == null) {

                System.out.println(
                        "No se encontro el audio: " + ruta
                );

                return;
            }

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(url);

            clip = AudioSystem.getClip();

            clip.open(audio);

            clip.loop(Clip.LOOP_CONTINUOUSLY);

            clip.start();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * Stops current audio.
     */
    public void stop() {

        if (clip != null) {

            clip.stop();

            clip.close();

            clip = null;
        }
    }
}