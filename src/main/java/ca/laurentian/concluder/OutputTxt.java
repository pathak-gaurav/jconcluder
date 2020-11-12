package ca.laurentian.concluder;

import ca.laurentian.concluder.refactorState.SystemConfiguration;
import prefuse.data.Graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

class OutputTxt {
    public OutputTxt(File file, Graph graph, String str[], String type, int SelectedNodeID) {
        try {
            //this is not OS Independent
            File f = new File("C:\\" + file.getName().replace(".xml", "") + "_" + type + ".txt");
            //create the file to store the data
            if (!f.exists())
                f.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            if (type.equals("weights")) {
                //str[i] = X.X%		NAME
                //...
                for (int i = 0; i < str.length; i++)
                    output.write(str[i] + "\r\n");
                //update the user of success
                new Concluder().warnWindow("Output weights to C:\\ successfully", 400, 100);
            } else if (type.equals("PCmatrix")) {
                String cellValue[][] = {
                        {"", "", "", "", "", "", ""},
                        {"", "", "", "", "", "", ""},
                        {"", "", "", "", "", "", ""},
                        {"", "", "", "", "", "", ""},
                        {"", "", "", "", "", "", ""},
                        {"", "", "", "", "", "", ""},
                        {"", "", "", "", "", "", ""},
                };//matrix used to store the input data in the form of string
                int n = 0;
                //fill all upper triangle elements
                if (str.length == 3) {
                    for (int i = 0; i < 2; i++) {
                        for (int j = i + 1; j < 3; j++) {
                            cellValue[i][j] = SystemConfiguration.formatNumber(Float.valueOf(str[n]));
                            n++;
                        }

                    }

                } else if (str.length == 6) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = i + 1; j < 4; j++) {
                            cellValue[i][j] = SystemConfiguration.formatNumber(Float.valueOf(str[n]));
                            n++;
                        }

                    }

                } else if (str.length == 10) {
                    for (int i = 0; i < 4; i++) {
                        for (int j = i + 1; j < 5; j++) {
                            cellValue[i][j] = SystemConfiguration.formatNumber(Float.valueOf(str[n]));
                            n++;
                        }

                    }

                } else if (str.length == 15) {
                    for (int i = 0; i < 5; i++) {
                        for (int j = i + 1; j < 6; j++) {
                            cellValue[i][j] = SystemConfiguration.formatNumber(Float.valueOf(str[n]));
                            n++;
                        }

                    }

                } else if (str.length == 21) {
                    for (int i = 0; i < 6; i++) {
                        for (int j = i + 1; j < 7; j++) {
                            cellValue[i][j] = SystemConfiguration.formatNumber(Float.valueOf(str[n]));
                            n++;
                        }

                    }

                }
                for (int i = 0; i < graph.getNode(SelectedNodeID).getChildCount(); i++) {
                    for (int j = 0; j < graph.getNode(SelectedNodeID).getChildCount(); j++) {
                        //fill main diagonal
                        if (i == j) {
                            cellValue[i][j] = "1.00";
                        }
                        //fill lower triangle
                        if (i > j) {
                            cellValue[i][j] = SystemConfiguration.formatNumber(1 / Float.valueOf(cellValue[j][i]));
                        }
                    }
                }
                //write PC to file
                for (int i = 0; i < cellValue.length; i++) {
                    for (int j = 0; j < cellValue.length; j++) {
                        output.write(cellValue[i][j] + " ");
                    }
                    output.write("\r\n");
                }
                new Concluder().warnWindow("Output PCmatrix to C:\\ successfully", 400, 100);
            }
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}