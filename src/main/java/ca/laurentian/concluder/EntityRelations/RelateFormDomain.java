package ca.laurentian.concluder.EntityRelations;

import ca.laurentian.concluder.refactorState.View_Mode_Administrator;
import ca.laurentian.concluder.refactorState.Weight_Redistributor;
import ca.laurentian.concluder.refactorState.Weight_Reevaluator;
import prefuse.data.Graph;
import prefuse.data.Node;

import javax.swing.*;

public class RelateFormDomain {
    private final String columnNames[] = {"E_i", "E_j", "Linguistic Term", "E_i > E_j", "E_i < E_j", "E_i = E_j ", "Swapped", "Weight"};

    private Object[][] commitTable;
    private JTable table_commit_local;

    private JTextField relateForm_textField_linguisticTerm;
    private JComboBox<String> relateForm_comboBox_linguisticTerm;
    private JComboBox<String> relateForm_comboBox_entityEi;
    private JComboBox<String> relateForm_comboBox_entityEj;
    private JRadioButton relateForm_rdbtnYes_entitiesEqual;
    private JRadioButton relateForm_rdbtnYes_entityEi_g_Ej;
    private JRadioButton relateForm_rdbtnNo_entityEi_g_Ej;
    private JTextField relateForm_textField_relativeWeightEntry;
    private JScrollPane relateForm_scrollpane;
    private JTabbedPane relateForm_tabbedPaneRef;

    private int viewMode;
    //private JSlider relateForm_slider;

    private Node parentNode;
    private Graph g;

    public RelateFormDomain(Node parentNode, Graph g, int viewMode) {
        super();
        this.parentNode = parentNode;
        this.g = g;
        this.viewMode = viewMode;
        retrieveDataBoundComonents();
    }

    public void loadRelateForm() {
        retrieveDataBoundComonents();
        loadCommitTableFromParent();
        addLinguisticTermToListFromParent();
        addChildListToEi();
    }

    private void retrieveDataBoundComonents() {
        relateForm_scrollpane = RelateForm.get_relateForm_scrollPane();
        relateForm_textField_linguisticTerm = RelateForm.get_relateForm_textField_linguisticTerm();
        relateForm_comboBox_linguisticTerm = RelateForm.get_relateForm_comboBox_linguisticTerm();
        relateForm_comboBox_entityEi = RelateForm.get_relateForm_comboBox_entityEi();
        relateForm_comboBox_entityEj = RelateForm.get_relateForm_comboBox_entityEj();
        relateForm_rdbtnYes_entitiesEqual = RelateForm.get_relateForm_rdbtnYes_entitiesEqual();
        relateForm_rdbtnYes_entityEi_g_Ej = RelateForm.get_relateForm_rdbtnYes_entityEi_g_E();
        relateForm_rdbtnNo_entityEi_g_Ej = RelateForm.get_relateForm_rdbtnNo_entityEi_g_E();
        relateForm_textField_relativeWeightEntry = RelateForm.get_relateForm_textField_relativeWeight();
        relateForm_tabbedPaneRef = RelateForm.get_relateForm_tabbedPaneRef();
        //relateForm_slider = RelateForm.get_relateForm_slider();
    }

    public void loadCommitTableFromParent() {
        String linguisticTerm = parentNode.getString("Linguistic_Term");
        double[][] PCMatrix = new Weight_Reevaluator(parentNode, null, -1).PCMatrix;
        int childCount = PCMatrix.length;
        commitTable = new Object[((childCount) * (childCount - 1)) / 2][8];
        int rowIndex = 0;
        for (int row = 0; row < PCMatrix.length; row++) {
            Node childRow = parentNode.getChild(row);
            for (int column = 0; column < PCMatrix[0].length; column++) {
                if (row < column) {
                    Node childAtColumn = parentNode.getChild(column);
                    Object[] record = new Object[8];
                    record[0] = childRow.getString("name");
                    record[1] = childAtColumn.getString("name");
                    record[2] = linguisticTerm;
                    if (PCMatrix[row][column] > 1)
                        record[3] = true;
                    else
                        record[3] = false;
                    if (PCMatrix[row][column] < 1) {
                        record[4] = true;
                        record[6] = true;
                    } else {
                        record[4] = false;
                        record[6] = false;
                    }
                    if (PCMatrix[row][column] == 1)
                        record[5] = true;
                    else
                        record[5] = false;
                    record[7] = PCMatrix[row][column];
                    commitTable[rowIndex] = record;
                    rowIndex++;
                }
            }
        }
        table_commit_local = new JTable(new CommitTableModel(commitTable, columnNames));
        resetUITable();
    }

    public void addLinguisticTermToListFromParent() {
        String linguisticTerm = parentNode.getString("Linguistic_Term");
        relateForm_comboBox_linguisticTerm.addItem(linguisticTerm);
        if (linguisticTerm.compareTo("Greater Than(>)") == 0)
            relateForm_comboBox_linguisticTerm.addItem("Less Than(<)");
        else {
            relateForm_comboBox_linguisticTerm.addItem("Greater Than(<)");
            relateForm_comboBox_linguisticTerm.addItem("Less Than(<)");
        }
        relateForm_comboBox_linguisticTerm.setSelectedIndex(0);
    }

    public void populateUserDefinedLinguisticTerm() {
        relateForm_comboBox_linguisticTerm = RelateForm.get_relateForm_comboBox_linguisticTerm();
        for (int rowIndex = 0; rowIndex < commitTable.length; rowIndex++)
            this.table_commit_local.setValueAt(relateForm_comboBox_linguisticTerm.getItemAt(relateForm_comboBox_linguisticTerm.getSelectedIndex()), rowIndex, 2);
        parentNode.setString("Linguistic_Term", relateForm_comboBox_linguisticTerm.getItemAt(relateForm_comboBox_linguisticTerm.getSelectedIndex()));
    }

    private void addChildListToEi() {
        for (int childIndex = 0; childIndex < parentNode.getChildCount() - 1; childIndex++)
            relateForm_comboBox_entityEi.addItem(parentNode.getChild(childIndex).getString("name"));
        relateForm_comboBox_entityEi.setSelectedIndex(0);
    }

    public void addChildListToEjGivenEi(int selectedIndex) {
        relateForm_comboBox_entityEj.removeAllItems();
        for (int childIndex = selectedIndex + 1; childIndex < parentNode.getChildCount(); childIndex++)
            relateForm_comboBox_entityEj.addItem(parentNode.getChild(childIndex).getString("name"));
        relateForm_comboBox_entityEj.setSelectedIndex(0);
    }

    public void setLocalDomainDataForDataBondComponents(int selectedIndexEi, int selectedIndexEj) {
        setEieEjUI(selectedIndexEi, selectedIndexEj);
        setEigEjUI(selectedIndexEi, selectedIndexEj);
        setEilEjUI(selectedIndexEi, selectedIndexEj);
        setRelativeWeight(selectedIndexEi, selectedIndexEj);
    }

    public void setEieEjUI(int selectedIndexEi, int selectedIndexEj) {
        relateForm_rdbtnYes_entitiesEqual.setSelected((Boolean) table_commit_local.getValueAt
                (((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 5));
    }

    public void setEigEjUI(int selectedIndexEi, int selectedIndexEj) {
        relateForm_rdbtnYes_entityEi_g_Ej.setSelected((Boolean) table_commit_local.getValueAt
                (((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 3));
    }

    public void setEilEjUI(int selectedIndexEi, int selectedIndexEj) {
        relateForm_rdbtnNo_entityEi_g_Ej.setSelected((Boolean) table_commit_local.getValueAt
                (((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 4));
    }

    public void setRelativeWeight(int selectedIndexEi, int selectedIndexEj) {
        relateForm_textField_relativeWeightEntry.setText(String.valueOf((Double) table_commit_local.getValueAt(((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 7)));
        //setSliderPosition((Double)commitTable[((selectedIndexEi*(parentNode.getChildCount()-1))-((selectedIndexEi*(selectedIndexEi-1))/2))+selectedIndexEj][7]);
    }

    private void setOrderPreservation(int selectedIndexEi, int selectedIndexEj) {
        setEieEjTable(selectedIndexEi, selectedIndexEj);
        setEilEjTable(selectedIndexEi, selectedIndexEj);
        setEigEjTable(selectedIndexEi, selectedIndexEj);
    }

    public void setEieEjTable(int selectedIndexEi, int selectedIndexEj) {
        table_commit_local.setValueAt
                (relateForm_rdbtnYes_entitiesEqual.isSelected(),
                        ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 5);
    }

    public void setEilEjTable(int selectedIndexEi, int selectedIndexEj) {
        table_commit_local.setValueAt
                (relateForm_rdbtnNo_entityEi_g_Ej.isSelected(),
                        ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 4);
    }

    public void setEigEjTable(int selectedIndexEi, int selectedIndexEj) {
        table_commit_local.setValueAt
                (relateForm_rdbtnYes_entityEi_g_Ej.isSelected(),
                        ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 3);
    }

    public void addLinguisticTermToListFromUserDefintion() {
        relateForm_comboBox_linguisticTerm.addItem(relateForm_textField_linguisticTerm.getText());
        relateForm_textField_linguisticTerm.setText("");
        relateForm_comboBox_linguisticTerm.setSelectedIndex(relateForm_comboBox_linguisticTerm.getItemCount() - 1);
    }

    public boolean shortCircuitEqualEntities() {
        if (JOptionPane.showConfirmDialog(null, "If Entities are equal continue to next comparison?") == JOptionPane.OK_OPTION) {
            int selectedIndexEi = relateForm_comboBox_entityEi.getSelectedIndex();
            int selectedIndexEj = relateForm_comboBox_entityEj.getSelectedIndex();
            setOrderPreservation(selectedIndexEi, selectedIndexEj);
            table_commit_local.setValueAt
                    (1.0, ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 7);
            ;
            resetUITable();
            return true;
        } else
            return false;
    }

    public boolean commitComparisonDataToCommitTable() {
        retrieveDataBoundComonents();
        if (!validateFormData())
            return false;
        int selectedIndexEi = relateForm_comboBox_entityEi.getSelectedIndex();
        int selectedIndexEj = relateForm_comboBox_entityEj.getSelectedIndex();
        setOrderPreservation(selectedIndexEi, selectedIndexEj);
        if (!relateForm_rdbtnYes_entityEi_g_Ej.isSelected()) {
            table_commit_local.setValueAt
                    (1 / Double.parseDouble(relateForm_textField_relativeWeightEntry.getText()),
                            ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 7);
            ;
            table_commit_local.setValueAt
                    (true,
                            ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 6);
            ;
        } else {
            table_commit_local.setValueAt
                    (Double.parseDouble(relateForm_textField_relativeWeightEntry.getText()),
                            ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 7);
            ;
            table_commit_local.setValueAt
                    (false,
                            ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 6);
            ;
        }
        return true;
    }

    private boolean validateFormData() {
        try {
            Double.parseDouble(relateForm_textField_relativeWeightEntry.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Relative weight must be a valid number.", "Information", JOptionPane.OK_CANCEL_OPTION);
            relateForm_textField_relativeWeightEntry.setText("");
            relateForm_textField_relativeWeightEntry.grabFocus();
            return false;
        }
        if (Double.parseDouble(relateForm_textField_relativeWeightEntry.getText()) == 1 && !relateForm_rdbtnYes_entitiesEqual.isSelected()) {
            JOptionPane.showMessageDialog(null, "Entities are not equal, cannot have relative weight of 1", "Error", JOptionPane.OK_CANCEL_OPTION);
            relateForm_textField_relativeWeightEntry.setText("");
            relateForm_textField_relativeWeightEntry.grabFocus();
            return false;
        } else if (Double.parseDouble(relateForm_textField_relativeWeightEntry.getText()) != 1 && relateForm_rdbtnYes_entitiesEqual.isSelected()) {
            JOptionPane.showMessageDialog(null, "Entities are equal, cannot have relative weight <> 1", "Error", JOptionPane.OK_CANCEL_OPTION);
            relateForm_textField_relativeWeightEntry.setText("");
            relateForm_textField_relativeWeightEntry.grabFocus();
            return false;
        }
        return true;
    }

    public void commitDataToParent() {
        String relativeWeightsComposite = "";
        for (int rowIndex = 0; rowIndex < commitTable.length; rowIndex++)
            relativeWeightsComposite += String.valueOf(table_commit_local.getValueAt(rowIndex, 7)) + " ";
        relativeWeightsComposite = relativeWeightsComposite.substring(0, relativeWeightsComposite.length() - 1);
        parentNode.setString("rel", relativeWeightsComposite);
        Weight_Reevaluator we = new Weight_Reevaluator(parentNode, null, -1);
        we.reevaluate();
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            Weight_Redistributor rw = new Weight_Redistributor(-1);
            rw.Redistribute_From_Root(parentNode.getChild(i));
        }
        new View_Mode_Administrator(viewMode, g);
    }

    private void resetUITable() {
        final JTable table_commit = new JTable(table_commit_local.getModel());
        table_commit_local = table_commit;
        for (int columnIndex = 0; columnIndex < table_commit.getColumnCount(); columnIndex++)
            table_commit.getColumnModel().getColumn(columnIndex).setCellRenderer(new BasicCellRenderer());
        table_commit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedIndexEi = -1;
                int selectedIndexEj = -1;
                int row = table_commit.rowAtPoint(evt.getPoint());
                int col = table_commit.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    ComboBoxModel<String> cbm_entityEi = relateForm_comboBox_entityEi.getModel();
                    ComboBoxModel<String> cbm_entityEj = relateForm_comboBox_entityEj.getModel();
                    int size = cbm_entityEi.getSize();
                    for (int i = 0; i < size; i++) {
                        String element = (String) cbm_entityEi.getElementAt(i);
                        if (element.compareTo((String) table_commit.getValueAt(row, 0)) == 0)
                            selectedIndexEi = i;
                    }
                    relateForm_comboBox_entityEi.setSelectedIndex(selectedIndexEi);
                    size = cbm_entityEj.getSize();
                    for (int i = 0; i < size; i++) {
                        String element = (String) cbm_entityEj.getElementAt(i);
                        if (element.compareTo((String) table_commit.getValueAt(row, 1)) == 0)
                            selectedIndexEj = i;
                    }
                    selectedIndexEj = row - ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2));
                    relateForm_comboBox_entityEj.setSelectedIndex(selectedIndexEj);
                    switch (col) {
                        case 0:
                            relateForm_tabbedPaneRef.setSelectedIndex(1);
                            break;
                        case 1:
                            relateForm_tabbedPaneRef.setSelectedIndex(1);
                            break;
                        case 2:
                            relateForm_tabbedPaneRef.setSelectedIndex(0);
                            break;
                        case 3:
                            relateForm_tabbedPaneRef.setSelectedIndex(2);
                            break;
                        case 4:
                            relateForm_tabbedPaneRef.setSelectedIndex(2);
                            break;
                        case 5:
                            relateForm_tabbedPaneRef.setSelectedIndex(2);
                            break;
                        case 6:
                            relateForm_tabbedPaneRef.setSelectedIndex(2);
                            break;
                        case 7:
                            relateForm_tabbedPaneRef.setSelectedIndex(3);
                            break;
                    }
                }
            }
        });
        relateForm_scrollpane.getViewport().removeAll();
        relateForm_scrollpane.getViewport().add(table_commit);
        relateForm_scrollpane.validate();
        relateForm_scrollpane.repaint();
    }
	
	/*
	public void navigateToRowColumnCell__Tab(int row, int col)
	{}
	
	public void setRelativeWieghtUponSilderMovement()
	{}
	
	public void setSliderPosition(int amountIncrease)
	{relateForm_slider.setValue(amountIncrease);}
	*/
}
