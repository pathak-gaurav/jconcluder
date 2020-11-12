package ca.laurentian.concluder.refactorState;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.visual.VisualItem;

import javax.swing.*;
import java.util.Iterator;

public class RemoveNode {
    public static boolean removedSuccess;

    public RemoveNode(Graph graph, int SelectedNodeID, VisualItem vi, int viewMode) {
        //isolated node
        if (graph.getNode(SelectedNodeID).getParent() == null && graph.getNode(SelectedNodeID).getChildCount() == 0) {
            //more than a root node exists
            if (SelectedNodeID != 0) {
                graph.removeNode(SelectedNodeID);
                removedSuccess = true;
            } else {
                //cannot remove root node, graph must have 1 node
                JOptionPane.showMessageDialog(null, "Cannot delete root node.  1 node is mandatory.", "Root Node", JOptionPane.ERROR_MESSAGE);
                removedSuccess = false;
            }
        }
        //~root node with children
        else if (graph.getNode(SelectedNodeID).getParent() == null && graph.getNode(SelectedNodeID).getChildCount() != 0) {
            JOptionPane.showMessageDialog(null, "Cannot delete a parent node.  Only leaves.  Request for Implementation?", "Parent Node", JOptionPane.ERROR_MESSAGE);
        }
        //internal node
        else if (graph.getNode(SelectedNodeID).getParent() != null && graph.getNode(SelectedNodeID).getChildCount() != 0) {
            JOptionPane.showMessageDialog(null, "Cannot delete an internal node.  Only leaves.  Request for Implementation?", "Internal Node", JOptionPane.ERROR_MESSAGE);
        }
        //child node with no children
        else {
            //the node to remove
            Node child = graph.getNode(SelectedNodeID);
            //node to remove parent node
            Node parent = child.getParent();
            int childIndex = parent.getChildIndex(child);
            //remove edge from parent to child
            //inefficient
            Iterator i = graph.edges();
            while (i.hasNext()) {
                Edge e = ((Edge) i.next());
                if (e.getSourceNode() == graph.getNode(SelectedNodeID) || e.getTargetNode() == graph.getNode(SelectedNodeID))
                    graph.removeEdge(e);
            }
            //remove the node
            graph.removeNode(SelectedNodeID);
            removedSuccess = true;

            //after deletion, parent child count
            int childnum = parent.getChildCount();
            //no children, no weight to fix
            if (childnum == 0) {
                parent.set("rel", "No Children");
            }
            //set single existing child weight to that of parent
            //trivial case
            else if (childnum == 1) {
                //TDJ Logic, I think this a work around
                parent.getChild(0).set("weight", parent.get("weight"));
                parent.getChild(0).set("weight2", 100);
                parent.set("rel", "Single Child");
                new Weight_Redistributor(viewMode).Redistribute_From_Root(parent.getChild(0));
            } else {
                //fix the relation string
                //normalize weights
                //recursively distribute down, violted dirty nodes
                //fix parent and child where node was deleted
                //fix relation, using remove entity
                //weights normalized
                Weight_Reevaluator we = new Weight_Reevaluator(parent, null, viewMode);
                we.removeEntity(childIndex);
                //redefine PC matrix
                we.extract();
                //normalize
                we.reevaluate();
            }
            //recursively fix violated dirty nodes effected by removal
            Weight_Redistributor rw = new Weight_Redistributor(viewMode);
            rw.operate(parent);
        }
    }
}