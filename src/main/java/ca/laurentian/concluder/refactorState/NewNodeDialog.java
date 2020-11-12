package ca.laurentian.concluder.refactorState;

import prefuse.data.Graph;
import prefuse.data.Node;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class NewNodeDialog extends JDialog {

    String originalName;
    private JTextField tfName;
    private JTextArea taDesc;
    private JLabel lbName;
    private JLabel lbDesc;
    private JButton btnOk;
    private JButton btnCancel;
    //exit code of the form
    //valid submission means the submitted information for the node criteria is valid
    private boolean validSubmit;
    private Graph graph;
    private boolean addingNode;

    public NewNodeDialog(JFrame parent, Graph graph, boolean enabled, String originalName, boolean addingNode) {
        //self referencial to close automatically within event handler
        final JDialog ref = this;

        this.graph = graph;
        //used to distinguish between an add dialog and edit criteria or show criteria   
        this.addingNode = addingNode;
        //used to prove if the node name already exists
        this.originalName = originalName;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;

        lbName = new JLabel("Name: ");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        panel.add(lbName, c);

        tfName = new JTextField(20);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        tfName.setEnabled(enabled);
        tfName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //ensure the node name meets all needed criteria
                if (testName(tfName.getText())) {
                    //node criteria submission correct
                    //form exit code is valid
                    validSubmit = true;
                    //destroy the form
                    ref.dispose();
                }
            }

        });
        panel.add(tfName, c);

        lbDesc = new JLabel("Description: ");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panel.add(lbDesc, c);

        taDesc = new JTextArea(5, 20);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        //taDesc.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        taDesc.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        taDesc.setEnabled(enabled);
        panel.add(taDesc, c);

        btnOk = new JButton("OK");
        //this is duplicate code to that same effect above
        //action performed and listener should be extracted
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (testName(tfName.getText())) {
                    validSubmit = true;
                    ref.dispose();
                }
            }
        });

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel bp = new JPanel();
        btnOk.setEnabled(enabled);
        bp.add(btnOk);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public String getName() {
        return tfName.getText().trim();
    }

    //used on edit, for existing
    public void setName(String name) {
        tfName.setText(name);
    }

    public String getDesc() {
        return taDesc.getText().trim();
    }

    //used on edit, for existing
    public void setDesc(String desc) {
        taDesc.setText(desc);
    }

    //make sure the name doesnt exist, and character limit = 32
    private boolean testName(String name) {
        //this should be implemented to be local, not global?? is that correct
        Iterator i = graph.nodes();
        while (i.hasNext()) {
            Node n = ((Node) i.next());
            if (
                    (n.getString("name").compareTo(name) == 0 && originalName.compareTo(n.getString("name")) == 0 && addingNode)
                            || (n.getString("name").compareTo(name) == 0 && originalName.compareTo(n.getString("name")) != 0)) {
                JOptionPane.showMessageDialog(this, "Node already exists, enter new name.", "Node Exists", JOptionPane.ERROR_MESSAGE);
                tfName.setText("");
                return false;
            }
        }
        if (name.length() > 32) {
            JOptionPane.showMessageDialog(NewNodeDialog.this,
                    "Node name has to be no larger than 32 characters. Please retry.",
                    "Create Node",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if ((name == null) || (name.length() <= 0)) {
            JOptionPane.showMessageDialog(NewNodeDialog.this,
                    "Please enter the node name",
                    "Create Node",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    //returns the state of the form
    public boolean validSubmission() {
        return validSubmit;
    }
}

