package ca.laurentian.concluder.refactorState;

import prefuse.data.Graph;
import prefuse.data.Tree;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.time.LocalDateTime;

import static ca.laurentian.concluder.constants.ConcluderConstant.LINGUISTIC_TERM;
import static ca.laurentian.concluder.constants.ConcluderConstant.NODE_ID;

public class FileIO {

    private File currentStateFile;
    private Graph currentStateGraph;
    private Tree currentStateTree;
    private String currentDirectory;

    public FileIO() {
        super();
    }

    public File getCurrentStateFile() {
        return currentStateFile;
    }

    public Graph getCurrentStateGraph() {
        return currentStateGraph;
    }

    public Tree getCurrentStateTree() {
        return currentStateTree;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String d) {
        currentDirectory = d;
    }

    public String saveFile(File f, Tree t, Graph g, String fileType) {
        currentStateFile = f;
        try {
            if (fileType.compareTo(SystemConfiguration.GRAPH_FILE) == 0) {
                PrefuseAPIAccess.exportGraph(f, g);
                currentStateGraph = g;
                return "0";
            } else if (fileType.compareTo(SystemConfiguration.TREE_FILE) == 0) {
                PrefuseAPIAccess.exportTree(f, t);
                currentStateTree = t;
                return "0";
            } else {
                return "Invalid File Type";
            }
        } catch (DataIOException e) {
            return e.toString();
        }
    }

    public String saveFileAs(String currentDirectory, String fileName, Graph g, Tree t, String fileType) {
        JFileChooser jfc = saveFileDialog(currentDirectory, fileName, "Save file as...");
        if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            currentStateFile = jfc.getSelectedFile();
            if (!(currentStateFile.getPath().endsWith(".xml") || currentStateFile.getPath().endsWith(".XML")))
                currentStateFile = new File(currentStateFile.getPath() + ".xml");
            this.currentDirectory = currentStateFile.getParent();
            try {
                if (fileType.compareTo(SystemConfiguration.GRAPH_FILE) == 0) {
                    PrefuseAPIAccess.exportGraph(currentStateFile, g);
                    currentStateGraph = g;
                    return "0";
                } else if (fileType.compareTo(SystemConfiguration.TREE_FILE) == 0) {
                    PrefuseAPIAccess.exportTree(currentStateFile, t);
                    currentStateTree = t;
                    return "0";
                } else {
                    return "Invalid File Type";
                }
            } catch (DataIOException e) {
                return e.toString();
            }
        } else {
            return "cancelClicked";
        }
    }

    public String openFile(String currentDirectory, String title, boolean newFile) {
        JFileChooser jfc;
        //use save file dialog to create new file
        if (newFile) {
            LocalDateTime now = LocalDateTime.now();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            String fileName = "test" + month + "" + day + "a";
            char x = 'a';
            while (new File(currentDirectory + "\\" + fileName + ".xml").exists()) {
                x++;
                fileName = "test" + month + "" + day + x;
            }
            jfc = saveFileDialog(currentDirectory, fileName, title);
        } else
            jfc = openFileDialog(currentDirectory, "*", title);

        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            currentStateFile = jfc.getSelectedFile();
            if (!(currentStateFile.getPath().endsWith(".xml") || currentStateFile.getPath().endsWith(".XML")))
                currentStateFile = new File(currentStateFile.getPath() + ".xml");
            this.currentDirectory = currentStateFile.getParent();
            try {
                if (newFile)
                    PrefuseAPIAccess.exportTree(jfc.getSelectedFile(), GraphLib.getInitializedTree());
                currentStateTree = PrefuseAPIAccess.importTree(jfc.getSelectedFile());
                if (currentStateTree.getNodeTable().getColumn(LINGUISTIC_TERM) == null)
                    currentStateTree.getNodeTable().addColumn(LINGUISTIC_TERM, String.class, "Greater Than(>)");
                //graph
                if (currentStateTree.getRootRow() == -1) {
                    JOptionPane.showMessageDialog(null, "JConcluder file format has been updated.\nFile conversion will take place to match version conformance.\n"
                            + "\nJConcluder assume previous graph was constructed properly.\n"
                            + "Assumptions are:\n"
                            + "1.  A source node not as target is taken for new tree root.\n"
                            + "2.  Source and target nodes are mapped to Parent Child respectively.\n"
                            + "\n\nNext file load will be current to Concluder file format.", "File Conversion", JOptionPane.INFORMATION_MESSAGE);

                    GraphMLReader gr = new GraphMLReader();
                    Graph g = gr.readGraph(jfc.getSelectedFile());
                    g.getNodeTable().addColumn(NODE_ID, int.class);
                    for (int i = 0; i < g.getNodeCount(); i++)
                        g.getNode(i).setInt(NODE_ID, g.getNode(i).getRow());
                    if (g.getNodeTable().getColumn("weight2") == null)
                        g.getNodeTable().addColumn("weight2", double.class, -1);
                    if (g.getNodeTable().getColumn(LINGUISTIC_TERM) == null)
                        g.getNodeTable().addColumn(LINGUISTIC_TERM, String.class, "Greater Than(>)");

                    //look for root
                    int r = -1;
                    for (int i = 0; i < g.getNodeTable().getRowCount(); i++) {
                        int tester = g.getNodeTable().getTuple(i).getInt(NODE_ID);
                        for (int j = 0; j < g.getEdgeTable().getRowCount(); j++) {
                            if (g.getEdgeTable().getTuple(j).getInt("target") != tester)
                                r = tester;
                            else
                                r = -1;
                            if (r == -1)
                                j = g.getEdgeTable().getRowCount();
                        }
                        if (r != -1)
                            i = g.getNodeTable().getRowCount();
                    }
                    if (r == -1) {
                        currentStateGraph = g;
                        return SystemConfiguration.GRAPH_FILE;
                    } else {
                        RootableTree rt = new RootableTree(g.getNodeTable(), g.getEdgeTable(), NODE_ID, "source", "target");
                        rt.setNewRoot(g.getNode(r));
                        currentStateTree = rt;
                        JOptionPane.showMessageDialog(null, "Root Node Identified:\n\n" + g.getNode(r).getString("name"), "Root Node Found", JOptionPane.INFORMATION_MESSAGE);
                        return SystemConfiguration.TREE_FILE;
                    }
                } else
                    return SystemConfiguration.TREE_FILE;
            } catch (DataIOException e) {
                return e.toString();
            }
        }
        return "cancelClicked";
    }

    //specific handlers based on a save file operation
    public JFileChooser saveFileDialog(String currentDirectory, String fileName, String caption) {
        JFileChooser jfc = new JFileChooser(currentDirectory) {
            private static final long serialVersionUID = 7919427933588163126L;

            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.exists()) {
                    int result = JOptionPane.showConfirmDialog(this, "The file already exists, overwrite this file?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        super.approveSelection();
                        return;
                    }
                    return;
                }
                super.approveSelection();
            }
        };
        jfc.setDialogTitle(caption);
        jfc.setSelectedFile(new File(fileName + ".xml"));
        FileFilter filter = new FileNameExtensionFilter("XML Files", "XML");
        jfc.addChoosableFileFilter(filter);
        jfc.setFileFilter(filter);
        return jfc;
    }

    //specific handler based on open file operation
    public JFileChooser openFileDialog(String currentDirectory, String fileName, String caption) {
        JFileChooser jfc = new JFileChooser(currentDirectory) {
            private static final long serialVersionUID = 7919427933588163126L;

            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                if (!f.exists())
                    JOptionPane.showConfirmDialog(this, "The file does not exist.", "Non existing file", JOptionPane.OK_CANCEL_OPTION);
                else {
                    super.approveSelection();
                }
            }
        };
        jfc.setDialogTitle(caption);
        jfc.setSelectedFile(new File(fileName + ".xml"));
        FileFilter filter = new FileNameExtensionFilter("XML Files", "XML");
        jfc.addChoosableFileFilter(filter);
        jfc.setFileFilter(filter);
        return jfc;
    }

    //why?? Author: TDJ
    public void closeFile() {
        currentStateFile = null;
        currentStateGraph = null;
    }
}
