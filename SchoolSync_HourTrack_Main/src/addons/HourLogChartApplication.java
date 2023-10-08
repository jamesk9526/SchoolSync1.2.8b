import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class HourLogChartApplication extends JFrame {

    private JTable hourLog;
    private JButton generateChartButton;

    public HourLogChartApplication() {
        setTitle("Hour Log Chart Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the components
        hourLog = new JTable();
        generateChartButton = new JButton("Generate Chart");

        // Set layout and add components
        setLayout(new BorderLayout());
        add(new JScrollPane(hourLog), BorderLayout.CENTER);
        add(generateChartButton, BorderLayout.SOUTH);

        // Configure generate chart button action listener
        generateChartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateChart();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void generateChart() {
        // Get the table model from the hourLog JTable
        DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

        // Get the total hours needed from the user
        String input = JOptionPane.showInputDialog(this, "Enter the total hours needed:");
        if (input == null) {
            return; // User cancelled
        }

        double totalHoursNeeded;
        try {
            totalHoursNeeded = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
            return;
        }

        // Calculate the percentage of hours for each student
        Map<String, Double> studentPercentages = calculateStudentPercentages(model, totalHoursNeeded);

        // Create and display the chart
        ChartPanel chartPanel = createChartPanel(studentPercentages);

        JFrame chartFrame = new JFrame("Hour Log Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(this);
        chartFrame.setVisible(true);
    }

    private Map<String, Double> calculateStudentPercentages(DefaultTableModel model, double totalHoursNeeded) {
        Map<String, Double> studentPercentages = new HashMap<>();

        // Iterate over each row in the table and calculate the percentage of hours for each student
        for (int row = 0; row < model.getRowCount(); row++) {
            String studentName = (String) model.getValueAt(row, 0);
            double hours = (double) model.getValueAt(row, 2);
            double percentage = (hours / totalHoursNeeded) * 100;
            studentPercentages.put(studentName, percentage);
        }

        return studentPercentages;
    }

    private ChartPanel createChartPanel(Map<String, Double> studentPercentages) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Add the student percentages to the dataset
        for (Map.Entry<String, Double> entry : studentPercentages.entrySet()) {
            String studentName = entry.getKey();
            double percentage = entry.getValue();
            dataset.setValue(percentage, "Percentage", studentName);
        }

        // Create the chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Student Hour Percentages", // Chart title
                "Student", // X-axis label
                "Percentage", // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Orientation
                false, // Include legend
                true, // Include tooltips
                false // Include URLs
        );

        return new ChartPanel(chart);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HourLogChartApplication application = new HourLogChartApplication();
                application.setVisible(true);
            }
        });
    }
}