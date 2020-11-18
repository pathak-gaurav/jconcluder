package ca.laurentian.concluder.refactorState;

import prefuse.data.Graph;
import prefuse.data.Node;

import java.util.Iterator;

public class ViewModeAdministrator {
     private static final byte VIEW_GLOBAL_MODE = 0;
     private static final String VIEW_GLOBAL_MODE_WEIGHT_SPECIFIER = "weight";

     private static final byte VIEW_LOCAL_MODE = 1;
     private static final String VIEW_LOCAL_MODE_WEIGHT_SPECIFIER = "weight2";

    private static final String NODE_DISPLAY = "noddis";
    private static final String NODE_NAME = "name";

    /**
     * View Mode Administrator processes the view mode call
     *
     * @param viewMode - The view mode which displays the given weight specified
     * @param g        - The graph to have the view mode applied
     */
    public ViewModeAdministrator(int viewMode, Graph g) {
        runViewMode(viewMode, g);
    }

    /**
     * Run the view mode request
     * Display can show either local or global weights
     *
     * @param viewMode - The view mode which displays the given weight specified
     * @param g        - The graph to have the view mode applied
     */
    private void runViewMode(int viewMode, Graph g) {
        if (viewMode == VIEW_LOCAL_MODE)
            viewModeGraphWalk(ViewModeAdministrator.VIEW_LOCAL_MODE_WEIGHT_SPECIFIER, g);
        else if (viewMode == VIEW_GLOBAL_MODE)
            viewModeGraphWalk(ViewModeAdministrator.VIEW_GLOBAL_MODE_WEIGHT_SPECIFIER, g);
    }

    /**
     * Walk the graph and switch between local and global weight display
     * Display can show either local or global weights
     *
     * @param viewModeWeightSpecifier - The node attribute field identifier for the given weight display
     * @param g                       - The graph to walk and display the given weight specified
     */
    private void viewModeGraphWalk(String viewModeWeightSpecifier, Graph g) {
        @SuppressWarnings("unchecked")
        Iterator<Node> i = g.nodes();
        while (i.hasNext()) {
            Node n = i.next();
            //set the display given
            n.set(NODE_DISPLAY, n.get(NODE_NAME) + "\n" + SystemConfiguration.formatNumber(n.getDouble(viewModeWeightSpecifier)));
        }
    }
}
