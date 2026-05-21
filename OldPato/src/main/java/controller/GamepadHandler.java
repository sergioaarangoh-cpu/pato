package controller;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * Maneja el input de un mando compatible con JInput, como un control de Xbox.
 */
public class GamepadHandler {

    private static final String JINPUT_LIBRARY_PATH_PROPERTY = "net.java.games.input.librarypath";
    private static final String DEFAULT_NATIVES_DIR = "natives";

    private static final float DEAD_ZONE = 0.18f;
    private static final int AIM_SPEED = 12;

    private Controller controller;
    private Component xAxis;
    private Component yAxis;
    private Component shootTrigger;
    private Component shootTriggerButton;

    private int aimX;
    private int aimY;
    private boolean available;
    private boolean shootPressed;
    private boolean previousShootPressed;

    /**
     * Busca un mando disponible y deja preparado el estado inicial del manejador.
     */
    public GamepadHandler() {
        findController();
    }

    /**
     * Actualiza la posicion de la mira y el estado de disparo usando el mando.
     *
     * @param panelWidth ancho actual del panel de juego
     * @param panelHeight alto actual del panel de juego
     */
    public void update(int panelWidth, int panelHeight) {
        if (!available || controller == null) {
            return;
        }

        if (!controller.poll()) {
            available = false;
            System.out.println("Mando desconectado.");
            return;
        }

        float x = readComponent(xAxis);
        float y = readComponent(yAxis);
        if (Math.abs(x) > DEAD_ZONE) {
            aimX += Math.round(x * AIM_SPEED);
        }
        if (Math.abs(y) > DEAD_ZONE) {
            aimY += Math.round(y * AIM_SPEED);
        }

        aimX = clamp(aimX, 0, Math.max(0, panelWidth - 1));
        aimY = clamp(aimY, 0, Math.max(0, panelHeight - 1));

        previousShootPressed = shootPressed;
        shootPressed = readComponent(shootTrigger) > 0.55f || readComponent(shootTriggerButton) > 0.5f;
    }

    /**
     * Centra la mira dentro del panel de juego.
     *
     * @param panelWidth ancho actual del panel de juego
     * @param panelHeight alto actual del panel de juego
     */
    public void centerAim(int panelWidth, int panelHeight) {
        aimX = Math.max(0, panelWidth / 2);
        aimY = Math.max(0, panelHeight / 2);
    }

    /**
     * Indica si hay un mando compatible disponible.
     *
     * @return {@code true} si el mando esta disponible
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Obtiene la coordenada horizontal de la mira controlada por mando.
     *
     * @return coordenada x de la mira
     */
    public int getAimX() {
        return aimX;
    }

    /**
     * Obtiene la coordenada vertical de la mira controlada por mando.
     *
     * @return coordenada y de la mira
     */
    public int getAimY() {
        return aimY;
    }

    /**
     * Detecta si el disparo del mando acaba de ser presionado.
     *
     * @return {@code true} solo en el primer ciclo del disparo
     */
    public boolean wasShootPressed() {
        return shootPressed && !previousShootPressed;
    }

    /**
     * Libera las referencias al mando y marca el manejador como no disponible.
     */
    public void dispose() {
        controller = null;
        xAxis = null;
        yAxis = null;
        shootTrigger = null;
        shootTriggerButton = null;
        available = false;
    }

    private void findController() {
        configureNativeLibraryPath();
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for (Controller candidate : controllers) {
            if (isGamepad(candidate)) {
                configureController(candidate);
                if (xAxis != null && yAxis != null) {
                    controller = candidate;
                    available = true;
                    System.out.println("Mando detectado: " + controller.getName());
                    printControllerMapping(candidate);
                    return;
                }
            }
        }

        System.out.println("No se detecto un mando compatible.");
    }

    private void configureNativeLibraryPath() {
        if (System.getProperty(JINPUT_LIBRARY_PATH_PROPERTY) != null) {
            return;
        }

        java.io.File nativesDirectory = findNativesDirectory();
        if (nativesDirectory != null) {
            System.setProperty(JINPUT_LIBRARY_PATH_PROPERTY, nativesDirectory.getAbsolutePath());
            System.out.println("JInput natives: " + nativesDirectory.getAbsolutePath());
        }
    }

    private java.io.File findNativesDirectory() {
        java.io.File currentDirectoryNatives = new java.io.File(DEFAULT_NATIVES_DIR);
        if (currentDirectoryNatives.isDirectory()) {
            return currentDirectoryNatives;
        }

        try {
            java.io.File classesDirectory = new java.io.File(
                    GamepadHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            java.io.File targetDirectory = classesDirectory.getParentFile();
            java.io.File projectDirectory = targetDirectory != null ? targetDirectory.getParentFile() : null;
            java.io.File projectNatives = projectDirectory != null ? new java.io.File(projectDirectory, DEFAULT_NATIVES_DIR) : null;
            if (projectNatives != null && projectNatives.isDirectory()) {
                return projectNatives;
            }
        } catch (Exception e) {
            System.out.println("No se pudo ubicar la carpeta natives: " + e.getMessage());
        }

        java.io.File absoluteNatives = new java.io.File("C:\\Users\\JSEF1\\pato\\natives");
        if (absoluteNatives.isDirectory()) {
            return absoluteNatives;
        }

        return null;
    }

    private boolean isGamepad(Controller candidate) {
        String type = String.valueOf(candidate.getType()).toLowerCase();
        String name = candidate.getName().toLowerCase();
        return type.contains("gamepad") || type.contains("stick") || name.contains("xbox");
    }

    private void configureController(Controller selectedController) {
        xAxis = null;
        yAxis = null;
        shootTrigger = null;
        shootTriggerButton = null;

        for (Component component : selectedController.getComponents()) {
            String identifier = String.valueOf(component.getIdentifier()).toLowerCase();
            String name = component.getName().toLowerCase();

            if (xAxis == null && ("x".equals(identifier) || name.contains("x axis") || name.contains("x-axis"))) {
                xAxis = component;
            } else if (yAxis == null && ("y".equals(identifier) || name.contains("y axis") || name.contains("y-axis"))) {
                yAxis = component;
            } else if (shootTrigger == null && isRightTrigger(identifier, name)) {
                shootTrigger = component;
            } else if (shootTriggerButton == null && isRightTriggerButton(identifier, name)) {
                shootTriggerButton = component;
            }
        }
    }

    /**
     * Verifica si un componente corresponde al gatillo derecho R2.
     *
     * @param identifier identificador del componente
     * @param name       nombre del componente
     * @return {@code true} si el componente se puede usar como R2
     */
    private boolean isRightTrigger(String identifier, String name) {
        return "z".equals(identifier)
                || "rz".equals(identifier)
                || name.contains("right trigger")
                || name.contains("right z")
                || name.contains("trigger");
    }

    /**
     * Verifica si un componente corresponde al boton digital R2.
     *
     * @param identifier identificador del componente
     * @param name       nombre del componente
     * @return {@code true} si el componente se puede usar como boton R2
     */
    private boolean isRightTriggerButton(String identifier, String name) {
        return "7".equals(identifier)
                || name.contains("r2")
                || name.contains("button 7");
    }

    private void printControllerMapping(Controller selectedController) {
        System.out.println("Componentes del mando:");
        for (Component component : selectedController.getComponents()) {
            System.out.println("- " + component.getIdentifier() + " / " + component.getName());
        }
    }

    private float readComponent(Component component) {
        if (component == null) {
            return 0;
        }
        return component.getPollData();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
