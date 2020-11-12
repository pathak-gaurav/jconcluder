package ca.laurentian.concluder.refactorState;

import prefuse.data.Node;

public class Weight_Reevaluator {
    //the first created actual PC matrix in the entire program, with a proper use
    public static double[][] PCMatrix;
    private int squareSizeOfPCMatrix;

    //stores each PC matrix row's geometric mean
    private double[] PCMatrixRowGeometricMeans;
    private double sumOfPCMatrixRowGeometricMeans;

    //stores the updated normalized child nodes new weight
    private double[] globalNormalizedChildWeights;
    private double[] localNormalizedChildWeights;

    //used to have access to parent weight
    //BUT...also stores the child weights as a composite string
    //i.e "1 1.5 2 4.5 7"...
    //this is used globally and within spaghetti code
    //the in memory tree of tables as discussed by WWK will replace this combersum maintenance
    private Node parentNode;
    private double parentNodeWeight;

    //the new node to be added, if called by addNewNode
    //else null if called by any other class
    private Node newNode;

    private int viewMode;

    public Weight_Reevaluator(Node parentNode, Node newNode, int viewMode) {
        this.parentNode = parentNode;
        parentNodeWeight = parentNode.getDouble("weight");
        //this will be null if called by relate, inconsistency, or delete node
        //only applicable to addNode
        this.newNode = newNode;
        this.viewMode = viewMode;
        //generate the PC matrix for this parent node
        extract();
    }

    public void extract() {
        //this shown the unnecessary cohersion x2 (1 and 2 below) of data, and shows the need for in memory data structure

        //(1)hack against poor architecture
        //first cannot be generically generated from split(" "), as has only 1 element
        //
        //second, can be generically generated from split(" "), for PC matrixes size(nxn) > 2
        //
        //this needs fixing
        //is not generic on a parent with 2 existing nodes
        //weight should be maintained, not forced to even distribution

        if (!((String) parentNode.getString("rel")).contains(" ")) {
            double[][] temp = {{1, Double.parseDouble((String) parentNode.get(3))}, {1 / Double.parseDouble((String) parentNode.get(3)), 1}};
            PCMatrix = temp;
            squareSizeOfPCMatrix = 2;
        } else {
            //(2)given the string as a parameter...

            //splits apart all the child relations, stored in this string
            //only stores the upper triangle of the PC matrix, all other values, main diagonal and lower triagle are absent
            String[] relativeWeights = ((String) this.parentNode.getString("rel")).split(" ");

            //since on the upper triangle is stored, the actual PC matrix must be constructed
            //this computes the size of n, of the nxn PC matrix
            //...
            // the string given "1 1 1 1 1 1 1 1 1 1", which length when split on " " is 10 elements
            //stores the first row upper triangle elements, 2nd row... 3rd row...
            //represents:
            // 1 1 1 1 		<- 5x5 (4-4) = 0 => 4+1=5x5(Done)
            //   1 1 1 		<- 4x4 (7-3) = 4
            //     1 1 		<- 3x3 (9-2) = 7
            //       1 		<- 2x2 (10-1) = 9
            // starting at 1 and subtracting by increments of 1, the PC matrix size can be found.  Then add 1 to complete.
            int numberOfComparisons = relativeWeights.length;
            squareSizeOfPCMatrix = 1;
            int sizeReduction = 1;
            while (numberOfComparisons > 0) {
                squareSizeOfPCMatrix++;
                numberOfComparisons -= sizeReduction;
                sizeReduction += 1;
            }

            //used to point at the next relative weight within the tedious string
            int indexIntoRelativeWeights = 0;

            //creates the first implicit PC matrix in the entire program
            PCMatrix = new double[squareSizeOfPCMatrix][squareSizeOfPCMatrix];
            for (int PCMatrixRow = 0; PCMatrixRow < squareSizeOfPCMatrix; PCMatrixRow++) {
                for (int PCMatrixColumn = 0; PCMatrixColumn < squareSizeOfPCMatrix; PCMatrixColumn++) {
                    if (PCMatrixRow == PCMatrixColumn)
                        PCMatrix[PCMatrixRow][PCMatrixColumn] = 1;
                    else if (PCMatrixRow < PCMatrixColumn) {
                        //as only upper triangular elements are stored
                        //this is the place where the poorly chosen string populates the table
                        PCMatrix[PCMatrixRow][PCMatrixColumn] = Double.parseDouble(relativeWeights[indexIntoRelativeWeights]);
                        indexIntoRelativeWeights++;
                    } else
                        PCMatrix[PCMatrixRow][PCMatrixColumn] = 1 / PCMatrix[PCMatrixColumn][PCMatrixRow];
                }
            }
        }
    }

    public void reevaluate() {
        normalize();
    }

    private void normalize() {
        computeRowsGeometricMeansForPCMatrix();
        computeSumOfPCMatrixRowGeometricMeans();
        createNormalizedWeightsForChildNodes();
        resetParentandChildNodesWithNewNormalizedWeights();
    }

    private void computeRowsGeometricMeansForPCMatrix() {
        PCMatrixRowGeometricMeans = new double[squareSizeOfPCMatrix];
        double RowProduct;
        for (int PCMatrixRow = 0; PCMatrixRow < squareSizeOfPCMatrix; PCMatrixRow++) {
            RowProduct = 1;
            for (int PCMatrixColumn = 0; PCMatrixColumn < squareSizeOfPCMatrix; PCMatrixColumn++)
                RowProduct *= PCMatrix[PCMatrixRow][PCMatrixColumn];
            PCMatrixRowGeometricMeans[PCMatrixRow] = Math.pow(RowProduct, 1 / (double) squareSizeOfPCMatrix);
        }
    }

    private void computeSumOfPCMatrixRowGeometricMeans() {
        sumOfPCMatrixRowGeometricMeans = 0;
        for (int RowGeometricMean = 0; RowGeometricMean < PCMatrixRowGeometricMeans.length; RowGeometricMean++)
            sumOfPCMatrixRowGeometricMeans += PCMatrixRowGeometricMeans[RowGeometricMean];
    }

    private void createNormalizedWeightsForChildNodes() {
        localNormalizedChildWeights = new double[squareSizeOfPCMatrix];
        globalNormalizedChildWeights = new double[squareSizeOfPCMatrix];
        for (int normalizedChild = 0; normalizedChild < squareSizeOfPCMatrix; normalizedChild++) {
            localNormalizedChildWeights[normalizedChild] = 100 * (PCMatrixRowGeometricMeans[normalizedChild] / sumOfPCMatrixRowGeometricMeans);
            globalNormalizedChildWeights[normalizedChild] = parentNodeWeight * (PCMatrixRowGeometricMeans[normalizedChild] / sumOfPCMatrixRowGeometricMeans);
        }
    }

    private void resetParentandChildNodesWithNewNormalizedWeights() {
        ///reset weight for each existing child
        for (int child = 0; child < parentNode.getChildCount(); child++) {
            Node thisChild = parentNode.getChild(child);
            //TDJ Logic, I think this a work around
            thisChild.set("weight", globalNormalizedChildWeights[child]);
            thisChild.set("weight2", localNormalizedChildWeights[child]);
        }
        //if adding a node, set weight and display
        if (newNode != null) {
            //TDJ Logic, I think this a work around
            newNode.set("weight", globalNormalizedChildWeights[squareSizeOfPCMatrix - 1]);
            newNode.set("weight2", localNormalizedChildWeights[squareSizeOfPCMatrix - 1]);
        }
    }

    //given the existing PC matrix, removing a node
    //removes the specific entity, and all corresponding comparisons
    public void removeEntity(int entity) {
        String newCompositeRelativeWeightString = "";
        for (int PCMatrixRow = 0; PCMatrixRow < squareSizeOfPCMatrix; PCMatrixRow++) {
            //skip removal row
            if (PCMatrixRow == entity) continue;
            for (int PCMatrixColumn = 0; PCMatrixColumn < squareSizeOfPCMatrix; PCMatrixColumn++) {
                //skip removal column for all comparisons
                if (PCMatrixColumn == entity) continue;
                else {
                    if (PCMatrixColumn > PCMatrixRow)
                        newCompositeRelativeWeightString += PCMatrix[PCMatrixRow][PCMatrixColumn] + " ";
                }
            }
        }
        newCompositeRelativeWeightString = newCompositeRelativeWeightString.substring(0, newCompositeRelativeWeightString.length() - 1);
        parentNode.set("rel", newCompositeRelativeWeightString);
    }
}