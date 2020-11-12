package ca.laurentian.concluder.EntityRelations;

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

    public int getRowCount() {
        return super.getDataVector().size();
    }

    public int getColumnCount() {
        return super.getColumnCount();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return String.class;
            }
            case 1: {
                return String.class;
            }
            case 2: {
                return String.class;
            }
            case 3: {
                return boolean.class;
            }
            case 4: {
                return boolean.class;
            }
            case 5: {
                return boolean.class;
            }
            case 6: {
                return boolean.class;
            }
            case 7: {
                return double.class;
            }
            default: {
                return String.class;
            }
        }
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
            super.setValueAt(aValue, rowIndex, columnIndex);
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (Exception e) {
        }
    }
}
