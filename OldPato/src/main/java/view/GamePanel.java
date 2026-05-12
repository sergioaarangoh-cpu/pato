package view;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private Image fondo;

    public GamePanel() {
        fondo = new ImageIcon(getClass().getResource("/main/resources/images/background.png")).getImage();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (fondo != null) {
            graphics.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
        
        Color color = new Color(37, 111, 186);
        graphics.setColor(color);
        graphics.setFont(new Font("Arial", Font.BOLD, 24));
        graphics.drawString("Duck Hunt", 100, 50);
    }
}
