package ca.laurentian.concluder;

import ca.laurentian.concluder.constants.ConcluderConstant;
import ca.laurentian.concluder.entityRelations.RelateForm;
import ca.laurentian.concluder.refactorState.FileIO;
import ca.laurentian.concluder.refactorState.HierarchyVisualization;
import ca.laurentian.concluder.refactorState.NewNodeDialog;
import ca.laurentian.concluder.refactorState.RemoveNode;
import ca.laurentian.concluder.refactorState.RootableTree;
import ca.laurentian.concluder.refactorState.SystemConfiguration;
import ca.laurentian.concluder.refactorState.SystemSettings;
import ca.laurentian.concluder.refactorState.TreeStructureValidation;
import ca.laurentian.concluder.refactorState.ViewModeAdministrator;
import ca.laurentian.concluder.refactorState.Weight_Redistributor;
import ca.laurentian.concluder.treemap.Treemap;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.visual.VisualItem;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import static ca.laurentian.concluder.constants.ConcluderConstant.RELATE;
import static ca.laurentian.concluder.constants.ConcluderConstant.TREEMAP;
import static ca.laurentian.concluder.constants.ConcluderConstant.UNLINK;
import static com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme.install;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class Concluder {
    //define graph,nodes,edges to store the data from xml file
    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";
    public static final String SELECTED = "sel";//store the selected node
    //node fields
    private static final String NAME = "name";//name of node
    private static final String WEIGHT = "weight";//weight of node
    private static final String DESC = "description";//description of node
    private static final String DIS = "noddis";//the displayed name of node(name + weight)
    //current selected node ID
    int selectedNodeID;//store the id of the selected node
    //holds all selected node IDs
    ArrayList<String> selectedNode = new ArrayList<>();//store all highlighted nodes
    ArrayList<VisualItem> selectedNodeVisualItem = new ArrayList<>();
    File file = null;//store the opened xml file
    //this will likely be changed to Tree
    Tree tree = null;
    Graph graph = null;
    RootableTree temp;
    //holds the workspace directory
    String curDir;
    JFrame frame;
    JLabel statusbar;
    JToolBar toolbar;
    ToolbarButton tbb, btnNew, btnOpen, btnSave, btnPrint, btnAdd, btnDelete, btnLink, btnUnlink,treeMap;
    JMenuItem mnuItemClose, mnuItemSave, mnuItemSaveAs, mnuItemPrint;
    //handles all file operations
    private final FileIO fio;
    //handles the visualization and display
    private HierarchyVisualization hv;
    private String fileType = null;
    private int viewMode;

    public Concluder() {
        try {
            //dialog to find workspace
            //returns workspace directory or aborts main program launch
            SystemSettings ws = new SystemSettings();
            ws.setModal(true);
            ws.setVisible(true);
            //successful workspace exit code
            if (ws.getExitCode() == 1) {
                curDir = ws.getWorkSpaceDirectory();
                SystemConfiguration.setSystemDefaultNumberFormat();
                SystemConfiguration.setSystemLocale(ws.getLocale());
                SystemConfiguration.setSystemNumberFormat();
                ws.dispose();
            }
            //program aborted
            else {
                ws.dispose();
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        fio = new FileIO();
        fio.setCurrentDirectory(curDir);
        hv = new HierarchyVisualization();

        frame = new JFrame(ConcluderConstant.JCONCLUDER);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/conclude.png")));
        frame.setTitle(ConcluderConstant.JCONCLUDER);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);


        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusbar = new JLabel("Ready::                     ");
        statusbar.setVisible(true);
        statusbar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        p.add(statusbar);
        frame.add(p, BorderLayout.SOUTH);
        createToolbar();
        createMenubar();
        viewMode = 1;
    }

    public static void main(String[] args) {
        install();
        Concluder c = new Concluder();
        c.launchFrame();
    }

    public void createToolbar() {
        toolbar = new JToolBar("Toolbar");
        btnNew = new ToolbarButton("New", new ImageIcon(getClass().getResource(
                "/images/new-file.png")), "Create new model");
        btnOpen = new ToolbarButton("Open", new ImageIcon(getClass()
                .getResource("/images/open_int.png")), "Open an existing model");
        btnSave = new ToolbarButton("Save", new ImageIcon(getClass()
                .getResource("/images/floppy-disk.png")), "Save this model");
        btnPrint = new ToolbarButton("Print", new ImageIcon(getClass()
                .getResource("/images/printer.png")), "Print the model");
        btnAdd = new ToolbarButton("Add", new ImageIcon(getClass().getResource(
                "/images/plus.png")), "Add a new node");
        btnDelete = new ToolbarButton("Delete", new ImageIcon(getClass()
                .getResource("/images/trash.png")), "Delete selected node");
        btnLink = new ToolbarButton("Link", new ImageIcon(getClass()
                .getResource("/images/link_int.png")), "Link");
        btnUnlink = new ToolbarButton(UNLINK, new ImageIcon(getClass()
                .getResource("/images/unlink_int.png")), UNLINK);
        tbb = new ToolbarButton(RELATE, new ImageIcon(getClass()
                .getResource("/images/balance-scale.png")), RELATE);
        treeMap = new ToolbarButton(TREEMAP, new ImageIcon(getClass()
                  .getResource("/images/treemap_chart.png")), TREEMAP);
        toolbar.setFloatable(true);
        toolbar.add(btnNew);
        toolbar.add(btnOpen);
        toolbar.add(btnSave);
        toolbar.add(btnPrint);
        toolbar.addSeparator();
        toolbar.add(btnAdd);
        toolbar.add(btnDelete);
        toolbar.add(btnLink);
        toolbar.add(btnUnlink);
        toolbar.addSeparator();
        toolbar.add(tbb);
        toolbar.add(treeMap);
        toolbar.addSeparator();
        frame.add(toolbar, BorderLayout.NORTH);
    }

    public void createMenubar() {
        JMenuBar menu = new JMenuBar();
        JMenu mnuFile = new JMenu("File");
        JMenuItem mnuItemNew = new JMenuItem("New", KeyEvent.VK_N);
        JMenuItem mnuItemOpen = new JMenuItem("Open...", KeyEvent.VK_O);
        mnuItemClose = new JMenuItem("Close", KeyEvent.VK_W);
        mnuItemSave = new JMenuItem("Save", KeyEvent.VK_S);
        mnuItemSaveAs = new JMenuItem("Save As...");
        mnuItemPrint = new JMenuItem("Print...", KeyEvent.VK_P);
        JMenuItem mnuItemQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
        mnuItemNew.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        mnuItemOpen.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        mnuItemClose.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        mnuItemSave.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        mnuItemPrint.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        mnuItemQuit.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        JMenu mnuView = new JMenu("View");
        JCheckBoxMenuItem mnuItemToolbar = new JCheckBoxMenuItem("Toolbar");
        JCheckBoxMenuItem mnuItemStatus = new JCheckBoxMenuItem("Status Bar");

        //change to View Mode
        JMenu mnuViewMode = new JMenu("View Mode");
        JMenuItem mnuItemLocalViewMode = new JMenuItem("Local View");
        JMenuItem mnuItemGlobalViewMode = new JMenuItem("Global View");
        mnuViewMode.add(mnuItemLocalViewMode);
        mnuViewMode.add(mnuItemGlobalViewMode);


        JMenu mnuZoom = new JMenu("Zoom");
        JMenuItem mnuItemZoomIn = new JMenuItem("Zoom In");
        JMenuItem mnuItemZoomOut = new JMenuItem("Zoom Out");
        JMenu mnuInitialize = new JMenu("Initialize");
        JMenu mnuCriteria = new JMenu("Criteria");
        JMenuItem mnuItemEdit = new JMenuItem("Edit");
        JMenuItem mnuItemLink = new JMenuItem("Link");
        JMenuItem mnuItemUnlink = new JMenuItem(UNLINK);
        JMenu mnuArrange = new JMenu("Arrange");
        JMenu mnuAnalyze = new JMenu("Analyze");
        JMenuItem mnuItemRelate = new JMenuItem(RELATE);
        JMenuItem mnuItemAnalyze = new JMenuItem("Analyze");
        JMenuItem mnuItemOutputPC = new JMenuItem("Output");
        JMenu mnuWeights = new JMenu("Weights");
        JMenuItem mnuItemWeights = new JMenuItem("Display");
        JMenuItem mnuItemOutputWeights = new JMenuItem("Output");
        JMenu mnuEvaluate = new JMenu("Evaluate");
        JMenu mnuHelp = new JMenu("Help");
        JMenuItem mnuItemAbout = new JMenuItem("About");
        JMenuItem mnuItemPublications = new JMenuItem("PUBLICATIONS");
        JMenuItem mnuItemInstructions = new JMenuItem("INSTRUCTIONS");
        JMenuItem mnuItemTutorial = new JMenuItem("TUTORIAL");
        menu.add(mnuFile);
        menu.add(mnuView);
        menu.add(mnuCriteria);
        menu.add(mnuAnalyze);
        menu.add(mnuWeights);
        menu.add(mnuHelp);
        mnuFile.add(mnuItemNew);
        mnuFile.add(mnuItemOpen);
        mnuFile.addSeparator();
        mnuFile.add(mnuItemClose);
        mnuFile.addSeparator();
        mnuFile.add(mnuItemSave);
        mnuFile.add(mnuItemSaveAs);
        mnuFile.add(mnuItemPrint);
        mnuFile.addSeparator();
        mnuFile.add(mnuItemQuit);
        mnuView.add(mnuItemToolbar);
        mnuView.add(mnuItemStatus);

        //add changed above
        mnuView.add(mnuViewMode);
        //create sub menu items
        //add new items

        mnuView.addSeparator();
        mnuView.add(mnuZoom);
        mnuZoom.add(mnuItemZoomIn);
        mnuZoom.add(mnuItemZoomOut);
        mnuCriteria.add(mnuItemEdit);
        mnuCriteria.add(mnuItemLink);
        mnuCriteria.add(mnuItemUnlink);
        mnuWeights.add(mnuItemWeights);
        mnuWeights.add(mnuItemOutputWeights);
        mnuAnalyze.add(mnuItemRelate);
        mnuAnalyze.add(mnuItemAnalyze);
        mnuAnalyze.add(mnuItemOutputPC);
        mnuItemToolbar.setState(true);
        mnuItemStatus.setState(true);
        mnuHelp.add(mnuItemAbout);
        frame.setJMenuBar(menu);
        mnuItemNew.addActionListener(new ListenMenuNew());
        mnuItemOpen.addActionListener(new ListenMenuOpen());
        mnuItemSave.addActionListener(new ListenMenuSave());
        mnuItemSaveAs.addActionListener(new ListenMenuSaveAs());
        tbb.addActionListener(new ListenRelate());
        mnuItemRelate.addActionListener(new ListenRelate());
        btnNew.addActionListener(new ListenMenuNew());
        btnOpen.addActionListener(new ListenMenuOpen());
        btnSave.addActionListener(new ListenMenuSave());
        btnAdd.addActionListener(new ListenNodeAdd());
        btnDelete.addActionListener(new ListenNodeDelete());
        btnLink.addActionListener(new ListenLink());
        btnUnlink.addActionListener(new ListenUnlink());
        mnuItemClose.addActionListener(new ListenMenuClose());
        treeMap.addActionListener(new TreeMapListner());
        mnuItemQuit.addActionListener(new ListenMenuQuit());
        mnuItemStatus.addActionListener(new ListenStatus());
        mnuItemToolbar.addActionListener(new ListenToolbar());

        //change name and item here, to two separate sub menus
        //reuse handler
        //implement a common function, single call for both handlers once modified as stated above
        mnuItemLocalViewMode.addActionListener(new ListenViewModeLocal());
        mnuItemGlobalViewMode.addActionListener(new ListenViewModeGlobal());

        mnuItemEdit.addActionListener(new ListenEdit());
        mnuItemLink.addActionListener(new ListenLink());
        mnuItemUnlink.addActionListener(new ListenUnlink());
        mnuItemZoomIn.addActionListener(e -> warnWindow("Drag down with right pressed to zoom in.", 400, 100));
        mnuItemZoomOut.addActionListener(e -> warnWindow("Drag up with right pressed to zoom out.", 400, 100));
        mnuItemAnalyze.addActionListener(new ListenAnalyze());
        mnuItemOutputPC.addActionListener(new ListenOutputPC());
        mnuItemWeights.addActionListener(new ListenWeights());
        mnuItemOutputWeights.addActionListener(new ListenOutputWeights());
        mnuItemAbout.addActionListener(new ListenAbout());
        enableMenuItems(false);
    }

    //enable/disable all items in menu bar
    public void enableMenuItems(boolean b) {
        mnuItemClose.setEnabled(b);
        mnuItemSave.setEnabled(b);
        mnuItemSaveAs.setEnabled(b);
        mnuItemPrint.setEnabled(b);
        btnSave.setEnabled(b);
        btnPrint.setEnabled(b);
        btnAdd.setEnabled(b);
        btnDelete.setEnabled(b);
        btnLink.setEnabled(b);
        btnUnlink.setEnabled(b);
        treeMap.setEnabled(b);
        tbb.setEnabled(b);
    }

    private void viewModeRequested(int viewMode) {
        new ViewModeAdministrator(viewMode, graph);
    }

    //display a warn window
    public void warnWindow(String warnMSG, int width, int height) {
        final JFrame warn = new JFrame();
        JLabel label = new JLabel(warnMSG);
        JButton ok = new JButton("Ok");
        Panel p1 = new Panel();
        Panel p2 = new Panel();
        p1.add(label);
        p2.add(ok);
        warn.setSize(width, height);
        warn.setLocation(500, 250);
        warn.add(p1, BorderLayout.NORTH);
        warn.add(p2, BorderLayout.SOUTH);
        warn.setVisible(true);
        ok.addActionListener(e -> warn.dispose());
    }

    //quit the opened hierarchy model
    public void quitFrame() {
        if (file != null) {
            String message = "Do you want to save the changes you made to " + file.getName() + "?";
            int response = JOptionPane.showConfirmDialog(frame, message, "Save Model", JOptionPane.YES_NO_CANCEL_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                fio.saveFile(file, tree, graph, fileType);
                close();
                close();
            } else if (response == JOptionPane.NO_OPTION) {
                close();
            }
        } else {
            close();
        }
    }

    //close window
    public void close() {
        frame.setVisible(false);
        frame.dispose();
        System.exit(0);
    }

    public void launchFrame() {
        frame.pack(); // Adjusts panel to components for display
        frame.setSize(850, 700);
        frame.setVisible(true);
        // Center frame
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension scrnsize = toolkit.getScreenSize();
        int x = (int) (scrnsize.getWidth() - frame.getWidth()) / 2;
        int y = (int) (scrnsize.getHeight() - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new ListenCloseWdw());
    }

    //control whether to display status bar(the statusbar is displayed when a hierarchy template is selected)
    public class ListenStatus implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            statusbar.setVisible(!statusbar.isVisible());
        }
    }

    //control whether to display tool bar
    public class ListenToolbar implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            toolbar.setVisible(!toolbar.isVisible());
        }
    }

    public class ListenViewModeLocal implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (file != null) {
                viewMode = 1;
                viewModeRequested(viewMode);
            }
        }
    }

    public class ListenViewModeGlobal implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (file != null) {
                viewMode = 0;
                //frame.remove(hv.d);
                viewModeRequested(viewMode);
                //hv.runLayout();
                //frame.add(hv.d);
            }
        }
    }

    public class ListenMenuSave implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //initial launch or previous file was closed
            if (file != null) {
                fio.saveFile(file, tree, graph, fileType);
            }
        }
    }

    public class ListenMenuSaveAs implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //initial launch or previous file was closed
            // can be simplified
            if (file != null) {
                fio.saveFileAs(curDir, file.getName().substring(0, file.getName().indexOf(".")), graph, tree, fileType);
                curDir = fio.getCurrentDirectory();
            }
        }
    }

    //overwrites existing files of the same name without question
    public class ListenMenuNew implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fileType = fio.openFile(curDir, "Open new file...", true);
            curDir = fio.getCurrentDirectory();
            file = fio.getCurrentStateFile();
            tree = fio.getCurrentStateTree();
            graph = tree;
            if (hv.d != null)
                frame.remove(hv.d);
            hv = new HierarchyVisualization();
            hv.Create(fileType, frame, graph,/*frame.getSize().width/2, frame.getSize().height/4,*/ selectedNode, selectedNodeVisualItem);
            new ViewModeAdministrator(viewMode, tree);
            frame.setTitle("JConcluder - " + file.getName());
            enableMenuItems(true);
        }
    }

    public class ListenMenuOpen implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fileType = fio.openFile(curDir, "Open existing file...", false);
            curDir = fio.getCurrentDirectory();
            if (fileType.compareTo(SystemConfiguration.TREE_FILE) == 0) {
                file = fio.getCurrentStateFile();
                tree = fio.getCurrentStateTree();
                graph = tree;
                tree.getRoot().set("weight2", 100.0);
                new Weight_Redistributor(viewMode).Redistribute_From_Root(tree.getRoot());
                new ViewModeAdministrator(viewMode, graph);
                if (hv.d != null)
                    frame.remove(hv.d);
                hv = new HierarchyVisualization();
                hv.Create(fileType, frame, graph,/*frame.getSize().width/2, frame.getSize().height/4,*/ selectedNode, selectedNodeVisualItem);
                frame.setTitle("JConcluder - " + file.getName());
                enableMenuItems(true);
            } else if (fileType.compareTo(SystemConfiguration.GRAPH_FILE) == 0) {
                file = fio.getCurrentStateFile();
                graph = fio.getCurrentStateGraph();
                if (hv.d != null)
                    frame.remove(hv.d);
                hv = new HierarchyVisualization();
                hv.Create(fileType, frame, graph,/*frame.getSize().width/2, frame.getSize().height/4,*/ selectedNode, selectedNodeVisualItem);
                new ViewModeAdministrator(viewMode, graph);
                frame.setTitle("JConcluder - " + file.getName());
                enableMenuItems(true);
            }
        }
    }

    public class ListenMenuClose implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (file != null) {
                fio.saveFile(file, tree, graph, fileType);
                fio.closeFile();
                file = null;
                frame.setTitle("JConcluder");
                frame.remove(hv.d);
                statusbar.setText("File closed.");
                enableMenuItems(false);
            }
        }
    }

    public class ListenNodeAdd implements ActionListener  {
        public void actionPerformed(ActionEvent event) {
            if (hv.SelectedNodeID != -1) {
                ///enabled form for node creation
                if (graph.getNode(hv.getSelectedNodeID()).getChildCount() == 8) {
                    JOptionPane.showMessageDialog(frame, "Maximum of only 8 child nodes allowable.", "Child Node Capacity", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                final NewNodeDialog nnd = new NewNodeDialog(frame, graph, true, graph.getNode(hv.SelectedNodeID).getString("name"), true);
                nnd.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        //all criteria is valid, node can be created
                        if (nnd.validSubmission()) {
                            frame.remove(hv.d);
                            new AddNewNode(graph, graph.getNode(hv.getSelectedNodeID()), nnd.getName(), nnd.getDesc(), frame, viewMode);
                            viewModeRequested(viewMode);
                            hv.runLayout();
                            frame.add(hv.d);
                        }
                    }
                });
                nnd.setVisible(true);
            } else
                warnWindow("Must select a node as parent to add.", 400, 100);
        }

    }

    public class ListenNodeDelete implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            if (!selectedNode.isEmpty()) {
                new RemoveNode(graph, hv.getSelectedNodeID(), hv.currentItem, viewMode);
                if (RemoveNode.removedSuccess) {
                    viewModeRequested(viewMode);
                    int index = selectedNode.indexOf(String.valueOf(hv.SelectedNodeID));
                    selectedNode.remove(index);
                    selectedNodeVisualItem.remove(index);
                    //update selected node to last selected
                    if (!selectedNode.isEmpty())
                        hv.SelectedNodeID = Integer.parseInt(selectedNode.get(selectedNode.size() - 1));
                    else
                        hv.SelectedNodeID = -1;
                }
            } else
                warnWindow("A node must be selected to delete.", 400, 100);
        }
    }

    public class ListenEdit implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            ///form enabled for editing
            final NewNodeDialog nnd = new NewNodeDialog(frame, graph, true, graph.getNode(hv.SelectedNodeID).getString("name"), false);
            nnd.setName(graph.getNode(hv.SelectedNodeID).getString("name"));
            nnd.setDesc(graph.getNode(hv.SelectedNodeID).getString("desc"));
            nnd.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    //node criteria is valid, submit new data
                    if (nnd.validSubmission()) {
                        graph.getNode(hv.getSelectedNodeID()).set("name", nnd.getName());
                        graph.getNode(hv.getSelectedNodeID()).set("noddis", nnd.getName() + "\n" + SystemConfiguration.formatNumber(graph.getNode(hv.SelectedNodeID).getDouble("weight")));
                        graph.getNode(hv.getSelectedNodeID()).set("desc", nnd.getDesc());
                    }
                }
            });
            nnd.setVisible(true);
        }
    }

    public class TreeMapListner implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(selectedNode.size() >= 1){
               Treemap treemap = new Treemap(graph.getNodeCount(),graph, hv);
            }else{
                JOptionPane.showMessageDialog(null,"Please add at least one node");
            }
        }
    }

    //connect two nodes
    public class ListenLink implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (selectedNode.size() == 2)//connect 2 selected nodes
            {
                if (fileType.compareTo(SystemConfiguration.TREE_FILE) == 0) {
                    //upon unlink of real tree graph
                    //edge is removed from graph
                    //here, edge is attempted to be added back
                    //but edge in temp exists, and hence invalid row id to edge table

                    RootableTree init = new RootableTree();
                    init.setTreeToClone((Tree) graph);
                    Tree temp = ((RootableTree) init.clone()).getClone();

                    int e;
                    if (Integer.parseInt(selectedNode.get(0)) > Integer.parseInt(selectedNode.get(1)))
                        e = temp.addChildEdge(Integer.parseInt(selectedNode.get(1)), Integer.parseInt(selectedNode.get(0)));
                    else
                        e = temp.addChildEdge(Integer.parseInt(selectedNode.get(0)), Integer.parseInt(selectedNode.get(1)));
                    if (TreeStructureValidation.testCycleExistence(graph)) {
                        JOptionPane.showMessageDialog(frame, "Can not add this edge.\nInvalidates tree structure", "Invalid Request", JOptionPane.ERROR_MESSAGE);
                        temp.removeChildEdge(e);
                    } else {
                        if (Integer.parseInt(selectedNode.get(0)) > Integer.parseInt(selectedNode.get(1)))
                            ((Tree) graph).addChildEdge(Integer.parseInt(selectedNode.get(1)), Integer.parseInt(
                                    selectedNode.get(0)));
                        else
                            ((Tree) graph).addChildEdge(Integer.parseInt(selectedNode.get(0)), Integer.parseInt(
                                    selectedNode.get(1)));
                        JOptionPane.showMessageDialog(frame, graph.getNode(Integer.parseInt(selectedNode.get(0))).getChildCount());
                    }
                } else
                    graph.addEdge(Integer.parseInt(selectedNode.get(0)), Integer.parseInt(selectedNode.get(1)));
            } else {
                warnWindow("You need 2 nodes to Link.", 400, 100);
            }

        }
    }

    //unconnect two nodes
    public class ListenUnlink implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (selectedNode.size() == 2)//unconnect 2 nodes
            {
                Node n1 = graph.getNode(Integer.parseInt(selectedNode.get(0)));
                Node n2 = graph.getNode(Integer.parseInt(selectedNode.get(1)));
                if (Integer.parseInt(selectedNode.get(0)) > Integer.parseInt(selectedNode.get(1))) {
                    Edge e = graph.getEdge(n2, n1);
                    ((Tree) graph).removeChildEdge(e);
                } else {
                    Edge e = graph.getEdge(n1, n2);
                    ((Tree) graph).removeChildEdge(e);
                }
            } else
                warnWindow("You can only select 2 nodes to Unlink.", 400, 100);
        }
    }

    //display weights of nodes
    public class ListenWeights implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            final JFrame weightframe = new JFrame("Weights");
            JButton ok = new JButton("Ok");
            Panel p1 = new Panel();
            p1.setLayout(null);
            Panel p2 = new Panel();
            p1.setSize(500, 350);
            int criteria = 0;//store the number of criteria
            for (int i = 0; i < graph.getNodeCount(); i++)//count the number of criteria
            {
                //record the number of child nodes
                if (graph.getNode(i).getChildCount() == 0)
                    criteria++;

            }

            JLabel[] nodeWeight = new JLabel[criteria];
            JLabel[] weightGraph = new JLabel[criteria];
            criteria = 0;

            for (int i = 0; i < graph.getNodeCount(); i++) {
                if (graph.getNode(i).getChildCount() == 0) {
                    nodeWeight[criteria] = new JLabel(SystemConfiguration.formatNumber(graph.getNode(i).getDouble(WEIGHT)) + "%       " + graph.getNode(i).get(NAME));
                    nodeWeight[criteria].setBounds(50, 20 + criteria * 25, 200, 15);
                    System.out.println("Bar graph");
                    weightGraph[criteria] = new JLabel();
                    weightGraph[criteria].setBounds(200, 20 + criteria * 25, Float.valueOf(graph.getNode(i).get(WEIGHT).toString()).intValue() * 3, 15);
                    weightGraph[criteria].setOpaque(true);
                    weightGraph[criteria].setBackground(Color.DARK_GRAY);
                    p1.add(nodeWeight[criteria]);
                    p1.add(weightGraph[criteria]);
                    criteria++;
                }
            }

            p2.add(ok);
            weightframe.getContentPane().setBackground(Color.DARK_GRAY);
            weightframe.setSize(500, 500);
            weightframe.setLocation(500, 150);
            weightframe.add(p1, BorderLayout.CENTER);
            weightframe.add(p2, BorderLayout.SOUTH);
            weightframe.setVisible(true);
            ok.addActionListener(e -> weightframe.dispose());
        }
    }

    //output weights to text file
    public class ListenOutputWeights implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            //count the number of criteria
            int criteria = 0;
            for (int i = 0; i < graph.getNodeCount(); i++) {
                if (graph.getNode(i).getChildCount() == 0) {
                    criteria++;
                }
            }
            String[] txt_werights = new String[criteria];
            criteria = 0;

            //read weights
            for (int i = 0; i < graph.getNodeCount(); i++) {
                if (graph.getNode(i).getChildCount() == 0) {
                    txt_werights[criteria] = SystemConfiguration.formatNumber(graph.getNode(i).getDouble(WEIGHT)) + "%       " + graph.getNode(i).get(NAME);
                    criteria++;
                }
            }
            //output to txt file
            new OutputTxt(file, graph, txt_werights, "weights", hv.getSelectedNodeID());
        }

    }

    public class ListenRelate implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (file == null)
                JOptionPane.showMessageDialog(frame, "No file loaded.", "No File", JOptionPane.ERROR_MESSAGE);
            else {
                if (!selectedNode.isEmpty()) {
                    int childNumber = graph.getNode(hv.getSelectedNodeID()).getChildCount();
                    if ((childNumber >= 2) && (childNumber <= 8)) {
                        new RelateForm(graph.getNode(hv.getSelectedNodeID()), graph, viewMode);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Node must have atleast 2 children.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else
                    JOptionPane.showMessageDialog(frame, "Must select a node.", "No Node", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //node-analysis Action Listener
    public class ListenAnalyze implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            //store the weights of all child nodes
            String[] childWeight = {"", "", "", "", "", "", ""};
            for (int i = 0; i < graph.getNode(hv.getSelectedNodeID()).getChildCount(); i++) {
                //child weight
                childWeight[i] = graph.getNode(hv.getSelectedNodeID()).getChild(i).get(1).toString();
            }

            //the number of the child nodes must be no less than 3 and no larger than 7
            if ((graph.getNode(hv.getSelectedNodeID()).getChildCount() >= 3) && (graph.getNode(hv.getSelectedNodeID()).getChildCount() <= 7)) {
                // get the ratio relation among all child nodes
                String relation = graph.getNode(hv.getSelectedNodeID()).get(3).toString();
                //child weight array of weights, relation composite string of weights from parent
                new Inconsistency(childWeight, relation, graph, file, hv.getSelectedNodeID(), viewMode);
            } else
                warnWindow("The selected node has to have more than 3 child nodes!", 400, 100);
        }
    }

    //output PC matrix to text file
    public class ListenOutputPC implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            //the number of the child nodes must be no less than 3 and no larger than 7
            if ((graph.getNode(hv.getSelectedNodeID()).getChildCount() >= 3) && (graph.getNode(hv.getSelectedNodeID()).getChildCount() <= 7)) {
                String relation = graph.getNode(hv.getSelectedNodeID()).get(3).toString();
                String[] relation2 = relation.split(" ");
                //relation2 array of child weights from parent
                new OutputTxt(file, graph, relation2, "PCmatrix", hv.getSelectedNodeID());
            } else
                warnWindow("The selected node has to have more than 3 child nodes!", 400, 100);
        }
    }

    public static class ListenAbout implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI("http://www.cs.laurentian.ca/wkoczkodaj/concluder/help/index.html"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    //quit program
    public class ListenMenuQuit implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            quitFrame();
        }
    }

    //close window
    public class ListenCloseWdw extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            quitFrame();
        }
    }
}