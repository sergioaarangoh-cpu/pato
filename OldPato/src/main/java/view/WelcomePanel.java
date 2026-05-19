package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Panel de bienvenida que se muestra antes de iniciar la partida.
 * <p>
 * Dibuja la imagen de bienvenida a pantalla completa y notifica al frame
 * principal cuando el usuario presiona Enter o hace clic sobre el panel.
 */
public class WelcomePanel extends JPanel {

    private static final String WELCOME_IMAGE = "OldPato/src/main/resources/images/welcomeScreen.png";

    private final Image welcomeImage;
    private final Runnable onStart;
    private boolean started;

    /**
     * Crea el panel de bienvenida y registra la accion que inicia el juego.
     *
     * @param onStart accion ejecutada cuando el usuario decide continuar al juego
     */
    public WelcomePanel(Runnable onStart) {
        this.onStart = onStart;
        this.welcomeImage = loadWelcomeImage();
        setFocusable(true);
        configureStartActions();
    }

    /**
     * Configura las entradas que permiten pasar de la bienvenida al juego.
     */
    private void configureStartActions() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startGame();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "startGame");
        getActionMap().put("startGame", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                startGame();
            }
        });
    }

    /**
     * Ejecuta la accion de inicio una sola vez.
     */
    private void startGame() {
        if (started) {
            return;
        }
        started = true;
        onStart.run();
    }

    /**
     * Resetea el panel para permitir iniciar el juego de nuevo.
     */
    public void reset() {
        started = false;
    }
    /**
     * Carga la imagen de bienvenida desde archivo local o desde el classpath.
     *
     * @return imagen de bienvenida, o {@code null} si no se pudo encontrar
     */
    private Image loadWelcomeImage() {
        Path currentDirectory = Paths.get("").toAbsolutePath();
        Path[] candidates = {
                Paths.get(WELCOME_IMAGE),
                currentDirectory.resolve(WELCOME_IMAGE),
                currentDirectory.resolve("src/main/resources/images/welcomeScreen.png")
        };

        for (Path candidate : candidates) {
            Path normalized = candidate.normalize();
            if (Files.exists(normalized)) {
                return new ImageIcon(normalized.toString()).getImage();
            }
        }

        URL resource = getClass().getResource("/images/welcomeScreen.png");
        if (resource != null) {
            return new ImageIcon(resource).getImage();
        }

        return null;
    }

    /**
     * Dibuja la imagen de bienvenida ocupando todo el panel.
     *
     * @param graphics contexto grafico usado por Swing
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (welcomeImage != null) {
            graphics.drawImage(welcomeImage, 0, 0, getWidth(), getHeight(), this);
            return;
        }

        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, getWidth(), getHeight());
    }
}