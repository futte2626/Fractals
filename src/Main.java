import coloringmethods.BooleanColorScheme;
import coloringmethods.GreyScaleScheme;
import rendermethods.EscapeTimeRenderer;

import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
        EscapeTimeRenderer renderer = new EscapeTimeRenderer();
        GreyScaleScheme colorScheme = new GreyScaleScheme();

        Mandelbrot panel = new Mandelbrot(renderer, colorScheme);
        frame.add(panel);
        frame.pack();
    }
}
