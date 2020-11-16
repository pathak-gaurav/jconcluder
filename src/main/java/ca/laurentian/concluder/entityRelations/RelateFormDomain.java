package ca.laurentian.concluder.entityRelations;

import ca.laurentian.concluder.refactorState.View_Mode_Administrator;
import ca.laurentian.concluder.refactorState.Weight_Redistributor;
import ca.laurentian.concluder.refactorState.Weight_Reevaluator;
import prefuse.data.Graph;
import prefuse.data.Node;

import javax.swing.*;

import static ca.laurentian.concluder.constants.ConcluderConstant.GREATER_THAN;
import static ca.laurentian.concluder.constants.ConcluderConstant.LESS_THAN;
import static ca.laurentian.concluder.constants.ConcluderConstant.LINGUISTIC_TERM;

public class RelateFormDomain {
    private final String[] columnNames = {"E_i", "E_j", "Linguistic Term", "E_i > E_j", "E_i < E_j", "E_i = E_j ", "Swapped", "Weight"};

    private Object[][] commitTable;
    private JTable tableCommitLocal;

    private JTextField relateFormTextFieldLinguisticTerm;
    private JComboBox<String> relateFormComboBoxLinguisticTerm;
    private JComboBox<String> relateFormComboBoxEntityEi;
    private JComboBox<String> relateFormComboBoxEntityEj;
    private JRadioButton relateFormRadioButtonYesEntitiesEqual;
    private JRadioButton relateFormRadioButtonYesEntityEiGEj;
    private JRadioButton relateFormRadioButtonNoEntityEiGEj;
    private JTextField relateFormTextFieldRelativeWeightEntry;
    private JScrollPane relateFormScrollPane;
    private JTabbedPane relateFormTabbedPaneRef;

    private final int viewMode;

    private final Node parentNode;
    private final Graph g;

    public RelateFormDomain(Node parentNode, Graph g, int viewMode) {
        super();
        this.parentNode = parentNode;
        this.g = g;
        this.viewMode = viewMode;
        retrieveDataBoundComponents();
    }

    public void loadRelateForm() {
        retrieveDataBoundComponents();
        loadCommitTableFromParent();
        addLinguisticTermToListFromParent();
        addChildListToEi();
    }

    private void retrieveDataBoundComponents() {
        relateFormScrollPane = RelateForm.getRelateFormScrollPane();
        relateFormTextFieldLinguisticTerm = RelateForm.getRelateFormTextFieldLinguisticTerm();
        relateFormComboBoxLinguisticTerm = RelateForm.getRelateFormComboBoxLinguisticTerm();
        relateFormComboBoxEntityEi = RelateForm.getRelateFormComboBoxEntityEi();
        relateFormComboBoxEntityEj = RelateForm.getRelateFormComboBoxEntityEj();
        relateFormRadioButtonYesEntitiesEqual = RelateForm.getRelateFormRadioButtonYesEntitiesEqual();
        relateFormRadioButtonYesEntityEiGEj = RelateForm.getRelateFormRadioButtonYesEntityEiGE();
        relateFormRadioButtonNoEntityEiGEj = RelateForm.getRelateFormRadioButtonNoEntityEiGE();
        relateFormTextFieldRelativeWeightEntry = RelateForm.getRelateFormTextFieldRelativeWeight();
        relateFormTabbedPaneRef = RelateForm.getRelateFormTabbedPaneRef();
    }

    public void loadCommitTableFromParent() {
        String linguisticTerm = parentNode.getString(LINGUISTIC_TERM);
        double[][] pcMatrix = new Weight_Reevaluator(parentNode, null, -1).PCMatrix;
        int childCount = pcMatrix.length;
        commitTable = new Object[((childCount) * (childCount - 1)) / 2][8];
        int rowIndex = 0;
        for (int row = 0; row < pcMatrix.length; row++) {
            Node childRow = parentNode.getChild(row);
            for (int column = 0; column < pcMatrix[0].length; column++) {
                if (row < column) {
                    Node childAtColumn = parentNode.getChild(column);
                    Object[] record = new Object[8];
                    record[0] = childRow.getString("name");
                    record[1] = childAtColumn.getString("name");
                    record[2] = linguisticTerm;
                    if (pcMatrix[row][column] > 1)
                        record[3] = true;
                    else
                        record[3] = false;
                    if (pcMatrix[row][column] < 1) {
                        record[4] = true;
                        record[6] = true;
                    } else {
                        record[4] = false;
                        record[6] = false;
                    }
                    if (pcMatrix[row][column] == 1)
                        record[5] = true;
                    else
                        record[5] = false;
                    record[7] = pcMatrix[row][column];
                    commitTable[rowIndex] = record;
                    rowIndex++;
                }
            }
        }
        tableCommitLocal = new JTable(new CommitTableModel(commitTable, columnNames));
        resetUITable();
    }

    public void addLinguisticTermToListFromParent() {
        String linguisticTerm = parentNode.getString(LINGUISTIC_TERM);
        relateFormComboBoxLinguisticTerm.addItem(linguisticTerm);
        if (linguisticTerm.compareTo(GREATER_THAN) != 0) {
            relateFormComboBoxLinguisticTerm.addItem("Greater Than(<)");
        }
        relateFormComboBoxLinguisticTerm.addItem(LESS_THAN);
        relateFormComboBoxLinguisticTerm.setSelectedIndex(0);
    }

    public void populateUserDefinedLinguisticTerm() {
        relateFormComboBoxLinguisticTerm = RelateForm.getRelateFormComboBoxLinguisticTerm();
        for (int rowIndex = 0; rowIndex < commitTable.length; rowIndex++)
            this.tableCommitLocal.setValueAt(
                    relateFormComboBoxLinguisticTerm.getItemAt(relateFormComboBoxLinguisticTerm.getSelectedIndex()), rowIndex, 2);
        parentNode.setString(LINGUISTIC_TERM, relateFormComboBoxLinguisticTerm.getItemAt(
                relateFormComboBoxLinguisticTerm.getSelectedIndex()));
    }

    private void addChildListToEi() {
        for (int childIndex = 0; childIndex < parentNode.getChildCount() - 1; childIndex++)
            relateFormComboBoxEntityEi.addItem(parentNode.getChild(childIndex).getString("name"));
        relateFormComboBoxEntityEi.setSelectedIndex(0);
    }

    public void addChildListToEjGivenEi(int selectedIndex) {
        relateFormComboBoxEntityEj.removeAllItems();
        for (int childIndex = selectedIndex + 1; childIndex < parentNode.getChildCount(); childIndex++)
            relateFormComboBoxEntityEj.addItem(parentNode.getChild(childIndex).getString("name"));
        relateFormComboBoxEntityEj.setSelectedIndex(0);
    }

    public void setLocalDomainDataForDataBondComponents(int selectedIndexEi, int selectedIndexEj) {
        setEieEjUI(selectedIndexEi, selectedIndexEj);
        setEigEjUI(selectedIndexEi, selectedIndexEj);
        setEilEjUI(selectedIndexEi, selectedIndexEj);
        setRelativeWeight(selectedIndexEi, selectedIndexEj);
    }

    public void setEieEjUI(int selectedIndexEi, int selectedIndexEj) {
        relateFormRadioButtonYesEntitiesEqual.setSelected((Boolean) tableCommitLocal.getValueAt
                (((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 5));
    }

    public void setEigEjUI(int selectedIndexEi, int selectedIndexEj) {
        relateFormRadioButtonYesEntityEiGEj.setSelected((Boolean) tableCommitLocal.getValueAt
                (((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 3));
    }

    public void setEilEjUI(int selectedIndexEi, int selectedIndexEj) {
        relateFormRadioButtonNoEntityEiGEj.setSelected((Boolean) tableCommitLocal.getValueAt
                (((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 4));
    }

    public void setRelativeWeight(int selectedIndexEi, int selectedIndexEj) {
        relateFormTextFieldRelativeWeightEntry.setText(String.valueOf(
                tableCommitLocal.getValueAt(((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 7)));
    }

    private void setOrderPreservation(int selectedIndexEi, int selectedIndexEj) {
        setEieEjTable(selectedIndexEi, selectedIndexEj);
        setEilEjTable(selectedIndexEi, selectedIndexEj);
        setEigEjTable(selectedIndexEi, selectedIndexEj);
    }

    public void setEieEjTable(int selectedIndexEi, int selectedIndexEj) {
        tableCommitLocal.setValueAt
                (relateFormRadioButtonYesEntitiesEqual.isSelected(),
                        ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 5);
    }

    public void setEilEjTable(int selectedIndexEi, int selectedIndexEj) {
        tableCommitLocal.setValueAt
                (relateFormRadioButtonNoEntityEiGEj.isSelected(),
                        ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 4);
    }

    public void setEigEjTable(int selectedIndexEi, int selectedIndexEj) {
        tableCommitLocal.setValueAt
                (relateFormRadioButtonYesEntityEiGEj.isSelected(),
                        ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 3);
    }

    public void addLinguisticTermToListFromUserDefinition() {
        relateFormComboBoxLinguisticTerm.addItem(relateFormTextFieldLinguisticTerm.getText());
        relateFormTextFieldLinguisticTerm.setText("");
        relateFormComboBoxLinguisticTerm.setSelectedIndex(relateFormComboBoxLinguisticTerm.getItemCount() - 1);
    }

    public boolean shortCircuitEqualEntities() {
        if (JOptionPane.showConfirmDialog(null, "If Entities are equal continue to next comparison?") == JOptionPane.OK_OPTION) {
            int selectedIndexEi = relateFormComboBoxEntityEi.getSelectedIndex();
            int selectedIndexEj = relateFormComboBoxEntityEj.getSelectedIndex();
            setOrderPreservation(selectedIndexEi, selectedIndexEj);
            tableCommitLocal.setValueAt
                    (1.0, ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 7);
            resetUITable();
            return true;
        } else
            return false;
    }

    public boolean commitComparisonDataToCommitTable() {
        retrieveDataBoundComponents();
        if (!validateFormData())
            return false;
        int selectedIndexEi = relateFormComboBoxEntityEi.getSelectedIndex();
        int selectedIndexEj = relateFormComboBoxEntityEj.getSelectedIndex();
        setOrderPreservation(selectedIndexEi, selectedIndexEj);
        if (!relateFormRadioButtonYesEntityEiGEj.isSelected()) {
            tableCommitLocal.setValueAt
                    (1 / Double.parseDouble(relateFormTextFieldRelativeWeightEntry.getText()),
                     ((selectedIndexEi * (parentNode.getChildCount() - 1)) -
                      ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj,
                     7);
            tableCommitLocal.setValueAt
                    (true,
                     ((selectedIndexEi * (parentNode.getChildCount() - 1)) -
                      ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj,
                     6);
        } else {
            tableCommitLocal.setValueAt
                    (Double.parseDouble(relateFormTextFieldRelativeWeightEntry.getText()),
                     ((selectedIndexEi * (parentNode.getChildCount() - 1)) -
                      ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 7);
            tableCommitLocal.setValueAt
                    (false,
                     ((selectedIndexEi * (parentNode.getChildCount() - 1)) -
                      ((selectedIndexEi * (selectedIndexEi - 1)) / 2)) + selectedIndexEj, 6);
        }
        return true;
    }

    private boolean validateFormData() {
        try {
            Double.parseDouble(relateFormTextFieldRelativeWeightEntry.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Relative weight must be a valid number.", "Information",
                                          JOptionPane.WARNING_MESSAGE);
            relateFormTextFieldRelativeWeightEntry.setText("");
            relateFormTextFieldRelativeWeightEntry.grabFocus();
            return false;
        }
        if (Double.parseDouble(relateFormTextFieldRelativeWeightEntry.getText()) == 1 && !relateFormRadioButtonYesEntitiesEqual.isSelected()) {
            JOptionPane.showMessageDialog(null, "Entities are not equal, cannot have relative weight of 1", "Error",
                                          JOptionPane.WARNING_MESSAGE);
            relateFormTextFieldRelativeWeightEntry.setText("");
            relateFormTextFieldRelativeWeightEntry.grabFocus();
            return false;
        } else if (Double.parseDouble(relateFormTextFieldRelativeWeightEntry.getText()) != 1 && relateFormRadioButtonYesEntitiesEqual.isSelected()) {
            JOptionPane.showMessageDialog(null, "Entities are equal, cannot have relative weight <> 1", "Error",
                                          JOptionPane.WARNING_MESSAGE);
            relateFormTextFieldRelativeWeightEntry.setText("");
            relateFormTextFieldRelativeWeightEntry.grabFocus();
            return false;
        }
        return true;
    }

    public void commitDataToParent() {
        StringBuilder relativeWeightsCompositeBuilder = new StringBuilder();
        for (int rowIndex = 0; rowIndex < commitTable.length; rowIndex++)
            relativeWeightsCompositeBuilder.append(tableCommitLocal.getValueAt(rowIndex, 7)).append(" ");
        String relativeWeightsComposite = relativeWeightsCompositeBuilder.toString();
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
        final JTable table_commit = new JTable(tableCommitLocal.getModel());
        tableCommitLocal = table_commit;
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
                    ComboBoxModel<String> cbm_entityEi = relateFormComboBoxEntityEi.getModel();
                    ComboBoxModel<String> cbm_entityEj = relateFormComboBoxEntityEj.getModel();
                    int size = cbm_entityEi.getSize();
                    for (int i = 0; i < size; i++) {
                        String element = cbm_entityEi.getElementAt(i);
                        if (element.compareTo((String) table_commit.getValueAt(row, 0)) == 0)
                            selectedIndexEi = i;
                    }
                    relateFormComboBoxEntityEi.setSelectedIndex(selectedIndexEi);
                    size = cbm_entityEj.getSize();
                    for (int i = 0; i < size; i++) {
                        String element = cbm_entityEj.getElementAt(i);
                        if (element.compareTo((String) table_commit.getValueAt(row, 1)) == 0)
                            selectedIndexEj = i;
                    }
                    selectedIndexEj = row - ((selectedIndexEi * (parentNode.getChildCount() - 1)) - ((selectedIndexEi * (selectedIndexEi - 1)) / 2));
                    relateFormComboBoxEntityEj.setSelectedIndex(selectedIndexEj);
                    switch (col) {
                        case 0, 1 -> relateFormTabbedPaneRef.setSelectedIndex(1);
                        case 2 -> relateFormTabbedPaneRef.setSelectedIndex(0);
                        case 3, 4, 5, 6 -> relateFormTabbedPaneRef.setSelectedIndex(2);
                        case 7 -> relateFormTabbedPaneRef.setSelectedIndex(3);
                        default -> throw new IllegalStateException("Unexpected value: " + col);
                    }
                }
            }
        });
        relateFormScrollPane.getViewport().removeAll();
        relateFormScrollPane.getViewport().add(table_commit);
        relateFormScrollPane.validate();
        relateFormScrollPane.repaint();
    }
}
