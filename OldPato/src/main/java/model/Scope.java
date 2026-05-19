package model;

public class Scope extends Entity {
    private static final String SCOPE_IMAGE = "pato\\OldPato\\src\\main\\resources\\images\\scope.png";
    private static final int SCOPE_SIZE = 50;

    public Scope() {
        this(0, 0);
    }

    public Scope(int x, int y) {
        this(x, y, SCOPE_IMAGE);
    }

    public Scope(int x, int y, String imagePath) {
        super(x, y, SCOPE_SIZE, SCOPE_SIZE, imagePath);
    }
}
