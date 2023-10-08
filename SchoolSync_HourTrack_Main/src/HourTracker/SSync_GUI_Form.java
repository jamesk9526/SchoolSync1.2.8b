package HourTracker;

import static HourTracker.SSync_GUI_Form.PreFillCSVCBData.addSubjectsToCSV;
import SchoolSync_Planner.planner_gui_form;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;

/**
 *
 * @author James Knox SanitySavingSoftware /
 */
public class SSync_GUI_Form extends javax.swing.JFrame {

    private Object Hours;

    /**
     * Creates new form HourTrackerGUIForm
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public SSync_GUI_Form() {

        initComponents();
        SSync_GUI gui = new SSync_GUI();
        pack();
        setLocationRelativeTo(null);
        jPanel1.setBackground(Color.white); // set background color
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // set look and feel
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }

        // This initialization component loads the custom subjects and students back into the program on startup  
        try {
            // Get the user's home directory
            String userHomeDirectory = System.getProperty("user.home");

            // Specify the CSV file path and name in the user's home directory
            String filePath = userHomeDirectory + File.separator + "SSstartup" + File.separator + "CBData.csv";

            FileReader reader = new FileReader(filePath);
            try (BufferedReader br = new BufferedReader(reader)) {
                DefaultComboBoxModel<String> studentModel = new DefaultComboBoxModel<>();
                DefaultComboBoxModel<String> subjectModel = new DefaultComboBoxModel<>();

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim(); // Trim leading and trailing spaces

                    // Ignore empty lines
                    if (line.isEmpty()) {
                        continue;
                    }

                    String[] data = line.split(",");
                    if (data.length == 1) {
                        String student = data[0];

                        // Ignore null or empty student names
                        if (student != null && !student.isEmpty()) {
                            studentModel.addElement(student);
                        }
                    } else if (data.length >= 2) {
                        String student = data[0];
                        String subject = data[1];

                        // Ignore null or empty student names and subjects
                        if (student != null && !student.isEmpty()) {
                            studentModel.addElement(student);
                        }

                        if (subject != null && !subject.isEmpty()) {
                            subjectModel.addElement(subject);
                        }
                    }
                }

                selectStudent.setModel(studentModel);
                selectSubject.setModel(subjectModel);
            }

        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
        performStartupOperations(); //moves all files from old version to SStartup folder useres home dir 
        loadExistingHourLog();
        setWelcomeLabelText();
        

    
    
    
    }

    


    public class PreFillCSVCBData {
    private static final String HOME_DIRECTORY = System.getProperty("user.home");
    private static final String STARTUP_FOLDER = "SSstartup";
    private static final String FLAG_FILE = "startup.flag";
    private static final String CSV_FILE = "CBdata.csv";
    private static final String SUBJECTS = "Reading,Math,Social Studies,Language Arts,Science";

    public static void addSubjectsToCSV() {
        File startupFolder = new File(HOME_DIRECTORY, STARTUP_FOLDER);
        File flagFile = new File(startupFolder, FLAG_FILE);
        File csvFile = new File(startupFolder, CSV_FILE);

        if (flagFile.exists()) {
            System.out.println("Flag file found. Skipping prefill process.");
            return;
        }

        if (!startupFolder.exists()) {
            startupFolder.mkdirs();
        }

        try {
            FileWriter writer = new FileWriter(csvFile);
            String[] subjects = SUBJECTS.split(",");
            for (String subject : subjects) {
                writer.write("," + subject.trim() + "\n");
            }
            writer.flush();
            writer.close();

            flagFile.createNewFile();
            System.out.println("Pre-fill process completed successfully.");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  
}
 








    
    public void performStartupOperations() {
        // Get the user's home directory
        String userHome = System.getProperty("user.home");

        // Define the source files to be moved
        File[] sourceFiles = {
            new File(userHome, "plannerdata.csv"),
            new File(userHome, "SShourlog.csv"),
            new File(userHome, "CBdata.csv")
        };

        // Define the destination directory path
        String destinationDirectory = userHome + File.separator + "SSstartup";

        // Create the destination directory if it doesn't exist
        File destinationDir = new File(destinationDirectory);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        // Move the source files to the destination directory
        for (File sourceFile : sourceFiles) {
            if (sourceFile.exists()) {
                try {
                    // Create a new file in the destination directory with the same name as the source file
                    File destinationFile = new File(destinationDir, sourceFile.getName());

                    // Move the source file to the destination file
                    Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // Display a success message
                    System.out.println("File moved successfully: " + destinationFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    // Display an error message
                }
            } else {
                // Display a message if the source file doesn't exist
                System.out.println("Source file not found: " + sourceFile.getAbsolutePath());
            }
        }

        // Create a flag file to indicate the completion of startup operations
        try {
            File flagFile = new File(destinationDir, "startup_complete.flag");
            flagFile.createNewFile();
            System.out.println("Startup operations completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            // Display an error message
        }
    }

    private JDateChooser fromDateChooser;
    private JDateChooser toDateChooser;

    private void setWelcomeLabelText() {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
// Check if the startup file exists
        String startupFilePath = System.getProperty("user.home") + File.separator + "SSstartup" + File.separator + "startup.txt";
        File startupFile = new File(startupFilePath);
        String firstName = "";

        if (startupFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(startupFile))) {
                firstName = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Prompt the user for their first name
            firstName = JOptionPane.showInputDialog(null, "Enter your first name:", "Welcome", JOptionPane.PLAIN_MESSAGE);

            // Save the first name to the startup file
            File startupFolder = new File(startupFile.getParent());
            startupFolder.mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(startupFile))) {
                writer.write(firstName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Set the text of the JLabel based on the first name
        if (!firstName.isEmpty()) {
            welcome_lable.setText("Welcome, " + firstName + "!");
        } else {
            welcome_lable.setText("Welcome");
        }
    }

    private void loadExistingHourLog() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String csvFilePath = Paths.get(System.getProperty("user.home"), "SSstartup", "SSHourlog.csv").toString();
        File csvFile = new File(csvFilePath);

        if (csvFile.exists()) {
            int option = JOptionPane.showOptionDialog(this, "HourLog file found. What would you like to do?", "HourLog Options",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new Object[]{"Open Default HourLog", "Import Custom HourLog", "Create New HourLog"}, null);

            switch (option) {
                case 0:
                    // Open Default HourLog
                    openHourLog(csvFile);
                    break;
                case 1:
                    // Import Custom HourLog
                    importHourLog();
                    break;
                case 2:
                    // Create New HourLog
                    createNewHourLog(csvFile);
                    break;
                default:
                    // User closed the dialog or chose an invalid option
                    break;
            }
        } else {
            int option = JOptionPane.showOptionDialog(this, "No HourLog file found. Would you like to create a new HourLog?", "Create New HourLog",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new Object[]{"Yes", "No"}, null);

            if (option == 0) {
                createNewHourLog(csvFile);
            }
        }

        // Append "Activity At Home" to rows with null values in index 5
        if (hourLog.getModel().getRowCount() > 0) {
            for (int i = 0; i < hourLog.getModel().getRowCount(); i++) {
                if (hourLog.getModel().getValueAt(i, 5) == null) {
                    hourLog.getModel().setValueAt("Activity At Home", i, 5);
                }
            }
        }
    }

    private void openHourLog(File csvFile) {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            DefaultTableModel model = (DefaultTableModel) hourLog.getModel();
            model.setRowCount(0); // Clear existing data

            String line;
            String cvsSplitBy = ",";
            boolean skipHeaders = true;

            while ((line = br.readLine()) != null) {
                if (skipHeaders) {
                    skipHeaders = false;
                    continue;
                }

                String[] data = line.split(cvsSplitBy);
                model.addRow(data);
            }

            JOptionPane.showMessageDialog(this, "Default HourLog opened successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading data from CSV.");
            e.printStackTrace();
        }
    }

    private void importHourLog() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import HourLog"); // Set the dialog title

        // Set the file filter to restrict to CSV files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        // Show the Open File dialog
        int openOption = fileChooser.showOpenDialog(this);

        if (openOption == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                DefaultTableModel model = (DefaultTableModel) hourLog.getModel();
                model.setRowCount(0); // Clear existing data

                String line;
                String cvsSplitBy = ",";
                boolean skipHeaders = true;

                while ((line = br.readLine()) != null) {
                    if (skipHeaders) {
                        skipHeaders = false;
                        continue;
                    }

                    String[] data = line.split(cvsSplitBy);
                    model.addRow(data);
                }

                JOptionPane.showMessageDialog(this, "Custom HourLog imported successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error importing HourLog from file.");
                e.printStackTrace();
            }
        }
    }

    private void createNewHourLog(File csvFile) {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        int option = JOptionPane.showConfirmDialog(this, "Do you want to save the old HourLog before creating a new one?", "Save Old HourLog", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save/Backup Old HourLog"); // Set the dialog title

            // Set the file filter to restrict to CSV files
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);

            // Show the Save File dialog
            int saveOption = fileChooser.showSaveDialog(this);

            if (saveOption == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                saveOldHourLog(selectedFile);
            }
        }

        try {
            File parentDir = csvFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            csvFile.createNewFile();

            DefaultTableModel model = (DefaultTableModel) hourLog.getModel();
            model.setRowCount(0); // Clear existing data

            JOptionPane.showMessageDialog(this, "New HourLog created successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating HourLog file.");
            e.printStackTrace();
        }
    }

    private void saveOldHourLog(File outputFile) {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        File sourceFile = Paths.get(System.getProperty("user.home"), "SSstartup", "SSHourlog.csv").toFile();

        try {
            // Check if the output file name ends with ".csv" and append if necessary
            String outputPath = outputFile.getAbsolutePath();
            if (!outputPath.toLowerCase().endsWith(".csv")) {
                outputPath += ".csv";
                outputFile = new File(outputPath);
            }

            // Copy the contents of the source file to the output file
            Files.copy(sourceFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            JOptionPane.showMessageDialog(this, "Old HourLog saved. Backup created successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving old HourLog.");
            e.printStackTrace();
        }

    }

    // Continue loading the program
    // Perform any additional actions or logic after loading the HourLog
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hourlogFrame = new javax.swing.JFrame();
        jScrollPane2 = new javax.swing.JScrollPane();
        hourLog = new javax.swing.JTable();
        houlogframe_exportspreadsheetbutton = new javax.swing.JButton();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        student_list = new javax.swing.JFrame();
        jScrollPaneStudentlist = new javax.swing.JScrollPane();
        studentListTable = new javax.swing.JTable();
        subject_list = new javax.swing.JFrame();
        jScrollPaneSubjectlist = new javax.swing.JScrollPane();
        SubjectListTable = new javax.swing.JTable();
        openCSVFileChooser = new javax.swing.JFileChooser();
        journal_frame = new javax.swing.JFrame();
        login = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jRadioButton1 = new javax.swing.JRadioButton();
        jButton7 = new javax.swing.JButton();
        TabbedPane_Main = new javax.swing.JTabbedPane();
        report_panle = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        main_input_panle = new javax.swing.JPanel();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jLabel7 = new javax.swing.JLabel();
        awaycheck = new javax.swing.JCheckBox();
        jLabel16 = new javax.swing.JLabel();
        fill_addhours_datefeild = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        setDateField = new javax.swing.JFormattedTextField();
        Labelforaway2 = new javax.swing.JLabel();
        selectStudent = new javax.swing.JComboBox<>();
        Labelforaway = new javax.swing.JLabel();
        selectSubject = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        core_select = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        hoursField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        addHoursButton = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jDesktopPane2 = new javax.swing.JDesktopPane();
        welcome_lable = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        planner_open_maintoolbar_link1 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel31 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jDesktopPane3 = new javax.swing.JDesktopPane();
        jLabel3 = new javax.swing.JLabel();
        html_out_report_generate_core_noncore = new javax.swing.JButton();
        jDesktopPane6 = new javax.swing.JDesktopPane();
        jLabel22 = new javax.swing.JLabel();
        export_csv_daterange = new javax.swing.JButton();
        report_daily_button = new javax.swing.JButton();
        html_export = new javax.swing.JButton();
        generate_pie_chart = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jDesktopPane4 = new javax.swing.JDesktopPane();
        nameField = new javax.swing.JTextField();
        addStudentButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        subjectField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        addSubjectButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        curriculumField = new javax.swing.JTextField();
        addSubjectButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jDesktopPane5 = new javax.swing.JDesktopPane();
        editcbdata = new javax.swing.JButton();
        jDesktopPane8 = new javax.swing.JDesktopPane();
        loadInfo = new javax.swing.JButton();
        more_tools = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jDesktopPane14 = new javax.swing.JDesktopPane();
        planner_tools_button = new javax.swing.JButton();
        hours_on_track = new javax.swing.JButton();
        transcriptgenloader = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        merge_hourlogs = new javax.swing.JButton();
        import_hourlog = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        hours_settings_menu = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        VisitSitePatreon = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jToolBar3 = new javax.swing.JToolBar();
        mainScrollPane = new javax.swing.JScrollPane();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel21 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        save_hourlog_main = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        openHourLog = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        backupdata_documents = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        planner_open_maintoolbar_link = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        merge_menu_item = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        ss_clear = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        aboutmenu = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        options = new javax.swing.JMenuItem();

        jScrollPane2.setViewportView(hourLog);

        hourLog.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        hourLog.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Student", "Subject", "Hours Core", "Hours Non-Core", "Away"
            }
        ));
        hourLog.setColumnSelectionAllowed(true);
        hourLog.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(hourLog);
        hourLog.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        houlogframe_exportspreadsheetbutton.setText("Export To SpreadSheet");
        houlogframe_exportspreadsheetbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                houlogframe_exportspreadsheetbuttonActionPerformed(evt);
            }
        });

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        hourlogFrame.setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout hourlogFrameLayout = new javax.swing.GroupLayout(hourlogFrame.getContentPane());
        hourlogFrame.getContentPane().setLayout(hourlogFrameLayout);
        hourlogFrameLayout.setHorizontalGroup(
            hourlogFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hourlogFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1072, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(hourlogFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(hourlogFrameLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(houlogframe_exportspreadsheetbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        hourlogFrameLayout.setVerticalGroup(
            hourlogFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hourlogFrameLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 632, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(hourlogFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(hourlogFrameLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(houlogframe_exportspreadsheetbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        studentListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Student"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        studentListTable.setColumnSelectionAllowed(true);
        jScrollPaneStudentlist.setViewportView(studentListTable);
        studentListTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout student_listLayout = new javax.swing.GroupLayout(student_list.getContentPane());
        student_list.getContentPane().setLayout(student_listLayout);
        student_listLayout.setHorizontalGroup(
            student_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(student_listLayout.createSequentialGroup()
                .addComponent(jScrollPaneStudentlist, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addContainerGap())
        );
        student_listLayout.setVerticalGroup(
            student_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneStudentlist, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
        );

        SubjectListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Student"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        SubjectListTable.getTableHeader().setReorderingAllowed(false);
        jScrollPaneSubjectlist.setViewportView(SubjectListTable);
        SubjectListTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout subject_listLayout = new javax.swing.GroupLayout(subject_list.getContentPane());
        subject_list.getContentPane().setLayout(subject_listLayout);
        subject_listLayout.setHorizontalGroup(
            subject_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subject_listLayout.createSequentialGroup()
                .addComponent(jScrollPaneSubjectlist, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addContainerGap())
        );
        subject_listLayout.setVerticalGroup(
            subject_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneSubjectlist, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout journal_frameLayout = new javax.swing.GroupLayout(journal_frame.getContentPane());
        journal_frame.getContentPane().setLayout(journal_frameLayout);
        journal_frameLayout.setHorizontalGroup(
            journal_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        journal_frameLayout.setVerticalGroup(
            journal_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout loginLayout = new javax.swing.GroupLayout(login);
        login.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SchoolSync V1.2.8.0b");
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(51, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(java.awt.Color.darkGray);
        setMinimumSize(new java.awt.Dimension(1091, 700));
        setName("main_Frame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1091, 700));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jRadioButton1.setText("Away From Home");
        getContentPane().add(jRadioButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 71, 376, 0));

        jButton7.setText("Generate Report Yearly");
        getContentPane().add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 77, 223, 0));

        TabbedPane_Main.setForeground(new java.awt.Color(153, 153, 153));
        TabbedPane_Main.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        TabbedPane_Main.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        TabbedPane_Main.setToolTipText("");
        TabbedPane_Main.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        TabbedPane_Main.setFont(TabbedPane_Main.getFont().deriveFont(TabbedPane_Main.getFont().getSize()+1f));
        TabbedPane_Main.setName(""); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        main_input_panle.setForeground(new java.awt.Color(255, 255, 255));
        main_input_panle.setPreferredSize(new java.awt.Dimension(800, 400));

        jLayeredPane3.setBackground(new java.awt.Color(102, 102, 102));

        jDesktopPane1.setBackground(new java.awt.Color(17, 17, 17));
        jDesktopPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(17, 17, 17), 10, true));
        jDesktopPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDesktopPane1.setLayout(new java.awt.GridLayout(9, 50, 2, 10));

        jLabel7.setFont(jLabel7.getFont().deriveFont((jLabel7.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, jLabel7.getFont().getSize()+7));
        jLabel7.setForeground(new java.awt.Color(80, 164, 171));
        jLabel7.setText("    Add Hours");
        jDesktopPane1.add(jLabel7);

        awaycheck.setText("Activity - Away From Home");
        awaycheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                awaycheckActionPerformed(evt);
            }
        });
        jDesktopPane1.add(awaycheck);
        jDesktopPane1.add(jLabel16);

        fill_addhours_datefeild.setBackground(new java.awt.Color(51, 51, 51));
        fill_addhours_datefeild.setText("Click To Select Date");
        fill_addhours_datefeild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fill_addhours_datefeildActionPerformed(evt);
            }
        });
        jDesktopPane1.add(fill_addhours_datefeild);

        jLabel8.setText(" Date (Select)");
        jDesktopPane1.add(jLabel8);

        try {
            setDateField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-##-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        setDateField.setActionCommand("setDate");
        setDateField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDateFieldActionPerformed(evt);
            }
        });
        jDesktopPane1.add(setDateField);

        Labelforaway2.setText(" Student");
        jDesktopPane1.add(Labelforaway2);

        selectStudent.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
        selectStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectStudentActionPerformed(evt);
            }
        });
        jDesktopPane1.add(selectStudent);

        Labelforaway.setText(" Subject");
        jDesktopPane1.add(Labelforaway);

        selectSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectSubjectActionPerformed(evt);
            }
        });
        jDesktopPane1.add(selectSubject);

        jLabel12.setText(" Core Or Non-Core?");
        jDesktopPane1.add(jLabel12);

        core_select.setModel(new javax.swing.SpinnerListModel(new String[] {"Core Hours", "Non-Core Hours"}));
        jDesktopPane1.add(core_select);

        jLabel5.setText(" Input Hours");
        jDesktopPane1.add(jLabel5);

        hoursField.setActionCommand("Hours");
        hoursField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hoursFieldActionPerformed(evt);
            }
        });
        jDesktopPane1.add(hoursField);
        jDesktopPane1.add(jLabel13);

        addHoursButton.setBackground(new java.awt.Color(80, 164, 171));
        addHoursButton.setFont(addHoursButton.getFont().deriveFont(addHoursButton.getFont().getSize()+2f));
        addHoursButton.setForeground(new java.awt.Color(0, 0, 0));
        addHoursButton.setText("Add Hours");
        addHoursButton.setToolTipText("");
        addHoursButton.setActionCommand("add_Hours");
        addHoursButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addHoursButtonMouseClicked(evt);
            }
        });
        addHoursButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addHoursButtonActionPerformed(evt);
            }
        });
        jDesktopPane1.add(addHoursButton);

        jLabel24.setFont(jLabel24.getFont().deriveFont(jLabel24.getFont().getSize()-2f));
        jLabel24.setForeground(new java.awt.Color(80, 164, 171));
        jLabel24.setText("Selecting Primary Subjects, Locks This To \"Core Hous\"");
        jDesktopPane1.add(jLabel24);
        jDesktopPane1.add(jLabel11);

        jDesktopPane2.setBackground(new java.awt.Color(51, 51, 51));
        jDesktopPane2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 51, 51), 5, true));
        jDesktopPane2.setForeground(new java.awt.Color(51, 51, 51));

        welcome_lable.setFont(welcome_lable.getFont().deriveFont((welcome_lable.getFont().getStyle() | java.awt.Font.ITALIC) | java.awt.Font.BOLD, welcome_lable.getFont().getSize()+5));
        welcome_lable.setForeground(new java.awt.Color(80, 164, 171));
        welcome_lable.setText("Name");

        jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getSize()+4f));
        jLabel6.setForeground(new java.awt.Color(80, 164, 171));
        jLabel6.setText("<--- Track Your Hours");

        jLabel15.setFont(jLabel15.getFont().deriveFont(jLabel15.getFont().getSize()+4f));
        jLabel15.setForeground(new java.awt.Color(80, 164, 171));
        jLabel15.setText("Make Your Plans ---> ");

        jLabel17.setFont(jLabel17.getFont().deriveFont(jLabel17.getFont().getSize()+2f));
        jLabel17.setText("Here are a few things to get you started!");

        planner_open_maintoolbar_link1.setBackground(new java.awt.Color(0, 102, 51));
        planner_open_maintoolbar_link1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        planner_open_maintoolbar_link1.setText("Launch Planner");
        planner_open_maintoolbar_link1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        planner_open_maintoolbar_link1.setMaximumSize(new java.awt.Dimension(135, 35));
        planner_open_maintoolbar_link1.setMinimumSize(new java.awt.Dimension(120, 35));
        planner_open_maintoolbar_link1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        planner_open_maintoolbar_link1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planner_open_maintoolbar_link1ActionPerformed(evt);
            }
        });

        jLabel18.setFont(jLabel18.getFont().deriveFont(jLabel18.getFont().getSize()+2f));
        jLabel18.setText("Save Your Work. At the Bottom Of This Tab. ");

        jLabel20.setFont(jLabel20.getFont().deriveFont(jLabel20.getFont().getSize()+2f));
        jLabel20.setText("Find The Generate Report Tab");

        jLabel23.setFont(jLabel23.getFont().deriveFont(jLabel23.getFont().getSize()+2f));
        jLabel23.setText("Try The \"Hour Total\" Reporting Feature.");

        jLabel29.setFont(jLabel29.getFont().deriveFont(jLabel29.getFont().getSize()+2f));
        jLabel29.setText("Feel Free to Explore The Software.");

        jLabel30.setFont(jLabel30.getFont().deriveFont(jLabel30.getFont().getSize()+2f));
        jLabel30.setText("New Fixes And Features Comming Soon.");

        jLabel31.setBackground(new java.awt.Color(0, 102, 51));
        jLabel31.setFont(jLabel31.getFont().deriveFont((jLabel31.getFont().getStyle() & ~java.awt.Font.ITALIC) & ~java.awt.Font.BOLD, jLabel31.getFont().getSize()+2));
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Announcements!");

        jDesktopPane2.setLayer(welcome_lable, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jLabel6, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jLabel15, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jLabel17, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(planner_open_maintoolbar_link1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jLabel18, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jLabel20, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jLabel23, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jLabel29, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jLabel30, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jSeparator3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jSeparator4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jLabel31, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane2Layout = new javax.swing.GroupLayout(jDesktopPane2);
        jDesktopPane2.setLayout(jDesktopPane2Layout);
        jDesktopPane2Layout.setHorizontalGroup(
            jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane2Layout.createSequentialGroup()
                .addGroup(jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jDesktopPane2Layout.createSequentialGroup()
                                    .addComponent(jLabel15)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(planner_open_maintoolbar_link1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel20)
                                .addComponent(jLabel23)
                                .addComponent(jLabel29)
                                .addComponent(jLabel30)
                                .addComponent(jLabel18)
                                .addComponent(jSeparator3)
                                .addComponent(jSeparator4))
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jDesktopPane2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(welcome_lable, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(185, Short.MAX_VALUE))
        );
        jDesktopPane2Layout.setVerticalGroup(
            jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel31)
                .addGap(18, 18, 18)
                .addComponent(welcome_lable, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(planner_open_maintoolbar_link1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addGap(18, 18, 18)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel30)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jLayeredPane3.setLayer(jDesktopPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(jDesktopPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane3Layout = new javax.swing.GroupLayout(jLayeredPane3);
        jLayeredPane3.setLayout(jLayeredPane3Layout);
        jLayeredPane3Layout.setHorizontalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jDesktopPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jLayeredPane3Layout.setVerticalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane3Layout.createSequentialGroup()
                .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDesktopPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout main_input_panleLayout = new javax.swing.GroupLayout(main_input_panle);
        main_input_panle.setLayout(main_input_panleLayout);
        main_input_panleLayout.setHorizontalGroup(
            main_input_panleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, main_input_panleLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLayeredPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1056, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );
        main_input_panleLayout.setVerticalGroup(
            main_input_panleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(main_input_panleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout report_panleLayout = new javax.swing.GroupLayout(report_panle);
        report_panle.setLayout(report_panleLayout);
        report_panleLayout.setHorizontalGroup(
            report_panleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(report_panleLayout.createSequentialGroup()
                .addGroup(report_panleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(report_panleLayout.createSequentialGroup()
                        .addGap(940, 940, 940)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(main_input_panle, javax.swing.GroupLayout.PREFERRED_SIZE, 1066, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );
        report_panleLayout.setVerticalGroup(
            report_panleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(report_panleLayout.createSequentialGroup()
                .addGroup(report_panleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(report_panleLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(476, 476, 476))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, report_panleLayout.createSequentialGroup()
                        .addComponent(main_input_panle, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        TabbedPane_Main.addTab("Track Hours - HourLog", report_panle);

        jDesktopPane3.setBackground(new java.awt.Color(51, 51, 51));
        jDesktopPane3.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jDesktopPane3.setForeground(new java.awt.Color(0, 0, 0));

        jLabel3.setFont(jLabel3.getFont().deriveFont((jLabel3.getFont().getStyle() | java.awt.Font.ITALIC), jLabel3.getFont().getSize()+8));
        jLabel3.setText("Generate Reports - Ready To Use");

        html_out_report_generate_core_noncore.setBackground(new java.awt.Color(0, 102, 51));
        html_out_report_generate_core_noncore.setText("Hour Total (Core and Non-Core Total across all Subjects per Student)");
        html_out_report_generate_core_noncore.setToolTipText("");
        html_out_report_generate_core_noncore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                html_out_report_generate_core_noncoreActionPerformed(evt);
            }
        });

        jLabel22.setFont(jLabel22.getFont().deriveFont((jLabel22.getFont().getStyle() | java.awt.Font.ITALIC), jLabel22.getFont().getSize()+8));
        jLabel22.setText("Experimental Features");

        export_csv_daterange.setBackground(new java.awt.Color(80, 164, 171));
        export_csv_daterange.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        export_csv_daterange.setForeground(new java.awt.Color(0, 0, 0));
        export_csv_daterange.setText("Export Spreadsheet By Date Range");
        export_csv_daterange.setMaximumSize(new java.awt.Dimension(300, 24));
        export_csv_daterange.setMinimumSize(new java.awt.Dimension(200, 24));
        export_csv_daterange.setPreferredSize(new java.awt.Dimension(125, 24));
        export_csv_daterange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export_csv_daterangeActionPerformed(evt);
            }
        });

        report_daily_button.setBackground(new java.awt.Color(0, 102, 51));
        report_daily_button.setText("Daily Report Hour Total (OneDay Reports)");
        report_daily_button.setToolTipText("");
        report_daily_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                report_daily_buttonActionPerformed(evt);
            }
        });

        jDesktopPane6.setLayer(jLabel22, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane6.setLayer(export_csv_daterange, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane6.setLayer(report_daily_button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane6Layout = new javax.swing.GroupLayout(jDesktopPane6);
        jDesktopPane6.setLayout(jDesktopPane6Layout);
        jDesktopPane6Layout.setHorizontalGroup(
            jDesktopPane6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDesktopPane6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(report_daily_button, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                        .addComponent(export_csv_daterange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jDesktopPane6Layout.setVerticalGroup(
            jDesktopPane6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(export_csv_daterange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(report_daily_button)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        html_export.setBackground(new java.awt.Color(0, 102, 51));
        html_export.setText("Current HourLog Report (Exports Everything Line By Line)");
        html_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                html_exportActionPerformed(evt);
            }
        });

        generate_pie_chart.setText("GoalViewer");
        generate_pie_chart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generate_pie_chartActionPerformed(evt);
            }
        });

        jDesktopPane3.setLayer(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane3.setLayer(html_out_report_generate_core_noncore, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane3.setLayer(jDesktopPane6, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane3.setLayer(html_export, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane3.setLayer(generate_pie_chart, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane3Layout = new javax.swing.GroupLayout(jDesktopPane3);
        jDesktopPane3.setLayout(jDesktopPane3Layout);
        jDesktopPane3Layout.setHorizontalGroup(
            jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(html_out_report_generate_core_noncore, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(html_export, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generate_pie_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addComponent(jDesktopPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );
        jDesktopPane3Layout.setVerticalGroup(
            jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane3Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(html_out_report_generate_core_noncore)
                        .addGap(18, 18, 18)
                        .addComponent(html_export))
                    .addComponent(jDesktopPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 259, Short.MAX_VALUE)
                .addComponent(generate_pie_chart)
                .addGap(33, 33, 33))
        );

        TabbedPane_Main.addTab("Generate Reports", jDesktopPane3);

        jLayeredPane2.setForeground(new java.awt.Color(51, 51, 51));

        jDesktopPane4.setBackground(new java.awt.Color(51, 51, 51));
        jDesktopPane4.setForeground(new java.awt.Color(255, 255, 255));

        nameField.setText("Name");
        nameField.setActionCommand("Name");
        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });

        addStudentButton.setBackground(java.awt.SystemColor.controlHighlight);
        addStudentButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        addStudentButton.setForeground(new java.awt.Color(0, 0, 0));
        addStudentButton.setText("Add New Student");
        addStudentButton.setActionCommand("add_Student");
        addStudentButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addStudentButtonMouseClicked(evt);
            }
        });
        addStudentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStudentButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Calibri Light", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Add Curriculum");

        subjectField.setText("e.g Math");
        subjectField.setActionCommand("Subject");
        subjectField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subjectFieldActionPerformed(evt);
            }
        });

        addSubjectButton.setBackground(java.awt.SystemColor.controlHighlight);
        addSubjectButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        addSubjectButton.setForeground(new java.awt.Color(0, 0, 0));
        addSubjectButton.setText("Add New Subject");
        addSubjectButton.setActionCommand("add_Subject");
        addSubjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubjectButtonActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Calibri Light", 0, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Add New Student");

        jLabel10.setFont(new java.awt.Font("Calibri Light", 0, 24)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Add New Subject");

        curriculumField.setActionCommand("Subject");
        curriculumField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                curriculumFieldActionPerformed(evt);
            }
        });

        addSubjectButton1.setBackground(java.awt.SystemColor.controlHighlight);
        addSubjectButton1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        addSubjectButton1.setForeground(new java.awt.Color(0, 0, 0));
        addSubjectButton1.setText("Add New Curriculum");
        addSubjectButton1.setActionCommand("add_Subject");
        addSubjectButton1.setEnabled(false);
        addSubjectButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubjectButton1ActionPerformed(evt);
            }
        });

        jLabel14.setText("Remember The Primary \"Core\" Subjects - Reading, Math, Social studies, Language Arts, and Science They are preloaded for you.");

        jDesktopPane4.setLayer(nameField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(addStudentButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(subjectField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(jSeparator2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(addSubjectButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(jLabel10, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(curriculumField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(addSubjectButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(jLabel14, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane4Layout = new javax.swing.GroupLayout(jDesktopPane4);
        jDesktopPane4.setLayout(jDesktopPane4Layout);
        jDesktopPane4Layout.setHorizontalGroup(
            jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane4Layout.createSequentialGroup()
                .addGroup(jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane4Layout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addGroup(jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jDesktopPane4Layout.createSequentialGroup()
                                .addGroup(jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(97, 97, 97)
                                .addGroup(jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                    .addComponent(subjectField)))
                            .addGroup(jDesktopPane4Layout.createSequentialGroup()
                                .addComponent(addStudentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(103, 103, 103)
                                .addComponent(addSubjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator2)))
                    .addGroup(jDesktopPane4Layout.createSequentialGroup()
                        .addGap(285, 285, 285)
                        .addGroup(jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(curriculumField)
                            .addComponent(addSubjectButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))))
                .addContainerGap(151, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addGap(30, 30, 30))
        );
        jDesktopPane4Layout.setVerticalGroup(
            jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane4Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDesktopPane4Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(subjectField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane4Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(addStudentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(addSubjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(curriculumField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addSubjectButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jDesktopPane5.setBackground(new java.awt.Color(51, 51, 51));

        editcbdata.setText("Edit (Coming Soon)");
        editcbdata.setEnabled(false);
        editcbdata.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editcbdataMouseClicked(evt);
            }
        });
        editcbdata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editcbdataActionPerformed(evt);
            }
        });

        jDesktopPane5.setLayer(editcbdata, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane5Layout = new javax.swing.GroupLayout(jDesktopPane5);
        jDesktopPane5.setLayout(jDesktopPane5Layout);
        jDesktopPane5Layout.setHorizontalGroup(
            jDesktopPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editcbdata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDesktopPane5Layout.setVerticalGroup(
            jDesktopPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(editcbdata)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jDesktopPane8.setBackground(new java.awt.Color(51, 51, 51));

        loadInfo.setText("Load Students And Subjects");
        loadInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadInfoActionPerformed(evt);
            }
        });

        jDesktopPane8.setLayer(loadInfo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane8Layout = new javax.swing.GroupLayout(jDesktopPane8);
        jDesktopPane8.setLayout(jDesktopPane8Layout);
        jDesktopPane8Layout.setHorizontalGroup(
            jDesktopPane8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loadInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDesktopPane8Layout.setVerticalGroup(
            jDesktopPane8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane8Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(loadInfo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLayeredPane2.setLayer(jDesktopPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jDesktopPane5, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jDesktopPane8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jDesktopPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDesktopPane8)
                    .addComponent(jDesktopPane5)))
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addComponent(jDesktopPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addComponent(jDesktopPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jDesktopPane8))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(44, Short.MAX_VALUE)
                .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        TabbedPane_Main.addTab("Create New", jPanel3);

        jDesktopPane14.setBackground(new java.awt.Color(51, 51, 51));

        planner_tools_button.setText("Planner (Beta Preview)");
        planner_tools_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planner_tools_buttonActionPerformed(evt);
            }
        });

        hours_on_track.setText("HoursOnTrack");
        hours_on_track.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hours_on_trackActionPerformed(evt);
            }
        });

        transcriptgenloader.setText("Transcript Coming Soon");
        transcriptgenloader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transcriptgenloaderActionPerformed(evt);
            }
        });

        jButton1.setText("Calculator");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        merge_hourlogs.setText("Merge CSV HourLogs");
        merge_hourlogs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                merge_hourlogsActionPerformed(evt);
            }
        });

        import_hourlog.setBackground(new java.awt.Color(80, 164, 171));
        import_hourlog.setForeground(new java.awt.Color(0, 0, 0));
        import_hourlog.setText("Import HourLog");
        import_hourlog.setMaximumSize(new java.awt.Dimension(96, 35));
        import_hourlog.setPreferredSize(new java.awt.Dimension(96, 35));
        import_hourlog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                import_hourlogActionPerformed(evt);
            }
        });

        jDesktopPane14.setLayer(planner_tools_button, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane14.setLayer(hours_on_track, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane14.setLayer(transcriptgenloader, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane14.setLayer(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane14.setLayer(merge_hourlogs, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane14.setLayer(import_hourlog, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane14Layout = new javax.swing.GroupLayout(jDesktopPane14);
        jDesktopPane14.setLayout(jDesktopPane14Layout);
        jDesktopPane14Layout.setHorizontalGroup(
            jDesktopPane14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane14Layout.createSequentialGroup()
                .addContainerGap(118, Short.MAX_VALUE)
                .addGroup(jDesktopPane14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(merge_hourlogs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(planner_tools_button, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jDesktopPane14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                    .addComponent(import_hourlog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(transcriptgenloader, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(hours_on_track, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(103, 103, 103))
        );
        jDesktopPane14Layout.setVerticalGroup(
            jDesktopPane14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane14Layout.createSequentialGroup()
                .addGap(183, 183, 183)
                .addGroup(jDesktopPane14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hours_on_track, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(transcriptgenloader, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(planner_tools_button, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDesktopPane14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(merge_hourlogs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(import_hourlog, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addContainerGap(129, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout more_toolsLayout = new javax.swing.GroupLayout(more_tools);
        more_tools.setLayout(more_toolsLayout);
        more_toolsLayout.setHorizontalGroup(
            more_toolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(more_toolsLayout.createSequentialGroup()
                .addComponent(jDesktopPane14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        more_toolsLayout.setVerticalGroup(
            more_toolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(more_toolsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(more_toolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDesktopPane14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TabbedPane_Main.addTab("Tools", more_tools);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1064, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
        );

        hours_settings_menu.addTab("Data", jPanel5);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1064, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
        );

        hours_settings_menu.addTab("Hours Settings", jPanel6);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(hours_settings_menu)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hours_settings_menu)
                .addContainerGap())
        );

        TabbedPane_Main.addTab("Settings", jPanel2);

        VisitSitePatreon.setBackground(new java.awt.Color(102, 0, 0));
        VisitSitePatreon.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        VisitSitePatreon.setForeground(new java.awt.Color(255, 255, 255));
        VisitSitePatreon.setText("Patreon");
        VisitSitePatreon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VisitSitePatreonActionPerformed(evt);
            }
        });
        TabbedPane_Main.addTab("Patreon", VisitSitePatreon);

        getContentPane().add(TabbedPane_Main, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1070, 490));

        jLabel2.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/libs/My project-1(1).png"))); // NOI18N
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-150, 0, 360, 50));

        jLabel9.setText("Version 1.2.8b");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 80, -1));

        jToolBar3.setRollover(true);
        getContentPane().add(jToolBar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 630, -1, -1));

        mainScrollPane.setBackground(new java.awt.Color(51, 51, 51));
        mainScrollPane.setViewportView(hourLog);
        getContentPane().add(mainScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 560, 1080, 80));

        jToolBar2.setFloatable(true);
        jToolBar2.setRollover(true);
        jToolBar2.add(jLabel21);

        jLabel19.setFont(jLabel19.getFont().deriveFont(jLabel19.getFont().getSize()+3f));
        jToolBar2.add(jLabel19);

        save_hourlog_main.setBackground(new java.awt.Color(80, 164, 171));
        save_hourlog_main.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        save_hourlog_main.setForeground(new java.awt.Color(0, 0, 0));
        save_hourlog_main.setText("ExportLog To Spreadsheet");
        save_hourlog_main.setMaximumSize(new java.awt.Dimension(175, 45));
        save_hourlog_main.setMinimumSize(new java.awt.Dimension(180, 45));
        save_hourlog_main.setPreferredSize(new java.awt.Dimension(200, 45));
        save_hourlog_main.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_hourlog_mainActionPerformed(evt);
            }
        });
        jToolBar2.add(save_hourlog_main);

        jLabel28.setText("|");
        jToolBar2.add(jLabel28);

        openHourLog.setBackground(new java.awt.Color(0, 102, 51));
        openHourLog.setFont(openHourLog.getFont().deriveFont(openHourLog.getFont().getStyle() | java.awt.Font.BOLD));
        openHourLog.setForeground(new java.awt.Color(255, 255, 255));
        openHourLog.setText("Open HourLog");
        openHourLog.setMaximumSize(new java.awt.Dimension(200, 45));
        openHourLog.setMinimumSize(new java.awt.Dimension(200, 45));
        openHourLog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openHourLogMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                openHourLogMouseEntered(evt);
            }
        });
        openHourLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openHourLogActionPerformed(evt);
            }
        });
        jToolBar2.add(openHourLog);

        jLabel25.setText("|");
        jToolBar2.add(jLabel25);

        jLabel27.setText("|");
        jToolBar2.add(jLabel27);

        backupdata_documents.setBackground(new java.awt.Color(80, 164, 171));
        backupdata_documents.setForeground(new java.awt.Color(0, 0, 0));
        backupdata_documents.setText("OneClickBackup");
        backupdata_documents.setFocusable(false);
        backupdata_documents.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        backupdata_documents.setMaximumSize(new java.awt.Dimension(97, 35));
        backupdata_documents.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        backupdata_documents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backupdata_documentsActionPerformed(evt);
            }
        });
        jToolBar2.add(backupdata_documents);

        jLabel26.setText("|");
        jToolBar2.add(jLabel26);

        planner_open_maintoolbar_link.setBackground(new java.awt.Color(0, 102, 51));
        planner_open_maintoolbar_link.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        planner_open_maintoolbar_link.setText("Launch Planner");
        planner_open_maintoolbar_link.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        planner_open_maintoolbar_link.setMaximumSize(new java.awt.Dimension(135, 35));
        planner_open_maintoolbar_link.setMinimumSize(new java.awt.Dimension(120, 35));
        planner_open_maintoolbar_link.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        planner_open_maintoolbar_link.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planner_open_maintoolbar_linkActionPerformed(evt);
            }
        });
        jToolBar2.add(planner_open_maintoolbar_link);

        getContentPane().add(jToolBar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 10, 610, 30));

        jSeparator1.setBackground(new java.awt.Color(102, 102, 102));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 1030, 10));

        jMenu1.setText("File");

        jMenuItem1.setText("Save HourLog");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Import HourLog");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        merge_menu_item.setText("Merge HourLogs - Multi File Merge");
        merge_menu_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                merge_menu_itemActionPerformed(evt);
            }
        });
        jMenu1.add(merge_menu_item);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        ss_clear.setText("Clear Students And Subjects");
        ss_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ss_clearActionPerformed(evt);
            }
        });
        jMenu2.add(ss_clear);

        jMenuBar1.add(jMenu2);

        jMenu5.setText("About");

        jMenuItem3.setText("Legal Terms & Conditions - And Info");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem3);

        jMenuItem4.setText("Report A Bug");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem4);

        aboutmenu.setText("About");
        aboutmenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutmenuActionPerformed(evt);
            }
        });
        jMenu5.add(aboutmenu);

        jMenuBar1.add(jMenu5);

        jMenu6.setText("Settings");

        options.setText("Options");
        options.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsActionPerformed(evt);
            }
        });
        jMenu6.add(options);

        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

        getAccessibleContext().setAccessibleName("V1.2.8b");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private double getDoubleValue(String input) {
        if (input != null && !input.trim().isEmpty()) {
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.err.println("Invalid input: " + input);
            }
        }
        return 0.0;
    }

    private Map<String, Map<String, Double>> coreTotals = new HashMap<>();
    private Map<String, Map<String, Double>> nonCoreTotals = new HashMap<>();

    private void updateCoreTotal(String student, String subject, String hours) {
        double coreHours = parseHours(hours);

        // Initialize the student's map if it doesn't exist
        if (!coreTotals.containsKey(student)) {
            coreTotals.put(student, new HashMap<>());
        }

        // Get the subject's map for the student
        Map<String, Double> studentTotals = coreTotals.get(student);

        // Update the total hours for the subject
        double totalHours = studentTotals.getOrDefault(subject, 0.0) + coreHours;
        studentTotals.put(subject, totalHours);

        System.out.println("Updated Core Total Hours for " + student + " - " + subject + ": " + totalHours);
        // You can perform any additional operations or update UI elements as needed
    }

    private void updateNonCoreTotal(String student, String selectedSubject, String hours) {
        double nonCoreHours = parseHours(hours);

        // Initialize the student's map if it doesn't exist
        if (!nonCoreTotals.containsKey(student)) {
            nonCoreTotals.put(student, new HashMap<>());
        }

        // Get the subject's map for the student
        Map<String, Double> studentTotals = nonCoreTotals.get(student);

        // Update the total hours for the subject
        double totalHours = studentTotals.getOrDefault(selectedSubject, 0.0) + nonCoreHours;
        studentTotals.put(selectedSubject, totalHours);

        System.out.println("Updated Non-Core Total Hours for " + student + " - " + selectedSubject + ": " + totalHours);
        // You can perform any additional operations or update UI elements as needed
    }

    private double parseDouble(String value) {
        if (value.isEmpty() || value.equals("*")) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }

    private double parseHours(String hours) {
        if (hours.equals("*")) {
            return 0.0; // Return 0 if the hours value is "*"
        } else {
            try {
                return Double.parseDouble(hours);
            } catch (NumberFormatException e) {
                System.out.println("Invalid hours format: " + hours);
                return 0.0; // Return 0 if the hours value cannot be parsed as double
            }
        }
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:

        // TODO add your handling code here:
        // Get the default table model
        DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report"); // Set the dialog title

        // Set the file filter to restrict to CSV files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        // Show the Save File dialog
        int option = fileChooser.showSaveDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Get the file path
            String filePath = selectedFile.getAbsolutePath();

            // Append .csv extension if not present
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            try {
                // Write the column headers to the CSV file
                try ( // Create a FileWriter object to write data to the CSV file
                        FileWriter writer = new FileWriter(filePath)) {
                    // Write the column headers to the CSV file
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        writer.append(model.getColumnName(i));
                        if (i < model.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");

                    // Write the data rows to the CSV file
                    for (int row = 0; row < model.getRowCount(); row++) {
                        for (int col = 0; col < model.getColumnCount(); col++) {
                            Object value = model.getValueAt(row, col);
                            if (value != null) {
                                writer.append(value.toString());
                            }
                            if (col < model.getColumnCount() - 1) {
                                writer.append(",");
                            }
                        }
                        writer.append("\n");
                    }

                    // Close the FileWriter
                    writer.flush();
                }

                // Display a success message
                JOptionPane.showMessageDialog(this, "Data exported successfully!");
            } catch (IOException ex) {
                Logger.getLogger(SSync_GUI_Form.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:

        JFileChooser fileChooser = new JFileChooser();

        // Set the file chooser to select only CSV files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getPath();

            try {
                // Create a FileReader object to read data from the CSV file
                FileReader reader = new FileReader(filePath);
                // Read the column headers from the CSV file
                try (BufferedReader br = new BufferedReader(reader)) {
                    // Read the column headers from the CSV file
                    String headerLine = br.readLine();
                    @SuppressWarnings("MismatchedReadAndWriteOfArray")
                    String[] headers = headerLine.split(",");
                    // Clear the existing data in the default table model
                    DefaultTableModel model = (DefaultTableModel) hourLog.getModel();
                    model.setRowCount(0);
                    // Read the data rows from the CSV file and add them to the table model
                    String dataLine;
                    while ((dataLine = br.readLine()) != null) {
                        String[] rowData = dataLine.split(",");
                        model.addRow(rowData);
                    }
                    // Close the FileReader
                }

                // Display a success message
                JOptionPane.showMessageDialog(this, "Data imported successfully!");

            } catch (IOException e) {
// Handle any IO exceptions
                JOptionPane.showMessageDialog(this, "Error importing data.");
            }
        }


    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private List<String> getUniqueStudents(DefaultTableModel model) {
        List<String> students = new ArrayList<>();
        int studentColumnIndex = 0; // Assuming student column is at index 0, adjust if needed

        for (int row = 0; row < model.getRowCount(); row++) {
            String student = model.getValueAt(row, studentColumnIndex).toString();
            if (!students.contains(student)) {
                students.add(student);
            }
        }

        return students;
    }

    private List<Object[]> filterRowsByStudent(DefaultTableModel model, String student) {
        List<Object[]> studentRows = new ArrayList<>();
        int studentColumnIndex = 0; // Assuming student column is at index 0, adjust if needed

        for (int row = 0; row < model.getRowCount(); row++) {
            String rowStudent = model.getValueAt(row, studentColumnIndex).toString();
            if (rowStudent.equals(student)) {
                Object[] rowData = new Object[model.getColumnCount()];
                for (int col = 0; col < model.getColumnCount(); col++) {
                    rowData[col] = model.getValueAt(row, col);
                }
                studentRows.add(rowData);
            }
        }

        return studentRows;
    }

    private Map<String, Double> combineHoursBySubject(List<Object[]> rows) {
        Map<String, Double> subjectHours = new HashMap<>();
        int subjectColumnIndex = 2; // Assuming subject column is at index 2, adjust if needed
        int hoursColumnIndex = 3; // Assuming hours column is at index 3, adjust if needed

        for (Object[] row : rows) {
            String subject = row[subjectColumnIndex].toString();
            double hours = Double.parseDouble(row[hoursColumnIndex].toString());

            subjectHours.put(subject, subjectHours.getOrDefault(subject, 0.0) + hours);
        }

        return subjectHours;
    }


    private void openHourLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openHourLogActionPerformed

        // Set the FlatMacDarkLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

// Create and configure the JFrame
        JFrame hourlogFrame = new JFrame("Hour Log Viewer");
        hourlogFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set the desired size
        int width = 900;
        int height = 610;
        hourlogFrame.setSize(width, height);

        // Set the frame's location
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (screenSize.width - hourlogFrame.getWidth()) / 2;
        int centerY = (screenSize.height - hourlogFrame.getHeight()) / 2;
        hourlogFrame.setLocation(centerX, centerY);

        // Add a WindowListener to handle the window closing event
        hourlogFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Re-dock the hourLog JTable to the mainScrollPane
                mainScrollPane.setViewportView(hourLog);
            }
        });

        // Set the layout of the JFrame
        hourlogFrame.setLayout(new BorderLayout());

        // Create a new JScrollPane
        JScrollPane scrollPane = new JScrollPane(hourLog);

        // Add the JScrollPane to the JFrame
        hourlogFrame.add(scrollPane, BorderLayout.CENTER);

        // Pack the components within the JFrame
        hourlogFrame.pack();

        // Set the JFrame to be visible
        hourlogFrame.setVisible(true);

    }//GEN-LAST:event_openHourLogActionPerformed

    private void openHourLogMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openHourLogMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_openHourLogMouseEntered

    private void openHourLogMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openHourLogMouseClicked

    }//GEN-LAST:event_openHourLogMouseClicked

    private Object[] getStudentNames() {
        List<String> studentNames = new ArrayList<>();

        try {
            // Get the user's home directory
            String userHome = System.getProperty("user.home");

            // Create a file object for the CBdat.csv file
            File file = new File(userHome + File.separator + "SSstartup" + File.separator + "CBdata.csv");

            // Create a FileReader object to read the CSV file
            FileReader reader = new FileReader(file);

            // Create a CSVReader object to parse the CSV file
            CSVReader csvReader = new CSVReader(reader);

            // Read the CSV file line by line
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length > 0) {
                    // Add the value from the first column to the studentNames list
                    studentNames.add(nextLine[0]);
                }
            }

            // Close the CSVReader and FileReader
            csvReader.close();
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Convert the studentNames list to an array and return it
        return studentNames.toArray();
    }

    private class AutoSaveTask extends TimerTask {

        @Override
        public void run() {
            // Perform the auto-save operation
            String userHomeDirectory = System.getProperty("user.home");
            String filePath = userHomeDirectory + File.separator + "SSstartup" + File.separator + "SSHourlog.csv";

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                // Write the data to the file
                DefaultTableModel model = (DefaultTableModel) hourLog.getModel();
                int rowCount = model.getRowCount();

                // Write the headers
                StringBuilder header = new StringBuilder();
                for (int column = 0; column < model.getColumnCount(); column++) {
                    header.append(model.getColumnName(column));
                    if (column < model.getColumnCount() - 1) {
                        header.append(",");
                    }
                }
                bw.write(header.toString());
                bw.newLine();

                // Write the data rows
                for (int row = 0; row < rowCount; row++) {
                    StringBuilder rowData = new StringBuilder();
                    for (int column = 0; column < model.getColumnCount(); column++) {
                        Object value = model.getValueAt(row, column);
                        rowData.append(value != null ? value.toString() : "");
                        if (column < model.getColumnCount() - 1) {
                            rowData.append(",");
                        }
                    }
                    bw.write(rowData.toString());
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private int getWeekNumber(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }

    public List<String> readStudentListFromCSV() {
        List<String> studentList = new ArrayList<>();

        String homeDir = System.getProperty("user.home");
        String csvFilePath = Paths.get(homeDir, "SSstartup", "CBdata.csv").toString();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String student = values[0].trim(); // Assuming student name is in the first column
                if (!student.isEmpty()) {
                    studentList.add(student);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return studentList;
    }

    class CheckBoxListCellRenderer extends JCheckBox implements ListCellRenderer<String> {

        @Override
        public java.awt.Component getListCellRendererComponent(JList<? extends String> list, String value,
                int index, boolean isSelected, boolean cellHasFocus) {
            setText(value);
            setSelected(isSelected);
            setFont(list.getFont());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            return this;
        }
    }

    JDateChooser fromDChooser = new JDateChooser();
    JDateChooser toDChooser = new JDateChooser();

    private double parseDoubleValue(String value) {
        double result = 0.0;
        try {
            result = Double.parseDouble(value); // Parse the value as a double
        } catch (NumberFormatException e) {
            // Handle any parsing errors
        }
        return result;
    }


    private void save_hourlog_mainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_hourlog_mainActionPerformed
        // Get the default table model
        DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

        // Show a dialog to get the export date from the user
        String exportDate = JOptionPane.showInputDialog(this, "Enter the export date (YYYY-MM-DD):");

        if (exportDate != null && !exportDate.isEmpty()) {
            // Parse the export date
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate;
            try {
                parsedDate = dateFormat.parse(exportDate);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please enter date in the format (YYYY-MM-DD).");
                return;
            }

            // Get the current week number
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);

            // Create a file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Report"); // Set the dialog title

            // Set the file filter to restrict to CSV files
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);

            // Show the Save File dialog
            int option = fileChooser.showSaveDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                // Get the directory path
                String directoryPath = selectedFile.getParent();

                // Append the export date and week number to the file name
                String fileName = "HourLog_" + exportDate + "_Week" + weekNumber + ".csv";
                String filePath = directoryPath + File.separator + fileName;

                try {
                    // Create a FileWriter object to write data to the CSV file
                    FileWriter writer = new FileWriter(filePath);

                    // Write the column headers to the CSV file
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        writer.append(model.getColumnName(i));
                        if (i < model.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");

                    // Write the data rows to the CSV file
                    for (int row = 0; row < model.getRowCount(); row++) {
                        for (int col = 0; col < model.getColumnCount(); col++) {
                            Object value = model.getValueAt(row, col);
                            if (value != null) {
                                writer.append(value.toString());
                            }
                            if (col < model.getColumnCount() - 1) {
                                writer.append(",");
                            }
                        }
                        writer.append("\n");
                    }

                    // Close the FileWriter
                    writer.close();

                    // Display a success message
                    JOptionPane.showMessageDialog(this, "Data exported successfully!");
                } catch (IOException ex) {
                    Logger.getLogger(SSync_GUI_Form.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_save_hourlog_mainActionPerformed

    private void ss_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ss_clearActionPerformed
        // TODO add your handling code here:

        // Get the user's home directory
        String userHome = System.getProperty("user.home");

        // Specify the path of the CSV file
        String filePath = userHome + File.separator + "SSstartup" + File.separator + "CBData.csv";

        // Display a confirmation dialog
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all subject and student data?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            // Ask the user if they want to make a backup
            int backupChoice = JOptionPane.showConfirmDialog(this, "Would you like to create a backup of the CSV file?", "Backup Confirmation", JOptionPane.YES_NO_OPTION);
            if (backupChoice == JOptionPane.YES_OPTION) {
                // Create a file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Backup File");

                // Set the default file name
                String defaultFileName = "CBData_Backup.csv";
                File defaultFile = new File(defaultFileName);
                fileChooser.setSelectedFile(defaultFile);

                // Show the file chooser dialog
                int saveChoice = fileChooser.showSaveDialog(this);
                if (saveChoice == JFileChooser.APPROVE_OPTION) {
                    // Get the selected file
                    File backupFile = fileChooser.getSelectedFile();
                    String backupFilePath = backupFile.getAbsolutePath();

                    // Copy the original CSV file to the backup file
                    try {
                        Files.copy(Paths.get(filePath), Paths.get(backupFilePath), StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(this, "Backup created successfully!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Error creating backup: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            // Clear the CSV file
            try {
                // Create a new FileWriter object with the append parameter set to false
                FileWriter fileWriter = new FileWriter(filePath, false);

                // Write an empty string to the file to clear its contents
                fileWriter.write("");

                // Close the FileWriter
                fileWriter.close();

                JOptionPane.showMessageDialog(this, "CSV file cleared successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error clearing CSV file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_ss_clearActionPerformed


    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        String message = "SchoolSync Documentation \n"
                + "\n"
                + "Introduction\n"
                + "\n"
                + "SchoolSync is a locally installed planning and hour tracking software created by James Knox and the Sanity Saving Software Team. It is specifically designed to assist parents in tracking and planning homeschooling activities and documenting logged hours. The software aims to provide an intuitive user interface and essential features to enhance homeschooling organization and efficiency.\n"
                + "\n"
                + "Features\n"
                + "1. HourLog\n"
                + "The HourLog application in SchoolSync allows parents to track and document the hours spent on various homeschooling activities. Parents can create categories, log hours, and generate reports to keep a record of the time spent on different subjects or tasks.\n"
                + "\n"
                + "2. Planner\n"
                + "The Planner application helps parents plan homeschooling activities, tasks, and events. Parents can create and manage tasks, set due dates, and organize their homeschooling schedule effectively.\n"
                + "\n"
                + "Getting Started\n"
                + "System Requirements\n"
                + "To use SchoolSync, ensure that your system meets the following requirements:\n"
                + "\n"
                + "Operating System: Windows 10, macOS 10.13 or later\n"
                + "Local Installer: The SchoolSync installer is available exclusively to Patreon subscribers at the moment.\n"
                + "Installation and Setup\n"
                + "To install SchoolSync, download the local installer provided through Patreon as a subscriber. Run the installer on your computer and follow the on-screen instructions to complete the installation process. Once installed, you can launch SchoolSync from your desktop or start menu.\n"
                + "\n"
                + "User Roles and Permissions\n"
                + "SchoolSync offers different user roles with specific permissions:\n"
                + "\n"
                + "Parent: Parents have full control over the software. They can plan homeschooling activities, create tasks, log hours, and generate reports.\n"
                + "Support and Troubleshooting\n"
                + "Help and Support\n"
                + "Support for SchoolSync is currently available exclusively to Patreon subscribers. Patreon subscribers can reach out for assistance and inquiries regarding SchoolSync by contacting our support team at [support email]. Our support staff will provide guidance and address any technical issues Patreon subscribers may encounter.\n"
                + "\n"
                + "Bug Reporting\n"
                + "If you come across any software bugs or issues while using SchoolSync, please report them by sending an email to contact@jamesnox.com. Provide detailed information about the problem, including steps to reproduce it. Our development team will investigate the issues and work towards resolving them.\n"
                + "\n"
                + "Frequently Asked Questions (FAQ)\n"
                + "We are currently working on creating a comprehensive FAQ section to address common concerns and provide solutions for SchoolSync. Please stay tuned for updates on our website for the availability of the FAQ section.\n"
                + "\n"
                + "Conclusion\n"
                + "SchoolSync, created by James Knox and the Sanity Saving Software Team, is a powerful local planning and hour tracking software designed to assist parents in homeschooling organization. With its HourLog and Planner applications, parents can efficiently track and plan homeschooling activities, log hours, and manage their homeschooling schedule effectively. SchoolSync aims to provide an intuitive and user-friendly experience for homeschooling parents.\n"
                + "\n"
                + "Please note that SchoolSync is not an online software or service. It is a locally installed software intended for individual use on supported operating systems.\n"
                + "\n"
                + "Thank you for choosing SchoolSync to simplify your homeschooling journey!\n"
                + "\n"
                + "Note: This documentation reflects the current functionality of the SchoolSync software as of its latest release.\n"
                + "\n"
                + "\n"
                + "Ongoing Development and Updates\n"
                + "At SchoolSync, we are committed to continuously improving our software and providing the best experience for our users. Our development team is dedicated to enhancing SchoolSync by adding new features, tools, and updates on a weekly basis. We strive to stay at the forefront of homeschooling organization and provide innovative solutions to meet the evolving needs of our users.\n"
                + "\n"
                + "Here's what you can expect from our ongoing development:\n"
                + "\n"
                + "Software Updates\n"
                + "We release regular software updates to address bug fixes, performance enhancements, and compatibility improvements. These updates ensure that SchoolSync remains stable, reliable, and compatible with the latest operating systems and technologies.\n"
                + "\n"
                + "New Features\n"
                + "We are constantly working on developing new features to enrich the SchoolSync experience. Our team carefully evaluates user feedback and suggestions to prioritize feature development. Keep an eye out for exciting additions that will further enhance your homeschooling planning and hour tracking capabilities.\n"
                + "\n"
                + "Tools and Enhancements\n"
                + "In addition to new features, we are dedicated to introducing tools and enhancements that streamline and simplify your homeschooling journey. These tools may include additional reporting options, customizable settings, improved user interface, and more. Our goal is to provide you with the tools you need to make homeschooling planning and tracking effortless.\n"
                + "\n"
                + "We value your input and encourage you to share your ideas and suggestions for future updates. Your feedback plays a vital role in shaping the direction of SchoolSync's development and ensuring that it meets the specific needs of homeschooling parents.\n"
                + "\n"
                + "Please note that new updates, features, and tools may require a software update or installation. We will provide clear instructions on how to access and implement these updates when they become available.\n"
                + "\n"
                + "Thank you for choosing SchoolSync as your homeschooling planning and hour tracking software. We look forward to continually improving and enhancing your experience with each update.\n"
                + "\n"
                + "Note: The frequency and availability of updates, features, and tools may vary based on our development schedule and priorities.\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "Legal -- \n"
                + "\n"
                + "\n"
                + "Included in the following text are \n"
                + "\n"
                + "-Terms and Conditions of SchoolSync Home Schooling Productivity Software\n"
                + "-How to Submit a Bug Report to contact@jamesnox.com\n"
                + "-Copyright Statement for SchoolSync and Sanity Saving Software\n"
                + "-Data Privacy Statement for SchoolSync and Sanity Saving Software\n"
                + "\n"
                + "-----Please note that this table of contents provides an overview of the statements covered. For detailed information, please refer to the respective sections above.\n"
                + "\n"
                + "----------------------------------------------------------------------------------------------------------------------------\n"
                + "How to Submit a Bug Report to contact@jamesnox.com\n"
                + "\n"
                + "If you have encountered a bug while using our software or website, we appreciate your effort in helping us improve our services. To submit a bug report to contact@jamesnox.com, please follow these steps:\n"
                + "\n"
                + "1. Provide a Descriptive Title: Start by giving your bug report a clear and concise title that summarizes the issue you are experiencing.\n"
                + "\n"
                + "2. Describe the Bug: In the body of the email, provide a detailed description of the bug you encountered. Include specific steps to reproduce the issue, along with any relevant information such as error messages or unexpected behavior.\n"
                + "\n"
                + "3. Include Screenshots or Videos: Whenever possible, attach screenshots or record a video demonstrating the bug. Visual aids can greatly assist our team in understanding the problem.\n"
                + "\n"
                + "4. Specify Software/Website Version: Mention the version number of the software or website you are using. This helps us identify if the bug is specific to a particular release.\n"
                + "\n"
                + "5. Include System Information: Provide details about your operating system, browser (if applicable), and any other relevant software or hardware configurations. This information can help us pinpoint potential compatibility issues.\n"
                + "\n"
                + "6. Attach Log Files (if available): If there are any log files associated with the bug, please attach them to the email. Log files often contain valuable information that can aid in diagnosing and resolving the issue.\n"
                + "\n"
                + "7. Reproducibility: Indicate whether the bug occurs consistently or intermittently. If it is intermittent, provide any patterns or specific conditions that may trigger the bug.\n"
                + "\n"
                + "8. Contact Information: Include your name and email address in the bug report so that we can reach out to you for further clarification or updates, if necessary.\n"
                + "\n"
                + "9. Send the Bug Report: Once you have compiled all the necessary information, send the bug report to contact@jamesnox.com. We appreciate your contribution to improving our software and website.\n"
                + "\n"
                + "Thank you for taking the time to report the bug. Your feedback is invaluable in helping us deliver a better user experience.\n"
                + "--------------------------------------------------------------------------------------------------------------------------------\n"
                + "Copyright Statement for SchoolSync and Sanity Saving Software:\n"
                + "\n"
                + "All content and software included in SchoolSync and Sanity Saving Software, including but not limited to text, graphics, logos, button icons, images, audio clips, digital downloads, data compilations, and software, are the property of SchoolSync and Sanity Saving Software or its licensors and are protected by copyright laws.\n"
                + "\n"
                + "The compilation of all content and software on SchoolSync and Sanity Saving Software is the exclusive property of SchoolSync and Sanity Saving Software and is protected by copyright laws.\n"
                + "\n"
                + "Any reproduction, modification, distribution, transmission, republication, display, or performance of the content and software on SchoolSync and Sanity Saving Software is strictly prohibited unless explicitly authorized by SchoolSync and Sanity Saving Software.\n"
                + "\n"
                + "All trademarks, service marks, and trade names appearing on SchoolSync and Sanity Saving Software are the property of their respective owners.\n"
                + "\n"
                + "SchoolSync and Sanity Saving Software respect the intellectual property rights of others. If you believe that your work has been copied in a way that constitutes copyright infringement, please contact us immediately at [insert contact information] with the following information:\n"
                + "\n"
                + "A description of the copyrighted work that you claim has been infringed.\n"
                + "The location of the allegedly infringing material on SchoolSync and Sanity Saving Software.\n"
                + "Your contact information, including your name, address, telephone number, and email address.\n"
                + "A statement by you that you have a good faith belief that the disputed use is not authorized by the copyright owner, its agent, or the law.\n"
                + "A statement by you, made under penalty of perjury, that the above information in your notice is accurate and that you are the copyright owner or authorized to act on the copyright owner's behalf.\n"
                + "We will investigate any allegations of copyright infringement and take appropriate action as required by law.\n"
                + "\n"
                + "Please note that this copyright statement may be subject to change without prior notice.\n"
                + "\n"
                + "-------------------------------------------------------------------------------------------------------------------------------\n"
                + "\n"
                + "Data Privacy Statement for SchoolSync and Sanity Saving Software:\n"
                + "\n"
                + "At SchoolSync and Sanity Saving Software, we are committed to protecting your privacy and safeguarding your personal data. This Data Privacy Statement outlines how we collect, use, and protect your information when you use our software and services.\n"
                + "\n"
                + "1. Information We Collect:\n"
                + "   - Personal Information: When you use our software, we may collect personal information such as your name, email address, and contact details. This information is voluntarily provided by you during the registration or account creation process.\n"
                + "   - Usage Data: We may also collect non-personal information about your usage of the software, including but not limited to device information, IP addresses, browser type, and operating system.\n"
                + "\n"
                + "2. Use of Information:\n"
                + "   - We use the personal information you provide to create and manage your account, provide customer support, and communicate important updates or notifications.\n"
                + "   - Non-personal information is collected to improve and optimize our software, understand user preferences, and enhance the overall user experience.\n"
                + "\n"
                + "3. Data Security:\n"
                + "   - We implement strict security measures to protect your personal data from unauthorized access, alteration, or disclosure. We use industry-standard encryption technologies and regularly update our security protocols to ensure the safety of your information.\n"
                + "\n"
                + "4. Data Sharing:\n"
                + "   - We do not sell, rent, or trade your personal information to third parties for marketing purposes.\n"
                + "   - We may share your information with trusted third-party service providers who assist us in delivering our services. These service providers are contractually obligated to maintain the confidentiality and security of your data.\n"
                + "   - We may disclose your information if required by law or to protect our legal rights, enforce our Terms of Service, or respond to a court order or legal request.\n"
                + "\n"
                + "5. Data Retention:\n"
                + "   - We retain your personal data only for as long as necessary to fulfill the purposes outlined in this Data Privacy Statement, or as required by applicable laws and regulations.\n"
                + "   - Upon your request, we will delete or anonymize your personal data, unless we are legally obligated to retain it.\n"
                + "\n"
                + "6. Third-Party Links:\n"
                + "   - Our software may contain links to third-party websites or services. Please note that this Data Privacy Statement does not apply to those third-party sites, and we encourage you to review their respective privacy policies.\n"
                + "\n"
                + "7. Updates to Privacy Statement:\n"
                + "   - We may update this Data Privacy Statement from time to time to reflect changes in our practices or legal obligations. We will notify you of any significant updates by posting a prominent notice on our website or through other appropriate channels.\n"
                + "\n"
                + "If you have any questions or concerns regarding your privacy or the protection of your personal data, please contact us at [insert contact information]. We are committed to addressing any issues promptly and ensuring the security and confidentiality of your information.\n"
                + "\n"
                + "By using SchoolSync and Sanity Saving Software, you acknowledge that you have read and understood this Data Privacy Statement and consent to the collection, use, and protection of your personal data as described herein.\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "--------------------------------------------------------------------------------------------------------------------------------\n"
                + "Terms and Conditions of SchoolSync Home Schooling Productivity Software\n"
                + "\n"
                + "Please read these terms and conditions carefully before using SchoolSync home schooling productivity software. By accessing or using the software, you agree to be bound by these terms and conditions.\n"
                + "\n"
                + "1. License: SchoolSync grants you a non-exclusive, non-transferable license to use the software for personal and non-commercial purposes. This license is subject to your compliance with these terms and conditions.\n"
                + "\n"
                + "2. Intellectual Property: The software and all related intellectual property rights are owned by SchoolSync. You acknowledge that the software contains proprietary information and trade secrets. You agree not to modify, copy, distribute, transmit, display, perform, reproduce, publish, license, create derivative works from, transfer, or sell any information, software, products, or services obtained from or through SchoolSync.\n"
                + "\n"
                + "3. User Responsibilities: You are responsible for maintaining the confidentiality of your account and password. You agree to provide accurate and complete information when using the software. You must not use the software for any unlawful or unauthorized purpose.\n"
                + "\n"
                + "4. Data Privacy: SchoolSync collects and processes personal data in accordance with its Privacy Policy. By using the software, you consent to the collection and processing of your personal data as described in the Privacy Policy.\n"
                + "\n"
                + "5. Limitation of Liability: SchoolSync shall not be liable for any direct, indirect, incidental, special, consequential, or exemplary damages, including but not limited to damages for loss of profits, goodwill, use, data, or other intangible losses, resulting from your use of the software or any interruptions or errors in the software.\n"
                + "\n"
                + "6. Termination: SchoolSync may terminate or suspend your access to the software at any time, without notice or liability, for any reason. Upon termination, you must cease all use of the software.\n"
                + "\n"
                + "7. Modifications: SchoolSync reserves the right to modify, suspend, or discontinue the software at any time without prior notice. SchoolSync shall not be liable to you or any third party for any modifications, suspensions, or discontinuations of the software.\n"
                + "\n"
                + "8. Governing Law: These terms and conditions shall be governed by and construed in accordance with the laws of [Jurisdiction]. Any dispute arising out of or relating to these terms and conditions shall be subject to the exclusive jurisdiction of the courts of [Jurisdiction].\n"
                + "\n"
                + "By using SchoolSync home schooling productivity software, you acknowledge that you have read, understood, and agreed to these terms and conditions. If you do not agree to these terms and conditions, you must not use the software.\n"
                + "\n";

        JTextPane textPane = new JTextPane();
        textPane.setText(message);
        textPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(450, 500));

        JOptionPane.showMessageDialog(null, scrollPane, "Popup Text Pane", JOptionPane.PLAIN_MESSAGE);

    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void merge_menu_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_merge_menu_itemActionPerformed
        // TODO add your handling code here:

        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV Files to Append"); // Set the dialog title

        // Set the file filter to restrict to CSV files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(true); // Allow multiple file selection

        // Show the Open File dialog
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();

            // Get the existing table model
            DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

            // Iterate through the selected files
            for (File file : selectedFiles) {
                String fileName = file.getName();
                int index = fileName.lastIndexOf("_Week");

                if (index != -1) {
                    try {
                        // Read the contents of the selected file
                        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

                        // Add the data rows to the table model
                        for (int i = 1; i < lines.size(); i++) {
                            String dataLine = lines.get(i);
                            String[] rowData = dataLine.split(",");
                            model.addRow(rowData);
                        }
                    } catch (IOException e) {
                        // Handle any exceptions during file reading
                        e.printStackTrace();
                    }
                }
            }

            // Notify the table model that the data has changed
            model.fireTableDataChanged();

            // Display a success message
            JOptionPane.showMessageDialog(this, "Data appended successfully!");
        }

    }//GEN-LAST:event_merge_menu_itemActionPerformed

    private void houlogframe_exportspreadsheetbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_houlogframe_exportspreadsheetbuttonActionPerformed
        // TODO add your handling code here:
        // Get the default table model
        DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

        // Show a dialog to get the export date from the user
        String exportDate = JOptionPane.showInputDialog(this, "Enter the export date (YYYY-MM-DD):");

        if (exportDate != null && !exportDate.isEmpty()) {
            // Parse the export date
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate;
            try {
                parsedDate = dateFormat.parse(exportDate);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please enter date in the format (YYYY-MM-DD).");
                return;
            }

            // Get the current week number
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);

            // Create a file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Report"); // Set the dialog title

            // Set the file filter to restrict to CSV files
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);

            // Show the Save File dialog
            int option = fileChooser.showSaveDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                // Get the directory path
                String directoryPath = selectedFile.getParent();

                // Append the export date and week number to the file name
                String fileName = "HourLog_" + exportDate + "_Week" + weekNumber + ".csv";
                String filePath = directoryPath + File.separator + fileName;

                try {
                    // Create a FileWriter object to write data to the CSV file
                    FileWriter writer = new FileWriter(filePath);

                    // Write the column headers to the CSV file
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        writer.append(model.getColumnName(i));
                        if (i < model.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");

                    // Write the data rows to the CSV file
                    for (int row = 0; row < model.getRowCount(); row++) {
                        for (int col = 0; col < model.getColumnCount(); col++) {
                            Object value = model.getValueAt(row, col);
                            if (value != null) {
                                writer.append(value.toString());
                            }
                            if (col < model.getColumnCount() - 1) {
                                writer.append(",");
                            }
                        }
                        writer.append("\n");
                    }

                    // Close the FileWriter
                    writer.close();

                    // Display a success message
                    JOptionPane.showMessageDialog(this, "Data exported successfully!");
                } catch (IOException ex) {
                    Logger.getLogger(SSync_GUI_Form.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_houlogframe_exportspreadsheetbuttonActionPerformed

    private Map<String, Double> studentGoalHours;


    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void aboutmenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutmenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_aboutmenuActionPerformed

    private void optionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsActionPerformed
        // TODO add your handling code here:

        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("My Application");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);

        // Create an instance of the SettingsPanel
        SettingsPanel settingsPanel = new SettingsPanel();

        // Add the SettingsPanel to the main frame
        frame.getContentPane().add(settingsPanel);

        // Display the settings panel
        settingsPanel.setVisible(true);

        // Retrieve the saved name from the SettingsPanel
        String savedName = settingsPanel.getSavedName();

        // Use the saved name for custom welcome messages or hints
        if (savedName != null && !savedName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Welcome, " + savedName + "!");
        }

        frame.setVisible(true);


    }//GEN-LAST:event_optionsActionPerformed

    private void planner_open_maintoolbar_linkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planner_open_maintoolbar_linkActionPerformed
        // TODO add your handling code here:
        try {
            // Set the FlatLaf Dark Look and Feel
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        // Create an instance of the planner_gui_form class
        planner_gui_form plannerForm = new planner_gui_form();

        // Set the visibility of the planner_gui_form
        plannerForm.setVisible(true);
    }//GEN-LAST:event_planner_open_maintoolbar_linkActionPerformed

    private JDateChooser dateChooser;

    private void backupdata_documentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backupdata_documentsActionPerformed
        // TODO add your handling code here:
        // Get the user's home directory
        String userHome = System.getProperty("user.home");

        // Define the source files to be backed up
        File[] sourceFiles = {
            new File(userHome + File.separator + "SSstartup", "plannerdata.csv"),
            new File(userHome + File.separator + "SSstartup", "SShourlog.csv"),
            new File(userHome + File.separator + "SSstartup", "CBdata.csv")
        };

        // Define the destination directory path
        String destinationDirectory = userHome + File.separator + "Documents" + File.separator + "SanitysavingSoftware" + File.separator + "backup_data";

        // Create the destination directory if it doesn't exist
        File destinationDir = new File(destinationDirectory);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        // Perform the backup of each file
        for (File sourceFile : sourceFiles) {
            if (sourceFile.exists()) {
                // Create a new file in the destination directory with the same name as the source file
                File destinationFile = new File(destinationDir, sourceFile.getName());

                if (destinationFile.exists()) {
                    // Ask the user if they want to overwrite the existing file
                    int option = JOptionPane.showConfirmDialog(this, "The file " + destinationFile.getName() + " already exists. Do you want to overwrite it?", "Overwrite Confirmation", JOptionPane.YES_NO_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        try {
                            // Copy the source file to the destination file, overwriting the existing file
                            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            // Display a success message to the user
                            System.out.println("File backup successful: " + destinationFile.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                            // Display an error message to the user
                        }
                    } else {
                        // Display a message that the file was not overwritten
                        System.out.println("File backup skipped: " + destinationFile.getAbsolutePath());
                    }
                } else {
                    try {
                        // Copy the source file to the destination file
                        Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        // Display a success message to the user
                        System.out.println("File backup successful: " + destinationFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Display an error message to the user
                    }
                }
            } else {
                // Display a message if the source file doesn't exist
                System.out.println("Source file not found: " + sourceFile.getAbsolutePath());
            }
        }

        // Launch the file explorer in the destination directory
        try {
            Desktop.getDesktop().open(destinationDir);
        } catch (IOException e) {
            e.printStackTrace();
            // Display an error message to the user
        }

        // Display a final success message to the user
        System.out.println("Backup process completed!");


    }//GEN-LAST:event_backupdata_documentsActionPerformed

    private void VisitSitePatreonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VisitSitePatreonActionPerformed
        // TODO add your handling code here:
        String url = "https://www.patreon.com/NoxSoftware"; // Replace with the desired website URL

        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (java.io.IOException e) {
            // Handle any exceptions

        }
    }//GEN-LAST:event_VisitSitePatreonActionPerformed

    private void import_hourlogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_import_hourlogActionPerformed
        // TODO add your handling code here:
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV Files to Append"); // Set the dialog title

        // Set the file filter to restrict to CSV files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(true); // Allow multiple file selection

        // Show the Open File dialog
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();

            // Get the default table model
            DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

            for (File file : selectedFiles) {
                String fileName = file.getName();
                int index = fileName.lastIndexOf("_Week");

                if (index != -1) {
                    try {
                        // Read the contents of the selected file
                        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

                        // Add the contents to the hourLog table model, skipping the header row
                        for (int i = 1; i < lines.size(); i++) {
                            String line = lines.get(i);
                            String[] values = line.split(",");
                            model.addRow(values);
                        }
                    } catch (IOException e) {
                        // Handle any exceptions during file reading
                        e.printStackTrace();
                    }
                }
            }

            // Display a success message
            JOptionPane.showMessageDialog(this, "Data appended successfully!");
        }

    }//GEN-LAST:event_import_hourlogActionPerformed

    private void merge_hourlogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_merge_hourlogsActionPerformed

        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV Files to Append"); // Set the dialog title

        // Set the file filter to restrict to CSV files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(true); // Allow multiple file selection

        // Show the Open File dialog
        int option = fileChooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();

            // Get the existing table model
            DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

            // Iterate through the selected files
            for (File file : selectedFiles) {
                String fileName = file.getName();
                int index = fileName.lastIndexOf("_Week");

                if (index != -1) {
                    try {
                        // Read the contents of the selected file
                        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

                        // Add the data rows to the table model
                        for (int i = 1; i < lines.size(); i++) {
                            String dataLine = lines.get(i);
                            String[] rowData = dataLine.split(",");
                            model.addRow(rowData);
                        }
                    } catch (IOException e) {
                        // Handle any exceptions during file reading
                        e.printStackTrace();
                    }
                }
            }

            // Notify the table model that the data has changed
            model.fireTableDataChanged();

            // Display a success message
            JOptionPane.showMessageDialog(this, "Data appended successfully!");
        }

    }//GEN-LAST:event_merge_hourlogsActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        calApp calculator = new calApp();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void transcriptgenloaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transcriptgenloaderActionPerformed
        loadHTMLFile("transcript.html");
        }

        private void loadHTMLFile(String fileName) {
            try {
                // Get the URL of the HTML file
                URL url = getClass().getResource(fileName);

                // Create a Desktop object to open the HTML file
                Desktop desktop = Desktop.getDesktop();

                if (url != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(url.toURI());
                } else {
                    System.out.println("Failed to open HTML file: " + fileName);
                }
            } catch (IOException | URISyntaxException e) {
            }

    }//GEN-LAST:event_transcriptgenloaderActionPerformed

    private void hours_on_trackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hours_on_trackActionPerformed

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Read student list from CBdata.csv
        List<String> studentList = readStudentListFromCSV();

        // Create a panel for student selection
        JPanel studentPanel = new JPanel();
        studentPanel.setLayout(new BorderLayout());

        JLabel studentLabel = new JLabel("Select Student(s):");
        studentLabel.setFont(studentLabel.getFont().deriveFont(Font.BOLD));

        JCheckBox selectAllCheckbox = new JCheckBox("All Students");
        selectAllCheckbox.addItemListener(e -> {
            boolean selected = selectAllCheckbox.isSelected();
            for (Component component : studentPanel.getComponents()) {
                if (component instanceof JCheckBox && component != selectAllCheckbox) {
                    ((JCheckBox) component).setSelected(selected);
                }
            }
        });

        JPanel checkboxPanel = new JPanel(new GridLayout(0, 1));
        checkboxPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String student : studentList) {
            JCheckBox checkBox = new JCheckBox(student);
            checkboxPanel.add(checkBox);
        }

        studentPanel.add(studentLabel, BorderLayout.NORTH);
        studentPanel.add(new JScrollPane(checkboxPanel), BorderLayout.CENTER);
        studentPanel.add(selectAllCheckbox, BorderLayout.SOUTH);

        // Create a panel for date range selection
        JPanel dateRangePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        dateRangePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel fromLabel = new JLabel("From Date:");
        JLabel toLabel = new JLabel("To Date:");
        JDateChooser fromDChooser = new JDateChooser();
        JDateChooser toDChooser = new JDateChooser();

        dateRangePanel.add(fromLabel);
        dateRangePanel.add(fromDChooser);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(toDChooser);

        // Create a panel for the main options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BorderLayout());
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        optionsPanel.add(studentPanel, BorderLayout.CENTER);
        optionsPanel.add(dateRangePanel, BorderLayout.SOUTH);

        // Show the student selection dialog
        int studentOption = JOptionPane.showConfirmDialog(this, optionsPanel, "Select Students", JOptionPane.OK_CANCEL_OPTION);
        if (studentOption == JOptionPane.CANCEL_OPTION) {
            return;
        }

        // Retrieve selected students
        List<String> selectedStudents = new ArrayList<>();
        for (Component component : checkboxPanel.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                    selectedStudents.add(checkBox.getText());
                }
            }
        }

        // Show the date range selection dialog
        int dateOption = JOptionPane.showConfirmDialog(this, dateRangePanel, "Select Date Range", JOptionPane.OK_CANCEL_OPTION);
        if (dateOption == JOptionPane.CANCEL_OPTION) {
            return;
        }

        Date fromDate = fromDChooser.getDate();
        Date toDate = toDChooser.getDate();

        if (selectedStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one student.");
            return;
        }

        if (fromDate == null || toDate == null || fromDate.after(toDate)) {
            JOptionPane.showMessageDialog(this, "Invalid date range selected.");
            return;
        }

        // Get user input for total yearly core hours, non-core hours, and at-home core hours needed
        double yearlyCoreHours = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter total yearly core hours:"));
        double nonCoreHours = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter non-core hours:"));
        double atHomeCoreNeeded = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter at-home core hours needed:"));

        // Calculate remaining core hours and at-home core hours
        double remainingCoreHours = yearlyCoreHours - nonCoreHours;
        double remainingAtHomeCoreHours = atHomeCoreNeeded - nonCoreHours;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save HTML Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();

                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".html")) {
                    fileToSave = new File(filePath + ".html");
                }

                DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false); // Ensure strict date parsing

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                StringBuilder htmlBuilder = new StringBuilder();
                htmlBuilder.append("<html><head><style>\n");
                htmlBuilder.append("table {\n");
                htmlBuilder.append("    border-collapse: collapse;\n");
                htmlBuilder.append("    width: 100%;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append("th, td {\n");
                htmlBuilder.append("    text-align: left;\n");
                htmlBuilder.append("    padding: 8px;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append("th {\n");
                htmlBuilder.append("    background-color: #006633;\n");
                htmlBuilder.append("    color: white;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append(".subject-total {\n");
                htmlBuilder.append("    color: blue;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append(".divider-line {\n");
                htmlBuilder.append("    border-top: 1px solid black;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append("</style>");

                // Generate JavaScript code for the pie chart
                htmlBuilder.append("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>");
                htmlBuilder.append("<script>");
                htmlBuilder.append("document.addEventListener('DOMContentLoaded', function() {");
                htmlBuilder.append("var ctx = document.getElementById('chartCanvas').getContext('2d');");
                htmlBuilder.append("var data = {");
                htmlBuilder.append("labels: ['Core Hours', 'Non-Core Hours', 'Remaining Core Hours'],");
                htmlBuilder.append("datasets: [{");
                htmlBuilder.append("data: [").append(decimalFormat.format(yearlyCoreHours)).append(", ")
                .append(decimalFormat.format(nonCoreHours)).append(", ")
                .append(decimalFormat.format(remainingCoreHours)).append("],");
                htmlBuilder.append("backgroundColor: ['rgb(255, 99, 132)', 'rgb(54, 162, 235)', 'rgb(75, 192, 192)']");
                htmlBuilder.append("}]");
                htmlBuilder.append("};");
                htmlBuilder.append("var options = {");
                htmlBuilder.append("plugins: {");
                htmlBuilder.append("legend: {");
                htmlBuilder.append("position: 'right'");
                htmlBuilder.append("}");
                htmlBuilder.append("}");
                htmlBuilder.append("};");
                htmlBuilder.append("var chart = new Chart(ctx, {");
                htmlBuilder.append("type: 'pie',");
                htmlBuilder.append("data: data,");
                htmlBuilder.append("options: options");
                htmlBuilder.append("});");
                htmlBuilder.append("});");
                htmlBuilder.append("</script></head><body>\n");
                htmlBuilder.append("<h1>Hour Log Report</h1>\n");
                htmlBuilder.append("<canvas id=\"chartCanvas\" width=\"200\" height=\"200\"></canvas>");
                htmlBuilder.append("<button onclick=\"window.print()\">Print</button>\n");

                for (String student : selectedStudents) {
                    htmlBuilder.append("<h2>").append(student).append("</h2>\n");
                    htmlBuilder.append("<div style=\"margin-bottom: 20px;\"></div>\n");

                    double totalCoreHours = 0.0;
                    double totalNonCoreHours = 0.0;
                    double totalAtHomeHours = 0.0;
                    double totalCoreAtHomeHours = 0.0;

                    Map<String, Double> subjectHoursMap = new HashMap<>();

                    htmlBuilder.append("<table>\n");
                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<th>Date</th>\n");
                    htmlBuilder.append("<th>Subject</th>\n");
                    htmlBuilder.append("<th>Core Hours</th>\n");
                    htmlBuilder.append("<th>|At Home Core|</th>\n");
                    htmlBuilder.append("<th>Non-Core Hours</th>\n");
                    htmlBuilder.append("</tr>\n");
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String studentName = model.getValueAt(i, 1).toString();
                        String dateString = model.getValueAt(i, 0).toString();

                        if (student.equals(studentName)) {
                            try {
                                Date entryDate = sdf.parse(dateString);

                                // Check if the entry date is within the selected date range
                                if (entryDate.compareTo(fromDate) >= 0 && entryDate.compareTo(toDate) <= 0) {
                                    String subject = model.getValueAt(i, 2).toString();
                                    String coreHoursStr = model.getValueAt(i, 3).toString();
                                    String nonCoreHoursStr = model.getValueAt(i, 4).toString();
                                    String atHomeColumnValue = model.getValueAt(i, 5).toString();

                                    double coreHours = parseDoubleValue(coreHoursStr);

                                    double atHomeHours = atHomeColumnValue.equalsIgnoreCase("Activity At Home") ? coreHours : 0.0;

                                    htmlBuilder.append("<tr>\n");
                                    htmlBuilder.append("<td>").append(dateString).append("</td>\n");
                                    htmlBuilder.append("<td>").append(subject).append("</td>\n");
                                    htmlBuilder.append("<td>").append(decimalFormat.format(coreHours)).append("</td>\n");
                                    htmlBuilder.append("<td>").append(decimalFormat.format(atHomeHours)).append("</td>\n");
                                    htmlBuilder.append("<td>").append(decimalFormat.format(nonCoreHours)).append("</td>\n");
                                    htmlBuilder.append("</tr>\n");

                                    totalCoreHours += coreHours;
                                    totalNonCoreHours += nonCoreHours;
                                    totalAtHomeHours += atHomeHours;
                                    totalCoreAtHomeHours += atHomeColumnValue.equalsIgnoreCase("Activity At Home") ? coreHours : 0.0;

                                    // Update subject hours map
                                    subjectHoursMap.put(subject, subjectHoursMap.getOrDefault(subject, 0.0) + coreHours);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    htmlBuilder.append("</table>\n");

                    htmlBuilder.append("<div class=\"divider-line\"></div>\n");

                    htmlBuilder.append("<table>\n");
                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<th>Subject</th>\n");
                    htmlBuilder.append("<th>Total Core Hours</th>\n");
                    htmlBuilder.append("</tr>\n");
                    for (Map.Entry<String, Double> entry : subjectHoursMap.entrySet()) {
                        String subject = entry.getKey();
                        double hours = entry.getValue();

                        htmlBuilder.append("<tr>\n");
                        htmlBuilder.append("<td>").append(subject).append("</td>\n");
                        htmlBuilder.append("<td>").append(decimalFormat.format(hours)).append("</td>\n");
                        htmlBuilder.append("</tr>\n");
                    }
                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<td class=\"subject-total\"><b>Total Core Hours</b></td>\n");
                    htmlBuilder.append("<td class=\"subject-total\"><b>").append(decimalFormat.format(totalCoreHours)).append("</b></td>\n");
                    htmlBuilder.append("</tr>\n");
                    htmlBuilder.append("</table>\n");

                    htmlBuilder.append("<div class=\"divider-line\"></div>\n");

                    htmlBuilder.append("<table>\n");
                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<th>Total Non-Core Hours</th>\n");
                    htmlBuilder.append("<th>Total At Home Core Hours</th>\n");
                    htmlBuilder.append("<th>Remaining Core Hours</th>\n");
                    htmlBuilder.append("<th>Remaining At Home Core Hours</th>\n");
                    htmlBuilder.append("</tr>\n");
                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<td>").append(decimalFormat.format(totalNonCoreHours)).append("</td>\n");
                    htmlBuilder.append("<td>").append(decimalFormat.format(totalCoreAtHomeHours)).append("</td>\n");
                    htmlBuilder.append("<td>").append(decimalFormat.format(remainingCoreHours)).append("</td>\n");
                    htmlBuilder.append("<td>").append(decimalFormat.format(remainingAtHomeCoreHours)).append("</td>\n");
                    htmlBuilder.append("</tr>\n");
                    htmlBuilder.append("</table>\n");

                    htmlBuilder.append("<div class=\"divider-line\"></div>\n");
                }

                htmlBuilder.append("</body></html>");

                // Save the HTML report to file
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(htmlBuilder.toString());
                    writer.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // Generate and save the pie chart as a PNG image
                PieChart chart = new PieChartBuilder().width(800).height(600).title("Core Hour Distribution").build();
                chart.getStyler().setLegendVisible(true);
                chart.getStyler().setSeriesColors(new Color[]{new Color(255, 99, 132),
                    new Color(54, 162, 235), new Color(75, 192, 192)});
            chart.addSeries("Core Hours", yearlyCoreHours);
            chart.addSeries("Non-Core Hours", nonCoreHours);
            chart.addSeries("Remaining Core Hours", remainingCoreHours);

            try {
                BitmapEncoder.saveBitmap(chart, filePath + "_chart.png", BitmapEncoder.BitmapFormat.PNG);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, "HTML report saved successfully!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for total yearly core hours, non-core hours, or at-home core hours needed.");
        }
        }
        }

        private double calculateStudentTotalHours(DefaultTableModel model, String studentName) {
            double totalHours = 0.0;

            // Iterate over each row in the table for the given student and calculate the total hours
            for (int row = 0; row < model.getRowCount(); row++) {
                String name = (String) model.getValueAt(row, 1);
                if (name.equals(studentName)) {
                    double coreHours = Double.parseDouble(model.getValueAt(row, 3).toString());
                    double nonCoreHours = Double.parseDouble(model.getValueAt(row, 4).toString());
                    totalHours += coreHours + nonCoreHours;
                }
            }

            return totalHours;
        }

        private double calculateStudentCoreHours(DefaultTableModel model, String studentName) {
            double coreHours = 0.0;

            // Iterate over each row in the table for the given student and calculate the core hours
            for (int row = 0; row < model.getRowCount(); row++) {
                String name = (String) model.getValueAt(row, 1);
                if (name.equals(studentName)) {
                    double hours = Double.parseDouble(model.getValueAt(row, 3).toString());
                    coreHours += hours;
                }
            }

            return coreHours;
        }

        private double calculateStudentNonCoreHours(DefaultTableModel model, String studentName) {
            double nonCoreHours = 0.0;

            // Iterate over each row in the table for the given student and calculate the non-core hours
            for (int row = 0; row < model.getRowCount(); row++) {
                String name = (String) model.getValueAt(row, 1);
                if (name.equals(studentName)) {
                    double hours = Double.parseDouble(model.getValueAt(row, 4).toString());
                    nonCoreHours += hours;
                }
            }

            return nonCoreHours;
        }

        private ChartPanel createChartPanel(Map<String, Double> studentHours, double totalHours, String chartTitle) {

            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                FlatIntelliJLaf.install();
            } catch (Exception e) {
                e.printStackTrace();
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Add the student hours to the dataset
            for (Map.Entry<String, Double> entry : studentHours.entrySet()) {
                String hourType = entry.getKey();
                double hours = entry.getValue();
                dataset.setValue(hours, "Hours", hourType);
            }

            // Create the chart
            JFreeChart chart = ChartFactory.createBarChart(
                chartTitle, // Chart title
                "Hour Type", // X-axis label
                "Hours", // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Orientation
                false, // Include legend
                true, // Include tooltips
                false // Include URLs
            );

            // Create and set the legend
            LegendTitle legend = new LegendTitle(chart.getPlot());
            legend.setFrame(BlockBorder.NONE);
            chart.addLegend(legend);

            // Customize the chart
            chart.getPlot().setBackgroundPaint(null); // Set plot background to transparent

            // Create the chart panel
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setBackground(Color.WHITE); // Set background color of the chart panel

            return chartPanel;

    }//GEN-LAST:event_hours_on_trackActionPerformed

    private void planner_tools_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planner_tools_buttonActionPerformed

    }//GEN-LAST:event_planner_tools_buttonActionPerformed

    @SuppressWarnings({})
    private void loadInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadInfoActionPerformed
        // Get the user's home directory
        String userHomeDirectory = System.getProperty("user.home");

        // Specify the default CSV file path and name in the user's home directory
        String defaultFilePath = userHomeDirectory + File.separator + "SSstartup" + File.separator + "CBData.csv";

        // Display a confirmation dialog
        int choice = JOptionPane.showConfirmDialog(this, "Do you want to import a backup file?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            // Create a file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Backup CSV File");

            // Show the file chooser dialog
            int openChoice = fileChooser.showOpenDialog(this);
            if (openChoice == JFileChooser.APPROVE_OPTION) {
                // Get the selected file
                File backupFile = fileChooser.getSelectedFile();
                String backupFilePath = backupFile.getAbsolutePath();

                // Import backup file contents to the default CSV file
                try (FileWriter fileWriter = new FileWriter(defaultFilePath, true); BufferedReader bufferedReader = new BufferedReader(new FileReader(backupFilePath))) {

                    // Skip the header line in the backup file
                    bufferedReader.readLine();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] data = line.split(",");
                        if (data.length == 2) {
                            String student = data[0];
                            String subject = data[1];
                            fileWriter.write(student + "," + subject + System.lineSeparator());
                        }
                    }

                    JOptionPane.showMessageDialog(this, "Backup file imported successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error importing backup file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Load the CSV file
        DefaultComboBoxModel<String> studentModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> subjectModel = new DefaultComboBoxModel<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(defaultFilePath))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    String student = data[0];
                    String subject = data[1];
                    studentModel.addElement(student);
                    subjectModel.addElement(subject);
                }
            }

            selectStudent.setModel(studentModel);
            selectSubject.setModel(subjectModel);

            JOptionPane.showMessageDialog(this, "Data loaded from CSV successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data from CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_loadInfoActionPerformed

    private void editcbdataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editcbdataActionPerformed
        // Check if the event source is the "editCBData" button

        // Check if the event source is the "editCBData" button
        try {
            // Create a file chooser dialog
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);

            // Show the file chooser dialog
            int result = fileChooser.showOpenDialog(this);

            // If the user selects a file, open the dialog to edit the CSV data
            if (result == JFileChooser.APPROVE_OPTION) {
                File csvFile = fileChooser.getSelectedFile();

                // Read the data from the selected CSV file
                List<String[]> dataRows = new ArrayList<>();
                String[] columnNames = {"Student", "Subject"};

                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    String line;

                    while ((line = br.readLine()) != null) {
                        String[] data = line.split(",");
                        dataRows.add(data); // Add the data rows to the list
                    }
                }

                // Create a table model with the column names and data rows
                DefaultTableModel tableModel = new DefaultTableModel(dataRows.toArray(new String[0][]), columnNames);

                // Create a JTable with the loaded data
                JTable dataTable = new JTable(tableModel);

                // Create a JScrollPane to hold the table
                JScrollPane scrollPane = new JScrollPane(dataTable);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                // Create a JDialog to display the table
                JDialog dialog = new JDialog();
                dialog.setTitle("Edit CBData");
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.getContentPane().add(scrollPane);

                // Create a "Save Changes" button
                JButton saveChangesButton = new JButton("Save Changes");
                saveChangesButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Write the modified data back to the CSV file
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
                            for (int row = 0; row < tableModel.getRowCount(); row++) {
                                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                                    bw.write(tableModel.getValueAt(row, col).toString());
                                    if (col < tableModel.getColumnCount() - 1) {
                                        bw.write(",");
                                    }
                                }
                                bw.newLine();
                            }
                            JOptionPane.showMessageDialog(null, "Changes saved successfully.");
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "Error saving changes to CBData.csv file.");
                        }
                    }
                });

                // Create a JPanel to hold the "Save Changes" button
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(saveChangesButton);

                // Add the button panel to the dialog
                dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

                // Set the size, position, and make the dialog visible
                dialog.pack();
                dialog.setLocationRelativeTo(null); // Center the dialog on the screen
                dialog.setVisible(true);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error opening CSV file.");
        }

    }//GEN-LAST:event_editcbdataActionPerformed

    private void editcbdataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editcbdataMouseClicked

    }//GEN-LAST:event_editcbdataMouseClicked

    private void addSubjectButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubjectButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addSubjectButton1ActionPerformed

    private void curriculumFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_curriculumFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_curriculumFieldActionPerformed

    private void addSubjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubjectButtonActionPerformed
        // TODO add your handling code here:
        String subjectName = subjectField.getText(); // Get the value from the text field

        DefaultComboBoxModel<String> selectSubjectModel = (DefaultComboBoxModel<String>) selectSubject.getModel();
        selectSubjectModel.addElement(subjectName); // Add the value to the combo box model

        // Clear the text field
        subjectField.setText("");

        try {
            // Get the user's home directory
            String userHomeDirectory = System.getProperty("user.home");

            // Specify the CSV file path and name in the user's home directory
            String filePath = userHomeDirectory + File.separator + "SSstartup" + File.separator + "CBData.csv";

            FileWriter writer = new FileWriter(filePath, true); // Append to the existing file

            // Create a new BufferedWriter
            try (BufferedWriter bw = new BufferedWriter(writer)) {
                // Write a comma-separated value to the second column of the CSV file
                bw.write("," + subjectName);
                bw.newLine();
            }

            JOptionPane.showMessageDialog(this, "Subject added and data saved to CSV successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data to CSV.");
            e.printStackTrace();
        }
    }//GEN-LAST:event_addSubjectButtonActionPerformed

    private void subjectFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subjectFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_subjectFieldActionPerformed

    private void addStudentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStudentButtonActionPerformed
        // TODO add your handling code here:
        String studentName = nameField.getText(); // Get the value from the text field

        DefaultComboBoxModel<String> selectStudentModel = (DefaultComboBoxModel<String>) selectStudent.getModel();
        selectStudentModel.addElement(studentName); // Add the value to the combo box model

        // Clear the text field
        nameField.setText("");

        try {
            // Get the user's home directory
            String userHomeDirectory = System.getProperty("user.home");

            // Specify the CSV file path and name in the user's home directory
            String filePath = userHomeDirectory + File.separator + "SSstartup" + File.separator + "CBData.csv";

            FileWriter writer = new FileWriter(filePath, true); // Append to the existing file

            // Create a new BufferedWriter
            try (BufferedWriter bw = new BufferedWriter(writer)) {
                // Write the student name to the first column of the CSV file
                bw.write(studentName);
                bw.newLine();
            }

            JOptionPane.showMessageDialog(this, "Student added and data saved to CSV successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data to CSV.");
            e.printStackTrace();
        }
    }//GEN-LAST:event_addStudentButtonActionPerformed

    private void addStudentButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addStudentButtonMouseClicked

    }//GEN-LAST:event_addStudentButtonMouseClicked

    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameFieldActionPerformed

    private void generate_pie_chartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generate_pie_chartActionPerformed
        // Get the table model from the hourLog JTable
        DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

        // Create a map to hold the total core and non-core hours for each student
        Map<String, Double> studentTotalCoreHours = new HashMap<>();
        Map<String, Double> studentTotalNonCoreHours = new HashMap<>();

        // Iterate over each entry in the hour log and calculate the total core and non-core hours for each student
        for (int row = 0; row < model.getRowCount(); row++) {
            String studentName = (String) model.getValueAt(row, 1);
            double coreHours = parseDoubleOrDefault(model.getValueAt(row, 3).toString(), 0.0);
            double nonCoreHours = parseDoubleOrDefault(model.getValueAt(row, 4).toString(), 0.0);

            // Update the total core and non-core hours for the current student
            studentTotalCoreHours.put(studentName, studentTotalCoreHours.getOrDefault(studentName, 0.0) + coreHours);
            studentTotalNonCoreHours.put(studentName, studentTotalNonCoreHours.getOrDefault(studentName, 0.0) + nonCoreHours);
        }

        // Check if there are any students in the hour log
        if (studentTotalCoreHours.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data available. Please add entries to the hour log.");
            return;
        }

        // Create a panel to hold the goal input fields
        JPanel goalInputPanel = new JPanel(new GridLayout(0, 2));
        for (String studentName : studentTotalCoreHours.keySet()) {
            double totalCoreHours = studentTotalCoreHours.get(studentName);
            double totalNonCoreHours = studentTotalNonCoreHours.get(studentName);
            double totalHours = totalCoreHours + totalNonCoreHours;

            // Create a label and text field for goal input
            JLabel nameLabel = new JLabel(studentName);
            JTextField goalTextField = new JTextField(10);
            goalInputPanel.add(nameLabel);
            goalInputPanel.add(goalTextField);

            // Pre-fill the goal text field with the total hours
            goalTextField.setText(String.valueOf(totalHours));
        }

        // Prompt the user to enter the goals for each student
        int result = JOptionPane.showConfirmDialog(this, goalInputPanel, "Enter Goals", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return; // User cancelled, exit the method
        }

        // Create and display pie charts for each student
        for (int i = 0; i < goalInputPanel.getComponentCount(); i += 2) {
            String studentName = ((JLabel) goalInputPanel.getComponent(i)).getText();
            JTextField goalTextField = (JTextField) goalInputPanel.getComponent(i + 1);

            double goalHours;
            try {
                goalHours = Double.parseDouble(goalTextField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number for " + studentName + ".");
                continue; // Invalid input, move to the next student
            }

            double totalCoreHours = studentTotalCoreHours.get(studentName);
            double totalNonCoreHours = studentTotalNonCoreHours.get(studentName);
            double totalHours = totalCoreHours + totalNonCoreHours;
            double coreCompletion = Math.min(totalCoreHours / goalHours, 1.0);
            double nonCoreCompletion = Math.min(totalNonCoreHours / goalHours, 1.0);

            // Create a pie dataset for the chart
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Core Hours", coreCompletion * 100);
            dataset.setValue("Non-Core Hours", nonCoreCompletion * 100);

            // Create a pie chart based on the dataset
            JFreeChart chart = ChartFactory.createPieChart(studentName + " Hour Summary", dataset, true, true, false);

            // Customize the chart appearance
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setSectionPaint("Core Hours", Color.blue);
            plot.setSectionPaint("Non-Core Hours", Color.green);
            plot.setExplodePercent("Core Hours", 0.1);

            // Create a chart panel to display the chart
            ChartPanel chartPanel = new ChartPanel(chart);

            // Create a JFrame to show the chart panel
            JFrame chartFrame = new JFrame(studentName + " Hour Chart");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.setSize(500, 500);
            chartFrame.setLocationRelativeTo(this);
            chartFrame.add(chartPanel);

            // Create a progress label to show the goal progress
            JLabel progressLabel = new JLabel("Goal Progress: " + String.format("%.2f", (totalHours / goalHours) * 100) + "%");
            progressLabel.setFont(progressLabel.getFont().deriveFont(Font.BOLD));
            progressLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Create a panel to hold the chart and progress label
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(chartPanel, BorderLayout.CENTER);
            contentPanel.add(progressLabel, BorderLayout.SOUTH);

            // Add the content panel to the chart frame
            chartFrame.getContentPane().add(contentPanel);

            // Display the chart frame
            chartFrame.setVisible(true);
        }
        }

        // Helper method to parse a double value with a default fallback
        private double parseDoubleOrDefault(String value, double defaultValue) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }

        }

        private double calculateTotalHoursForRow(int row) {
            DefaultTableModel model = (DefaultTableModel) hourLog.getModel();
            double coreHours = Double.parseDouble(model.getValueAt(row, 3).toString());
            double nonCoreHours = Double.parseDouble(model.getValueAt(row, 4).toString());
            return coreHours + nonCoreHours;
        }

        private void generatePieCharts() {
            // Check if there are any students selected
            if (studentGoalHours.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No students selected. Please set a goal for at least one student.");
                return;
            }

            // Get the table model from the hourLog JTable
            DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

            // Create a map to hold the total core and non-core hours for each student
            Map<String, Double> studentTotalCoreHours = new HashMap<>();
            Map<String, Double> studentTotalNonCoreHours = new HashMap<>();

            // Iterate over each entry in the hour log and calculate the total core and non-core hours for each student
            for (int row = 0; row < model.getRowCount(); row++) {
                String studentName = (String) model.getValueAt(row, 1);
                double coreHours = Double.parseDouble(model.getValueAt(row, 3).toString());
                double nonCoreHours = Double.parseDouble(model.getValueAt(row, 4).toString());

                // Update the total core and non-core hours for the current student
                studentTotalCoreHours.put(studentName, studentTotalCoreHours.getOrDefault(studentName, 0.0) + coreHours);
                studentTotalNonCoreHours.put(studentName, studentTotalNonCoreHours.getOrDefault(studentName, 0.0) + nonCoreHours);
            }

            // Create a pie chart frame for each selected student
            for (String studentName : studentGoalHours.keySet()) {
                // Get the goal hours for the current student
                double goalHours = studentGoalHours.get(studentName);

                // Check if the student is selected for the report
                if (!studentGoalHours.containsKey(studentName) || goalHours <= 0) {
                    continue;
                }

                double totalCoreHours = studentTotalCoreHours.get(studentName);
                double totalNonCoreHours = studentTotalNonCoreHours.get(studentName);

                // Calculate the total hours for the student
                double totalHours = totalCoreHours + totalNonCoreHours;

                // Calculate the goal progress for the current student
                double progress = Math.min(totalHours / goalHours, 1.0);

                // Create a pie dataset for the chart
                DefaultPieDataset dataset = new DefaultPieDataset();
                dataset.setValue("Core Hours", totalCoreHours);
                dataset.setValue("Non-Core Hours", totalNonCoreHours);

                // Create a pie chart based on the dataset
                JFreeChart chart = ChartFactory.createPieChart(studentName + " Hour Summary", dataset, true, true, false);

                // Customize the chart appearance
                PiePlot plot = (PiePlot) chart.getPlot();
                plot.setSectionPaint("Core Hours", Color.blue);
                plot.setSectionPaint("Non-Core Hours", Color.green);
                plot.setExplodePercent("Core Hours", 0.1);

                // Create a chart panel to display the chart
                ChartPanel chartPanel = new ChartPanel(chart);

                // Create a JFrame to show the chart panel
                JFrame chartFrame = new JFrame(studentName + " Hour Chart");
                chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                chartFrame.setSize(500, 500);
                chartFrame.setLocationRelativeTo(null);
                chartFrame.add(chartPanel);

                // Create a progress label to show the goal progress
                JLabel progressLabel = new JLabel("Goal Progress: " + String.format("%.2f", progress * 100) + "%");
                progressLabel.setFont(progressLabel.getFont().deriveFont(Font.BOLD));
                progressLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // Create a panel to hold the chart and progress label
                JPanel contentPanel = new JPanel(new BorderLayout());
                contentPanel.add(chartPanel, BorderLayout.CENTER);
                contentPanel.add(progressLabel, BorderLayout.SOUTH);

                // Add the content panel to the chart frame
                chartFrame.getContentPane().add(contentPanel);

                // Display the chart frame
                chartFrame.setVisible(true);
            }

        }

        private double getTotalHours(Map<String, Double> studentTotalCoreHours, Map<String, Double> studentTotalNonCoreHours) {
            double totalHours = 0.0;
            for (String studentName : studentTotalCoreHours.keySet()) {
                double totalCoreHours = studentTotalCoreHours.get(studentName);
                double totalNonCoreHours = studentTotalNonCoreHours.get(studentName);

                totalHours += totalCoreHours + totalNonCoreHours;
            }
            return totalHours;
    }//GEN-LAST:event_generate_pie_chartActionPerformed

    private void html_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_html_exportActionPerformed
        // TODO add your handling code here:
        // Display a warning dialog
        int choice = JOptionPane.showConfirmDialog(this, "If you are trying to back this up so that it can be reloaded into the software later, \n please use the Export HourLog as CSV Spreadsheet button.\nAre you sure you want to export as HTML?", "Warning", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.NO_OPTION) {
            return; // Abort exporting if user chooses "No"
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save HTML File");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("HTML Files", "html");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".html")) {
                filePath += ".html";
            }

            try {
                FileWriter fileWriter = new FileWriter(filePath);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.write("<html><head>");
                bufferedWriter.write("<style>");
                bufferedWriter.write("table { border-collapse: collapse; width: 100%; }");
                bufferedWriter.write("th, td { text-align: left; padding: 8px; }");
                bufferedWriter.write("th { background-color: #006400; }");
                bufferedWriter.write(".print-button { margin-bottom: 10px; }");
                bufferedWriter.write(".print-button button { background-color: #4CAF50; color: white; padding: 8px 12px; border: none; cursor: pointer; }");
                bufferedWriter.write("</style>");
                bufferedWriter.write("<title>Hour Log</title></head><body>");

                bufferedWriter.write("<div class=\"print-button\"><button onclick=\"window.print()\">Print Table</button></div>");

                bufferedWriter.write("<h1>Hour Log</h1>");

                bufferedWriter.write("<table>");
                bufferedWriter.write("<tr><th>Date</th><th>Student</th><th>Subject</th><th>Core Hours</th><th>Non-Core Hours</th></tr>");

                for (int i = 0; i < hourLog.getRowCount(); i++) {
                    bufferedWriter.write("<tr>");
                    for (int j = 0; j < hourLog.getColumnCount(); j++) {
                        Object cellValue = hourLog.getValueAt(i, j);
                        bufferedWriter.write("<td>" + cellValue + "</td>");
                    }
                    bufferedWriter.write("</tr>");
                }

                bufferedWriter.write("</table>");

                bufferedWriter.write("</body></html>");

                bufferedWriter.close();

                JOptionPane.showMessageDialog(this, "HTML file exported successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting HTML file.");
            }

        }
    }//GEN-LAST:event_html_exportActionPerformed

    @SuppressWarnings("null")
    private void report_daily_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_report_daily_buttonActionPerformed

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Read student list from CBdata.csv
        List<String> studentList = readStudentListFromCSV();

        // Create a panel for student selection
        JPanel studentPanel = new JPanel();
        studentPanel.setLayout(new BorderLayout());

        JLabel studentLabel = new JLabel("Select Student(s):");
        studentLabel.setFont(studentLabel.getFont().deriveFont(Font.BOLD));

        JCheckBox selectAllCheckbox = new JCheckBox("All Students");
        selectAllCheckbox.addItemListener(e -> {
            boolean selected = selectAllCheckbox.isSelected();
            for (Component component : studentPanel.getComponents()) {
                if (component instanceof JCheckBox && component != selectAllCheckbox) {
                    ((JCheckBox) component).setSelected(selected);
                }
            }
        });

        JPanel checkboxPanel = new JPanel(new GridLayout(0, 1));
        checkboxPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String student : studentList) {
            JCheckBox checkBox = new JCheckBox(student);
            checkboxPanel.add(checkBox);
        }

        studentPanel.add(studentLabel, BorderLayout.NORTH);
        studentPanel.add(new JScrollPane(checkboxPanel), BorderLayout.CENTER);
        studentPanel.add(selectAllCheckbox, BorderLayout.SOUTH);

        // Create a panel for date selection
        JPanel datePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        datePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel dateLabel = new JLabel("Select Date:");
        JDateChooser dateChooser = new JDateChooser();

        datePanel.add(dateLabel);
        datePanel.add(dateChooser);

        // Create a panel for the main options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BorderLayout());
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        optionsPanel.add(studentPanel, BorderLayout.CENTER);
        optionsPanel.add(datePanel, BorderLayout.SOUTH);

        // Show the student and date selection dialog
        int option = JOptionPane.showConfirmDialog(this, optionsPanel, "Select Student and Date", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION) {
            return;
        }

        // Retrieve selected student
        List<String> selectedStudents = new ArrayList<>();
        for (Component component : checkboxPanel.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                    selectedStudents.add(checkBox.getText());
                }
            }
        }

        Date selectedDate = dateChooser.getDate();

        if (selectedStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one student.");
            return;
        }

        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Invalid date selected.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save HTML Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();

                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".html")) {
                    fileToSave = new File(filePath + ".html");
                }

                DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false); // Ensure strict date parsing

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                StringBuilder htmlBuilder = new StringBuilder();
                htmlBuilder.append("<html><head><style>\n");
                htmlBuilder.append("table {\n");
                htmlBuilder.append("    border-collapse: collapse;\n");
                htmlBuilder.append("    width: 100%;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append("th, td {\n");
                htmlBuilder.append("    text-align: left;\n");
                htmlBuilder.append("    padding: 8px;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append("th {\n");
                htmlBuilder.append("    background-color: #006633;\n");
                htmlBuilder.append("    color: white;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append(".subject-total {\n");
                htmlBuilder.append("    color: blue;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append(".divider-line {\n");
                htmlBuilder.append("    border-top: 1px solid black;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append("</style></head><body>\n");
                htmlBuilder.append("<h1>Hour Log Report</h1>\n");
                htmlBuilder.append("<button onclick=\"window.print()\">Print</button>\n");

                for (String student : selectedStudents) {
                    htmlBuilder.append("<h2>").append(student).append("</h2>\n");
                    htmlBuilder.append("<div style=\"margin-bottom: 20px;\"></div>\n");

                    double totalCoreHours = 0.0;
                    double totalNonCoreHours = 0.0;
                    Map<String, Double> subjectHoursMap = new HashMap<>();

                    htmlBuilder.append("<table>\n");
                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<th>Date</th>\n");
                    htmlBuilder.append("<th>Subject</th>\n");
                    htmlBuilder.append("<th>Core Hours</th>\n");
                    htmlBuilder.append("<th>Non-Core Hours</th>\n");
                    htmlBuilder.append("</tr>\n");

                    for (int i = 0; i < model.getRowCount(); i++) {
                        String studentName = model.getValueAt(i, 1).toString();
                        String dateString = model.getValueAt(i, 0).toString();

                        System.out.println("Student Name: " + studentName);
                        System.out.println("Date String: " + dateString);

                        if (student.equals(studentName)) {
                            try {
                                Date entryDate = sdf.parse(dateString);

                                System.out.println("Entry Date: " + entryDate);

                                // Check if the entry date matches the selected date
                                if (entryDate.equals(selectedDate)) {
                                    String subject = model.getValueAt(i, 2).toString();
                                    String coreHoursStr = model.getValueAt(i, 3).toString();
                                    String nonCoreHoursStr = model.getValueAt(i, 4).toString();

                                    double coreHours = parseDoubleValue(coreHoursStr);
                                    double nonCoreHours = parseDoubleValue(nonCoreHoursStr);

                                    htmlBuilder.append("<tr>\n");
                                    htmlBuilder.append("<td>").append(dateString).append("</td>\n");
                                    htmlBuilder.append("<td>").append(subject).append("</td>\n");
                                    htmlBuilder.append("<td>").append(decimalFormat.format(coreHours)).append("</td>\n");
                                    htmlBuilder.append("<td>").append(decimalFormat.format(nonCoreHours)).append("</td>\n");
                                    htmlBuilder.append("</tr>\n");

                                    totalCoreHours += coreHours;
                                    totalNonCoreHours += nonCoreHours;

                                    double subjectHours = subjectHoursMap.getOrDefault(subject, 0.0);
                                    subjectHours += coreHours + nonCoreHours;
                                    subjectHoursMap.put(subject, subjectHours);
                                }
                            } catch (ParseException e) {
                                // Invalid date encountered, add "0000-00-00" to the output
                                htmlBuilder.append("<tr>\n");
                                htmlBuilder.append("<td>").append("0000-00-00").append("</td>\n");
                                htmlBuilder.append("<td>").append(model.getValueAt(i, 2)).append("</td>\n");
                                htmlBuilder.append("<td>").append(model.getValueAt(i, 3)).append("</td>\n");
                                htmlBuilder.append("<td>").append(model.getValueAt(i, 4)).append("</td>\n");
                                htmlBuilder.append("</tr>\n");
                            }
                        }
                    }

                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<th>Total</th>\n");
                    htmlBuilder.append("<th></th>\n");
                    htmlBuilder.append("<th>").append(decimalFormat.format(totalCoreHours)).append("</th>\n");
                    htmlBuilder.append("<th>").append(decimalFormat.format(totalNonCoreHours)).append("</th>\n");
                    htmlBuilder.append("</tr>\n");

                    htmlBuilder.append("</table>\n");

                    // Add subject totals with a different color
                    htmlBuilder.append("<h3 class=\"subject-total\">Subject Totals</h3>\n");
                    htmlBuilder.append("<table>\n");
                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<th>Subject</th>\n");
                    htmlBuilder.append("<th>Total Hours</th>\n");
                    htmlBuilder.append("</tr>\n");

                    for (Map.Entry<String, Double> entry : subjectHoursMap.entrySet()) {
                        String subject = entry.getKey();
                        double subjectHours = entry.getValue();

                        htmlBuilder.append("<tr>\n");
                        htmlBuilder.append("<td>").append(subject).append("</td>\n");
                        htmlBuilder.append("<td>").append(decimalFormat.format(subjectHours)).append("</td>\n");
                        htmlBuilder.append("</tr>\n");
                    }

                    htmlBuilder.append("</table>\n");
                    // Add black divider line
                    htmlBuilder.append("<hr class=\"divider-line\">\n");
                }

                htmlBuilder.append("<p>This report contains logs for ").append(selectedDate).append("</p>\n");

                htmlBuilder.append("</body></html>");

                // Write the HTML content to the file
                FileWriter fileWriter = new FileWriter(fileToSave);
                fileWriter.write(htmlBuilder.toString());
                fileWriter.close();

                JOptionPane.showMessageDialog(this, "HTML report saved successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting HTML report: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }//GEN-LAST:event_report_daily_buttonActionPerformed

    private void export_csv_daterangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_csv_daterangeActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

        // Create JDateChooser instances for selecting the start and end dates
        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();

        // Prompt the user to select the start and end dates
        Object[] message = {"Select start date:", startDateChooser, "Select end date:", endDateChooser};
        int option = JOptionPane.showConfirmDialog(this, message, "Select Date Range", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            // Get the selected start and end dates
            Date startDate = startDateChooser.getDate();
            Date endDate = endDateChooser.getDate();

            // Create a list to store selected students
            List<String> selectedStudents = new ArrayList<>();

            // Create a checkbox list to select students
            Object[] studentOptions = getStudentNames(); // Modify this method to get student names
            Object[] messageStudents = {"Select students:", studentOptions};
            int optionStudents = JOptionPane.showConfirmDialog(this, messageStudents, "Select Students", JOptionPane.OK_CANCEL_OPTION);

            if (optionStudents == JOptionPane.OK_OPTION) {
                // Get the selected students
                for (int i = 1; i < studentOptions.length; i++) { // Start from 1 to skip the "Select All" option
                    JCheckBox checkBox = (JCheckBox) studentOptions[i];
                    if (checkBox.isSelected()) {
                        selectedStudents.add(checkBox.getText());
                    }
                }

                // Create a file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Report"); // Set the dialog title

                // Set the file filter to restrict to CSV files
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                fileChooser.setFileFilter(filter);

                // Show the Save File dialog
                int saveOption = fileChooser.showSaveDialog(this);

                if (saveOption == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    // Get the directory path
                    String directoryPath = selectedFile.getParent();

                    // Create a SimpleDateFormat object to format the export date
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    // Append the export date to the file name
                    String exportStartDate = dateFormat.format(startDate);
                    String exportEndDate = dateFormat.format(endDate);
                    String fileName = "HourLog_" + exportStartDate + "_" + exportEndDate + ".csv";
                    String filePath = directoryPath + File.separator + fileName;

                    try {
                        // Create a FileWriter object to write data to the CSV file
                        FileWriter writer = new FileWriter(filePath);

                        // Write the column headers to the CSV file
                        for (int i = 0; i < model.getColumnCount(); i++) {
                            writer.append(model.getColumnName(i));
                            if (i < model.getColumnCount() - 1) {
                                writer.append(",");
                            }
                        }
                        writer.append("\n");

                        // Write the data rows within the selected date range and for selected students to the CSV file
                        for (int row = 0; row < model.getRowCount(); row++) {
                            // Get the date value from the first column
                            Object dateValue = model.getValueAt(row, 0);
                            if (dateValue instanceof Date) {
                                Date date = (Date) dateValue;
                                if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0) {
                                    // Get the student name from the second column
                                    Object studentValue = model.getValueAt(row, 1);
                                    if (studentValue != null && selectedStudents.contains(studentValue.toString())) {
                                        for (int col = 0; col < model.getColumnCount(); col++) {
                                            Object value = model.getValueAt(row, col);
                                            if (value != null) {
                                                writer.append(value.toString());
                                            }
                                            if (col < model.getColumnCount() - 1) {
                                                writer.append(",");
                                            }
                                        }
                                        writer.append("\n");
                                    }
                                }
                            }
                        }

                        // Close the FileWriter
                        writer.close();

                        // Display a success message
                        JOptionPane.showMessageDialog(this, "Data exported successfully!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Error exporting data to CSV.");
                        ex.printStackTrace();
                    }
                }
            }
        }
    }//GEN-LAST:event_export_csv_daterangeActionPerformed

    private void html_out_report_generate_core_noncoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_html_out_report_generate_core_noncoreActionPerformed
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Read student list from CBdata.csv
        List<String> studentList = readStudentListFromCSV();

        // Create a panel for student selection
        JPanel studentPanel = new JPanel();
        studentPanel.setLayout(new BorderLayout());

        JLabel studentLabel = new JLabel("Select Student(s):");
        studentLabel.setFont(studentLabel.getFont().deriveFont(Font.BOLD));

        JCheckBox selectAllCheckbox = new JCheckBox("All Students");
        selectAllCheckbox.addItemListener(e -> {
            boolean selected = selectAllCheckbox.isSelected();
            for (Component component : studentPanel.getComponents()) {
                if (component instanceof JCheckBox && component != selectAllCheckbox) {
                    ((JCheckBox) component).setSelected(selected);
                }
            }
        });

        JPanel checkboxPanel = new JPanel(new GridLayout(0, 1));
        checkboxPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String student : studentList) {
            JCheckBox checkBox = new JCheckBox(student);
            checkboxPanel.add(checkBox);
        }

        studentPanel.add(studentLabel, BorderLayout.NORTH);
        studentPanel.add(new JScrollPane(checkboxPanel), BorderLayout.CENTER);
        studentPanel.add(selectAllCheckbox, BorderLayout.SOUTH);

        // Create a panel for date range selection
        JPanel dateRangePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        dateRangePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel fromLabel = new JLabel("From Date:");
        JLabel toLabel = new JLabel("To Date:");
        JDateChooser fromDChooser = new JDateChooser();
        JDateChooser toDChooser = new JDateChooser();

        dateRangePanel.add(fromLabel);
        dateRangePanel.add(fromDChooser);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(toDChooser);

        // Create a panel for the main options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BorderLayout());
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        optionsPanel.add(studentPanel, BorderLayout.CENTER);
        optionsPanel.add(dateRangePanel, BorderLayout.SOUTH);

        // Show the student selection dialog
        int studentOption = JOptionPane.showConfirmDialog(this, optionsPanel, "Select Students", JOptionPane.OK_CANCEL_OPTION);
        if (studentOption == JOptionPane.CANCEL_OPTION) {
            return;
        }

        // Retrieve selected students
        List<String> selectedStudents = new ArrayList<>();
        for (Component component : checkboxPanel.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                    selectedStudents.add(checkBox.getText());
                }
            }
        }

        // Show the date range selection dialog
        int dateOption = JOptionPane.showConfirmDialog(this, dateRangePanel, "Select Date Range", JOptionPane.OK_CANCEL_OPTION);
        if (dateOption == JOptionPane.CANCEL_OPTION) {
            return;
        }

        Date fromDate = fromDChooser.getDate();
        Date toDate = toDChooser.getDate();

        if (selectedStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one student.");
            return;
        }

        if (fromDate == null || toDate == null || fromDate.after(toDate)) {
            JOptionPane.showMessageDialog(this, "Invalid date range selected.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save HTML Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();

                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".html")) {
                    fileToSave = new File(filePath + ".html");
                }

                DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false); // Ensure strict date parsing

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                StringBuilder htmlBuilder = new StringBuilder();
                htmlBuilder.append("<html><head><style>\n");
                htmlBuilder.append("table {\n");
                htmlBuilder.append("    border-collapse: collapse;\n");
                htmlBuilder.append("    width: 100%;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append("th, td {\n");
                htmlBuilder.append("    text-align: left;\n");
                htmlBuilder.append("    padding: 8px;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append("th {\n");
                htmlBuilder.append("    background-color: #006633;\n");
                htmlBuilder.append("    color: white;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append(".subject-total {\n");
                htmlBuilder.append("    color: blue;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append(".divider-line {\n");
                htmlBuilder.append("    border-top: 1px solid black;\n");
                htmlBuilder.append("}\n");
                htmlBuilder.append("</style></head><body>\n");
                htmlBuilder.append("<h1>Hour Log Report</h1>\n");
                htmlBuilder.append("<button onclick=\"window.print()\">Print</button>\n");

                for (String student : selectedStudents) {
                    htmlBuilder.append("<h2>").append(student).append("</h2>\n");
                    htmlBuilder.append("<div style=\"margin-bottom: 20px;\"></div>\n");

                    double totalCoreHours = 0.0;
                    double totalNonCoreHours = 0.0;
                    double totalAtHomeHours = 0.0;
                    double totalCoreAtHomeHours = 0.0;

                    Map<String, Double> subjectHoursMap = new HashMap<>();

                    htmlBuilder.append("<table>\n");
                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<th>Date</th>\n");
                    htmlBuilder.append("<th>Subject</th>\n");
                    htmlBuilder.append("<th>Core Hours</th>\n");
                    htmlBuilder.append("<th>|At Home Core|</th>\n");
                    htmlBuilder.append("<th>Non-Core Hours</th>\n");
                    htmlBuilder.append("</tr>\n");
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String studentName = model.getValueAt(i, 1).toString();
                        String dateString = model.getValueAt(i, 0).toString();

                        if (student.equals(studentName)) {
                            try {
                                Date entryDate = sdf.parse(dateString);

                                // Check if the entry date is within the selected date range
                                if (entryDate.compareTo(fromDate) >= 0 && entryDate.compareTo(toDate) <= 0) {
                                    String subject = model.getValueAt(i, 2).toString();
                                    String coreHoursStr = model.getValueAt(i, 3).toString();
                                    String nonCoreHoursStr = model.getValueAt(i, 4).toString();
                                    String atHomeColumnValue = model.getValueAt(i, 5).toString();

                                    double coreHours = parseDoubleValue(coreHoursStr);
                                    double nonCoreHours = parseDoubleValue(nonCoreHoursStr);
                                    double atHomeHours = atHomeColumnValue.equalsIgnoreCase("Activity At Home") ? coreHours : 0.0;

                                    htmlBuilder.append("<tr>\n");
                                    htmlBuilder.append("<td>").append(dateString).append("</td>\n");
                                    htmlBuilder.append("<td>").append(subject).append("</td>\n");
                                    htmlBuilder.append("<td>").append(decimalFormat.format(coreHours)).append("</td>\n");
                                    htmlBuilder.append("<td>").append(decimalFormat.format(atHomeHours)).append("</td>\n");
                                    htmlBuilder.append("<td>").append(decimalFormat.format(nonCoreHours)).append("</td>\n");

                                    htmlBuilder.append("</tr>\n");

                                    // Calculate "At Home" hours
                                    if (atHomeColumnValue.equalsIgnoreCase("Activity At Home")) {
                                        totalAtHomeHours += atHomeHours;
                                    }

                                    totalCoreHours += coreHours;
                                    totalNonCoreHours += nonCoreHours;

                                    double subjectHours = subjectHoursMap.getOrDefault(subject, 0.0);
                                    subjectHours += coreHours + nonCoreHours;
                                    subjectHoursMap.put(subject, subjectHours);
                                }
                            } catch (ParseException e) {
                                // Invalid date encountered, add "0000-00-00" to the output
                                htmlBuilder.append("<tr>\n");
                                htmlBuilder.append("<td>").append("0000-00-00").append("</td>\n");
                                htmlBuilder.append("<td>").append(model.getValueAt(i, 2)).append("</td>\n");
                                htmlBuilder.append("<td>").append(model.getValueAt(i, 3)).append("</td>\n");
                                htmlBuilder.append("<td>").append(model.getValueAt(i, 4)).append("</td>\n");
                                htmlBuilder.append("</tr>\n");
                            }
                        }

                    }

                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<th>Total</th>\n");
                    htmlBuilder.append("<th></th>\n");
                    htmlBuilder.append("<th>").append(decimalFormat.format(totalCoreHours)).append("</th>\n");
                    htmlBuilder.append("<th>").append(decimalFormat.format(totalAtHomeHours)).append("</th>\n");
                    htmlBuilder.append("<th>").append(decimalFormat.format(totalNonCoreHours)).append("</th>\n");
                    htmlBuilder.append("</tr>\n");

                    htmlBuilder.append("</table>\n");

                    // Add subject totals with a different color
                    htmlBuilder.append("<h3 class=\"subject-total\">Subject Totals</h3>\n");
                    htmlBuilder.append("<table>\n");
                    htmlBuilder.append("<tr>\n");
                    htmlBuilder.append("<th>Subject</th>\n");
                    htmlBuilder.append("<th>Total Hours</th>\n");
                    htmlBuilder.append("</tr>\n");

                    for (Map.Entry<String, Double> entry : subjectHoursMap.entrySet()) {
                        String subject = entry.getKey();
                        double subjectHours = entry.getValue();

                        htmlBuilder.append("<tr>\n");
                        htmlBuilder.append("<td>").append(subject).append("</td>\n");
                        htmlBuilder.append("<td>").append(decimalFormat.format(subjectHours)).append("</td>\n");
                        htmlBuilder.append("</tr>\n");
                    }

                    htmlBuilder.append("</table>\n");
                    // Add black divider line
                    htmlBuilder.append("<hr class=\"divider-line\">\n");
                }

                htmlBuilder.append("<p>This report contains logs for weeks ").append(getWeekNumber(fromDate)).append(" to ").append(getWeekNumber(toDate)).append(" of ").append(getYear(fromDate)).append("</p>\n");

                htmlBuilder.append("</body></html>");

                // Write the HTML content to the file
                FileWriter fileWriter = new FileWriter(fileToSave);
                fileWriter.write(htmlBuilder.toString());
                fileWriter.close();

                JOptionPane.showMessageDialog(this, "HTML report saved successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting HTML report: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_html_out_report_generate_core_noncoreActionPerformed

    private void addHoursButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addHoursButtonActionPerformed
        // TODO add your handling code here:

        String date = setDateField.getText();
        String selectedStudent = selectStudent.getSelectedItem().toString();
        String selectedSubject = selectSubject.getSelectedItem().toString();

        String hours = hoursField.getText(); // Get the value from the hoursField

        DefaultTableModel model = (DefaultTableModel) hourLog.getModel();

        // Check if the hours value is null or empty, and add "*" if necessary
        if (hours == null || hours.isEmpty()) {
            hours = "0";
        }

        // Fill null columns with "-0"
        String coreHours = "0";
        String nonCoreHours = "0";

        Object spinnerValue = core_select.getValue();
        if (spinnerValue != null && spinnerValue.equals("Core Hours")) {
            coreHours = hours;
            nonCoreHours = "0";
        } else {
            coreHours = "0";
            nonCoreHours = hours;
        }

        // Create the new row with filled values
        Object[] newRow = {date, selectedStudent, selectedSubject, coreHours, nonCoreHours, "Activity At Home"}; // Add an empty string for the sixth column

        // Check if the checkbox is checked
        boolean isAwayChecked = awaycheck.isSelected();
        if (isAwayChecked) {
            newRow[5] = "Away"; // Set the sixth column as "away"
        }

        model.insertRow(0, newRow);
        updateCoreTotal(selectedStudent, selectedSubject, coreHours); // Call the core hours update function
        updateNonCoreTotal(selectedStudent, selectedSubject, nonCoreHours); // Call the non-core hours update function

        // Clear the text field after adding the value
        hoursField.setText("");

        Timer autoSaveTimer = new Timer();
        autoSaveTimer.schedule(new AutoSaveTask(), 5000);

    }//GEN-LAST:event_addHoursButtonActionPerformed

    private void addHoursButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addHoursButtonMouseClicked

    }//GEN-LAST:event_addHoursButtonMouseClicked

    private void hoursFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hoursFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hoursFieldActionPerformed

    private void selectSubjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectSubjectActionPerformed
        // TODO add your handling code here:
        String selectedSubject = selectSubject.getSelectedItem().toString();

        if (selectedSubject.equals("Reading") || selectedSubject.equals("Math") ||
            selectedSubject.equals("Social Studies") || selectedSubject.equals("Language Arts") ||
            selectedSubject.equals("Science")) {
            core_select.setValue("Core Hours");
        }
    }//GEN-LAST:event_selectSubjectActionPerformed

    private void selectStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectStudentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectStudentActionPerformed

    private void setDateFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setDateFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_setDateFieldActionPerformed

    private void fill_addhours_datefeildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fill_addhours_datefeildActionPerformed

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SSync_GUI_Form.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a JDateChooser instance
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");

        // Show the date chooser dialog
        int option = JOptionPane.showOptionDialog(
            this,
            dateChooser,
            "Select Date",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            null
        );

        // Check if the OK button was clicked
        if (option == JOptionPane.OK_OPTION) {
            // Get the selected date from the date chooser
            Date selectedDate = dateChooser.getDate();

            // Format the date as "yyyy-MM-dd"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(selectedDate);

            // Set the formatted date in the setDateField text field
            setDateField.setText(formattedDate);
        }

    }//GEN-LAST:event_fill_addhours_datefeildActionPerformed

    private void awaycheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_awaycheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_awaycheckActionPerformed

    private void planner_open_maintoolbar_link1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planner_open_maintoolbar_link1ActionPerformed
        // TODO add your handling code here:
             // TODO add your handling code here:
        try {
            // Set the FlatLaf Dark Look and Feel
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        // Create an instance of the planner_gui_form class
        planner_gui_form plannerForm = new planner_gui_form();

        // Set the visibility of the planner_gui_form
        plannerForm.setVisible(true);
                                             
    }//GEN-LAST:event_planner_open_maintoolbar_link1ActionPerformed

    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        PreFillCSVCBData.addSubjectsToCSV();
        FlatDarkLaf.setup();
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // In the constructor or initialization method
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new SSync_GUI_Form().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Labelforaway;
    private javax.swing.JLabel Labelforaway2;
    private javax.swing.JTable SubjectListTable;
    private javax.swing.JTabbedPane TabbedPane_Main;
    private javax.swing.JButton VisitSitePatreon;
    private javax.swing.JMenuItem aboutmenu;
    private javax.swing.JButton addHoursButton;
    private javax.swing.JButton addStudentButton;
    private javax.swing.JButton addSubjectButton;
    private javax.swing.JButton addSubjectButton1;
    private javax.swing.JCheckBox awaycheck;
    private javax.swing.JButton backupdata_documents;
    private javax.swing.JSpinner core_select;
    private javax.swing.JTextField curriculumField;
    private javax.swing.JButton editcbdata;
    private javax.swing.JButton export_csv_daterange;
    private javax.swing.JButton fill_addhours_datefeild;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton generate_pie_chart;
    private javax.swing.JButton houlogframe_exportspreadsheetbutton;
    public javax.swing.JTable hourLog;
    public javax.swing.JFrame hourlogFrame;
    private javax.swing.JTextField hoursField;
    private javax.swing.JButton hours_on_track;
    private javax.swing.JTabbedPane hours_settings_menu;
    private javax.swing.JButton html_export;
    private javax.swing.JButton html_out_report_generate_core_noncore;
    private javax.swing.JButton import_hourlog;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton7;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JDesktopPane jDesktopPane14;
    private javax.swing.JDesktopPane jDesktopPane2;
    private javax.swing.JDesktopPane jDesktopPane3;
    private javax.swing.JDesktopPane jDesktopPane4;
    private javax.swing.JDesktopPane jDesktopPane5;
    private javax.swing.JDesktopPane jDesktopPane6;
    private javax.swing.JDesktopPane jDesktopPane8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    public javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JRadioButton jRadioButton1;
    public javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneStudentlist;
    private javax.swing.JScrollPane jScrollPaneSubjectlist;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JFrame journal_frame;
    private javax.swing.JButton loadInfo;
    private javax.swing.JPanel login;
    private javax.swing.JScrollPane mainScrollPane;
    private javax.swing.JPanel main_input_panle;
    private javax.swing.JButton merge_hourlogs;
    private javax.swing.JMenuItem merge_menu_item;
    private javax.swing.JPanel more_tools;
    private javax.swing.JTextField nameField;
    private javax.swing.JFileChooser openCSVFileChooser;
    private javax.swing.JButton openHourLog;
    private javax.swing.JMenuItem options;
    private javax.swing.JButton planner_open_maintoolbar_link;
    private javax.swing.JButton planner_open_maintoolbar_link1;
    private javax.swing.JButton planner_tools_button;
    private javax.swing.JButton report_daily_button;
    private javax.swing.JPanel report_panle;
    private javax.swing.JButton save_hourlog_main;
    private javax.swing.JComboBox<String> selectStudent;
    private javax.swing.JComboBox<String> selectSubject;
    private javax.swing.JFormattedTextField setDateField;
    private javax.swing.JMenuItem ss_clear;
    private javax.swing.JTable studentListTable;
    private javax.swing.JFrame student_list;
    private javax.swing.JTextField subjectField;
    private javax.swing.JFrame subject_list;
    private javax.swing.JButton transcriptgenloader;
    private javax.swing.JLabel welcome_lable;
    // End of variables declaration//GEN-END:variables

    private static class hoursLogViewer {

        public hoursLogViewer() {
        }
    }
}
