package ca.laurentian.concluder.refactorState;

import prefuse.data.Graph;
import prefuse.data.Tree;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.GraphMLWriter;
import prefuse.data.io.TreeMLReader;
import prefuse.data.io.TreeMLWriter;

import java.io.File;

/**
 * class is incomplete and very important to layered architecture
 *
 * @author tylerjessup
 */
public class PrefuseAPIAccess {
    public static void exportGraph(File f, Graph g) throws DataIOException {
        GraphMLWriter gw = new GraphMLWriter();
        gw.writeGraph(g, f);
    }

    public static void exportTree(File f, Tree g) throws DataIOException {
        TreeMLWriter gw = new TreeMLWriter();
        gw.writeGraph(g, f);
    }

    public static Tree importTree(File f) throws DataIOException, IllegalArgumentException {
        TreeMLReader gr = new TreeMLReader();
        return (Tree) gr.readGraph(f);
    }

    public static Graph importGraph(File f) throws DataIOException, IllegalArgumentException {
        GraphMLReader gr = new GraphMLReader();
        return (Tree) gr.readGraph(f);
    }

    //field 0 of node
    public static String getNodeName(Graph g, int ID) {
        return g.getNode(ID).getString("name");
    }

    //field 2?? of node
    public static String getNodeDescription(Graph g, int ID) {
        return g.getNode(ID).getString("desc");
    }
}
