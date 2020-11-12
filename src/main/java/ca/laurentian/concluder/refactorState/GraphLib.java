//This code in parts was adopted and modified to suit.
///Structure used as a template
//Taken from http://prefuse.org/.
//Date 2016-08-02

package ca.laurentian.concluder.refactorState;

import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Tree;

public class GraphLib {
    /* Node table schema used for generated Graphs */
    public static final Schema LABEL_SCHEMA = new Schema();

    static {
        LABEL_SCHEMA.addColumn("name", String.class, "");
        LABEL_SCHEMA.addColumn("weight", double.class, -1);
        LABEL_SCHEMA.addColumn("desc", String.class, "");
        LABEL_SCHEMA.addColumn("rel", String.class, "");
        LABEL_SCHEMA.addColumn("noddis", String.class, "");
        LABEL_SCHEMA.addColumn("weight2", double.class, -1);
        LABEL_SCHEMA.addColumn("Node_ID", int.class, 777);
    }

    private GraphLib() {
    }

    public static Tree getInitializedTree() {
        RootableTree t = new RootableTree();
        t.getNodeTable().addColumns(LABEL_SCHEMA);
        t.setNodeKey("Node_ID");
        Node r = t.addRoot();
        //manage node identities
        r.setString("name", "ROOT");
        r.setDouble("weight", 100.0);
        r.setString("noddis", r.getString("name") + "\n" + r.getDouble("weight"));
        r.setString("rel", "No Children");
        r.setDouble("weight2", 100.0);
        return t;
    }
} 
