package util;

import javax.sound.sampled.*;
import java.io.File;

public class SoundTest {
    public static void main(String[] args) throws Exception {
        AudioInputStream audio = AudioSystem.getAudioInputStream(
                new File("pato/OldPato/src/main/resources/sounds/welcomeMusic.wav")
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
