import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("SkySurvivor");
        Image icon = Toolkit.getDefaultToolkit().getImage("icon.jpg"); 
        frame.setIconImage(icon);
		frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SkySurvivor fm = new SkySurvivor ();
        frame.add(fm);
        frame.pack();
        fm.requestFocus();
        frame.setVisible(true);
    }
}