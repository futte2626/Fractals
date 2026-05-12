package ui;

import coloringmethods.*;
import controllers.InputController;
import model.FractalModel;
import model.SceneSettings;
import rendermethods.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainFrame extends JFrame {

    FractalModel  model;
    ScenePanel    scene;

    public MainFrame() {
        model = new FractalModel(
                new SceneSettings(-0.5, 0, 800, 800, 3),
                new MultiThreadingRectangleRender(),
                new GreyScaleScheme(),
                150
        );

        scene = new ScenePanel(model);
        model.scenePanel    = scene;
        model.settingsPanel = null;

        new InputController(scene, model);

        setJMenuBar(buildMenuBar());

        setLayout(new BorderLayout());
        add(scene, BorderLayout.CENTER);

        setSize(1300, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        model.render();
    }

    // ================================================================
    //  Menu bar
    // ================================================================
    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.add(buildFileMenu());
        bar.add(buildViewMenu());
        bar.add(buildSettingsMenu());
        return bar;
    }

    // ----------------------------------------------------------------
    //  FILE
    // ----------------------------------------------------------------
    private JMenu buildFileMenu() {
        JMenu menu = new JMenu("File");

        JMenuItem saveImage = new JMenuItem("Save Image…");
        saveImage.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        saveImage.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this,
                    "Filename (without extension):", "Save Image", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.isBlank()) {
                try {
                    model.SaveImage(name.trim());
                    JOptionPane.showMessageDialog(this, "Saved as " + name.trim() + ".jpg");
                } catch (IOException ex) {
                    showError("Could not save image:\n" + ex.getMessage());
                }
            }
        });
        menu.add(saveImage);

        JMenuItem saveVideo = new JMenuItem("Save Zoom Animation…");
        saveVideo.addActionListener(e -> {
            JTextField durField  = new JTextField("20", 6);
            JTextField nameField = new JTextField("fractal_zoom", 12);
            JPanel p = formPanel("Duration (seconds):", durField,
                    "Output filename:",    nameField);
            if (confirm(p, "Save Zoom Animation")) {
                try {
                    double secs = Double.parseDouble(durField.getText().trim());
                    String name = nameField.getText().trim();
                    new Thread(() -> model.renderZoomAnimation(secs, name)).start();
                } catch (NumberFormatException ex) { showError("Invalid duration value."); }
            }
        });
        menu.add(saveVideo);

        menu.addSeparator();

        JMenuItem sampleTest = new JMenuItem("Run Sample Test…");
        sampleTest.addActionListener(e -> {
            String raw = JOptionPane.showInputDialog(this,
                    "Number of samples:", "Sample Test", JOptionPane.PLAIN_MESSAGE);
            if (raw != null) {
                try { model.PrintSampleTest(Integer.parseInt(raw.trim())); }
                catch (NumberFormatException ex) { showError("Enter a valid integer."); }
            }
        });
        menu.add(sampleTest);

        JMenuItem runtimeTest = new JMenuItem("Run Runtime Test…");
        runtimeTest.addActionListener(e -> {
            JTextField maxIterField = new JTextField("100", 6);
            JTextField samplesField = new JTextField("20",  6);
            JPanel p = formPanel("Max iterations:",   maxIterField,
                    "Samples per step:", samplesField);
            if (confirm(p, "Run Runtime Test")) {
                try {
                    int maxIter = Integer.parseInt(maxIterField.getText().trim());
                    int samples = Integer.parseInt(samplesField.getText().trim());
                    new Thread(() -> model.SaveRunTimeTest(maxIter, samples)).start();
                } catch (NumberFormatException ex) { showError("Enter valid integers."); }
            }
        });
        menu.add(runtimeTest);

        return menu;
    }

    // ----------------------------------------------------------------
    //  VIEW
    // ----------------------------------------------------------------
    private JMenu buildViewMenu() {
        JMenu menu = new JMenu("View");

        // Info overlay
        JCheckBoxMenuItem toggleInfo = new JCheckBoxMenuItem("Show Info Overlay", true);
        toggleInfo.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
        toggleInfo.addActionListener(e -> scene.setShowOverlay(toggleInfo.isSelected()));
        menu.add(toggleInfo);

        // Reference orbit
        JCheckBoxMenuItem toggleOrbit = new JCheckBoxMenuItem("Show Reference Orbit", false);
        toggleOrbit.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
        toggleOrbit.addActionListener(e -> scene.setShowReferenceOrbit(toggleOrbit.isSelected()));
        menu.add(toggleOrbit);

        menu.addSeparator();

        // Renderer submenu
        JMenu rendererMenu = new JMenu("Renderer");
        ButtonGroup rg = new ButtonGroup();
        record RendererOption(String label, boolean def, Runnable apply) {}
        RendererOption[] renderers = {
                new RendererOption("Escape Time",                  false, () -> model.renderer = new EscapeTimeRenderer()),
                new RendererOption("Rectangle – Unoptimised",      false, () -> model.renderer = new UnoptimisedRectangleRenderer()),
                new RendererOption("Rectangle – Same Iteration",   false, () -> model.renderer = new SameIterationRectangleRender()),
                new RendererOption("Rectangle – Escape Cache",     false, () -> model.renderer = new EscapeCacheRectangleRender()),
                new RendererOption("Multithreading – Escape Time", false, () -> model.renderer = new EscapeTimeMultiThreading()),
                new RendererOption("Multithreading – Rectangle",   true,  () -> model.renderer = new MultiThreadingRectangleRender()),
        };
        for (RendererOption r : renderers) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(r.label(), r.def());
            item.addActionListener(e -> {
                r.apply().run();
                model.render();
            });
            rg.add(item);
            rendererMenu.add(item);
        }
        menu.add(rendererMenu);

        // Color scheme submenu
        JMenu colorMenu = new JMenu("Color Scheme");
        ButtonGroup cg = new ButtonGroup();
        record ColorOption(String label, boolean def, Runnable apply) {}
        ColorOption[] schemes = {
                new ColorOption("Black and White", false, () -> model.colorScheme = new BooleanColorScheme()),
                new ColorOption("Grey Scale",      true,  () -> model.colorScheme = new GreyScaleScheme()),
                new ColorOption("Wave",            false, () -> model.colorScheme = new WaveScheme()),
                new ColorOption("Rainbow",         false, () -> model.colorScheme = new RainbowScheme()),
        };
        for (ColorOption s : schemes) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(s.label(), s.def());
            item.addActionListener(e -> { s.apply().run(); model.ClearList(); model.render(); });
            cg.add(item);
            colorMenu.add(item);
        }
        menu.add(colorMenu);

        return menu;
    }

    // ----------------------------------------------------------------
    //  SETTINGS
    // ----------------------------------------------------------------
    private JMenu buildSettingsMenu() {
        JMenu menu = new JMenu("Settings");

        // Max iterations submenu
        JMenu iterMenu = new JMenu("Max Iterations");
        JMenuItem iterPlus10  = new JMenuItem("+10");
        JMenuItem iterMinus10 = new JMenuItem("−10");
        JMenuItem iterSet     = new JMenuItem("Set value…");
        iterPlus10.setAccelerator(KeyStroke.getKeyStroke("ctrl PLUS"));
        iterMinus10.setAccelerator(KeyStroke.getKeyStroke("ctrl MINUS"));
        iterPlus10.addActionListener(e -> {
            model.maxIterations += 10;
            model.ClearList(); model.render();
        });
        iterMinus10.addActionListener(e -> {
            model.maxIterations = Math.max(0, model.maxIterations - 10);
            model.ClearList(); model.render();
        });
        iterSet.addActionListener(e -> {
            String raw = JOptionPane.showInputDialog(this,
                    "Max iterations:", String.valueOf(model.maxIterations));
            if (raw != null) {
                try {
                    model.maxIterations = Math.max(0, Integer.parseInt(raw.trim()));
                    model.ClearList(); model.render();
                } catch (NumberFormatException ex) { showError("Enter a valid integer."); }
            }
        });
        iterMenu.add(iterPlus10);
        iterMenu.add(iterMinus10);
        iterMenu.addSeparator();
        iterMenu.add(iterSet);
        menu.add(iterMenu);

        menu.addSeparator();

        JMenuItem locationSave = new JMenuItem("Save current location");
        locationSave.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
        locationSave.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this,
                    "Location name:", "Save location", JOptionPane.PLAIN_MESSAGE);
            if (name != null) {
                try {
                    FileWriter writer = new FileWriter("position.txt", true);
                    writer.write(name + " " + model.settings.centerX + " " + -model.settings.centerY + " " + model.settings.scale + "\n");
                    writer.close();
                } catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
        });
        menu.add(locationSave);

        // Go to location
        JMenu locMenu = new JMenu("Go to Location");
        record Loc(String label, double cx, double cy, double scale) {}
        ArrayList<Loc> locs =  new ArrayList<>();

        locs.add(new Loc("Home",-0.7,0.0,2.5));
        locs.add(new Loc("Seahorse Valley", -0.743643887037151,  0.13182590420533, 0.002));
        locs.add(new Loc("Elephant Valley",0.285,0.01,0.02));
        locs.add(new Loc("Tip",-1.9427478335542994, 0.0,5e-15));

        //reads file
        File locationFile = new File("position.txt");
        try (Scanner myReader = new Scanner(locationFile)) {
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(" ");
                locs.add(new Loc(data[0], Double.parseDouble(data[1]), -Double.parseDouble(data[2]), Double.parseDouble(data[3])));

            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        for (Loc loc : locs) {
            JMenuItem item = new JMenuItem(loc.label());
            item.addActionListener(e -> {
                model.settings.centerX = loc.cx();
                model.settings.centerY = loc.cy();
                model.settings.scale   = loc.scale();
                model.ClearList(); model.render();
            });
            locMenu.add(item);
        }
        menu.add(locMenu);



        // Set center coordinates
        JMenuItem setCenter = new JMenuItem("Set Center Coordinates…");
        setCenter.addActionListener(e -> {
            JTextField xField = new JTextField(String.valueOf(model.settings.centerX), 16);
            JTextField yField = new JTextField(String.valueOf(model.settings.centerY), 16);
            JPanel p = formPanel("Center X:", xField, "Center Y:", yField);
            if (confirm(p, "Set Center Coordinates")) {
                try {
                    model.settings.centerX = Double.parseDouble(xField.getText().trim());
                    model.settings.centerY = Double.parseDouble(yField.getText().trim());
                    model.ClearList(); model.render();
                } catch (NumberFormatException ex) { showError("Enter valid numbers."); }
            }
        });
        menu.add(setCenter);

        // Set scale
        JMenuItem setScale = new JMenuItem("Set Scale…");
        setScale.addActionListener(e -> {
            String raw = JOptionPane.showInputDialog(this,
                    "Scale:", String.valueOf(model.settings.scale));
            if (raw != null) {
                try {
                    model.settings.scale = Double.parseDouble(raw.trim());
                    model.ClearList(); model.render();
                } catch (NumberFormatException ex) { showError("Enter a valid number."); }
            }
        });
        menu.add(setScale);

        return menu;
    }

    // ================================================================
    //  Helpers
    // ================================================================
    private JPanel formPanel(String lbl1, JComponent f1, String lbl2, JComponent f2) {
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel(lbl1)); p.add(f1);
        p.add(new JLabel(lbl2)); p.add(f2);
        return p;
    }

    private boolean confirm(JPanel panel, String title) {
        return JOptionPane.showConfirmDialog(this, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                == JOptionPane.OK_OPTION;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}