package ca.laurentian.concluder.refactorState;

//This code was adopted and modified to suit.  Tyler D. Jessup
//Taken from http://www.geeksforgeeks.org/detect-cycle-undirected-graph/
//Date:  2016-08-02

// A Java Program to detect cycle in an undirected graph

import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.Tuple;

import java.util.Iterator;
import java.util.LinkedList;

// This class represents a directed graph using adjacency list
// representation
public class TreeStructureValidation {
    private int V;   // No. of vertices
    private LinkedList<Integer> adj[]; // Adjacency List Represntation

    // Constructor
    TreeStructureValidation(int v) {
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList();
    }

    public static boolean testCycleExistence(Graph t) {
        TreeStructureValidation g1 = new TreeStructureValidation(t.getNodeCount());
        Table edgeTable = t.getEdgeTable();
        for (int i = 0; i < edgeTable.getRowCount(); i++) {
            Tuple edge = edgeTable.getTuple(i);
            g1.addEdge(edge.getInt("source"), edge.getInt("target"));
        }
        return g1.isCyclic();
    }

    // Function to add an edge into the graph
    void addEdge(int v, int w) {
        adj[v].add(w);
        adj[w].add(v);
    }

    // A recursive function that uses visited[] and parent to detect
    // cycle in subgraph reachable from vertex v.
    Boolean isCyclicUtil(int v, Boolean visited[], int parent) {
        // Mark the current node as visited
        visited[v] = true;
        Integer i;

        // Recur for all the vertices adjacent to this vertex
        Iterator<Integer> it = adj[v].iterator();
        while (it.hasNext()) {
            i = it.next();

            // If an adjacent is not visited, then recur for that
            // adjacent
            if (!visited[i]) {
                if (isCyclicUtil(i, visited, v))
                    return true;
            }

            // If an adjacent is visited and not parent of current
            // vertex, then there is a cycle.
            else if (i != parent)
                return true;
        }
        return false;
    }

    // Returns true if the graph contains a cycle, else false.
    Boolean isCyclic() {
        // Mark all the vertices as not visited and not part of
        // recursion stack
        Boolean visited[] = new Boolean[V];
        for (int i = 0; i < V; i++)
            visited[i] = false;

        // Call the recursive helper function to detect cycle in
        // different DFS trees
        for (int u = 0; u < V; u++)
            if (!visited[u]) // Don't recur for u if already visited
                if (isCyclicUtil(u, visited, -1))
                    return true;

        return false;
    }
}
// This code is contributed by Aakash Hasija