/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HourTracker;

/**
 *
 * @author James Knox
 */
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.swing.JTable;

class TablePrintable implements Printable {
    private JTable table;

    public TablePrintable(JTable table) {
        this.table = table;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        // Get the printable area's dimensions
        int x = (int) pageFormat.getImageableX();
        int y = (int) pageFormat.getImageableY();
        int width = (int) pageFormat.getImageableWidth();
        int height = (int) pageFormat.getImageableHeight();

        // Calculate the number of rows and columns to fit in the printable area
        int totalRows = table.getRowCount();
        int totalCols = table.getColumnCount();
        int rowsPerPage = (int) Math.ceil((double) height / table.getRowHeight());
        int colsPerPage = (int) Math.ceil((double) width / table.getColumnModel().getTotalColumnWidth());
        int lastRow = Math.min((pageIndex + 1) * rowsPerPage, totalRows);
        int lastCol = Math.min((pageIndex + 1) * colsPerPage, totalCols);

        // Set up the Graphics object for printing
        graphics.translate(x, y);

        // Print the table content within the printable area
        for (int row = pageIndex * rowsPerPage; row < lastRow; row++) {
            for (int col = pageIndex * colsPerPage; col < lastCol; col++) {
                int viewColumn = table.convertColumnIndexToView(col);
                int modelRow = table.convertRowIndexToModel(row);
                int modelCol = table.convertColumnIndexToModel(viewColumn);
                Object value = table.getModel().getValueAt(modelRow, modelCol);
                String text = (value != null) ? value.toString() : "";
                int cellWidth = table.getColumnModel().getColumn(viewColumn).getWidth();
                int cellHeight = table.getRowHeight(row);
                int cellX = x + ((col % colsPerPage) * cellWidth);
                int cellY = y + ((row % rowsPerPage) * cellHeight);

                // Draw the cell content
                graphics.drawString(text, cellX, cellY + cellHeight);
            }
        }

        return Printable.PAGE_EXISTS;
    }
}