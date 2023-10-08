package HourTracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author James
 */
public class SchoolSync_Main {

    /**
     *
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        SSync_GUI_Form mainFrame = new SSync_GUI_Form();
        mainFrame.setTitle("SchoolSync");
        mainFrame.setSize(600, 400);

        SSync_GUI myFrame = new SSync_GUI();
        SSync_GUI gui = new SSync_GUI();
        mainFrame.add(myFrame.getContentPane());

        mainFrame.setVisible(true);
        
        loadDataFromFile();

        
        gui.display(); // or some other method that displays the user interface
    }

    /**
     *
     */
    public static Map<String, Map<String, Double>> studentHours;

    /**
     *
     */
    public static List<String> subjects;

    /**
     *
     */
    public static SSync_GUI gui;

    /**
     *
     * @param gui
     */
    public SchoolSync_Main(SSync_GUI gui) {
        studentHours = new HashMap<>();
        subjects = new ArrayList<>();
        SchoolSync_Main.gui = gui;
        // Add default subjects
        addSubject("Math");
        addSubject("Science");
        addSubject("English");
        addSubject("History");
        addSubject("Non-Core Subject");
        
    }

    /**
     *
     * @param subject
     */
    public static void addSubject(String subject) {
        subjects.add(subject);
        gui.updateSubjects(subjects);
    }

    /**
     *
     * @param scanner
     */
    public static void addSubject(Scanner scanner) {
        System.out.println("Enter subject name:");
        String subject = scanner.next();
        addSubject(subject);
        System.out.println("Subject " + subject + " added.");
    }

    /**
     *
     * @param name
     */
    public static void addStudent(String name) {
        if (!studentHours.containsKey(name)) {
            Map<String, Double> put = studentHours.put(name, new HashMap<String, Double>());
            for (String subject : subjects) {
                studentHours.get(name).put(subject, 0.0);
            }
            gui.updateStudents(studentHours.keySet());
        }
    }

    /**
     *
     * @param scanner
     */
    public static void addStudent(Scanner scanner) {
        System.out.println("Enter student name:");
        String name = scanner.next();
        addStudent(name);
        System.out.println("Student " + name + " added.");
    }

    /**
     *
     * @param name
     * @param subject
     * @param hours
     */
    public static void addHours(String name, String subject, double hours) {
        if (!studentHours.containsKey(name)) {
            gui.showError("Error: student " + name + " does not exist.");
            return;
        }
        if (!studentHours.get(name).containsKey(subject)) {
            gui.showError("Error: subject " + subject + " does not exist.");
            return;
        }
        double currentHours = studentHours.get(name).get(subject);
        studentHours.get(name).put(subject, currentHours + hours);
        gui.updateHours(name, subject, currentHours + hours);
    }

    /**
     *
     * @param scanner
     */
    public static void addHours(Scanner scanner) {
        System.out.println("Enter student name:");
        String name = scanner.next();
        System.out.println("Enter subject:");
        String subject = scanner.next();
        System.out.println("Enter hours (0.25, 0.5, or 1):");
        double hours = scanner.nextDouble();
        addHours(name, subject, hours);
        System.out.println(hours + " hours added to " + subject + " for " + name);
    }

    /**
     * 
     */
     public static void generateReport() {
        String report = generateReportString();
        gui.displayReport(report);
    }

    /**
     *
     * @return
     */
    public static String generateReportString() {
        StringBuilder report = new StringBuilder();
        report.append("Name,");
        for (String subject : subjects) {
            report.append(subject).append(",");
        }
        report.append("Total\n");

        for (Map.Entry<String, Map<String, Double>> entry : studentHours.entrySet()) {
            String studentName = entry.getKey();
            Map<String, Double> subjectHours = entry.getValue();

            double totalHours = 0.0;
            for (String subject : subjects) {
                Double hours = subjectHours.get(subject);
                if (hours != null) {
                    report.append(hours).append(",");
                    totalHours += hours;
                } else {
                    report.append("0,");
                }
            }
            report.append(totalHours).append("\n");
        }

        return report.toString();
    }

    /**
     *
     * @param report
     */
    public static void generateMonthlyReport(StringBuilder report) {
        System.out.println("Enter month and year (format: mm/yyyy):");
        String monthYearStr = scanner.next();
        String[] monthYearArr = monthYearStr.split("/");
        int month = Integer.parseInt(monthYearArr[0]);
        int year = Integer.parseInt(monthYearArr[1]);

        for (Map.Entry<String, Map<String, Double>> entry : studentHours.entrySet()) {
            String studentName = entry.getKey();
            Map<String, Double> subjectHours = entry.getValue();

            double totalHours = 0.0;
            for (Map.Entry<String, Double> subjectEntry : subjectHours.entrySet()) {
                String subject = subjectEntry.getKey();
                double hours = subjectEntry.getValue();

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                int subjectMonth = cal.get(Calendar.MONTH) + 1;
                int subjectYear = cal.get(Calendar.YEAR);
                if (subjectMonth == month && subjectYear == year) {
                    totalHours += hours;
                }
            }

            report.append(studentName).append(",");
            report.append(totalHours).append(",");
            report.append(monthYearStr);
            report.append("\n");
        }
    }

    /**
     *
     * @param report
     */
    public static void generateYearlyReport(StringBuilder report) {
        System.out.println("Enter year:");
        int year = scanner.nextInt();

        for (Map.Entry<String, Map<String, Double>> entry : studentHours.entrySet()) {
            String studentName = entry.getKey();
            Map<String, Double> subjectHours = entry.getValue();

            double totalHours = 0.0;
            for (Map.Entry<String, Double> subjectEntry : subjectHours.entrySet()) {
                String subject = subjectEntry.getKey();
                double hours = subjectEntry.getValue();

                Calendar cal = Calendar.getInstance();
            }
        }
    }

    /**
     *
     * @param report
     */
    public static void saveReportToFile(String report) {
        System.out.println("Enter report file name:");
        String reportFileName = scanner.next();

        try {
            FileWriter writer = new FileWriter(reportFileName);
            writer.write(report);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing report to file.");
        }
    }

    /**
     *
     * @param report
     */
    public static void generateWeeklyReport(StringBuilder report) {
        // Get the current week
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        // Add the hours worked by each student for the current week to the report
        for (Map.Entry<String, Map<String, Double>> entry : studentHours.entrySet()) {
            String studentName = entry.getKey();
            Map<String, Double> subjectHours = entry.getValue();
            report.append(studentName).append(",");
            double totalHours = 0.0;
            for (String subject : subjects) {
                double hours = subjectHours.get(subject);
                totalHours += hours;
                report.append(hours).append(",");
            }
            report.append(totalHours).append("\n");
        }
    }
    
    /**
     *
     */
    public static void saveDataToFile() {
    try {
        FileWriter writer = new FileWriter("filename");
        writer.write(generateReportString());
        writer.close();
    } catch (IOException e) {
        System.err.println("Error writing to file: " + e.getMessage());
    }
}

    /**
     *
     * @throws FileNotFoundException
     */
    public static void loadDataFromFile() throws FileNotFoundException {
        File file = new File("filename");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    // Parse the line and load the data into the application
                }
            }
        }
}

}
