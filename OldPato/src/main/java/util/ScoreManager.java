package util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Gestiona el almacenamiento y consulta de puntajes.
 *
 * @author sergioaarangoh-cpu, juancho-2006, victor48994850938630968350698509680498
 */
public final class ScoreManager {

    private static final String SCORE_FILE_PATH =
            "OldPato/src/main/resources/data/scores.txt";

    private static final String FIELD_SEPARATOR = ";";

    private ScoreManager() {
    }

    /**
     * Guarda un puntaje.
     *
     * @param playerName nombre del jugador
     * @param score      puntaje obtenido
     */
    public static void saveScore(String playerName, int score) {

        String record =
                sanitizePlayerName(playerName)
                        + FIELD_SEPARATOR
                        + score;

        FileManager.appendLine(SCORE_FILE_PATH, record);
    }

    /**
     * Obtiene los mejores puntajes.
     *
     * @param limit cantidad máxima de resultados
     * @return lista ordenada de mayor a menor
     */
    public static List<ScoreEntry> getTopScores(int limit) {

        List<ScoreEntry> scores = readScores();

        scores.sort(
                Comparator.comparingInt(
                        ScoreEntry::getScore
                ).reversed()
        );

        return new ArrayList<>(
                scores.subList(
                        0,
                        Math.min(limit, scores.size())
                )
        );
    }

    /**
     * Lee todos los puntajes almacenados.
     *
     * @return lista de puntajes
     */
    private static List<ScoreEntry> readScores() {

        List<ScoreEntry> scores = new ArrayList<>();

        List<String> lines =
                FileManager.readLines(SCORE_FILE_PATH);

        for (String line : lines) {

            String[] parts =
                    line.split(FIELD_SEPARATOR, 2);

            if (parts.length != 2) {
                continue;
            }

            try {

                scores.add(
                        new ScoreEntry(
                                parts[0],
                                Integer.parseInt(parts[1].trim())
                        )
                );

            } catch (NumberFormatException e) {
                System.out.println(
                        "Registro inválido encontrado: "
                                + line
                );
            }
        }

        return scores;
    }

    /**
     * Limpia el nombre para evitar errores al guardar.
     *
     * @param playerName nombre ingresado
     * @return nombre válido
     */
    private static String sanitizePlayerName(String playerName) {

        String name =
                playerName == null
                        ? ""
                        : playerName.trim();

        if (name.isEmpty()) {
            return "Jugador";
        }

        return name.replace(FIELD_SEPARATOR, ",");
    }

    /**
     * Representa un registro de puntaje.
     */
    public static class ScoreEntry {

        private final String playerName;
        private final int score;

        /**
         * Constructor.
         *
         * @param playerName nombre del jugador
         * @param score      puntaje obtenido
         */
        public ScoreEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        /**
         * Obtiene el nombre del jugador.
         *
         * @return nombre
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * Obtiene el puntaje.
         *
         * @return puntaje
         */
        public int getScore() {
            return score;
        }
    }
}
