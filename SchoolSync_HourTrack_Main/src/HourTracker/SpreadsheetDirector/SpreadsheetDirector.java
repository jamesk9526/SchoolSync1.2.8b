/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HourTracker.SpreadsheetDirector;

/**
 *
 * @author James Knox
 */
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SpreadsheetDirector extends JFrame {
  private static final String CSV_FILE = "csv_files.txt";
    private DefaultListModel<String> csvListModel;

    public SpreadsheetDirector() {
        initialize();
        loadCSVFiles();
    }

    private void initialize() {
        // Set the Flat Mac Dark look and feel
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setTitle("CSV File Manager");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create GUI components
        JPanel mainPanel = new JPanel(new BorderLayout());

        csvListModel = new DefaultListModel<>();
        JList<String> csvList = new JList<>(csvListModel);
        JScrollPane scrollPane = new JScrollPane(csvList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add CSV File");
        JButton launchButton = new JButton("Launch CSV");
        JButton spreadsheetButton = new JButton("Launch Spreadsheet");

        buttonPanel.add(addButton);
        buttonPanel.add(launchButton);
        buttonPanel.add(spreadsheetButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCSVFile();
            }
        });

        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchCSVFile(csvList.getSelectedIndex());
            }
        });

        spreadsheetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchSpreadsheetSoftware();
            }
        });

        // Set the main panel as the content pane
        setContentPane(mainPanel);
    }

    private void loadCSVFiles() {
        List<String> csvFiles = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(CSV_FILE))) {
            while (scanner.hasNextLine()) {
                String csvFile = scanner.nextLine();
                csvFiles.add(csvFile);
            }
        } catch (IOException e) {
            // File doesn't exist or cannot be read, ignore and proceed with an empty list
        }

        for (String csvFile : csvFiles) {
            csvListModel.addElement(csvFile);
        }
    }

    private void saveCSVFiles() {
        try (FileWriter writer = new FileWriter(CSV_FILE)) {
            for (int i = 0; i < csvListModel.getSize(); i++) {
                String csvFile = csvListModel.getElementAt(i);
                writer.write(csvFile + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCSVFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            csvListModel.addElement(filePath);
            saveCSVFiles();
        }
    }

    private void launchCSVFile(int index) {
        if (index >= 0 && index < csvListModel.getSize()) {
            String filePath = csvListModel.getElementAt(index);
            File file = new File(filePath);

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Unable to launch CSV file: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Desktop operations are not supported on this platform.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid index. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void launchSpreadsheetSoftware() {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            try {
                Desktop.getDesktop().open(new File("."));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Unable to launch spreadsheet software: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Desktop operations are not supported on this platform.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Set the Flat Mac Dark look and feel
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SpreadsheetDirector().setVisible(true);
            }
        });
    }
}