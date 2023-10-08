/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HourTracker;

/**
 *
 * @author James Knox
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsPanel extends JPanel {
    private JTextField nameField;
    private JButton saveButton;

    private String savedName;

    public SettingsPanel() {
        setLayout(new FlowLayout());

        JLabel nameLabel = new JLabel("Enter your name:");
        nameField = new JTextField(20);
        saveButton = new JButton("Save");

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savedName = nameField.getText();
                JOptionPane.showMessageDialog(SettingsPanel.this, "Name saved successfully!");
            }
        });

        add(nameLabel);
        add(nameField);
        add(saveButton);
    }

    public String getSavedName() {
        return savedName;
    }

    public void updateDisplay() {
        if (savedName != null && !savedName.isEmpty()) {
            nameField.setText(savedName);
        }
    }
}
