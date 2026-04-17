package ui;

import coloringmethods.*;
import model.FractalModel;
import model.SceneSettings;
import rendermethods.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class SettingsPanel extends JPanel {
    private final FractalModel model;

    private final JLabel infoLabel;
    private final JButton increaseIterations;
    private final JButton decreaseIterations;
    private final JTextField iterationsField;

    private final JTextField centerXField;
    private final JTextField centerYField;
    private final JComboBox<String> locationBox;
    private final JTextField scaleField;

    private final JComboBox<String> rendererBox;
    private final JComboBox<String> colorschemeBox;

    // New for Test & Images
    private final JTextField imageNameField;
    private final JButton saveImageButton;
    private final JTextField sampleTestField;
    private final JTextField runtimeTestField;
    private final JButton startRunTimeTest;
    private final JButton animateButton;

    // Fonts
    private final Font titleFont = new Font("SansSerif", Font.BOLD, 16);
    private final Font normalFont = new Font("SansSerif", Font.PLAIN, 12);

    public SettingsPanel(FractalModel model) {
        this.model = model;

        setPreferredSize(new Dimension(300, model.settings.height));
        setBackground(Color.LIGHT_GRAY);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;

        int row = 0;

        // ---- Information Section ----
        JLabel infoHeader = new JLabel("Information:");
        infoHeader.setFont(titleFont);
        infoHeader.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(infoHeader, gbc);

        infoLabel = new JLabel();
        infoLabel.setFont(normalFont);
        gbc.gridy = row++;
        add(infoLabel, gbc);

        increaseIterations = new JButton("+10 Iterations");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        add(increaseIterations, gbc);

        decreaseIterations = new JButton("-10 Iterations");
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(decreaseIterations, gbc);

        iterationsField = new JTextField(String.valueOf(model.maxIterations));
        iterationsField.setFont(normalFont);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(iterationsField, gbc);

        // ---- Set Variables Section ----
        JLabel setVarsHeader = new JLabel("Set variables:");
        setVarsHeader.setFont(titleFont);
        setVarsHeader.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(setVarsHeader, gbc);

        JLabel centerLabel = new JLabel("Center point:");
        centerLabel.setFont(normalFont);
        gbc.gridy = row++;
        add(centerLabel, gbc);

        centerXField = new JTextField(String.valueOf(model.settings.centerX));
        centerYField = new JTextField(String.valueOf(model.settings.centerY));

        // Known locations dropdown
        String[] locations = {
                "Home",
                "Seahorse Valley",
                "Elephant Valley",
                "Tip"
        };

        locationBox = new JComboBox<>(locations);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(locationBox, gbc);

        centerXField.setFont(normalFont);
        centerYField.setFont(normalFont);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        add(centerXField, gbc);
        gbc.gridx = 1;
        add(centerYField, gbc);
        row++;

        JLabel scaleLabel = new JLabel("Scale:");
        scaleLabel.setFont(normalFont);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(scaleLabel, gbc);

        scaleField = new JTextField(String.valueOf(model.settings.scale));
        scaleField.setFont(normalFont);
        gbc.gridy = row++;
        add(scaleField, gbc);

        JLabel methodLabel = new JLabel("Change renderer/colorscheme:");
        methodLabel.setFont(titleFont);
        methodLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(methodLabel, gbc);

        JLabel rendererLabel = new JLabel("Change renderer:");
        rendererLabel.setFont(normalFont);
        gbc.gridy = row++;
        add(rendererLabel, gbc);

        String[] renderMetodes = {
                "Escape Time",
                "Rectangle Method (Unoptimised)",
                "Rectangle Method (Same iteration)",
                "Rectangle Method (Escape Cache)",
                "Rectangle Method (Paper split)",
                "Rectangle Method (Min Size)",
                "Multithreading (Escape Time)",
                "Multithreading (Rectangle Method)"
        };

        rendererBox = new JComboBox<>(renderMetodes);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(rendererBox, gbc);

        JLabel colorSchemeLabel = new JLabel("Change colorscheme:");
        colorSchemeLabel.setFont(normalFont);
        gbc.gridy = row++;
        add(colorSchemeLabel, gbc);

        String[] colorSchemes = {
                "Black and White",
                "Grey Scale",
                "Wave",
                "Rainbow"
        };

        colorschemeBox = new JComboBox<>(colorSchemes);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(colorschemeBox, gbc);



        // ---- Test & Images Section ----
        JLabel testHeader = new JLabel("Test and images:");
        testHeader.setFont(titleFont);
        testHeader.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(testHeader, gbc);





        JLabel pictureLabel = new JLabel("Take Picture:");
        pictureLabel.setFont(normalFont);
        gbc.gridy = row++;
        add(pictureLabel, gbc);

        imageNameField = new JTextField("myImage.png");
        imageNameField.setFont(normalFont);
        gbc.gridy = row++;
        add(imageNameField, gbc);

        saveImageButton = new JButton("Save Current Image");
        gbc.gridy = row++;
        add(saveImageButton, gbc);


        JLabel iterationTestLabel = new JLabel("Start a sample:");
        iterationTestLabel.setFont(normalFont);
        gbc.gridy = row++;
        add(iterationTestLabel, gbc);

        sampleTestField = new JTextField("100");
        sampleTestField.setFont(normalFont);
        gbc.gridy = row++;
        add(sampleTestField, gbc);



        JLabel runtimeTestLabel = new JLabel("Start runtime test:");
        runtimeTestLabel.setFont(normalFont);
        gbc.gridy = row++;
        add(runtimeTestLabel, gbc);

        runtimeTestField = new JTextField("100 20");
        runtimeTestField.setFont(normalFont);
        gbc.gridy = row++;
        add(runtimeTestField, gbc);

        startRunTimeTest = new JButton("Start runtime test");
        gbc.gridy = row++;
        add(startRunTimeTest, gbc);

        animateButton = new JButton("Render Zoom Animation");

        gbc.gridy = row++;
        add(animateButton, gbc);

        animateButton.addActionListener(e -> {
            new Thread(() -> {
                model.renderZoomAnimation(90, "fractal_zoom");
            }).start();
        });

        // ---- Action Listeners ----
        increaseIterations.addActionListener(e -> {
            model.maxIterations += 10;
            iterationsField.setText(String.valueOf(model.maxIterations));
            model.render();
        });

        decreaseIterations.addActionListener(e -> {
            model.maxIterations = Math.max(0, model.maxIterations - 10);
            iterationsField.setText(String.valueOf(model.maxIterations));
            model.render();
        });

        iterationsField.addActionListener(e -> {
            try {
                int val = Integer.parseInt(iterationsField.getText());
                model.maxIterations = Math.max(0, val);
                model.render();
            } catch (NumberFormatException ex) {
                iterationsField.setText(String.valueOf(model.maxIterations));
            }
        });

        sampleTestField.addActionListener(e -> {
            try {
                int val = Integer.parseInt(sampleTestField.getText());
                model.PrintSampleTest(val);
            } catch (Exception ignored) {
            }
        });

        centerXField.addActionListener(e -> updateVariables());
        centerYField.addActionListener(e -> updateVariables());

        locationBox.addActionListener(e -> {
            String selected = (String) locationBox.getSelectedItem();

            if(selected == null) return;

            switch(selected) {

                case "Home":
                    model.settings.centerX = -0.7;
                    model.settings.centerY = 0.0;
                    model.settings.scale = 2.5;
                    break;

                case "Seahorse Valley":
                    model.settings.centerX = -0.743643887037151;
                    model.settings.centerY = 0.13182590420533;
                    model.settings.scale = 0.002;
                    break;

                case "Elephant Valley":
                    model.settings.centerX = 0.285;
                    model.settings.centerY = 0.01;
                    model.settings.scale = 0.02;
                    break;
                case "Tip":
                    model.settings.centerX = -1.9427478335542994;
                    model.settings.centerY = 0.0;
                    model.settings.scale = 5E-15;
            }

            updateInfoLabel();
            model.render();
        });

        rendererBox.addActionListener(e -> {
            String selected = (String) rendererBox.getSelectedItem();

            if(selected == null) return;
            switch(selected) {
                case "Escape Time":
                    model.renderer = new EscapeTimeRenderer();
                    break;

                case "Rectangle Method (Unoptimised)":
                    model.renderer = new UnoptimisedRectangleRenderer();
                    break;

                case "Rectangle Method (Same iteration)":
                    model.renderer = new SameIterationRectangleRender();
                    break;
                case "Rectangle Method (Escape Cache)":
                    model.renderer = new EscapeCacheRectangleRender();
                    break;
                case "Rectangle Method (Paper split)":
                    model.renderer = new PaperSplitRectangleRender();
                    break;
                case "Rectangle Method (Min Size)":
                    model.renderer = new MinSizeRectangleRender();
                    break;
                case "Multithreading (Escape Time)":
                    model.renderer = new EscapeTimeMultiThreading();
                    break;
                case "Multithreading (Rectangle Method)":
                    model.renderer = new MultiThreadingRectangleRender();
                    break;
            }

            updateInfoLabel();
            model.render();
        });

        colorschemeBox.addActionListener(e -> {
            String selected = (String) colorschemeBox.getSelectedItem();

            if(selected == null) return;
            switch(selected) {

                case "Black and White":
                    model.colorScheme = new BooleanColorScheme();
                    break;

                case "Grey Scale":
                    model.colorScheme = new GreyScaleScheme();
                    break;
                case "Wave":
                    model.colorScheme = new WaveScheme();
                    break;
                case "Rainbow":
                    model.colorScheme = new RainbowScheme();
                    break;
            }

            updateInfoLabel();
            model.render();
        });

        scaleField.addActionListener(e -> updateVariables());

        saveImageButton.addActionListener(e -> {
            String filename = imageNameField.getText().trim();
            if(!filename.isEmpty()) {
                try {
                    model.SaveImage(filename);
                } catch (IOException ignored) {}
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a filename", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        startRunTimeTest.addActionListener(e -> {
            String input = runtimeTestField.getText().trim();
            String input1 = input.split(" ")[0];
            String input2 = input.split(" ")[1];

            int maxIterations = Integer.parseInt(input1);
            int samples = Integer.parseInt(input2);

            model.SaveRunTimeTest(maxIterations, samples);
        });

        // ---- Vertical glue to push content to top ----
        gbc.gridx = 0;
        gbc.gridy = 1000;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(Box.createVerticalGlue(), gbc);

        updateInfoLabel();
    }

    private void updateVariables() {
        try {
            double newX = Double.parseDouble(centerXField.getText());
            double newY = Double.parseDouble(centerYField.getText());
            double newScale = Double.parseDouble(scaleField.getText());
            model.settings.centerX = newX;
            model.settings.centerY = newY;
            model.settings.scale = newScale;
            model.render();
        } catch (NumberFormatException ex) {
            centerXField.setText(String.valueOf(model.settings.centerX));
            centerYField.setText(String.valueOf(model.settings.centerY));
            scaleField.setText(String.valueOf(model.settings.scale));
        }
    }

    public void updateInfoLabel() {
        SceneSettings s = model.settings;
        infoLabel.setText("<html>" +
                "Center: (" + (float)s.centerX + ", " + (float)s.centerY + ")<br>" +
                "Scale: " + (float)s.scale + "<br>" +
                "Size: " + s.width + "x" + s.height + "<br>" +
                "Latest Render Time: " + model.getFrameTime()/1000000f + "ms<br>" +
                "Max Iterations: " + model.maxIterations +
                "</html>");

        centerXField.setText(String.valueOf(s.centerX));
        centerYField.setText(String.valueOf(s.centerY));
        scaleField.setText(String.valueOf(s.scale));
    }
}