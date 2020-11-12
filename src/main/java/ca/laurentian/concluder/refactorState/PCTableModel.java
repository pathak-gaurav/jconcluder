package ca.laurentian.concluder.refactorState;

//This code in parts was adopted and modified to suit.  Tyler D. Jessup
//Structure used as a template
//Taken from https://www.ociweb.com/javasig/knowledgebase/April1999/JTable.pdf
//Date: 2016-08-02

import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PCTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;
    //define the format of numbers
    //should be Number Format as used elsewhere
    NumberFormat df = new DecimalFormat("#0.000");

    //column headers and row headers should be entity names
    public PCTableModel(Object[][] data, String[] columnHeaders) {
        super(data, columnHeaders);
    }

    public int getRowCount() {
        return super.getDataVector().size();
    }

    public int getColumnCount() {
        return super.getColumnCount();
    }

    //main diagonal is not editable, all other cells true
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return rowIndex != columnIndex;
    }

    //this is bad, should be double
    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    public String getColumnName(int columnIndex) {
        return super.getColumnName(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return super.getValueAt(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            //make sure value is instance of double
            String inValue = (String) aValue;
            double newValue = SystemConfiguration.unformatNumberString(inValue);
            //assign symmetric elements
            super.setValueAt(SystemConfiguration.formatNumber(newValue), rowIndex, columnIndex);
            super.setValueAt("" + SystemConfiguration.formatNumber(1 / newValue), columnIndex, rowIndex);
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (Exception e) {
            //invalid input not instance of double
        }
    }
}
