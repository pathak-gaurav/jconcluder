package ca.laurentian.concluder.refactorState;

import prefuse.data.Node;

public class Weight_Redistributor {
    private int viewMode;

    public Weight_Redistributor(int viewMode) {
        this.viewMode = viewMode;
    }

    public void operate(Node parent) {
        Redistribute(parent);
    }

    //this is a recursive definition, to redistribute updated weight values
    //this is based on the fact that the parent has already reevaluated its child weights
    //evaluates for all children
    private void Redistribute(Node parent) {
        //if current node has no children, stop the redistribution process
        if (parent.getChildCount() == 0)
            return;
            //special case that the relation string can not execute generically
            //only a single child, trivial distribution of parent weight down
        else if (parent.getChildCount() == 1) {
            Node child = parent.getChild(0);
            child.set("weight", parent.get("weight"));
            child.set("weight2", 100.0);
            Redistribute(child);
        }
        //2 or more children, must be handled by Weight_Revaluator class
        else {
            //for all the children of this parent...
            for (int i = 0; i < parent.getChildCount(); i++) {
                Node child = parent.getChild(i);
                //most complex case of 2 or more children
                if (child.getChildCount() > 1) {
                    Weight_Reevaluator we = new Weight_Reevaluator(child, null, viewMode);
                    we.reevaluate();
                }
                //again trivial case of 1 child
                else if (child.getChildCount() == 1) {
                    Node grandChild = child.getChild(0);
                    grandChild.set("weight", child.get("weight"));
                    grandChild.set("weight2", 100.0);
                } else {/*no children*/}
                Redistribute(child);
            }
        }
    }

    //this is a recursive definition(calling above), to redistribute updated weight values
    //this is based on the fact that the parent has NOT already reevaluated its child weights
    //hence from root
    public void Redistribute_From_Root(Node root) {
        if (root.getChildCount() == 0)
            return;
        else if (root.getChildCount() == 1) {
            Node child = root.getChild(0);
            child.set("weight", root.get("weight"));
            child.set("weight2", 100.0);
            Redistribute_From_Root(child);
        } else {
            Weight_Reevaluator we = new Weight_Reevaluator(root, null, viewMode);
            we.reevaluate();
            for (int i = 0; i < root.getChildCount(); i++) {
                Node child = root.getChild(i);
                if (child.getChildCount() > 1) {
                    we = new Weight_Reevaluator(child, null, viewMode);
                    we.reevaluate();
                } else if (child.getChildCount() == 1) {
                    Node grandChild = child.getChild(0);
                    grandChild.set("weight", child.get("weight"));
                    grandChild.set("weight2", 100.0);
                } else {/*no children*/}
                Redistribute(child);
            }
        }
    }
}
