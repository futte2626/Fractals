package ui;

import model.FractalModel;
import model.SceneSettings;

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
    private final JTextField scaleField;

    // New for Test & Images
    private final JTextField imageNameField;
    private final JButton saveImageButton;

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

        centerXField.addActionListener(e -> updateVariables());
        centerYField.addActionListener(e -> updateVariables());
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