package ca.laurentian.concluder.refactorState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;


public class SystemSettings extends JDialog {

    final private JComboBox<String> comboBox;
    private JLabel lblWorkSpace;
    ///holds the folder to be workspace
    private String workSpace;
    private Locale locale = Locale.CANADA;
    //used to indicate a valid workspace was selected
    private int exitCode = 1;

    /**
     * Create the frame.
     */
    public SystemSettings() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                //default the workspace to current working directory
                workSpace = System.getProperty("user.dir") + File.separator + "Model";
                //display this option to the user
                lblWorkSpace.setText("Work Space:  " + workSpace);
                comboBox.setSelectedIndex(31);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                //shows invalid workspace, user aborted program
                exitCode = 0;
            }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/conclude.png")));
        setTitle("System Configuration");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 750, 300);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0};
        gbl_contentPane.rowHeights = new int[]{0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{1.0};
        gbl_contentPane.rowWeights = new double[]{.45, .45, .1};
        getContentPane().setLayout(gbl_contentPane);

        GridBagLayout gbl_p = new GridBagLayout();
        gbl_p.columnWidths = new int[]{0, 0};
        gbl_p.rowHeights = new int[]{0, 0};
        gbl_p.columnWeights = new double[]{.7, .3};
        gbl_p.rowWeights = new double[]{.5, .5};
        JPanel p = new JPanel();
        p.setBorder(new TitledBorder("Work Space"));
        p.setLayout(gbl_p);
        GridBagConstraints gbc_p = new GridBagConstraints();
        gbc_p.anchor = GridBagConstraints.WEST;
        gbc_p.insets = new Insets(0, 0, 0, 0);
        gbc_p.gridx = 0;
        gbc_p.gridy = 0;
        gbc_p.fill = GridBagConstraints.BOTH;
        getContentPane().add(p, gbc_p);

        JLabel lblPrompt = new JLabel("JConcluder stores your projects in a folder work space named 'Model'.  Confirm workspace:");
        GridBagConstraints gbc_lblPrompt = new GridBagConstraints();
        gbc_lblPrompt.anchor = GridBagConstraints.WEST;
        gbc_lblPrompt.insets = new Insets(0, 0, 0, 0);
        gbc_lblPrompt.gridx = 0;
        gbc_lblPrompt.gridy = 0;
        p.add(lblPrompt, gbc_lblPrompt);

        lblWorkSpace = new JLabel();
        lblWorkSpace.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_lblWorkSpace = new GridBagConstraints();
        gbc_lblWorkSpace.anchor = GridBagConstraints.WEST;
        gbc_lblWorkSpace.insets = new Insets(10, 0, 10, 0);
        gbc_lblWorkSpace.gridx = 0;
        gbc_lblWorkSpace.gridy = 1;
        gbc_lblWorkSpace.fill = GridBagConstraints.HORIZONTAL;
        p.add(lblWorkSpace, gbc_lblWorkSpace);

        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //allow user to browse to an existing workspace
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    workSpace = jfc.getSelectedFile().getPath() + File.separator + "Model";
                    lblWorkSpace.setText("Work Space:  " + workSpace);
                    //ensureWorkSpace();
                }
            }
        });
        GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
        gbc_btnBrowse.gridwidth = 2;
        gbc_btnBrowse.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnBrowse.insets = new Insets(0, 15, 0, 0);
        gbc_btnBrowse.gridx = 1;
        gbc_btnBrowse.gridy = 0;
        gbc_lblWorkSpace.gridwidth = 2;
        p.add(btnBrowse, gbc_btnBrowse);

        GridBagLayout gbl_p2 = new GridBagLayout();
        gbl_p2.columnWidths = new int[]{0, 0};
        gbl_p2.rowHeights = new int[]{0};
        gbl_p2.columnWeights = new double[]{.7, .3};
        gbl_p2.rowWeights = new double[]{1.0};
        JPanel p2 = new JPanel();
        p2.setBorder(new TitledBorder("Locale"));
        p2.setLayout(gbl_p2);
        GridBagConstraints gbc_p2 = new GridBagConstraints();
        gbc_p2.anchor = GridBagConstraints.WEST;
        gbc_p2.insets = new Insets(0, 0, 0, 0);
        gbc_p2.gridx = 0;
        gbc_p2.gridy = 1;
        gbc_p2.fill = GridBagConstraints.BOTH;
        getContentPane().add(p2, gbc_p2);

        JLabel lblJconcluderUsesNumber = new JLabel("JConcluder uses number formatting given a Locale.  Confirm Locale:");
        GridBagConstraints gbc_lblJconcluderUsesNumber = new GridBagConstraints();
        gbc_lblJconcluderUsesNumber.insets = new Insets(15, 0, 15, 0);
        gbc_lblJconcluderUsesNumber.gridx = 0;
        gbc_lblJconcluderUsesNumber.gridy = 0;
        gbc_lblJconcluderUsesNumber.anchor = GridBagConstraints.WEST;
        p2.add(lblJconcluderUsesNumber, gbc_lblJconcluderUsesNumber);

        comboBox = new JComboBox<String>();
        //decimal point symbol problem
        //--------------------------------------------------------------
        final Locale[] la = DecimalFormat.getAvailableLocales();
        ArrayList<String> las = new ArrayList<String>();
        for (int i = 0; i < la.length; i++) {
            if (la[i].getDisplayCountry() != "")
                las.add(la[i].getDisplayName());
            //test all locales form valid formatters
            //DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(la[i]);
            //otherSymbols.setDecimalSeparator('.');
            //DecimalFormat df = new DecimalFormat("#0.0", otherSymbols);
        }
        //alphabetical order
        las.sort(null);
        for (int i = 0; i < las.size(); i++)
            comboBox.addItem(las.get(i));

        //----------------------------------------------------------------
        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    for (int i = 0; i < la.length; i++) {
                        if (la[i].getDisplayCountry() != "") {
                            if (la[i].getDisplayName().compareTo(comboBox.getItemAt(comboBox.getSelectedIndex())) == 0)
                                locale = la[i];
                        }
                    }
                }
            }
        });
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.insets = new Insets(15, 15, 15, 0);
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.gridx = 1;
        gbc_comboBox.gridy = 0;
        gbc_comboBox.gridwidth = 2;
        p2.add(comboBox, gbc_comboBox);

        GridBagLayout gbl_p3 = new GridBagLayout();
        gbl_p3.columnWidths = new int[]{0, 0};
        gbl_p3.rowHeights = new int[]{0};
        gbl_p3.columnWeights = new double[]{.7, .3};
        gbl_p3.rowWeights = new double[]{1.0};
        JPanel p3 = new JPanel();
        p3.setLayout(gbl_p3);

        JButton btnOK = new JButton("OK");
        btnOK.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ensureWorkSpace();
            }
        });
        GridBagConstraints gbc_btnOk = new GridBagConstraints();
        gbc_btnOk.gridx = 1;
        gbc_btnOk.gridy = 0;
        gbc_btnOk.anchor = GridBagConstraints.EAST;
        p3.add(btnOK, gbc_btnOk);
        GridBagConstraints gbc_p3 = new GridBagConstraints();
        gbc_p3.anchor = GridBagConstraints.WEST;
        gbc_p3.insets = new Insets(0, 0, 0, 0);
        gbc_p3.gridx = 0;
        gbc_p3.gridy = 3;
        gbc_p3.fill = GridBagConstraints.BOTH;
        getContentPane().add(p3, gbc_p3);
        //pack();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SystemSettings frame = new SystemSettings();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    ///validate and create the given workspace
    public void ensureWorkSpace() {
        File f = new File(workSpace);
        if (!f.isDirectory()) {
            f.mkdir();
            setVisible(false);
            //workspace created
            exitCode = 1;
        } else {
            int response = JOptionPane.showConfirmDialog(this, "Workspace directory already exists.\nConfirm correct directory and use as workspace?\n\n\nWork Space correct?  " + workSpace + "     \n\n\n");
            if (response == JOptionPane.YES_OPTION) {
                setVisible(false);
                ///workspace create
                exitCode = 1;
            }
        }
    }

    //return the exit code of this form(valid workspace or abort) to main program Concluder
    //1 workspace created
    //0 program aborted
    public int getExitCode() {
        return exitCode;
    }

    //return the workspace chosen to Concluder main
    public String getWorkSpaceDirectory() {
        return workSpace;
    }

    public Locale getLocale() {
        return locale;
    }
}
