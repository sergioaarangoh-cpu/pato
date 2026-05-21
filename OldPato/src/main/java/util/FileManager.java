package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Maneja operaciones de lectura y escritura de archivos.
 *
 * @author TuNombre
 */
public final class FileManager {

    private FileManager() {
    }

    /**
     * Lee todas las líneas de un archivo.
     *
     * @param filePath ruta del archivo
     * @return lista de líneas encontradas
     */
    public static List<String> readLines(String filePath) {

        List<String> lines = new ArrayList<>();

        try {
            File file = new File(filePath);

            if (!file.exists()) {
                return lines;
            }

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo: " + e.getMessage());
        }

        return lines;
    }

    /**
     * Agrega una línea al final del archivo.
     *
     * @param filePath ruta del archivo
     * @param line     línea a guardar
     */
    public static void appendLine(String filePath, String line) {

        try {
            File file = new File(filePath);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            FileWriter writer = new FileWriter(file, true);

            writer.write(line);
            writer.write(System.lineSeparator());

            writer.close();

        } catch (IOException e) {
            System.out.println("No se pudo escribir en el archivo: " + e.getMessage());
        }
    }
}
