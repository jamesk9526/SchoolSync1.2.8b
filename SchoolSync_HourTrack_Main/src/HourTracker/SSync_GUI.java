package HourTracker;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.border.Border;


/**
 *
 * @author James
 */
public class SSync_GUI extends JFrame implements ActionListener {

    /**
     *
     */
    public final JButton addHoursButton;

    /**
     *
     */
    public final JButton addSubjectButton;

    /**
     *
     */
    public final JButton addStudentButton;

    /**
     *
     */
    public final JButton generateReportButton;

    /**
     *
     */
    public final JButton quitButton;

    /**
     *
     */
    public final JComboBox<String> nameComboBox;

    /**
     *
     */
    public final JComboBox<String> subjectComboBox;

    /**
     *
     */
    public final JTextField hoursTextField;

    /**
     *
     * @param header
     * @param data
     * @return
     */
    public JTable createTable(String[] header, Object[][] data) {
        JTable table = new JTable(data, header);
    return table;
    }

    /**
     * @param filename
     * @throws IOException
     */
     public void loadDataFromFile(String filename) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            List<String[]> data = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                data.add(row);
            }
            reader.close();

            String[] header = data.get(0);
            int nameIndex = ArrayUtils.indexOf(header, "Name");
            int subjectIndex = ArrayUtils.indexOf(header, "Subject");
            int hoursIndex = ArrayUtils.indexOf(header, "Hours");

            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                String name = row[nameIndex];
                String subject = row[subjectIndex];
                double hours = Double.parseDouble(row[hoursIndex]);

                try {
                    hours = Double.parseDouble(row[hoursIndex]);
                } catch (NumberFormatException e) {
                    // handle the error
                    JOptionPane.showMessageDialog(this, "Error loading data from file: invalid value in Hours column", "Load Error",
                            JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                addSubject(subject);

                Map<String, Double> studentData = studentHours.getOrDefault(name, new HashMap<>());
                studentData.put(subject, hours);
                studentHours.put(name, studentData);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data from file.", "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     *
     */
    public final Map<String, Map<String, Double>> studentHours;

    /**
     *
     */
    public final List<String> subjects;

    /**
     *
     */
    public final JButton importButton; // is import button

    /**
     *
     */
    public SSync_GUI() {
        setTitle("SchoolSync");
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTable csvTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(csvTable);
        add(scrollPane);
        csvTable.setModel(readCSV("filename.csv"));

        // Initialize student hours and subjects
        studentHours = new HashMap<>();
        subjects = new ArrayList<>();
        addSubject("Math");
        addSubject("Science");
        addSubject("English");
        addSubject("History");
        addSubject("Non-Core Subject");

        // Create components
        JLabel nameLabel = new JLabel("Student name:");
        nameComboBox = new JComboBox<>(studentHours.keySet().toArray(new String[0]));
        nameComboBox.setEditable(true);

        JLabel subjectLabel = new JLabel("Subject:");
        subjectComboBox = new JComboBox<>(subjects.toArray(String[]::new));
        subjectComboBox.setEditable(true);

        JLabel hoursLabel = new JLabel("Hours (0.25, 0.5, or 1):");
        JCheckBox atHomeCheckBox = new JCheckBox("At Home");
        hoursTextField = new JTextField(5);

        addHoursButton = new JButton("Add hours");
        addSubjectButton = new JButton("Add subject");
        addStudentButton = new JButton("Add student");
        generateReportButton = new JButton("Generate report");
        quitButton = new JButton("Quit");
        Component textArea = null;

        importButton = new JButton("Import");
        importButton.addActionListener(this);

        // Create the table
        csvTable = new JTable();

        // Add components to layout
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        panel.setLayout(layout);

        // Add some padding
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        panel.setBorder(padding);

        // Horizontal layout
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(nameLabel)
                                        .addComponent(subjectLabel)
                                        .addComponent(atHomeCheckBox)
                                        .addComponent(hoursLabel))
                                .addGap(10)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(nameComboBox)
                                        
                                        .addComponent(subjectComboBox)
                                        .addComponent(hoursTextField)))
                        .addComponent(addHoursButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(addSubjectButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(addStudentButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(generateReportButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(quitButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(importButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)));
        // Vertical layout
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameLabel)
                        .addComponent(nameComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(subjectLabel)
                        .addComponent(subjectComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(hoursLabel)
                        .addComponent(hoursTextField))
                .addGap(10)
                .addComponent(addHoursButton)
                .addGap(10)
                .addComponent(atHomeCheckBox)
                .addGap(10)
                .addComponent(addSubjectButton)
                .addGap(10)
                .addComponent(addStudentButton)
                .addGap(10)
                .addComponent(generateReportButton)
                .addGap(10)
                .addComponent(quitButton)
                .addGap(10)
                .addComponent(importButton)
                .addGap(10));

        // Set preferred size for buttons
        addHoursButton.setPreferredSize(new Dimension(150, 30));
        addSubjectButton.setPreferredSize(new Dimension(150, 30));
        addStudentButton.setPreferredSize(new Dimension(150, 30));
        generateReportButton.setPreferredSize(new Dimension(150, 30));
        quitButton.setPreferredSize(new Dimension(150, 30));
        importButton.setPreferredSize(new Dimension(150, 30));

        // Add action listeners
        addHoursButton.addActionListener(this);
        addSubjectButton.addActionListener(this);
        addStudentButton.addActionListener(this);
        generateReportButton.addActionListener(this);
        quitButton.addActionListener(this);
        importButton.addActionListener(this);

        // Add panel to frame and show it
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(false);
    }

    /**
     *
     * @param subject
     */
    public void addSubject(String subject) {
        subjects.add(subject);
    }

    /**
     *
     * @param name
     */
    public void addStudent(String name) {
        if (!studentHours.containsKey(name)) {
            studentHours.put(name, new HashMap<>());
            nameComboBox.addItem(name);
        }
    }

    /**
     *
     * @param name
     * @param subject
     * @param hours
     */
    public void addHours(String name, String subject, double hours) {
        if (studentHours.containsKey(name)) {
            Map<String, Double> subjectHours = studentHours.get(name);
            if (subjectHours.containsKey(subject)) {
                double currentHours = subjectHours.get(subject);
                subjectHours.put(subject, currentHours + hours);
            } else {
                subjectHours.put(subject, hours);
            }
        } else {
            Map<String, Double> subjectHours = new HashMap<>();
            subjectHours.put(subject, hours);
            studentHours.put(name, subjectHours);
            nameComboBox.addItem(name);
        }
    }

    /**
     *
     */
    public void generateReport() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                for (String name : studentHours.keySet()) {
                    Map<String, Double> subjectHours = studentHours.get(name);
                    for (String subject : subjectHours.keySet()) {
                        double hours = subjectHours.get(subject);
                        writer.write(name + "," + subject + "," + hours + "\n");
                    }
                }
                JOptionPane.showMessageDialog(this, "Report generated successfully.", "Report",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error generating report.", "Report Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     *
     * @param filename
     */
    public void saveToFile(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            for (String name : studentHours.keySet()) {
                Map<String, Double> subjectHours = studentHours.get(name);
                for (String subject : subjectHours.keySet()) {
                    double hours = subjectHours.get(subject);
                    writer.write(name + "," + subject + "," + hours + "\n");
                }
            }
            writer.close();
            JOptionPane.showMessageDialog(this, "Data saved to file.", "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data to file.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == importButton) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getPath();

                try {
                    loadDataFromFile(filename);
                    // Update the GUI components to reflect the new data
                    nameComboBox.setModel(new DefaultComboBoxModel<>(studentHours.keySet().toArray(new String[0])));
                    subjectComboBox.setModel(new DefaultComboBoxModel<>(subjects.toArray(String[]::new)));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading data from file.", "Load Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (source == addHoursButton) {
            String studentName = (String) nameComboBox.getSelectedItem();
            String subject = (String) subjectComboBox.getSelectedItem();
            String hoursString = hoursTextField.getText();

            if (hoursString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter hours.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double hours = Double.parseDouble(hoursString);
                if (hours != 0.25 && hours != 0.5 && hours != 1.0) {
                    JOptionPane.showMessageDialog(this, "Hours must be 0.25, 0.5, or 1.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Map<String, Double> hoursMap = studentHours.getOrDefault(studentName, new HashMap<>());
                double totalHours = hoursMap.getOrDefault(subject, 0.0) + hours;
                hoursMap.put(subject, totalHours);
                studentHours.put(studentName, hoursMap);

                JOptionPane.showMessageDialog(this, String.format("%.2f hours added for %s in %s.", hours, studentName,
                        subject), "Hours Added", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid hours entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (source == addSubjectButton) {
            String subject = (String) JOptionPane.showInputDialog(this, "Enter subject name:");

            if (subject != null && !subject.isEmpty() && !subjects.contains(subject)) {
                addSubject(subject);
                JOptionPane.showMessageDialog(this, "Subject added: " + subject, "Subject Added",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } else if (source == addStudentButton) {
            String studentName = (String) JOptionPane.showInputDialog(this, "Enter student name:");

            if (studentName != null && !studentName.isEmpty() && !studentHours.containsKey(studentName)) {
                studentHours.put(studentName, new HashMap<>());
                nameComboBox.addItem(studentName);
                JOptionPane.showMessageDialog(this, "Student added: " + studentName, "Student Added",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } else if (source == generateReportButton) {
            if (studentHours.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data to generate report.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            int choice = fileChooser.showSaveDialog(this);

            if (choice == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                try {
                    // Write header row
                    try (FileWriter writer = new FileWriter(filename)) {
                        // Write header row
                        writer.write("Name,");
                        for (String subject : subjects) {
                            writer.write(subject + ",");
                        }
                        writer.write("Total\n");
                        
                        // Write data rows
                        for (String studentName : studentHours.keySet()) {
                            Map<String, Double> hoursMap = studentHours.get(studentName);
                            double totalHours = 0.0;
                            
                            writer.write(studentName + ",");
                            for (String subject : subjects) {
                                double hours = hoursMap.getOrDefault(subject, 0.0);
                                writer.write(String.format("%.2f,", hours));
                                totalHours += hours;
                            }
                            writer.write(String.format("%.2f\n", totalHours));
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Report generated successfully.", "Report Generated",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error writing to file.", "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
        }
    }

    ;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        SSync_GUI gui = new SSync_GUI();
    }

    void updateStudents(Set<String> keySet) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
        // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void updateSubjects(List<String> subjects) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
        // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void showError(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
        // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void updateHours(String name, String subject, double d) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
        // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     *
     * @param report
     */
    public void displayReport(String report) {
    }

    /**
     *
     */
    public void display() {
    }
//Changed From Table Model

    /**
     *
     * @return
     */
     public DefaultTableModel loadCsvData() {
        String[] columns = {"Name", "Subject", "Hours"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try {
            CSVReader reader;
            reader = new CSVReader(new FileReader("*.csv"));
            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                model.addRow(row);
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading data from file.", "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return model;
    }

    /**
     *
     * @param filename
     * @return
     */
    public DefaultTableModel readCSV(String filename) {
        DefaultTableModel model = new DefaultTableModel();
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(filename));
            String[] headers = reader.readNext();
            model.setColumnIdentifiers(headers);
            String[] row;
            while ((row = reader.readNext()) != null) {
                model.addRow(row);
            }
            } catch (IOException e) {
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return model; 
    }
     
     

     
}
