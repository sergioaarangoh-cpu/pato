package model;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Musica {

    private Clip clip;

    public void reproducir(String ruta) {
        detener();

        try (AudioInputStream audio = abrirAudio(ruta)) {
            clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            System.err.println("No se pudo reproducir el audio: " + ruta);
            e.printStackTrace();
        }
    }

    private AudioInputStream abrirAudio(String ruta) throws Exception {
        URL recurso = buscarRecurso(ruta);
        if (recurso != null) {
            return AudioSystem.getAudioInputStream(recurso);
        }

        Path archivo = buscarArchivo(ruta);
        if (archivo != null) {
            return AudioSystem.getAudioInputStream(archivo.toFile());
        }

        throw new IOException("No se encontro el archivo de audio desde: "
                + Paths.get("").toAbsolutePath());
    }

    private URL buscarRecurso(String ruta) {
        String normalizada = ruta.replace('\\', '/');
        String[] candidatos = {
                normalizada,
                "/" + normalizada,
                quitarPrefijoRecursos(normalizada),
                "/" + quitarPrefijoRecursos(normalizada)
        };

        for (String candidato : candidatos) {
            URL recurso = getClass().getResource(candidato);
            if (recurso != null) {
                return recurso;
            }
        }
        return null;
    }

    private String quitarPrefijoRecursos(String ruta) {
        String prefijo = "src/main/resources/";
        int indice = ruta.indexOf(prefijo);
        if (indice >= 0) {
            return ruta.substring(indice + prefijo.length());
        }
        return ruta;
    }

    private Path buscarArchivo(String ruta) {
        Path directorioActual = Paths.get("").toAbsolutePath();
        Path[] candidatos = {
                Paths.get(ruta),
                directorioActual.resolve(ruta),
                directorioActual.resolve("OldPato").resolve(ruta),
                directorioActual.resolve(quitarPrefijoProyecto(ruta))
        };

        for (Path candidato : candidatos) {
            Path normalizado = candidato.normalize();
            if (Files.exists(normalizado)) {
                return normalizado;
            }
        }
        return null;
    }

    private String quitarPrefijoProyecto(String ruta) {
        String normalizada = ruta.replace('\\', '/');
        String prefijo = "OldPato/";
        if (normalizada.startsWith(prefijo)) {
            return normalizada.substring(prefijo.length());
        }
        return ruta;
    }

    public void detener() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }
}
