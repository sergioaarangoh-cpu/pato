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
            findController();
            if (!available || controller == null) {
                return;
            }
        }

        try {
            boolean polled = (boolean) invokeNoArgs(controller.getClass(), controller, "poll");
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
            Object environment = invokeNoArgs(environmentClass, null, "getDefaultEnvironment");
            Object[] controllers = (Object[]) invokeNoArgs(environment.getClass(), environment, "getControllers");
            if (controllers == null || controllers.length == 0) {
                System.out.println("JInput no reportó controladores.");
                available = false;
                return;
            }

            for (Object candidate : controllers) {
                String candidateName = String.valueOf(invokeNoArgs(candidate.getClass(), candidate, "getName"));
                String candidateType = String.valueOf(invokeNoArgs(candidate.getClass(), candidate, "getType"));
                System.out.println("Controlador detectado por JInput: " + candidateName + " [type=" + candidateType + "]");
                if (isGamepad(candidate)) {
                    configureController(candidate);
                    if (xAxis != null && yAxis != null) {
                        controller = candidate;
                        available = true;
                        System.out.println("Mando detectado: " + invokeNoArgs(controller.getClass(), controller, "getName"));
                        return;
                    }
                }
            }

            System.out.println("No se detecto un mando compatible.");
            available = false;
        } catch (ClassNotFoundException e) {
            System.out.println("JInput no esta instalado. Agrega la libreria para usar el control de Xbox.");
            available = false;
        } catch (Exception e) {
            System.out.println("No se pudo inicializar el mando: " + e.getMessage());
            available = false;
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
        String type = String.valueOf(invokeNoArgs(candidate.getClass(), candidate, "getType")).toLowerCase();
        String name = String.valueOf(invokeNoArgs(candidate.getClass(), candidate, "getName")).toLowerCase();
        return type.contains("gamepad")
                || type.contains("stick")
                || type.contains("unknown")
                || name.contains("xbox")
                || name.contains("x-input")
                || name.contains("xinput")
                || name.contains("wireless controller")
                || name.contains("controller");
    }

    /**
     * Guarda las referencias a ejes y botones usados por el juego.
     *
     * @param selectedController mando elegido
     * @throws Exception si falla la lectura de componentes
     */
    private void configureController(Object selectedController) throws Exception {
        xAxis = null;
        yAxis = null;
        shootButton = null;
        shootTrigger = null;

        Object[] components = (Object[]) invokeNoArgs(selectedController.getClass(), selectedController, "getComponents");
        for (Object component : components) {
            String identifier = String.valueOf(invokeNoArgs(component.getClass(), component, "getIdentifier")).toLowerCase();
            String name = String.valueOf(invokeNoArgs(component.getClass(), component, "getName")).toLowerCase();

            if (xAxis == null && ("x".equals(identifier) || identifier.endsWith(".x") || name.contains("x axis"))) {
                xAxis = component;
            } else if (yAxis == null && ("y".equals(identifier) || identifier.endsWith(".y") || name.contains("y axis"))) {
                yAxis = component;
            } else if (shootTrigger == null && ("z".equals(identifier) || "rz".equals(identifier)
                    || identifier.endsWith(".z") || identifier.endsWith(".rz") || name.contains("trigger"))) {
                shootTrigger = component;
            } else if (shootButton == null && ("0".equals(identifier) || identifier.contains("button 0")
                    || name.contains("button 0") || name.equals("a") || name.contains("south"))) {
                shootButton = component;
            }
        }

        assignFallbackAxes(components);
    }

    /**
     * Si no se encontraron ejes X/Y por nombre, usa los primeros ejes analogicos absolutos.
     *
     * @param components lista de componentes del mando
     * @throws Exception si falla la reflexion sobre componentes
     */
    private void assignFallbackAxes(Object[] components) throws Exception {
        if (xAxis != null && yAxis != null) {
            return;
        }

        for (Object component : components) {
            boolean analog = (boolean) invokeNoArgs(component.getClass(), component, "isAnalog");
            boolean relative = (boolean) invokeNoArgs(component.getClass(), component, "isRelative");
            if (!analog || relative) {
                continue;
            }
            if (xAxis == null) {
                xAxis = component;
            } else if (yAxis == null) {
                yAxis = component;
                return;
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
        return ((Number) invokeNoArgs(component.getClass(), component, "getPollData")).floatValue();
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
     * Invoca un metodo sin argumentos habilitando acceso reflejado cuando sea necesario.
     *
     * @param targetClass clase donde buscar el metodo
     * @param target instancia destino, o {@code null} para metodos estaticos
     * @param methodName nombre del metodo
     * @return resultado de la invocacion
     * @throws Exception si no se puede invocar el metodo
     */
    private Object invokeNoArgs(Class<?> targetClass, Object target, String methodName) throws Exception {
        java.lang.reflect.Method method = targetClass.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(target);
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
