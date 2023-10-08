package HourTracker;

/**
 *
 * @author James
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author James
 */
public class CsvToJTable extends JFrame {
    
    /**
     *
     */
    public JTable table;

    /**
     *
     */
    public DefaultTableModel tableModel;
    
    /**
     *
     */
    public CsvToJTable() {
        setTitle("CSV to JTable Example");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane);
        
        String csvFile = "*.csv";
        String line;
        String[] headers = null;
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            // Read in the headers from the CSV file
            line = br.readLine();
            if (line != null) {
                headers = line.split(",");
                tableModel.setColumnIdentifiers(headers);
            }
            
            // Populate the TableModel with the data from the CSV file
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                tableModel.addRow(data);
            }
            
        } catch (IOException e) {
        }
        
        setVisible(true);
    }
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        CsvToJTable csvToJTable = new CsvToJTable();
    }
}

