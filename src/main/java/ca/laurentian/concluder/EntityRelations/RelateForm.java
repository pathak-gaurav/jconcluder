package ca.laurentian.concluder.EntityRelations;

import prefuse.data.Graph;
import prefuse.data.Node;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.util.Map;


public class RelateForm extends JDialog {

    private static final long serialVersionUID = 1L;

    private static JTextField textField_linguisticTerm;
    private static JComboBox<String> comboBox_linguisticTerm;

    private static JComboBox<String> comboBox_entityEi;
    private static JComboBox<String> comboBox_entityEj;

    private static JRadioButton rdbtnYes_entitiesEqual;
    private static JRadioButton rdbtnYes_entityEi_g_Ej;
    private static JRadioButton rdbtnNo_entityEi_g_Ej;
    private static JTextField textField_relativeWeight;

    private static JScrollPane scrollPane;

    private static JSlider slider;

    private static JTabbedPane tabbedPaneRef;

    private RelateFormDomain rfd;

    public RelateForm(Node _parentNode, Graph g, int _viewMode) {
        final JDialog ref = this;
        final Node parentNode = _parentNode;
        final Graph graph = g;
        final int viewMode = _viewMode;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                ref.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                rfd = new RelateFormDomain(parentNode, graph, viewMode);
                rfd.loadRelateForm();
                ref.validate();
                ref.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        setTitle("Entity Relations");
        setLocationRelativeTo(null);
        setBounds(100, 100, 850, 624);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0};
        gridBagLayout.columnWeights = new double[]{.95, .025, .025};
        gridBagLayout.rowWeights = new double[]{.25, .70, 0.05};
        getContentPane().setLayout(gridBagLayout);

        final JButton btnNewButton_1 = new JButton("<< Prev");
        final JButton btnNewButton_2 = new JButton("Next >>");
        final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPaneRef = tabbedPane;

        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnNewButton_2.setEnabled(true);
                if (tabbedPane.getSelectedIndex() == 1)
                    btnNewButton_1.setEnabled(false);
                else
                    btnNewButton_1.setEnabled(true);
                tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() - 1);
            }
        });

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Linguistic Expression Specification", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 5, 5);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        gbc_panel.gridwidth = 3;
        getContentPane().add(panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.5, 0.5, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.25, 0.75, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel label = new JLabel("Specify a linguistic expression by one of two ways (a drop down list or a user text) to be used in the entire process.");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.anchor = GridBagConstraints.WEST;
        gbc_label.gridwidth = 2;
        gbc_label.insets = new Insets(10, 0, 10, 0);
        gbc_label.gridx = 0;
        gbc_label.gridy = 0;
        panel.add(label, gbc_label);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Program Defined Expressions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.fill = GridBagConstraints.BOTH;
        gbc_panel_1.insets = new Insets(0, 0, 0, 5);
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 1;
        panel.add(panel_1, gbc_panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[]{0, 0, 0};
        gbl_panel_1.rowHeights = new int[]{0, 0, 0};
        gbl_panel_1.columnWeights = new double[]{0.03, 0.97, Double.MIN_VALUE};
        gbl_panel_1.rowWeights = new double[]{0.5, 0.5, Double.MIN_VALUE};
        panel_1.setLayout(gbl_panel_1);

        JLabel label_1 = new JLabel("Default Expressions:");
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.anchor = GridBagConstraints.EAST;
        gbc_label_1.insets = new Insets(0, 0, 5, 5);
        gbc_label_1.gridx = 0;
        gbc_label_1.gridy = 0;
        panel_1.add(label_1, gbc_label_1);

        comboBox_linguisticTerm = new JComboBox<String>();
        comboBox_linguisticTerm.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    rfd.populateUserDefinedLinguisticTerm();
                }
            }
        });
        GridBagConstraints gbc_comboBox_linguisticTerm = new GridBagConstraints();
        gbc_comboBox_linguisticTerm.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_linguisticTerm.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox_linguisticTerm.gridx = 1;
        gbc_comboBox_linguisticTerm.gridy = 0;
        panel_1.add(comboBox_linguisticTerm, gbc_comboBox_linguisticTerm);

        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(null, "User Defined Expressions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_2 = new GridBagConstraints();
        gbc_panel_2.fill = GridBagConstraints.BOTH;
        gbc_panel_2.gridx = 1;
        gbc_panel_2.gridy = 1;
        panel.add(panel_2, gbc_panel_2);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{0, 0, 0};
        gbl_panel_2.rowHeights = new int[]{0, 0, 0};
        gbl_panel_2.columnWeights = new double[]{0.03, 0.97, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{0.5, 0.5, Double.MIN_VALUE};
        panel_2.setLayout(gbl_panel_2);

        JLabel label_2 = new JLabel("Enter expression:");
        GridBagConstraints gbc_label_2 = new GridBagConstraints();
        gbc_label_2.anchor = GridBagConstraints.EAST;
        gbc_label_2.insets = new Insets(0, 0, 5, 5);
        gbc_label_2.gridx = 0;
        gbc_label_2.gridy = 0;
        panel_2.add(label_2, gbc_label_2);

        textField_linguisticTerm = new JTextField();
        textField_linguisticTerm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rfd.addLinguisticTermToListFromUserDefintion();
            }
        });
        textField_linguisticTerm.setColumns(10);
        GridBagConstraints gbc_textField_linguisticTerm = new GridBagConstraints();
        gbc_textField_linguisticTerm.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_linguisticTerm.insets = new Insets(0, 0, 5, 0);
        gbc_textField_linguisticTerm.gridx = 1;
        gbc_textField_linguisticTerm.gridy = 0;
        panel_2.add(textField_linguisticTerm, gbc_textField_linguisticTerm);

        JButton button = new JButton("Add Expression");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rfd.addLinguisticTermToListFromUserDefintion();
            }
        });
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.anchor = GridBagConstraints.FIRST_LINE_END;
        gbc_button.gridx = 1;
        gbc_button.gridy = 1;
        panel_2.add(button, gbc_button);
        GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
        gbc_btnNewButton_1.gridx = 1;
        gbc_btnNewButton_1.gridy = 2;
        gbc_btnNewButton_1.insets = new Insets(0, 0, 0, 5);
        gbc_btnNewButton_1.fill = GridBagConstraints.BOTH;
        getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);
        btnNewButton_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnNewButton_1.setEnabled(true);
                if (tabbedPane.getSelectedIndex() == tabbedPane.getTabCount() - 2)
                    btnNewButton_2.setEnabled(false);
                else
                    btnNewButton_2.setEnabled(true);
                tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
            }
        });
        GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
        gbc_btnNewButton_2.gridx = 2;
        gbc_btnNewButton_2.gridy = 2;
        gbc_btnNewButton_2.fill = GridBagConstraints.BOTH;
        getContentPane().add(btnNewButton_2, gbc_btnNewButton_2);


        tabbedPane.addTab("Instructions", createInstructionsTab());
        tabbedPane.addTab("Enities to Compare", createEntitiesToCompareTab());
        tabbedPane.addTab("Order Preservation", createOrderPreservationTab());
        tabbedPane.addTab("Comparison Scale Value", createComparisonScaleValueTab());
        tabbedPane.addTab("Commit Data", createSummaryTab());
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                btnNewButton_2.setEnabled(true);
                btnNewButton_1.setEnabled(true);
                if (index == 0)
                    btnNewButton_1.setEnabled(false);

                if (index == tabbedPane.getTabCount() - 1)
                    btnNewButton_2.setEnabled(false);
            }
        };
        tabbedPane.addChangeListener(changeListener);

        GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
        gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
        gbc_tabbedPane.fill = GridBagConstraints.BOTH;
        gbc_tabbedPane.gridx = 0;
        gbc_tabbedPane.gridy = 1;
        gbc_tabbedPane.gridwidth = 3;
        getContentPane().add(tabbedPane, gbc_tabbedPane);
        btnNewButton_1.setEnabled(false);
        setVisible(true);
    }

    public static JScrollPane get_relateForm_scrollPane() {
        return RelateForm.scrollPane;
    }

    public static JSlider get_relateForm_slider() {
        return RelateForm.slider;
    }

    public static JTextField get_relateForm_textField_linguisticTerm() {
        return RelateForm.textField_linguisticTerm;
    }

    public static JComboBox<String> get_relateForm_comboBox_linguisticTerm() {
        return comboBox_linguisticTerm;
    }

    public static JComboBox<String> get_relateForm_comboBox_entityEi() {
        return comboBox_entityEi;
    }

    public static JComboBox<String> get_relateForm_comboBox_entityEj() {
        return comboBox_entityEj;
    }

    public static JRadioButton get_relateForm_rdbtnYes_entitiesEqual() {
        return rdbtnYes_entitiesEqual;
    }

    public static JRadioButton get_relateForm_rdbtnYes_entityEi_g_E() {
        return rdbtnYes_entityEi_g_Ej;
    }

    public static JTextField get_relateForm_textField_relativeWeight() {
        return textField_relativeWeight;
    }

    public static JRadioButton get_relateForm_rdbtnNo_entityEi_g_E() {
        return rdbtnNo_entityEi_g_Ej;
    }

    public static JTabbedPane get_relateForm_tabbedPaneRef() {
        return tabbedPaneRef;
    }

    private JPanel createInstructionsTab() {
        JPanel p = new JPanel();
        p.setBorder(new TitledBorder(null, "Entity Relations Instructions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagLayout gbl_p = new GridBagLayout();
        gbl_p.columnWidths = new int[]{0};
        gbl_p.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        gbl_p.columnWeights = new double[]{1.0};
        gbl_p.rowWeights = new double[]{1.42, 1.42, 1.42, 1.42, 1.42, 1.42, 1.42, 0.0};
        p.setLayout(gbl_p);

        JLabel lblNewLabel = new JLabel("This task is to relate entities (objects or alternatives) to each other by two or four steps depending on the situation.");
        lblNewLabel.setForeground(Color.BLACK);
        Font font = lblNewLabel.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        lblNewLabel.setFont(font.deriveFont(attributes));


        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.insets = new Insets(3, 0, 10, 0);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
        p.add(lblNewLabel, gbc_lblNewLabel);

        JLabel lblNewLabel_2 = new JLabel("Step 1.  Specify a linguistic expression by one of two ways (a drop down list or a user text) to be used in the entire process.");
        GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
        gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_2.gridx = 0;
        gbc_lblNewLabel_2.gridy = 1;
        gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
        p.add(lblNewLabel_2, gbc_lblNewLabel_2);

        JLabel lblStepSelect = new JLabel("Step 2.  Select two entities to compare.");
        GridBagConstraints gbc_lblStepSelect = new GridBagConstraints();
        gbc_lblStepSelect.insets = new Insets(0, 0, 5, 0);
        gbc_lblStepSelect.gridx = 0;
        gbc_lblStepSelect.gridy = 2;
        gbc_lblStepSelect.anchor = GridBagConstraints.WEST;
        p.add(lblStepSelect, gbc_lblStepSelect);

        JLabel lblStepSwap = new JLabel("Step 3.  Swap entities E_i and E_j to simplify comparisons \"larger\"(or \"more important\" or as defined in Step 1) to \"smaller\".");
        GridBagConstraints gbc_lblStepSwap = new GridBagConstraints();
        gbc_lblStepSwap.insets = new Insets(0, 0, 0, 0);
        gbc_lblStepSwap.gridx = 0;
        gbc_lblStepSwap.gridy = 3;
        gbc_lblStepSwap.anchor = GridBagConstraints.WEST;
        p.add(lblStepSwap, gbc_lblStepSwap);

        JLabel lblEqualityOfEntities = new JLabel("Equality of entities will terminate further data entry.");
        GridBagConstraints gbc_lblEqualityOfEntities = new GridBagConstraints();
        gbc_lblEqualityOfEntities.insets = new Insets(0, 45, 5, 0);
        gbc_lblEqualityOfEntities.gridx = 0;
        gbc_lblEqualityOfEntities.gridy = 4;
        gbc_lblEqualityOfEntities.anchor = GridBagConstraints.WEST;
        p.add(lblEqualityOfEntities, gbc_lblEqualityOfEntities);

        JLabel lblStepEnter = new JLabel("Step 4.  Enter the value via slider or enter manually as number from a scale 1 to [User Defined Upper Bound]");
        GridBagConstraints gbc_lblStepEnter = new GridBagConstraints();
        gbc_lblStepEnter.insets = new Insets(0, 0, 5, 0);
        gbc_lblStepEnter.gridx = 0;
        gbc_lblStepEnter.gridy = 5;
        gbc_lblStepEnter.anchor = GridBagConstraints.WEST;
        p.add(lblStepEnter, gbc_lblStepEnter);

        JLabel lblSepSubmit = new JLabel("Step 5.  Commit comparison data For E_i and E_j.  Repeat Step 2. through Step 4. until all entities are compared.");
        GridBagConstraints gbc_lblSepSubmit = new GridBagConstraints();
        gbc_lblSepSubmit.insets = new Insets(0, 0, 5, 0);
        gbc_lblSepSubmit.gridx = 0;
        gbc_lblSepSubmit.gridy = 6;
        gbc_lblSepSubmit.anchor = GridBagConstraints.WEST;
        p.add(lblSepSubmit, gbc_lblSepSubmit);

        JLabel lblStepReview = new JLabel("Step 6.  Commit all data, or select on error and reprocess until data is accepted.");
        GridBagConstraints gbc_lblStepReview = new GridBagConstraints();
        gbc_lblStepReview.gridx = 0;
        gbc_lblStepReview.gridy = 7;
        gbc_lblStepReview.insets = new Insets(0, 0, 5, 0);
        gbc_lblStepReview.anchor = GridBagConstraints.WEST;
        p.add(lblStepReview, gbc_lblStepReview);

        return p;
    }

    private JPanel createEntitiesToCompareTab() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Entities to Compare", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 2;

        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.05, .45, .05, .45};
        gbl_panel.rowWeights = new double[]{0.0, .03, .97};
        panel.setLayout(gbl_panel);

        JLabel lblStepSelect = new JLabel("Step 2.  Select two entities to compare.");
        GridBagConstraints gbc_lblStepSelect = new GridBagConstraints();
        gbc_lblStepSelect.insets = new Insets(10, 0, 10, 0);
        gbc_lblStepSelect.gridx = 0;
        gbc_lblStepSelect.gridy = 0;
        gbc_lblStepSelect.gridwidth = 4;
        gbc_lblStepSelect.anchor = GridBagConstraints.WEST;
        panel.add(lblStepSelect, gbc_lblStepSelect);

        JLabel lblEntityA = new JLabel("Entity E_i:");
        GridBagConstraints gbc_lblEntityA = new GridBagConstraints();
        gbc_lblEntityA.insets = new Insets(0, 0, 5, 5);
        gbc_lblEntityA.gridx = 0;
        gbc_lblEntityA.gridy = 1;
        gbc_lblEntityA.anchor = GridBagConstraints.EAST;
        panel.add(lblEntityA, gbc_lblEntityA);

        comboBox_entityEi = new JComboBox<String>();
        comboBox_entityEi.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    rfd.addChildListToEjGivenEi(comboBox_entityEi.getSelectedIndex());
                }
            }
        });
        GridBagConstraints gbc_comboBox_entityEi = new GridBagConstraints();
        gbc_comboBox_entityEi.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox_entityEi.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_entityEi.gridx = 1;
        gbc_comboBox_entityEi.gridy = 1;
        panel.add(comboBox_entityEi, gbc_comboBox_entityEi);

        JLabel lblEntityB = new JLabel("Entity E_j:");
        GridBagConstraints gbc_lblEntityB = new GridBagConstraints();
        gbc_lblEntityB.anchor = GridBagConstraints.EAST;
        gbc_lblEntityB.insets = new Insets(0, 0, 5, 5);
        gbc_lblEntityB.gridx = 2;
        gbc_lblEntityB.gridy = 1;
        panel.add(lblEntityB, gbc_lblEntityB);

        comboBox_entityEj = new JComboBox<String>();
        comboBox_entityEj.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    rfd.setLocalDomainDataForDataBondComponents(comboBox_entityEi.getSelectedIndex(), comboBox_entityEj.getSelectedIndex());
                }
            }
        });
        GridBagConstraints gbc_comboBox_entityEj = new GridBagConstraints();
        gbc_comboBox_entityEj.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox_entityEj.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox_entityEj.gridx = 3;
        gbc_comboBox_entityEj.gridy = 1;
        panel.add(comboBox_entityEj, gbc_comboBox_entityEj);

        return panel;
    }

    private JPanel createOrderPreservationTab() {
        JPanel panel_7 = new JPanel();
        panel_7.setBorder(new TitledBorder(null, "Order Preservation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_7 = new GridBagConstraints();
        gbc_panel_7.insets = new Insets(0, 0, 5, 0);
        gbc_panel_7.fill = GridBagConstraints.BOTH;
        gbc_panel_7.gridx = 0;
        gbc_panel_7.gridy = 3;

        GridBagLayout gbl_panel_7 = new GridBagLayout();
        gbl_panel_7.columnWidths = new int[]{0, 0};
        gbl_panel_7.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        gbl_panel_7.columnWeights = new double[]{.5, 1.0};
        gbl_panel_7.rowWeights = new double[]{.25, 0.0, 1.0, .25, .25, .25};
        panel_7.setLayout(gbl_panel_7);

        JLabel lblNewLabel_2 = new JLabel("Step 3.  Swap entities E_i and E_j to simplify comparisons \"larger\"(or \"more important\" or as defined in Step 1) to \"smaller\".");
        GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
        gbc_lblNewLabel_2.insets = new Insets(10, 0, 5, 0);
        gbc_lblNewLabel_2.gridx = 0;
        gbc_lblNewLabel_2.gridy = 0;
        gbc_lblNewLabel_2.gridwidth = 3;
        gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
        panel_7.add(lblNewLabel_2, gbc_lblNewLabel_2);

        JLabel lblEqualityOfEntities = new JLabel("Equality of entities will terminate further data entry.");
        GridBagConstraints gbc_lblEqualityOfEntities = new GridBagConstraints();
        gbc_lblEqualityOfEntities.insets = new Insets(0, 45, 5, 5);
        gbc_lblEqualityOfEntities.gridx = 0;
        gbc_lblEqualityOfEntities.gridy = 1;
        gbc_lblEqualityOfEntities.anchor = GridBagConstraints.WEST;
        panel_7.add(lblEqualityOfEntities, gbc_lblEqualityOfEntities);

        JLabel lblNewLabel_3 = new JLabel("E_i  =  E_j   ?");
        GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
        gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_3.gridx = 0;
        gbc_lblNewLabel_3.gridy = 2;
        gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
        panel_7.add(lblNewLabel_3, gbc_lblNewLabel_3);

        JPanel panel_4 = new JPanel();
        GridBagConstraints gbc_panel_4 = new GridBagConstraints();
        gbc_panel_4.insets = new Insets(0, 0, 5, 5);
        gbc_panel_4.fill = GridBagConstraints.BOTH;
        gbc_panel_4.gridx = 1;
        gbc_panel_4.gridy = 2;
        panel_7.add(panel_4, gbc_panel_4);
        GridBagLayout gbl_panel_4 = new GridBagLayout();
        gbl_panel_4.columnWidths = new int[]{0, 0};
        gbl_panel_4.rowHeights = new int[]{0};
        gbl_panel_4.columnWeights = new double[]{0.5, 0.5};
        gbl_panel_4.rowWeights = new double[]{1.0};
        panel_4.setLayout(gbl_panel_4);

        rdbtnYes_entitiesEqual = new JRadioButton("Yes");
        rdbtnYes_entitiesEqual.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rfd.shortCircuitEqualEntities()) {
                    tabbedPaneRef.setSelectedIndex(4);
                    comboBox_entityEi.setSelectedIndex(0);
                    comboBox_entityEj.setSelectedIndex(0);
                }
            }
        });

        GridBagConstraints gbc_rdbtnYes_entitiesEqual = new GridBagConstraints();
        gbc_rdbtnYes_entitiesEqual.insets = new Insets(0, 0, 0, 5);
        gbc_rdbtnYes_entitiesEqual.gridx = 0;
        gbc_rdbtnYes_entitiesEqual.gridy = 0;
        panel_4.add(rdbtnYes_entitiesEqual, gbc_rdbtnYes_entitiesEqual);

        JLabel lblEiEj = new JLabel("E_i  >  E_j   ?");
        GridBagConstraints gbc_lblEiEj = new GridBagConstraints();
        gbc_lblEiEj.insets = new Insets(0, 0, 5, 5);
        gbc_lblEiEj.gridx = 0;
        gbc_lblEiEj.gridy = 3;
        gbc_lblEiEj.anchor = GridBagConstraints.EAST;
        panel_7.add(lblEiEj, gbc_lblEiEj);

        JPanel panel_8 = new JPanel();
        GridBagConstraints gbc_panel_8 = new GridBagConstraints();
        gbc_panel_8.insets = new Insets(0, 0, 5, 5);
        gbc_panel_8.fill = GridBagConstraints.BOTH;
        gbc_panel_8.gridx = 1;
        gbc_panel_8.gridy = 3;
        panel_7.add(panel_8, gbc_panel_8);
        GridBagLayout gbl_panel_8 = new GridBagLayout();
        gbl_panel_8.columnWidths = new int[]{0, 0};
        gbl_panel_8.rowHeights = new int[]{0};
        gbl_panel_8.columnWeights = new double[]{0.5, .5};
        gbl_panel_8.rowWeights = new double[]{0.0};
        panel_8.setLayout(gbl_panel_8);

        ButtonGroup bg = new ButtonGroup();

        rdbtnYes_entityEi_g_Ej = new JRadioButton("Yes");
        GridBagConstraints gbc_rdbtnYes_entityEi_g_Ej = new GridBagConstraints();
        gbc_rdbtnYes_entityEi_g_Ej.insets = new Insets(0, 0, 0, 5);
        gbc_rdbtnYes_entityEi_g_Ej.gridx = 0;
        gbc_rdbtnYes_entityEi_g_Ej.gridy = 0;
        panel_8.add(rdbtnYes_entityEi_g_Ej, gbc_rdbtnYes_entityEi_g_Ej);

        bg.add(rdbtnYes_entitiesEqual);
        bg.add(rdbtnYes_entityEi_g_Ej);

        rdbtnNo_entityEi_g_Ej = new JRadioButton("No");
        GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
        gbc_rdbtnNewRadioButton.gridx = 1;
        gbc_rdbtnNewRadioButton.gridy = 0;
        panel_8.add(rdbtnNo_entityEi_g_Ej, gbc_rdbtnNewRadioButton);
        bg.add(rdbtnNo_entityEi_g_Ej);
        return panel_7;
    }

    private JPanel createComparisonScaleValueTab() {
        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(null, "Comparison Scale Value ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_2 = new GridBagConstraints();
        gbc_panel_2.insets = new Insets(0, 0, 5, 0);
        gbc_panel_2.fill = GridBagConstraints.BOTH;
        gbc_panel_2.gridx = 0;
        gbc_panel_2.gridy = 4;

        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
        gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        gbl_panel_2.columnWeights = new double[]{.1, .4, .001, .001};
        gbl_panel_2.rowWeights = new double[]{0.03, 0.03, 0.07, .4, .25, .25};
        panel_2.setLayout(gbl_panel_2);


        JLabel lblStepEnter = new JLabel("Step 4.  Enter the value via slider or enter manually as number from a scale 1 to [User Defined Upper Limit]");
        GridBagConstraints gbc_lblStepEnter = new GridBagConstraints();
        gbc_lblStepEnter.insets = new Insets(10, 5, 10, 0);
        gbc_lblStepEnter.gridx = 0;
        gbc_lblStepEnter.gridy = 0;
        gbc_lblStepEnter.anchor = GridBagConstraints.WEST;
        gbc_lblStepEnter.gridwidth = 5;
        panel_2.add(lblStepEnter, gbc_lblStepEnter);

        JLabel lblStep = new JLabel("Step 5.  Commit comparison data For E_i and E_j.");
        GridBagConstraints gbc_lblStep = new GridBagConstraints();
        gbc_lblStep.insets = new Insets(0, 0, 10, 0);
        gbc_lblStep.gridx = 0;
        gbc_lblStep.gridy = 1;
        gbc_lblStepEnter.anchor = GridBagConstraints.WEST;
        panel_2.add(lblStep, gbc_lblStep);

        JLabel lblSubmittingValue = new JLabel("Relative weight:");
        GridBagConstraints gbc_lblSubmittingValue = new GridBagConstraints();
        gbc_lblSubmittingValue.insets = new Insets(0, 0, 5, 5);
        gbc_lblSubmittingValue.gridx = 0;
        gbc_lblSubmittingValue.gridy = 2;
        gbc_lblSubmittingValue.anchor = GridBagConstraints.EAST;
        panel_2.add(lblSubmittingValue, gbc_lblSubmittingValue);

        textField_relativeWeight = new JTextField();
        GridBagConstraints gbc_textField_relativeWeight = new GridBagConstraints();
        gbc_textField_relativeWeight.insets = new Insets(0, 0, 5, 5);
        gbc_textField_relativeWeight.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_relativeWeight.gridx = 1;
        gbc_textField_relativeWeight.gridy = 2;
        panel_2.add(textField_relativeWeight, gbc_textField_relativeWeight);
        textField_relativeWeight.setColumns(10);

        JPanel panel_3 = new JPanel();
        GridBagConstraints gbc_panel_3 = new GridBagConstraints();
        gbc_panel_3.insets = new Insets(0, 0, 5, 5);
        gbc_panel_3.fill = GridBagConstraints.BOTH;
        gbc_panel_3.gridx = 1;
        gbc_panel_3.gridy = 3;
        panel_2.add(panel_3, gbc_panel_3);
        panel_3.setLayout(new GridLayout(1, 0, 0, 0));

        slider = new JSlider();
        slider.setMinimum(1);
        slider.setMaximum(8);
        slider.setPaintTicks(true);
        slider.setValue(1);
        panel_3.add(slider);

        JButton btnNewButton = new JButton(">");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				/*
				if(slider.getValue()+2<=slider.getMaximum())
					rfd.setSliderPosition(slider.getValue()+2);
				*/
            }
        });
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.anchor = GridBagConstraints.WEST;
        gbc_btnNewButton.insets = new Insets(0, 0, 8, 5);
        gbc_btnNewButton.gridx = 2;
        gbc_btnNewButton.gridy = 3;
        panel_2.add(btnNewButton, gbc_btnNewButton);

        JButton button = new JButton(">>");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				/*
				if(slider.getValue()+1<=slider.getMaximum())
					rfd.setSliderPosition(slider.getValue()+1);
				*/
            }
        });
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.anchor = GridBagConstraints.WEST;
        gbc_button.gridx = 3;
        gbc_button.gridy = 3;
        gbc_button.insets = new Insets(0, 0, 8, 5);
        panel_2.add(button, gbc_button);

        JButton btnSubmitEiAnd = new JButton("Commit Comparison Data");
        btnSubmitEiAnd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rfd.commitComparisonDataToCommitTable())
                    tabbedPaneRef.setSelectedIndex(4);
            }
        });
        GridBagConstraints gbc_btnSubmitEiAnd = new GridBagConstraints();
        gbc_btnSubmitEiAnd.insets = new Insets(0, 0, 5, 0);
        gbc_btnSubmitEiAnd.gridx = 4;
        gbc_btnSubmitEiAnd.gridy = 3;
        panel_2.add(btnSubmitEiAnd, gbc_btnSubmitEiAnd);

        return panel_2;
    }

    private JPanel createSummaryTab() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Committing Data", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0};
        gbl_panel.columnWeights = new double[]{.9, .05, 0.05};
        gbl_panel.rowWeights = new double[]{0.03, .97, .07};
        panel.setLayout(gbl_panel);

        JLabel lblNewLabel_4 = new JLabel("Step 6.  Review and submit all comparison data.  If an error was made, double click row and column and return to the field of error, edit and recommit.");
        GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
        gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_4.gridx = 0;
        gbc_lblNewLabel_4.gridy = 0;
        gbc_lblNewLabel_4.gridwidth = 3;
        gbc_lblNewLabel_4.anchor = GridBagConstraints.WEST;
        panel.add(lblNewLabel_4, gbc_lblNewLabel_4);

        scrollPane = new JScrollPane();
        GridBagConstraints gbc_table = new GridBagConstraints();
        gbc_table.insets = new Insets(0, 0, 5, 0);
        gbc_table.gridx = 0;
        gbc_table.gridy = 1;
        gbc_table.gridwidth = 3;
        gbc_table.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc_table);

        JButton btnSubmittAllData = new JButton("Committ All Data");
        btnSubmittAllData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rfd.commitDataToParent();
                dispose();
            }
        });
        GridBagConstraints gbc_btnSubmittAllData = new GridBagConstraints();
        gbc_btnSubmittAllData.gridx = 2;
        gbc_btnSubmittAllData.gridy = 2;
        gbc_btnSubmittAllData.anchor = GridBagConstraints.EAST;
        panel.add(btnSubmittAllData, gbc_btnSubmittAllData);

        JButton btn_nextComparison = new JButton("Next Comparison");
        btn_nextComparison.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tabbedPaneRef.setSelectedIndex(1);
            }
        });
        GridBagConstraints gbc_nextComparison = new GridBagConstraints();
        gbc_nextComparison.gridx = 1;
        gbc_nextComparison.gridy = 2;
        gbc_nextComparison.anchor = GridBagConstraints.EAST;
        panel.add(btn_nextComparison, gbc_nextComparison);

        return panel;
    }
}
