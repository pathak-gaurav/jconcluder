package ca.laurentian.concluder.entityRelations;

//This code in parts was adopted and modified to suit.  Tyler D. Jessup
//Structure used as a template
//Taken from https://www.ociweb.com/javasig/knowledgebase/April1999/JTable.pdf
//Date: 2016-08-02

import javax.swing.table.DefaultTableModel;

public class CommitTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    public CommitTableModel(Object[][] data, String[] columnHeaders) {
        super(data, columnHeaders);
    }

    @Override
    public int getRowCount() {
        return super.getDataVector().size();
    }

    @Override
    public int getColumnCount() {
        return super.getColumnCount();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 3, 4, 5, 6 -> {
                return boolean.class;
            }
            case 7 -> {
                return double.class;
            }
            default -> {
                return String.class;
            }
        }
    }

    @Override
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
            super.setValueAt(aValue, rowIndex, columnIndex);
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
