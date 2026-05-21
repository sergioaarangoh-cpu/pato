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
    private static final String POPUP_IMAGE = "OldPato/src/main/resources/images/instructions.png";
    private static final String LOGO_IMAGE = "OldPato/src/main/resources/images/logo_uam.png";
    private static final String JUANCHO_IMAGE = "OldPato/src/main/resources/images/juancho.png";
    private static final String CHECHO_IMAGE = "OldPato/src/main/resources/images/checho.png";
    private static final String VICTOR_IMAGE = "OldPato/src/main/resources/images/victor.png";
    private static final String FOOTER_TEXT = "OldPato - por Sergio, Juan Sebastian y Victor - POO";
    private static final int LOGO_SIZE = 50;
    private static final int AUTHOR_IMAGE_WIDTH = 63;
    private static final int AUTHOR_IMAGE_HEIGHT = 50;
    private static final int AUTHOR_IMAGE_GAP = 12;
    private static final int AUTHOR_IMAGES_ABOVE_FOOTER = 20;
    private static final int FOOTER_BOTTOM_MARGIN = 12;

    private final Image welcomeImage;
    private final Image popupImage;
    private final Image logoImage;
    private final Image juanchoImage;
    private final Image chechoImage;
    private final Image victorImage;
    private final Consumer<String> onStart;
    private final JTextField nameField;
    private final JLabel errorLabel;
    private final Font arcadeFont;
    private boolean started;

    /**
     * Crea el panel de bienvenida y registra la accion que inicia el juego.
     *
     * @param onStart accion ejecutada con el nombre del jugador al continuar
     */
    public WelcomePanel(Consumer<String> onStart) {
        this.onStart = onStart;
        this.welcomeImage = loadWelcomeImage();
        this.popupImage = loadImage(POPUP_IMAGE, "/images/instructions.png");
        this.logoImage = loadImage(LOGO_IMAGE, "/images/logo_uam.png");
        this.juanchoImage = loadImage(JUANCHO_IMAGE, "/images/juancho.png");
        this.chechoImage = loadImage(CHECHO_IMAGE, "/images/checho.png");
        this.victorImage = loadImage(VICTOR_IMAGE, "/images/victor.png");
        this.arcadeFont = loadArcadeFont(10f);
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
        nameLabel.setFont(arcadeFont);

        nameField.setFont(arcadeFont);
        nameField.setHorizontalAlignment(JTextField.CENTER);

        JButton startButton = new JButton("Iniciar");
        startButton.setFont(arcadeFont);
        startButton.addActionListener(e -> startGame());

        JButton imageButton = new JButton("Ver Instrucciones");
        imageButton.setFont(arcadeFont);
        imageButton.addActionListener(e -> showImagePopup());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        buttonPanel.add(imageButton);

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
        formPanel.add(buttonPanel, constraints);

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
     * Muestra una imagen del juego dentro de un popup.
     */
    private void showImagePopup() {
        Image image = popupImage != null ? popupImage : welcomeImage;
        if (image == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la imagen.");
            return;
        }

        Image scaledImage = image.getScaledInstance(520, 360, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        JOptionPane.showMessageDialog(this, imageLabel, "Instrucciones", JOptionPane.PLAIN_MESSAGE);
        requestNameFocus();
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
        return loadImage(WELCOME_IMAGE, "/images/welcomeScreen.png");
    }

    /**
     * Carga la fuente arcade usada en la pantalla de bienvenida.
     *
     * @param size tamano de la fuente
     * @return fuente arcade, o una fuente de respaldo si no se puede cargar
     */
    private Font loadArcadeFont(float size) {
        try {
            return Font.createFont(
                    Font.TRUETYPE_FONT,
                    new java.io.File("OldPato\\src\\main\\resources\\fonts\\ARCADE_N.TTF")
            ).deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            System.out.println("Error cargando fuente: " + e.getMessage());
            return new Font("Arial", Font.BOLD, Math.round(size));
        }
    }

    /**
     * Carga una imagen desde archivo local o desde el classpath.
     *
     * @param filePath     ruta del archivo dentro del proyecto
     * @param resourcePath ruta de respaldo dentro del classpath
     * @return imagen cargada, o {@code null} si no se pudo encontrar
     */
    private Image loadImage(String filePath, String resourcePath) {
        Path currentDirectory = Paths.get("").toAbsolutePath();
        Path[] candidates = {
                Paths.get(filePath),
                currentDirectory.resolve(filePath),
                currentDirectory.resolve(filePath.replace("OldPato/src/main/resources/", "src/main/resources/"))
        };

        for (Path candidate : candidates) {
            Path normalized = candidate.normalize();
            if (Files.exists(normalized)) {
                return new ImageIcon(normalized.toString()).getImage();
            }
        }

        URL resource = getClass().getResource(resourcePath);
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
        } else {
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }

        if (logoImage != null) {
            graphics.drawImage(logoImage, 0, 0, LOGO_SIZE, LOGO_SIZE, this);
        }

        graphics.setFont(arcadeFont);
        graphics.setColor(Color.WHITE);
        FontMetrics metrics = graphics.getFontMetrics();
        int footerX = (getWidth() - metrics.stringWidth(FOOTER_TEXT)) / 2;
        int footerY = getHeight() - FOOTER_BOTTOM_MARGIN;
        drawAuthorImages(graphics, footerY);
        graphics.drawString(FOOTER_TEXT, Math.max(0, footerX), footerY);
    }

    /**
     * Dibuja las imagenes de los autores centradas encima del texto inferior.
     *
     * @param graphics contexto grafico usado por Swing
     * @param footerY  coordenada vertical del texto inferior
     */
    private void drawAuthorImages(Graphics graphics, int footerY) {
        int totalWidth = AUTHOR_IMAGE_WIDTH * 3 + AUTHOR_IMAGE_GAP * 2;
        int startX = (getWidth() - totalWidth) / 2;
        int imageY = footerY - AUTHOR_IMAGES_ABOVE_FOOTER - AUTHOR_IMAGE_HEIGHT;

        drawAuthorImage(graphics, chechoImage, startX, imageY);
        drawAuthorImage(graphics, juanchoImage, startX + AUTHOR_IMAGE_WIDTH + AUTHOR_IMAGE_GAP, imageY);
        drawAuthorImage(graphics, victorImage, startX + (AUTHOR_IMAGE_WIDTH + AUTHOR_IMAGE_GAP) * 2, imageY);
    }

    /**
     * Dibuja una imagen de autor con el tamano definido para la bienvenida.
     *
     * @param graphics contexto grafico usado por Swing
     * @param image    imagen del autor
     * @param x        coordenada horizontal
     * @param y        coordenada vertical
     */
    private void drawAuthorImage(Graphics graphics, Image image, int x, int y) {
        if (image != null) {
            graphics.drawImage(image, x, y, AUTHOR_IMAGE_WIDTH, AUTHOR_IMAGE_HEIGHT, this);
        }
    }
}
