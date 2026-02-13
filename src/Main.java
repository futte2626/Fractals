import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
        DrawPanel panel = new DrawPanel();
        frame.add(panel);
        frame.pack();
    }
}
