package ca.laurentian.concluder;

import ca.laurentian.concluder.refactorState.Weight_Redistributor;
import ca.laurentian.concluder.refactorState.Weight_Reevaluator;
import prefuse.data.Graph;
import prefuse.data.Node;

import javax.swing.JFrame;

public class AddNewNode {

    public AddNewNode(Graph graph, Node source, String name, String desc, JFrame frame, int viewMode) {
        //the new node that is requested to be added to the graph
        //no edge to parent, isolated
        Node newNode = graph.addNode();
        newNode.set("name", name);
        newNode.set("desc", desc);
        newNode.set("Node_ID", newNode.getRow());
        newNode.set("rel", "No Children");

        int childCount = source.getChildCount();
        //trivial case of single node add
        if (childCount == 0) {
            newNode.set("weight", source.get("weight"));
            newNode.set("weight2", 100.0);
            source.set("rel", "Single Child");
        }
        //hack against poor architecture
        //the relation string to hold all upper triangle comparisons
        //is a work around against the generic case using Split(" ")
        //must change to existing weight, rather than even distribution
        else if (childCount == 1) {
            source.set("rel", "1");
            //more sophisticated weight distributor using actual PC matrix
            Weight_Reevaluator wre = new Weight_Reevaluator(source, newNode, viewMode);
            wre.reevaluate();
        } else {
            if (childCount != 2) {
                //generic form using relation string
                //this is a nightmare to use and maintain

                //all upper triangular relative weights
                //this is the generic form that catches all cases of nxn >2
                String[] relativeWeights = source.getString("rel").split(" ");

                //computes theoretical size n of nxn PC matrix of the comparison count
                int numberOfComparisons = relativeWeights.length;

                //algorithm that calulates the size n of the PC matrix
                int squareSizeOfPCMatrix = 0;
                int sizeReduction = 1;
                while (numberOfComparisons > 0) {
                    squareSizeOfPCMatrix++;
                    numberOfComparisons -= sizeReduction;
                    sizeReduction += 1;
                }
                squareSizeOfPCMatrix++;

                //creates new composite string with added node, all weights 1 of new node
                String newCompositeRelativeWeightString;

                int k = 0;
                StringBuilder newCompositeRelativeWeightStringBuilder = new StringBuilder();
                for (int i = squareSizeOfPCMatrix; i > 0; i--) {
                    for (int j = 0; j < i - 1; j++) {
                        //include existing weights
                        newCompositeRelativeWeightStringBuilder.append(relativeWeights[k]).append(" ");
                        k++;
                    }
                    //add the new node weight of 1
                    newCompositeRelativeWeightStringBuilder.append("1 ");
                }
                newCompositeRelativeWeightString = newCompositeRelativeWeightStringBuilder.toString();
                newCompositeRelativeWeightString = newCompositeRelativeWeightString.substring(0, newCompositeRelativeWeightString.length() - 1);
                source.set("rel", newCompositeRelativeWeightString);
            }
            //new node creates 3 children
            //initialize all weights to be equal
            //changes are maintained through relate
            else {
                //hack against string format for upper triangle
                source.set("rel", source.get(3) + " 1 1");
            }
            //since a node is added, normailze and add new node
            Weight_Reevaluator wre = new Weight_Reevaluator(source, newNode, viewMode);
            wre.reevaluate();
        }
        //add the new edge from parent to child
        graph.addEdge(source, newNode);
        //recursively redistribute weights violated(dirtied) by new node addition
        Weight_Redistributor rw = new Weight_Redistributor(viewMode);
        rw.operate(source);
    }
}
	