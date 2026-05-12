package view;

import javax.swing.*;

public class MainFrame extends JFrame {
    public  MainFrame(){
        GamePanel panel = new GamePanel();
        add(panel);

        setTitle("OldPato");
        setSize(900,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

    }


}
