package ca.laurentian.concluder.refactorState;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.Control;
import prefuse.controls.PanControl;
import prefuse.controls.SubtreeDragControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.VisualItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HierarchyVisualization extends Visualization {
    //define graph,nodes,edges to store the data from xml file
    public static final String SELECTED = "sel";//store the selected node
    private static final String DIS = "noddis";//the displayed name of node(name + weight)
    private static String NODES = null;
    private static String EDGES = null;

    //selected node defaults to root
    public int SelectedNodeID = -1;

    public Display d;
    public Point current;
    public VisualItem currentItem;

    //create and open a visualization in java window to display models
    public void Create(String fileType, JFrame frame, Graph g,  /*double displayWidth, double displayHeight,*/ ArrayList<String> _selectednode, ArrayList<VisualItem> _selectedNodeVisualItem) {
        //add the graph in xml file to m_vis
        final ArrayList<String> selectednode = _selectednode;
        final ArrayList<VisualItem> selectedNodeVisualItem = _selectedNodeVisualItem;
        final JFrame _frame = frame;
        final Graph _g = g;
        final NumberFormat df = new DecimalFormat("#0.0");

        //new to handle compatibility
        NodeLinkTreeLayout treeLayout = null;
        if (fileType.compareTo(SystemConfiguration.TREE_FILE) == 0) {
            treeLayout = new NodeLinkTreeLayout(SystemConfiguration.TREE_FILE);
            add(fileType, g);
            setInteractive(SystemConfiguration.TREE_EDGES, null, false);
            EDGES = SystemConfiguration.TREE_EDGES;
            NODES = SystemConfiguration.TREE_NODES;
        } else if (fileType.compareTo(SystemConfiguration.GRAPH_FILE) == 0) {
            treeLayout = new NodeLinkTreeLayout(SystemConfiguration.GRAPH_FILE);
            add(fileType, g);
            setInteractive(SystemConfiguration.GRAPH_EDGES, null, false);
            NODES = SystemConfiguration.GRAPH_NODES;
            EDGES = SystemConfiguration.GRAPH_EDGES;
        }

        //define the display of nodes(shape,description,.etc)
        LabelRenderer nodeRenderer = new LabelRenderer(DIS);
        nodeRenderer.setRoundedCorner(8, 8); // round the corners
        nodeRenderer.setHorizontalPadding(10);
        nodeRenderer.setVerticalPadding(10);

        //add nodes to m_vis
        setRendererFactory(new DefaultRendererFactory(nodeRenderer));

        //define color for nodes(text color, node color, edge color, selected node color,.etc)
        ColorAction nodeFill = new ColorAction(NODES, VisualItem.FILLCOLOR, ColorLib.hex("cecece"));

        nodeFill.add("_hover", ColorLib.hex("89c3dd"));
        nodeFill.add(VisualItem.HIGHLIGHT, ColorLib.hex("89c3dd"));
        nodeFill.add(VisualItem.FIXED, ColorLib.hex("89c3dd"));

        ColorAction nodeBorder = new ColorAction(NODES, VisualItem.STROKECOLOR, ColorLib.hex("999999"));
        nodeBorder.add("_hover", ColorLib.hex("353535"));

        ColorAction nodeText = new ColorAction(NODES, VisualItem.TEXTCOLOR, ColorLib.gray(0));
        ColorAction edgeFill = new ColorAction(EDGES, VisualItem.STROKECOLOR, ColorLib.gray(200));
        FontAction nodeFont = new FontAction(NODES, FontLib.getFont("Arial", 12));

        //add nodefill and nodeborder,nodetext,edgefill nodefont to layout
        ActionList colors = new ActionList(Activity.INFINITY);
        colors.add(nodeFill);
        colors.add(nodeBorder);
        colors.add(nodeText);
        colors.add(edgeFill);
        colors.add(nodeFont);
        colors.add(new RepaintAction());
        putAction("colors", colors);

        // Create the layout action for the graph

        putAction("treeLayout", treeLayout);
        treeLayout.setOrientation(Constants.ORIENT_TOP_BOTTOM);//display tree from top to bottom

        //set space between nodes and subtrees
        treeLayout.setBreadthSpacing(20);
        treeLayout.setSubtreeSpacing(70);

        treeLayout.setLayoutAnchor(new Point2D.Double(frame.getSize().width / 2, frame.getSize().height / 4));


        //add color and treelayout to layout
        ActionList layout = new ActionList(ActionList.INFINITY);
        layout.add(colors);
        layout.add(treeLayout);
        layout.add(new RepaintAction());
        putAction("layout", layout);
        putAction("repaint", new RepaintAction());

        addFocusGroup(SELECTED);
        d = new Display(this);
        d.setSize(850, 570); // set the preferred display size
        // drag items and their subtrees
        d.addControlListener(new SubtreeDragControl());
        // pan with left-click drag on background
        d.addControlListener(new PanControl());
        // zoom with right-click drag
        d.addControlListener(new ZoomControl());
        //*** WORK ON THIS NEXT
        d.addControlListener(new ZoomToFitControl());
        d.addControlListener(new Control() {
            public boolean isEnabled() {
                return true;
            }

            public void setEnabled(boolean enabled) {
            }

            public void itemClicked(VisualItem item, MouseEvent e) {
                // IMPORTANT: These four lines are all you need to
                // change the selected items programmatically
                TupleSet focused = getFocusGroup(SELECTED);
                focused.clear();
                SelectedNodeID = item.getRow();
                current = e.getLocationOnScreen();
                currentItem = item;

                if (e.getClickCount() == 2) {


                    //remove highlight on all other current selections
                    for (int i = 0; i < selectedNodeVisualItem.size(); i++)
                        selectedNodeVisualItem.get(i).setHighlighted(false);
                    //clear both selected node containers
                    selectednode.clear();
                    selectedNodeVisualItem.clear();
                    //add only this selected node
                    selectednode.add(String.valueOf(SelectedNodeID));
                    selectedNodeVisualItem.add(item);
                    //the only item selected is highlighted
                    item.setHighlighted(true);
                    final NewNodeDialog nnd = new NewNodeDialog(_frame, _g, true, _g.getNode(SelectedNodeID).getString("name"), false);
                    nnd.setName(_g.getNode(SelectedNodeID).getString("name"));
                    nnd.setDesc(_g.getNode(SelectedNodeID).getString("desc"));
                    nnd.addWindowListener(new WindowListener() {

                        @Override
                        public void windowOpened(WindowEvent e) {
                        }

                        @Override
                        public void windowClosing(WindowEvent e) {
                        }

                        @Override
                        public void windowClosed(WindowEvent e) {
                            //node criteria is valid, submit new data
                            if (nnd.validSubmission()) {
                                _g.getNode(SelectedNodeID).set("name", nnd.getName());
                                _g.getNode(SelectedNodeID).set("noddis", nnd.getName() + "\n" + df.format(_g.getNode(SelectedNodeID).get("weight")));
                                _g.getNode(SelectedNodeID).set("desc", nnd.getDesc());
                            }
                        }

                        @Override
                        public void windowIconified(WindowEvent e) {
                        }

                        @Override
                        public void windowDeiconified(WindowEvent e) {
                        }

                        @Override
                        public void windowActivated(WindowEvent e) {
                        }

                        @Override
                        public void windowDeactivated(WindowEvent e) {
                        }
                    });
                    nnd.setVisible(true);
                } else {
                    //already selected
                    if (selectednode.contains(String.valueOf(SelectedNodeID))) {
                        int index = selectednode.indexOf(String.valueOf(SelectedNodeID));
                        selectednode.remove(index);
                        selectedNodeVisualItem.remove(index);
                        if (selectednode.size() > 0)
                            SelectedNodeID = Integer.parseInt(selectednode.get(selectednode.size() - 1));
                        else
                            SelectedNodeID = -1;
                        item.setHighlighted(false);
                    }
                    //not selected
                    else {
                        if (selectednode.size() == 2) {
                            selectednode.remove(0);
                            selectedNodeVisualItem.get(0).setHighlighted(false);
                            ;
                            selectedNodeVisualItem.remove(0);
                        }
                        selectednode.add(String.valueOf(SelectedNodeID));
                        selectedNodeVisualItem.add(item);
                        item.setHighlighted(true);
                    }
                    //item.isHighlighted()
                }
                focused.addTuple(item);
                run("colors");
            }

            public void itemDragged(VisualItem item, MouseEvent e) {
            }

            public void itemMoved(VisualItem item, MouseEvent e) {
            }

            public void itemWheelMoved(VisualItem item, MouseWheelEvent e) {
            }

            public void itemPressed(VisualItem item, MouseEvent e) {
            }

            public void itemReleased(VisualItem item, MouseEvent e) {
            }

            public void itemEntered(VisualItem item, MouseEvent e) {
            }

            public void itemExited(VisualItem item, MouseEvent e) {
            }

            public void itemKeyPressed(VisualItem item, KeyEvent e) {
            }

            public void itemKeyReleased(VisualItem item, KeyEvent e) {
            }

            public void itemKeyTyped(VisualItem item, KeyEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }

            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
            }

            public void mouseWheelMoved(MouseWheelEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        });
        // Hold the visualization in the fram
        frame.add(d);
        frame.pack();
        //assign the color
        run("colors");
        //start up the layout
        run("treeLayout");
    }

    public void runLayout() {
        run("treeLayout");
    }

    public int getSelectedNodeID() {
        return SelectedNodeID;
    }
}
