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
                new EscapeCacheRectangleRender(),
                new GreyScaleScheme(),
                150
        );

        setTitle("Mandelbrot explorer");

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

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        bar.add(buildFileMenu());
        bar.add(buildViewMenu());
        bar.add(buildSettingsMenu());
        bar.add(buildHelpMenu());
        return bar;
    }

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
                    showError("Could not save image: " + ex.getMessage());
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
                double secs = Double.parseDouble(durField.getText().trim());
                String name = nameField.getText().trim();
                model.renderZoomAnimation(secs, name);
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

        JCheckBoxMenuItem toggleCursor = new JCheckBoxMenuItem("Show cursor", true);
        toggleCursor.addActionListener(e -> scene.setShowCrosshair(toggleCursor.isSelected()));
        menu.add(toggleCursor);

        menu.addSeparator();

        // Renderer submenu
        JMenu rendererMenu = new JMenu("Renderer");
        ButtonGroup rg = new ButtonGroup();
        record RendererOption(String label, Runnable apply) {}
        RendererOption[] renderers = {
                new RendererOption("Escape Time", () -> model.renderer = new EscapeTimeRenderer()),
                new RendererOption("Rectangle – Unoptimised", () -> model.renderer = new UnoptimisedRectangleRenderer()),
                new RendererOption("Rectangle – Same Iteration",  () -> model.renderer = new SameIterationRectangleRender()),
                new RendererOption("Rectangle – Escape Cache",  () -> model.renderer = new EscapeCacheRectangleRender()),
                new RendererOption("Multithreading – Escape Time", () -> model.renderer = new EscapeTimeMultiThreading()),
                new RendererOption("Multithreading – Rectangle", () -> model.renderer = new MultiThreadingRectangleRender()),
        };
        for (RendererOption r : renderers) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(r.label());
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
        record ColorOption(String label, Runnable apply) {}
        ColorOption[] schemes = {
                new ColorOption("Black and White", () -> model.colorScheme = new BooleanColorScheme()),
                new ColorOption("Grey Scale", () -> model.colorScheme = new GreyScaleScheme()),
                new ColorOption("Wave", () -> model.colorScheme = new WaveScheme()),
                new ColorOption("Rainbow", () -> model.colorScheme = new RainbowScheme()),
                new ColorOption("Custom Gradiant", () -> openCustomGradientDialog()),
        };
        for (ColorOption s : schemes) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(s.label());
            item.addActionListener(e -> {
                s.apply().run();
                model.ClearList();
                model.render();
            });
            cg.add(item);
            colorMenu.add(item);
        }
        menu.add(colorMenu);

        return menu;
    }

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
                model.maxIterations = Math.max(0, Integer.parseInt(raw.trim()));
                model.ClearList();
                model.render();
            }
        });
        iterMenu.add(iterPlus10);
        iterMenu.add(iterMinus10);
        iterMenu.addSeparator();
        iterMenu.add(iterSet);
        menu.add(iterMenu);

        menu.addSeparator();

        // Go to location
        JMenu locMenu = new JMenu("Go to Location");
        record Loc(String label, double cx, double cy, double scale) {}
        ArrayList<Loc> locs =  new ArrayList<>();

        locs.add(new Loc("Home",-0.7,0.0,2.5));
        locs.add(new Loc("Seahorse Valley", -0.743643887037151,  0.13182590420533, 0.002));
        locs.add(new Loc("Elephant Valley",0.285,0.01,0.02));
        locs.add(new Loc("Tip",-1.9427478335542994, 0.0,5e-15));

        //reads file (and locations)
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

        // Save locations
        JMenuItem locationSave = new JMenuItem("Save current location");
        locationSave.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
        locationSave.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this,
                    "Location name:", "Save location", JOptionPane.PLAIN_MESSAGE);
            if (name != null) {
                try {
                    FileWriter writer = new FileWriter("position.txt", true);
                    writer.write(name + " " + model.settings.centerX + " " + -model.settings.centerY + " " + model.settings.scale + "\n");
                    Loc loc = new Loc(name, model.settings.centerX, model.settings.centerY, model.settings.scale);
                    JMenuItem item = new JMenuItem(loc.label());
                    item.addActionListener(E -> {
                        model.settings.centerX = loc.cx();
                        model.settings.centerY = loc.cy();
                        model.settings.scale   = loc.scale();
                        model.ClearList(); model.render();
                    });
                    locMenu.add(item);

                    writer.close();
                } catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
        });
        menu.add(locationSave);

        // Set center coordinates
        JMenuItem setCenter = new JMenuItem("Set Center Coordinates…");
        setCenter.addActionListener(e -> {
            JTextField xField = new JTextField(String.valueOf(model.settings.centerX), 16);
            JTextField yField = new JTextField(String.valueOf(model.settings.centerY), 16);
            JPanel p = formPanel("Center X:", xField, "Center Y:", yField);
            if (confirm(p, "Set Center Coordinates")) {
                model.settings.centerX = Double.parseDouble(xField.getText());
                model.settings.centerY = Double.parseDouble(yField.getText());
                model.ClearList(); model.render();
            }
        });
        menu.add(setCenter);

        // Set scale
        JMenuItem setScale = new JMenuItem("Set Scale…");
        setScale.addActionListener(e -> {
            String scale = JOptionPane.showInputDialog(this,
                    "Scale:", String.valueOf(model.settings.scale));
            if (scale != null) {
                model.settings.scale = Double.parseDouble(scale);
                model.ClearList(); model.render();
            }
        });
        menu.add(setScale);

        return menu;
    }

    private JMenu buildHelpMenu() {
        JMenu helpMenu = new JMenu("Help");

        JLabel label = new JLabel("You're on your own");
        helpMenu.add(label);

        return helpMenu;
    }

    private void openCustomGradientDialog() {
        model.colorScheme = new CustomGradiantScheme();
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        final Color[] c1 = {Color.BLACK};
        final Color[] c2 = {Color.WHITE};

        JButton pick1 = new JButton("Choose Color 1");
        JPanel preview1 = new JPanel();
        preview1.setPreferredSize(new Dimension(40, 20));
        preview1.setBackground(c1[0]);
        preview1.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        pick1.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Pick Color 1", Color.white);
            if (chosen != null) {
                CustomGradiantScheme.color1 = chosen;
                preview1.setBackground(chosen);
                model.ClearList();
                model.render();
            }
        });

        JButton pick2 = new JButton("Choose Color 2");
        JPanel preview2 = new JPanel();
        preview2.setPreferredSize(new Dimension(40, 20));
        preview2.setBackground(c2[0]);
        preview2.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        pick2.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Pick Color 2", Color.black);
            if (chosen != null) {
                CustomGradiantScheme.color2 = chosen;
                preview2.setBackground(chosen);
                model.ClearList();
                model.render();
            }
        });

        JPanel top = new JPanel(new GridLayout(2, 2, 10, 10));
        top.add(pick1);
        top.add(preview1);
        top.add(pick2);
        top.add(preview2);

        JSlider selectWeight = new JSlider(JSlider.HORIZONTAL, 0, 2500, 1000);
        selectWeight.setPreferredSize(new Dimension(200, 25));
        selectWeight.addChangeListener(e -> {
            CustomGradiantScheme.weight = selectWeight.getValue()/1000.0;
            model.ClearList();
            model.render();
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(selectWeight, BorderLayout.CENTER);

        panel.add(top, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

    }

    // ================================================================
    //  Helpers
    // ================================================================
    private JPanel formPanel(String lbl1, JComponent f1, String lbl2, JComponent f2) {
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel(lbl1));
        p.add(f1);
        p.add(new JLabel(lbl2));
        p.add(f2);
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