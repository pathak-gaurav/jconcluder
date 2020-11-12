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
                if (currentStateTree.getNodeTable().getColumn("Linguistic_Term") == null)
                    currentStateTree.getNodeTable().addColumn("Linguistic_Term", String.class, "Greater Than(>)");
                //graph
                if (currentStateTree.getRootRow() == -1) {
                    JOptionPane.showMessageDialog(null, "JConcluder file format has been updated.\nFile conversion will take place to match version conformance.\n"
                            + "\nJConcluder assume previous graph was constructed properly.\n"
                            + "Assumptions are:\n"
                            + "1.  A source node not as target is taken for new tree root.\n"
                            + "2.  Source and target nodes are mapped to Parent Child respectivly.\n"
                            + "\n\nNext file load will be current to Concluder file format.", "File Conversion", JOptionPane.INFORMATION_MESSAGE);

                    GraphMLReader gr = new GraphMLReader();
                    Graph g = gr.readGraph(jfc.getSelectedFile());
                    g.getNodeTable().addColumn("Node_ID", int.class);
                    for (int i = 0; i < g.getNodeCount(); i++)
                        g.getNode(i).setInt("Node_ID", g.getNode(i).getRow());
                    if (g.getNodeTable().getColumn("weight2") == null)
                        g.getNodeTable().addColumn("weight2", double.class, -1);
                    if (g.getNodeTable().getColumn("Linguistic_Term") == null)
                        g.getNodeTable().addColumn("Linguistic_Term", String.class, "Greater Than(>)");

                    //look for root
                    int r = -1;
                    for (int i = 0; i < g.getNodeTable().getRowCount(); i++) {
                        int tester = g.getNodeTable().getTuple(i).getInt("Node_ID");
                        for (int j = 0; j < g.getEdgeTable().getRowCount(); j++) {
                            if (!(g.getEdgeTable().getTuple(j).getInt("target") == tester))
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
                        RootableTree rt = new RootableTree(g.getNodeTable(), g.getEdgeTable(), "Node_ID", "source", "target");
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
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        default:
                            return;
                    }
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

    //this never seems to be actually used
    //the graph writer likely would avoid any use of this
	
	/*
	public static String saveFileToXML(String xml) 
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    
		try 
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.newDocument();

			Element root = dom.createElement("graphml");
			Element root2 = dom.createElement("graph");
        
			Attr attr = dom.createAttribute("xmlns");
			attr.setValue("http://graphml.graphdrawing.org/xmlns");
			root.setAttributeNode(attr);
		
			attr = dom.createAttribute("edgedefault");
			attr.setValue("undirected");
			root2.setAttributeNode(attr);
        
			root.appendChild(root2);
		
			Element e;
			
			//----------------------------------------------------------------
			
			e = dom.createElement("key");
			attr = dom.createAttribute("id");
			attr.setValue("name");
			e.setAttributeNode(attr);
			
			attr = dom.createAttribute("for");
			attr.setValue("node");
			e.setAttributeNode(attr);
			
			attr = dom.createAttribute("attr.name");
			attr.setValue("name");
			e.setAttributeNode(attr);
		
			attr = dom.createAttribute("attr.type");
			attr.setValue("string");
			e.setAttributeNode(attr);
		
			root2.appendChild(e);
			
			//---------------------------------------------------------
			//----------------------------------------------------------
			
			e = dom.createElement("key");
			attr = dom.createAttribute("id");
			attr.setValue("weight");
			e.setAttributeNode(attr);
			
			//common code
			attr = dom.createAttribute("for");
			attr.setValue("node");
			e.setAttributeNode(attr);
			//
			
			attr = dom.createAttribute("attr.name");
			attr.setValue("weight");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("attr.type");
			attr.setValue("double");
			e.setAttributeNode(attr);
        
			root2.appendChild(e);
			
			//----------------------------------------------------------
			
			//----------------------------------------------------------
			e = dom.createElement("key");
			attr = dom.createAttribute("id");
			attr.setValue("description");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("for");
			attr.setValue("node");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("attr.name");
			attr.setValue("desc");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("attr.type");
			attr.setValue("string");
			e.setAttributeNode(attr);
        
			root2.appendChild(e);
			
			//-------------------------------------------------------------------
			//-------------------------------------------------------------------
			
			e = dom.createElement("key");
			attr = dom.createAttribute("id");
			attr.setValue("relation");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("for");
			attr.setValue("node");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("attr.name");
			attr.setValue("rel");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("attr.type");
			attr.setValue("string");
			e.setAttributeNode(attr);
        
			root2.appendChild(e);
			
			//-----------------------------------------------------------------------
			//-----------------------------------------------------------------------
			
			e = dom.createElement("key");
			attr = dom.createAttribute("id");
			attr.setValue("nodedisplay");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("for");
			attr.setValue("node");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("attr.name");
			attr.setValue("noddis");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("attr.type");
			attr.setValue("string");
			e.setAttributeNode(attr);
        
			root2.appendChild(e);
			
			//-----------------------------------------------------------------------------------
			//-----------------------------------------------------------------------------------
			
			//new weight2
			e = dom.createElement("key");
			attr = dom.createAttribute("id");
			attr.setValue("weight2");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("for");
			attr.setValue("node");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("attr.name");
			attr.setValue("weight2");
			e.setAttributeNode(attr);
        
			attr = dom.createAttribute("attr.type");
			attr.setValue("double");
			e.setAttributeNode(attr);
        
			root2.appendChild(e);
			
			//-------------------------------------------------------------------------------
			//-------------------------------------------------------------------------------
			
			//------------------------------------------------------------------------------
			//------------------------------------------------------------------------------
			//CREATE ROOT BY HAND
			
			e = dom.createElement("node");
			attr = dom.createAttribute("id");
			attr.setValue("0");
			e.setAttributeNode(attr);
        
			root2.appendChild(e);
			
			Element node = dom.createElement("data");
			Attr nodeAttr = dom.createAttribute("key");
			nodeAttr.setValue("name");
			node.setAttributeNode(nodeAttr);
			node.appendChild(dom.createTextNode("ROOT"));
			e.appendChild(node);
		
			node = dom.createElement("data");
			nodeAttr = dom.createAttribute("key");
			nodeAttr.setValue("weight");
			node.setAttributeNode(nodeAttr);
			node.appendChild(dom.createTextNode("100"));
			e.appendChild(node);
		
			node = dom.createElement("data");
			nodeAttr = dom.createAttribute("key");
			nodeAttr.setValue("description");
			node.setAttributeNode(nodeAttr);
			node.appendChild(dom.createTextNode("root..."));
			e.appendChild(node);
		
			node = dom.createElement("data");
			nodeAttr = dom.createAttribute("key");
			nodeAttr.setValue("nodedisplay");
			node.setAttributeNode(nodeAttr);
			node.appendChild(dom.createTextNode("root"+"\n"+ "100"));
			e.appendChild(node);
			
			node = dom.createElement("data");
			nodeAttr = dom.createAttribute("key");
			nodeAttr.setValue("weight2");
			node.setAttributeNode(nodeAttr);
			node.appendChild(dom.createTextNode("99.99"));
			e.appendChild(node);
			
			dom.appendChild(root);
			
			//---------------------------------------------------------------------------
			//---------------------------------------------------------------------------
			
			try 
			{
				Transformer tr = TransformerFactory.newInstance().newTransformer();
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
				tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(xml)));
			} 
			catch(TransformerException te) 
			{System.out.println(te.getMessage());return te.toString();} 
			catch (IOException ioe) 
			{System.out.println(ioe.getMessage());return ioe.toString();}
			return "0";
		} 
		catch (ParserConfigurationException pce) 
		{return pce.toString();}
	}
	*/
}
