package view;

import model.Duck;
import model.Duckencia;
import model.EvilDuck;
import model.Scope;
import util.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Panel principal del juego encargado de dibujar el fondo, el titulo y el pato.
 */
public class GamePanel extends JPanel {

    private static final String BACKGROUND_IMAGE = "/images/background.png";
    private static final String DUCK_LEFT_IMAGE = "/images/duckleft.png";
    private static final String DUCK_RIGHT_IMAGE = "/images/duckright.png";
    private static final String DUCKENCIA_LEFT_IMAGE = "/images/duckencialeft.png";
    private static final String DUCKENCIA_RIGHT_IMAGE = "/images/duckenciaright.png";
    private static final String BACKGROUND_SOUND = "/sounds/background.wav";
    private static final String EVIL_DUCK_LEFT_IMAGE = "/images/evilduckleft.png";
    private static final String EVIL_DUCK_RIGHT_IMAGE = "/images/evilduckright.png";
    private static final String EXPLOSION_IMAGE = "/images/explosion.png";
    private static final int EXPLOSION_DURATION_MS = 200;
    private static final int EXPLOSION_SCALE_NUMERATOR = 3;
    private static final int EXPLOSION_SCALE_DIVISOR = 8;

    // tamanos de entrada por borde, deben coincidir con el tamano real de cada tipo de pato
    private static final int NORMAL_DUCK_SIZE = 90;
    private static final int BOMB_DUCK_SIZE = 120;
    private static final int EVIL_DUCK_SIZE = 70;

    // tiempo que un pato permanece en pantalla si no recibe un disparo, estilo Fruit Ninja
    private static final int DUCK_LIFETIME_MS = 6000;
    private static final int BOMB_LIFETIME_MS = 5000;

    // cada cuanto entra un pato nuevo por el borde
    private static final int DUCK_SPAWN_INTERVAL_MS = 1200;
    private static final int BOMB_SPAWN_INTERVAL_MS = 2500;

    // rango de velocidad al entrar, para variar el ritmo entre patos
    private static final int MIN_SPEED = 3;
    private static final int MAX_SPEED = 6;
    private static final double SPEED_MULTIPLIER = 0.85;

    private static final int INITIAL_DUCKS = 3;
    private static final int INITIAL_BOMBS = 2;

    // cada cuanto reaparece el EvilDuck y cuanto dura visible
    private static final int EVIL_DUCK_SPAWN_INTERVAL_MS = 10000;
    private static final int EVIL_DUCK_VISIBLE_MS = 2000;

    // frenzy mode: cada cuanto se activa, cuanto dura y que tan intenso es
    private static final int FRENZY_INTERVAL_MS = 30000;
    private static final int FRENZY_DURATION_MS = 5000;
    private static final double FRENZY_SPEED_MULTIPLIER = 1.8;
    private static final float FRENZY_MUSIC_SPEED = 1.15f;
    private static final int FRENZY_EXTRA_DUCKS = 4;
    private static final int FRENZY_TEXT_BLINK_INTERVAL_MS = 200;
    private static final String FRENZY_TEXT = "Frenzy mode";

    private Image fondo;
    private Image explosionImage;
    private List<Duck> ducks;
    private SoundManager soundManager;
    private Scope scope;
    private Font arcadeFont;
    private EvilDuck evilDuck;
    private Random random;
    private int aimX;
    private int aimY;
    private int explosionX;
    private int explosionY;
    private boolean showExplosion;
    private boolean frenzyActive;
    private Timer explosionTimer;
    private Timer duckSpawnTimer;
    private Timer bombSpawnTimer;
    private Timer evilDuckTimer;

    /**
     * Crea el panel, carga las imagenes iniciales e inicia los hilos de los patos.
     *
     * @param soundManager manejador de audio compartido
     */
    public GamePanel(SoundManager soundManager) {
        this.soundManager = soundManager;

        try {
            arcadeFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/fonts/ARCADE_N.TTF")
            ).deriveFont(Font.PLAIN, 20f);
        } catch (Exception e) {
            System.out.println("Error cargando fuente: " + e.getMessage());
            arcadeFont = new Font("Arial", Font.BOLD, 20);
        }

        fondo = new ImageIcon(getClass().getResource(BACKGROUND_IMAGE)).getImage();
        explosionImage = new ImageIcon(getClass().getResource(EXPLOSION_IMAGE)).getImage();
        ducks = new ArrayList<>();
        scope = new Scope();
        random = new Random();
        explosionTimer = new Timer(EXPLOSION_DURATION_MS, e -> {
            showExplosion = false;
            repaint();
        });
        explosionTimer.setRepeats(false);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setAimPosition(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                setAimPosition(e.getX(), e.getY());
            }
        });

        // inicia la música de fondo
        soundManager.playMusic(BACKGROUND_SOUND);

        for (int i = 0; i < INITIAL_DUCKS; i++) {
            spawnDuck();
        }
        for (int i = 0; i < INITIAL_BOMBS; i++) {
            spawnDuckencia();
        }

        duckSpawnTimer = new Timer(DUCK_SPAWN_INTERVAL_MS, e -> spawnDuck());
        duckSpawnTimer.start();

        bombSpawnTimer = new Timer(BOMB_SPAWN_INTERVAL_MS, e -> {
            if (!frenzyActive) {
                spawnDuckencia();
            }
        });
        bombSpawnTimer.start();

        evilDuck = new EvilDuck(0, 0, 900, 700, EVIL_DUCK_LEFT_IMAGE, EVIL_DUCK_RIGHT_IMAGE);

        Thread evilThread = new Thread(evilDuck);
        evilThread.start();
        evilDuckTimer = new Timer(EVIL_DUCK_SPAWN_INTERVAL_MS, e -> {
            if (ducks.contains(evilDuck)) {
                ducks.remove(evilDuck);
            }
            respawnAtRandomEdge(evilDuck, EVIL_DUCK_SIZE);
            evilDuck.setSpeedMultiplier(frenzyActive ? FRENZY_SPEED_MULTIPLIER : 1.0);
            ducks.add(evilDuck);
            repaint();

            Timer hideEvilDuckTimer = new Timer(EVIL_DUCK_VISIBLE_MS, hideEvent -> {
                ducks.remove(evilDuck);
                repaint();
            });
            hideEvilDuckTimer.setRepeats(false);
            hideEvilDuckTimer.start();
        });
        evilDuckTimer.start();

        Timer frenzyTriggerTimer = new Timer(FRENZY_INTERVAL_MS, e -> startFrenzy());
        frenzyTriggerTimer.start();

        Timer timer = new Timer(1000 / 60, e -> {
            List<Duck> expired = new ArrayList<>();
            for (Duck duck : ducks) {
                duck.setPanelSize(panelWidthOrDefault(), panelHeightOrDefault());
                if (duck != evilDuck && duck.isExpired()) {
                    expired.add(duck);
                }
            }
            for (Duck duck : expired) {
                duck.stop();
                ducks.remove(duck);
            }
            repaint();
        });
        timer.start();
    }

    /**
     * Crea un Duck normal que entra por un borde aleatorio y lo agrega en juego.
     */
    private void spawnDuck() {
        EdgeSpawn spawn = randomEdgeSpawn(NORMAL_DUCK_SIZE);
        Duck duck = new Duck(spawn.x, spawn.y, panelWidthOrDefault(), panelHeightOrDefault(),
                DUCK_LEFT_IMAGE, DUCK_RIGHT_IMAGE);
        duck.setSpeed(spawn.speedX, spawn.speedY);
        duck.setLifetime(DUCK_LIFETIME_MS);
        if (frenzyActive) {
            duck.setSpeedMultiplier(FRENZY_SPEED_MULTIPLIER);
        }
        ducks.add(duck);
        new Thread(duck).start();
    }

    /**
     * Activa el frenzy mode: patos mas rapidos y frecuentes, sin bombas, EvilDuck mas comun,
     * banner titilando y musica acelerada. Dura FRENZY_DURATION_MS y luego vuelve a la normalidad.
     */
    private void startFrenzy() {
        frenzyActive = true;

        List<Duck> bombs = new ArrayList<>();
        for (Duck duck : ducks) {
            if (duck instanceof Duckencia) {
                bombs.add(duck);
            }
        }
        for (Duck bomb : bombs) {
            bomb.stop();
            ducks.remove(bomb);
        }

        duckSpawnTimer.setDelay(DUCK_SPAWN_INTERVAL_MS / 2);
        evilDuckTimer.setDelay(EVIL_DUCK_SPAWN_INTERVAL_MS / 2);

        for (Duck duck : ducks) {
            duck.setSpeedMultiplier(FRENZY_SPEED_MULTIPLIER);
        }
        for (int i = 0; i < FRENZY_EXTRA_DUCKS; i++) {
            spawnDuck();
        }

        soundManager.setMusicSpeed(FRENZY_MUSIC_SPEED);

        Timer endFrenzyTimer = new Timer(FRENZY_DURATION_MS, e -> endFrenzy());
        endFrenzyTimer.setRepeats(false);
        endFrenzyTimer.start();
    }

    /**
     * Termina el frenzy mode y restaura la velocidad y frecuencia normales.
     */
    private void endFrenzy() {
        frenzyActive = false;
        duckSpawnTimer.setDelay(DUCK_SPAWN_INTERVAL_MS);
        evilDuckTimer.setDelay(EVIL_DUCK_SPAWN_INTERVAL_MS);

        for (Duck duck : ducks) {
            duck.setSpeedMultiplier(1.0);
        }

        soundManager.setMusicSpeed(1.0f);
    }

    /**
     * Crea un Duckencia (bomba) que entra por un borde aleatorio y lo agrega en juego.
     */
    private void spawnDuckencia() {
        EdgeSpawn spawn = randomEdgeSpawn(BOMB_DUCK_SIZE);
        Duckencia bomb = new Duckencia(spawn.x, spawn.y, panelWidthOrDefault(), panelHeightOrDefault(),
                DUCKENCIA_LEFT_IMAGE, DUCKENCIA_RIGHT_IMAGE);
        bomb.setSpeed(spawn.speedX, spawn.speedY);
        bomb.setLifetime(BOMB_LIFETIME_MS);
        ducks.add(bomb);
        new Thread(bomb).start();
    }

    /**
     * Reubica un pato existente en un borde aleatorio, usado para reaparecer el EvilDuck.
     *
     * @param duck pato a reposicionar
     * @param size tamano usado para calcular el punto de entrada
     */
    private void respawnAtRandomEdge(Duck duck, int size) {
        EdgeSpawn spawn = randomEdgeSpawn(size);
        duck.setPosition(spawn.x, spawn.y);
        duck.setSpeed(spawn.speedX, spawn.speedY);
    }

    /**
     * Calcula un punto de entrada aleatorio sobre alguno de los cuatro bordes del panel,
     * junto con una velocidad que apunta hacia adentro de la pantalla.
     *
     * @param size tamano del pato que va a entrar
     * @return punto y velocidad de entrada
     */
    private EdgeSpawn randomEdgeSpawn(int size) {
        int w = panelWidthOrDefault();
        int h = panelHeightOrDefault();
        int side = random.nextInt(4);
        int speedX = (int) Math.round((MIN_SPEED + random.nextInt(MAX_SPEED - MIN_SPEED + 1)) * SPEED_MULTIPLIER);
        int speedY = (int) Math.round((MIN_SPEED + random.nextInt(MAX_SPEED - MIN_SPEED + 1)) * SPEED_MULTIPLIER);

        switch (side) {
            case 0: // borde superior, entra hacia abajo
                return new EdgeSpawn(random.nextInt(Math.max(1, w - size)), 0,
                        random.nextBoolean() ? speedX : -speedX, speedY);
            case 1: // borde inferior, entra hacia arriba
                return new EdgeSpawn(random.nextInt(Math.max(1, w - size)), h - size,
                        random.nextBoolean() ? speedX : -speedX, -speedY);
            case 2: // borde izquierdo, entra hacia la derecha
                return new EdgeSpawn(0, random.nextInt(Math.max(1, h - size)),
                        speedX, random.nextBoolean() ? speedY : -speedY);
            default: // borde derecho, entra hacia la izquierda
                return new EdgeSpawn(w - size, random.nextInt(Math.max(1, h - size)),
                        -speedX, random.nextBoolean() ? speedY : -speedY);
        }
    }

    /**
     * Punto y velocidad de entrada calculados para un pato que aparece por el borde.
     */
    private static final class EdgeSpawn {
        private final int x;
        private final int y;
        private final int speedX;
        private final int speedY;

        private EdgeSpawn(int x, int y, int speedX, int speedY) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
        }
    }

    /**
     * Ancho del panel a usar en calculos de movimiento, con un valor por defecto
     * mientras el componente aun no tiene tamano asignado por Swing.
     *
     * @return ancho actual del panel, o 900 si todavia no esta disponible
     */
    private int panelWidthOrDefault() {
        return getWidth() > 0 ? getWidth() : 900;
    }

    /**
     * Alto del panel a usar en calculos de movimiento, con un valor por defecto
     * mientras el componente aun no tiene tamano asignado por Swing.
     *
     * @return alto actual del panel, o 700 si todavia no esta disponible
     */
    private int panelHeightOrDefault() {
        return getHeight() > 0 ? getHeight() : 700;
    }

    /**
     * Dibuja todos los elementos visibles del juego.
     *
     * @param graphics contexto grafico usado por Swing para pintar el panel.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (fondo != null) {
            graphics.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }

        graphics.setColor(Color.WHITE);
        graphics.setFont(arcadeFont);
        graphics.drawString("OldPato", 10, 20);

        for (Duck duck : ducks) {
            duck.draw(graphics);
        }
        drawExplosion(graphics);
        if (frenzyActive) {
            drawFrenzyBanner(graphics);
        }
        scope.draw(graphics);
    }

    /**
     * Dibuja el texto "Frenzy mode" titilando en la parte superior del panel.
     *
     * @param graphics contexto grafico usado por Swing
     */
    private void drawFrenzyBanner(Graphics graphics) {
        boolean visible = (System.currentTimeMillis() / FRENZY_TEXT_BLINK_INTERVAL_MS) % 2 == 0;
        if (!visible) {
            return;
        }

        Font bannerFont = arcadeFont.deriveFont(Font.PLAIN, 36f);
        graphics.setFont(bannerFont);
        graphics.setColor(Color.RED);
        FontMetrics metrics = graphics.getFontMetrics(bannerFont);
        int textWidth = metrics.stringWidth(FRENZY_TEXT);
        graphics.drawString(FRENZY_TEXT, (getWidth() - textWidth) / 2, 45);
    }

    /**
     * Muestra la explosion durante un instante en la posicion indicada.
     *
     * @param x coordenada horizontal del disparo
     * @param y coordenada vertical del disparo
     */
    public void showExplosionAt(int x, int y) {
        explosionX = x;
        explosionY = y;
        showExplosion = true;
        explosionTimer.restart();
        repaint();
    }

    /**
     * Dibuja la explosion centrada en el ultimo disparo.
     *
     * @param graphics contexto grafico usado por Swing
     */
    private void drawExplosion(Graphics graphics) {
        if (!showExplosion || explosionImage == null) {
            return;
        }

        int width = explosionImage.getWidth(this);
        int height = explosionImage.getHeight(this);
        width = width * EXPLOSION_SCALE_NUMERATOR / EXPLOSION_SCALE_DIVISOR;
        height = height * EXPLOSION_SCALE_NUMERATOR / EXPLOSION_SCALE_DIVISOR;
        graphics.drawImage(explosionImage, explosionX - width / 2, explosionY - height / 2, width, height, this);
    }

    /**
     * Obtiene la lista de patos en pantalla.
     *
     * @return lista de patos
     */
    public List<Duck> getDucks() {
        return ducks;
    }

    /**
     * Remueve un pato del juego al recibir un disparo.
     * El EvilDuck es una instancia reutilizada entre apariciones, asi que solo se oculta
     * en vez de detener su hilo.
     *
     * @param duck pato a remover
     */
    public void removeDuck(Duck duck) {
        if (duck != evilDuck) {
            duck.stop();
        }
        ducks.remove(duck);
    }

    /**
     * Mueve la mira al punto indicado y mantiene sus coordenadas centrales.
     *
     * @param x coordenada horizontal de apuntado
     * @param y coordenada vertical de apuntado
     */
    public void setAimPosition(int x, int y) {
        aimX = clamp(x, 0, Math.max(0, getWidth() - 1));
        aimY = clamp(y, 0, Math.max(0, getHeight() - 1));
        scope.setX(aimX - scope.getWidth() / 2);
        scope.setY(aimY - scope.getHeight() / 2);
        repaint();
    }

    /**
     * Obtiene la coordenada horizontal actual de la mira.
     *
     * @return coordenada x de la mira
     */
    public int getAimX() {
        return aimX;
    }

    /**
     * Obtiene la coordenada vertical actual de la mira.
     *
     * @return coordenada y de la mira
     */
    public int getAimY() {
        return aimY;
    }

    /**
     * Restringe un valor dentro de un rango.
     *
     * @param value valor a restringir
     * @param min   valor minimo
     * @param max   valor maximo
     * @return valor dentro del rango indicado
     */
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Detiene temporizadores internos del panel antes de abandonar la partida.
     */
    public void stopGame() {
        if (explosionTimer != null) {
            explosionTimer.stop();
        }
    }
}
