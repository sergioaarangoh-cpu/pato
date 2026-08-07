package util;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Utilidad manual para probar la reproduccion de sonidos del proyecto.
 */
public class SoundTest {
    /**
     * Reproduce un sonido de bienvenida para verificar la configuracion de audio.
     *
     * @param args argumentos de linea de comandos no utilizados
     * @throws Exception si el archivo de audio no se puede cargar o reproducir
     */
    public static void main(String[] args) throws Exception {
        AudioInputStream audio = AudioSystem.getAudioInputStream(
                new File("pato/OldPato/src/main/resources/sounds/powerup.wav")
        );

        AudioInputStream audio2 = AudioSystem.getAudioInputStream(
                new File("pato/OldPato/src/main/resources/sounds/welcomeMusic.wav")
        );

        Clip clip = AudioSystem.getClip();
        Clip clip2 = AudioSystem.getClip();

        clip.open(audio);
        clip.start();
        // Espera a que termine de sonar
        Thread.sleep(clip.getMicrosecondLength() / 1000);
        clip.close();

        clip2.open(audio2);
        clip2.start();
        Thread.sleep(clip2.getMicrosecondLength() / 1000);
        clip.close();
    }
}
