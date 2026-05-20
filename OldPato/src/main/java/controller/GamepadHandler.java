package controller;

/**
 * Maneja el input de un mando compatible con JInput, como un control de Xbox.
 *
 * <p>La clase usa reflexion para que el proyecto pueda compilar aunque la
 * libreria JInput no este instalada. Cuando JInput esta disponible en el
 * classpath, busca un mando, lee los ejes analogicos y detecta el disparo.</p>
 *
 * @author sergioaarangoh-cpu
 * @version 1.1
 */
public class GamepadHandler {

    private static final float DEAD_ZONE = 0.18f;
    private static final int AIM_SPEED = 12;

    private Object controller;
    private Object xAxis;
    private Object yAxis;
    private Object shootButton;
    private Object shootTrigger;

    private int aimX;
    private int aimY;
    private boolean available;
    private boolean shootPressed;
    private boolean previousShootPressed;

    /**
     * Crea el manejador e intenta encontrar un mando conectado.
     */
    public GamepadHandler() {
        findController();
    }

    /**
     * Actualiza el estado del mando y mueve la mira dentro del panel.
     *
     * @param panelWidth ancho actual del panel de juego
     * @param panelHeight alto actual del panel de juego
     */
    public void update(int panelWidth, int panelHeight) {
        if (!available || controller == null) {
            return;
        }

        try {
            boolean polled = (boolean) controller.getClass().getMethod("poll").invoke(controller);
            if (!polled) {
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
            shootPressed = isButtonPressed(shootButton) || readComponent(shootTrigger) > 0.55f;
        } catch (Exception e) {
            available = false;
            System.out.println("No se pudo leer el mando: " + e.getMessage());
        }
    }

    /**
     * Centra la mira del mando cuando empieza la partida.
     *
     * @param panelWidth ancho actual del panel de juego
     * @param panelHeight alto actual del panel de juego
     */
    public void centerAim(int panelWidth, int panelHeight) {
        aimX = Math.max(0, panelWidth / 2);
        aimY = Math.max(0, panelHeight / 2);
    }

    /**
     * Indica si hay un mando disponible.
     *
     * @return {@code true} si se detecto un mando compatible
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Obtiene la posicion horizontal de la mira controlada por el mando.
     *
     * @return coordenada x de la mira
     */
    public int getAimX() {
        return aimX;
    }

    /**
     * Obtiene la posicion vertical de la mira controlada por el mando.
     *
     * @return coordenada y de la mira
     */
    public int getAimY() {
        return aimY;
    }

    /**
     * Detecta el momento exacto en que se presiona el boton de disparo.
     *
     * @return {@code true} solo en el frame donde empieza el disparo
     */
    public boolean wasShootPressed() {
        return shootPressed && !previousShootPressed;
    }

    /**
     * Libera referencias al mando.
     */
    public void dispose() {
        controller = null;
        xAxis = null;
        yAxis = null;
        shootButton = null;
        shootTrigger = null;
        available = false;
    }

    /**
     * Busca el primer mando compatible expuesto por JInput.
     */
    private void findController() {
        try {
            Class<?> environmentClass = Class.forName("net.java.games.input.ControllerEnvironment");
            Object environment = environmentClass.getMethod("getDefaultEnvironment").invoke(null);
            Object[] controllers = (Object[]) environment.getClass().getMethod("getControllers").invoke(environment);

            for (Object candidate : controllers) {
                if (isGamepad(candidate)) {
                    configureController(candidate);
                    if (xAxis != null && yAxis != null) {
                        controller = candidate;
                        available = true;
                        System.out.println("Mando detectado: " + controller.getClass().getMethod("getName").invoke(controller));
                        return;
                    }
                }
            }

            System.out.println("No se detecto un mando compatible.");
        } catch (ClassNotFoundException e) {
            System.out.println("JInput no esta instalado. Agrega la libreria para usar el control de Xbox.");
        } catch (Exception e) {
            System.out.println("No se pudo inicializar el mando: " + e.getMessage());
        }
    }

    /**
     * Verifica si un controlador de JInput parece ser un gamepad o joystick.
     *
     * @param candidate controlador detectado por JInput
     * @return {@code true} si el controlador puede usarse como mando
     * @throws Exception si falla la reflexion sobre JInput
     */
    private boolean isGamepad(Object candidate) throws Exception {
        String type = String.valueOf(candidate.getClass().getMethod("getType").invoke(candidate)).toLowerCase();
        String name = String.valueOf(candidate.getClass().getMethod("getName").invoke(candidate)).toLowerCase();
        return type.contains("gamepad") || type.contains("stick") || name.contains("xbox");
    }

    /**
     * Guarda las referencias a ejes y botones usados por el juego.
     *
     * @param selectedController mando elegido
     * @throws Exception si falla la lectura de componentes
     */
    private void configureController(Object selectedController) throws Exception {
        Object[] components = (Object[]) selectedController.getClass().getMethod("getComponents").invoke(selectedController);
        for (Object component : components) {
            String identifier = String.valueOf(component.getClass().getMethod("getIdentifier").invoke(component)).toLowerCase();
            String name = String.valueOf(component.getClass().getMethod("getName").invoke(component)).toLowerCase();

            if (xAxis == null && ("x".equals(identifier) || name.contains("x axis"))) {
                xAxis = component;
            } else if (yAxis == null && ("y".equals(identifier) || name.contains("y axis"))) {
                yAxis = component;
            } else if (shootTrigger == null && ("z".equals(identifier) || "rz".equals(identifier) || name.contains("trigger"))) {
                shootTrigger = component;
            } else if (shootButton == null && ("0".equals(identifier) || name.contains("button 0") || name.contains("a"))) {
                shootButton = component;
            }
        }
    }

    /**
     * Lee el valor analogico de un componente.
     *
     * @param component componente de JInput
     * @return valor normalizado del componente, o {@code 0} si no existe
     * @throws Exception si falla la lectura del componente
     */
    private float readComponent(Object component) throws Exception {
        if (component == null) {
            return 0;
        }
        return ((Number) component.getClass().getMethod("getPollData").invoke(component)).floatValue();
    }

    /**
     * Verifica si un boton esta presionado.
     *
     * @param component componente de boton
     * @return {@code true} si el boton esta presionado
     * @throws Exception si falla la lectura del componente
     */
    private boolean isButtonPressed(Object component) throws Exception {
        return readComponent(component) > 0.5f;
    }

    /**
     * Restringe un valor dentro de un rango.
     *
     * @param value valor a restringir
     * @param min valor minimo
     * @param max valor maximo
     * @return valor dentro del rango indicado
     */
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}

