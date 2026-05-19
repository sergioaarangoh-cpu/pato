package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * Panel de bienvenida que se muestra antes de iniciar la partida.
 * Dibuja la imagen de bienvenida a pantalla completa, pide el nombre del
 * jugador y notifica al frame principal cuando el usuario decide continuar.
 */
public class WelcomePanel extends JPanel {

    private static final String WELCOME_IMAGE = "OldPato/src/main/resources/images/welcomeScreen.png";

    private final Image welcomeImage;
    private final Consumer<String> onStart;
    private final JTextField nameField;
    private final JLabel errorLabel;
    private boolean started;

    /**
     * Crea el panel de bienvenida y registra la accion que inicia el juego.
     *
     * @param onStart accion ejecutada con el nombre del jugador al continuar
     */
    public WelcomePanel(Consumer<String> onStart) {
        this.onStart = onStart;
        this.welcomeImage = loadWelcomeImage();
        this.nameField = new JTextField(18);
        this.errorLabel = new JLabel(" ");
        setFocusable(true);
        setLayout(new GridBagLayout());
        buildNameForm();
        configureStartActions();
    }

    /**
     * Construye el formulario que solicita el nombre del jugador.
     */
    private void buildNameForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(true);
        formPanel.setBackground(new Color(0, 0, 0, 170));
        formPanel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel nameLabel = new JLabel("Nombre del jugador");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setHorizontalAlignment(JTextField.CENTER);

        JButton startButton = new JButton("Iniciar");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.addActionListener(e -> startGame());

        errorLabel.setForeground(new Color(255, 120, 120));
        errorLabel.setFont(new Font("Arial", Font.BOLD, 13));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 8, 0);
        formPanel.add(nameLabel, constraints);

        constraints.gridy = 1;
        formPanel.add(nameField, constraints);

        constraints.gridy = 2;
        constraints.insets = new Insets(10, 0, 4, 0);
        formPanel.add(startButton, constraints);

        constraints.gridy = 3;
        constraints.insets = new Insets(2, 0, 0, 0);
        formPanel.add(errorLabel, constraints);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 0;
        panelConstraints.gridy = 0;
        panelConstraints.anchor = GridBagConstraints.SOUTH;
        panelConstraints.insets = new Insets(0, 0, 70, 0);
        add(formPanel, panelConstraints);
    }

    /**
     * Configura las entradas que permiten pasar de la bienvenida al juego.
     */
    private void configureStartActions() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        nameField.addActionListener(e -> startGame());
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "startGame");
        getActionMap().put("startGame", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                startGame();
            }
        });
    }

    /**
     * Valida el nombre y ejecuta la accion de inicio una sola vez.
     */
    private void startGame() {
        String playerName = nameField.getText().trim();
        if (playerName.isEmpty()) {
            errorLabel.setText("Ingresa tu nombre para continuar");
            nameField.requestFocusInWindow();
            return;
        }

        if (started) {
            return;
        }
        started = true;
        errorLabel.setText(" ");
        onStart.accept(playerName);
    }

    /**
     * Envia el foco al campo de nombre para que el usuario pueda escribir de inmediato.
     */
    public void requestNameFocus() {
        nameField.requestFocusInWindow();
    }

    /**
     * Resetea el panel para permitir iniciar el juego de nuevo.
     */
    public void reset() {
        started = false;
        nameField.setText("");
        errorLabel.setText(" ");
        SwingUtilities.invokeLater(this::requestNameFocus);
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