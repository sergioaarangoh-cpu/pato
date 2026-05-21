package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Guarda y consulta los puntajes historicos de OldPato.
 */
public final class ScoreManager {

    private static final String SCORE_FILE_NAME = "scores.txt";
    private static final String FIELD_SEPARATOR = ";";

    private ScoreManager() {
    }

    /**
     * Guarda un puntaje sin borrar los registros anteriores.
     *
     * @param playerName nombre del jugador
     * @param score      puntaje conseguido
     */
    public static void saveScore(String playerName, int score) {
        try {
            Path scoreFile = getScoreFile();
            Path parent = scoreFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            String line = sanitizePlayerName(playerName) + FIELD_SEPARATOR + score + System.lineSeparator();
            Files.write(
                    scoreFile,
                    line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.out.println("No se pudo guardar el puntaje: " + e.getMessage());
        }
    }

    /**
     * Obtiene los mejores puntajes guardados.
     *
     * @param limit cantidad maxima de puntajes a devolver
     * @return lista ordenada de mayor a menor puntaje
     */
    public static List<ScoreEntry> getTopScores(int limit) {
        List<ScoreEntry> scores = readScores();
        scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
        return new ArrayList<>(scores.subList(0, Math.min(limit, scores.size())));
    }

    /**
     * Lee todos los puntajes almacenados en el archivo.
     *
     * @return registros validos encontrados
     */
    private static List<ScoreEntry> readScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        Path scoreFile = getScoreFile();
        if (!Files.exists(scoreFile)) {
            return scores;
        }

        try {
            for (String line : Files.readAllLines(scoreFile, StandardCharsets.UTF_8)) {
                String[] parts = line.split(FIELD_SEPARATOR, 2);
                if (parts.length != 2) {
                    continue;
                }
                scores.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1].trim())));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("No se pudieron leer los puntajes: " + e.getMessage());
        }
        return scores;
    }

    /**
     * Obtiene la ruta donde se guardan los puntajes.
     *
     * @return ruta del archivo persistente de puntajes
     */
    private static Path getScoreFile() {
        Path projectFolder = Paths.get("OldPato");
        if (Files.exists(projectFolder)) {
            return projectFolder.resolve(SCORE_FILE_NAME);
        }
        return Paths.get(SCORE_FILE_NAME);
    }

    /**
     * Limpia el nombre para mantener el archivo de puntajes parseable.
     *
     * @param playerName nombre ingresado
     * @return nombre seguro para guardar
     */
    private static String sanitizePlayerName(String playerName) {
        String name = playerName == null ? "" : playerName.trim();
        if (name.isEmpty()) {
            name = "Jugador";
        }
        return name.replace(FIELD_SEPARATOR, ",");
    }

    /**
     * Registro de puntaje de una partida.
     */
    public static class ScoreEntry {
        private final String playerName;
        private final int score;

        /**
         * Crea un registro de puntaje.
         *
         * @param playerName nombre del jugador
         * @param score      puntaje conseguido
         */
        public ScoreEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        /**
         * Obtiene el nombre del jugador.
         *
         * @return nombre del jugador
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * Obtiene el puntaje conseguido.
         *
         * @return puntaje
         */
        public int getScore() {
            return score;
        }
    }
}
