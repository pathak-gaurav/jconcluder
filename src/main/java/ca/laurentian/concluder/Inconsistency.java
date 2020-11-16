package ca.laurentian.concluder;

import ca.laurentian.concluder.refactorState.PCTableModel;
import ca.laurentian.concluder.refactorState.RXTable;
import ca.laurentian.concluder.refactorState.View_Mode_Administrator;
import ca.laurentian.concluder.refactorState.Weight_Redistributor;
import ca.laurentian.concluder.refactorState.Weight_Reevaluator;
import prefuse.data.Graph;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static ca.laurentian.concluder.refactorState.SystemConfiguration.*;
import static javax.swing.JLabel.*;

class Inconsistency extends JFrame implements ActionListener {
    //i,j,k for the current highlighted triad{aij,aik,akj}
    static int highlightedI;
    static int highlightedJ;
    static int highlightedK;

    //count how many times button "next_inconsistency" has been clicked by user
    int buttonNextInconsistencyCounter = 0;

    //the number of the child nodes of the selected node
    int childNumber;

    //new an array to store all inconsistency values of the PC matrix
    double[] inconsistency = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    //new UI components
    JButton buttonOk = new JButton();
    JButton buttonMaxInconsistency = new JButton();
    JButton buttonNextInconsistency = new JButton();
    JButton buttonReduceInconsistency = new JButton();
    JButton buttonReduceTriadInconsistency = new JButton();
    JButton buttonLocateElement = new JButton();
    JLabel labelMaxInconsistency = new JLabel();
    JLabel labelReduceInconsistency = new JLabel();

    //new a matrix to store the PC matrix in the form of string
    String[][] cellValue = {
            {"", "", "", "", "", "", ""},
            {"", "", "", "", "", "", ""},
            {"", "", "", "", "", "", ""},
            {"", "", "", "", "", "", ""},
            {"", "", "", "", "", "", ""},
            {"", "", "", "", "", "", ""},
            {"", "", "", "", "", "", ""},
    };

    //new a matrix to store the PC matrix in the form of Objects/Table
    Object[][] cells =
            {
                    {"", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", ""},
            };

    //new a matrix to store those elements with the same value of inconsistency
    int[][] sameInconsistencyTriad = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    //the number of triple elements group with the same value of inconsistency
    int samecount = 0;
    String[] columns = {"", "", "", "", "", "", ""};
    int num = 0;
    Graph graph;
    File file;
    int ananode;//the selected parent node
    JFrame frame = new JFrame();
    RXTable table;
    //DefaultTableModel model = new DefaultTableModel(cells, columns);
    JScrollPane scroll = new JScrollPane();
    private final int viewMode;

    public Inconsistency(String[] childWeight, String relation, Graph g, File f, int A, int viewMode) {
        this.viewMode = viewMode;
        graph = g;
        file = f;
        ananode = A;
        frame.setSize(530, 570);
        frame.setTitle("Inconsistency analysis");
        frame.setBackground(Color.lightGray);
        frame.setResizable(true);
        frame.setLocation(400, 120);
        Panel p1 = new Panel();
        Panel p2 = new Panel();
        Panel p3 = new Panel();

        PCTableModel model = new PCTableModel(cells, columns);
        table = new RXTable(model);
        table.setSelectAllForEdit(true);
        table.setRowHeight(57);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setCellSelectionEnabled(false);

        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(CENTER);
        table.setDefaultRenderer(Object.class, r);

        javax.swing.table.TableColumnModel tcm = table.getColumnModel();
        javax.swing.table.TableColumn tc;
        for (int i = 0; i < table.getRowCount(); i++) {
            tc = tcm.getColumn(i);
            tc.setPreferredWidth(64);
        }
        scroll.getViewport().add(table);
        scroll.setBounds(10, 10, 100, 100);
        labelMaxInconsistency.setText("Inconsistency:                      ");
        buttonOk.setText("Ok");
        buttonOk.addActionListener(this);
        buttonMaxInconsistency.setText("Maximal inconsistency");
        buttonMaxInconsistency.addActionListener(this);
        buttonNextInconsistency.setText(">");
        buttonNextInconsistency.addActionListener(this);
        labelReduceInconsistency.setText("Reduce inconsistency by:  ");
        buttonReduceInconsistency.setText("BAL");
        buttonReduceInconsistency.addActionListener(this);
        buttonReduceTriadInconsistency.setText("Triad");
        buttonReduceTriadInconsistency.addActionListener(this);
        buttonLocateElement.setText("Most inconsistent element");
        buttonLocateElement.addActionListener(this);
        table.getModel().addTableModelListener(new TableModelListener1());
        p1.add(scroll);
        p2.add(labelMaxInconsistency);
        p2.add(buttonMaxInconsistency);
        p2.add(buttonNextInconsistency);
        p2.add(labelReduceInconsistency);
        p2.add(buttonReduceInconsistency);
        p2.add(buttonReduceTriadInconsistency);
        p2.add(buttonLocateElement);
        p3.add(buttonOk);
        p2.setLayout(new FlowLayout(FlowLayout.LEFT));
        p1.setBounds(10, 10, 100, 100);
        p2.setBounds(10, 100, 100, 130);
        frame.add(p1, BorderLayout.NORTH);
        frame.add(p2, BorderLayout.CENTER);
        frame.add(p3, BorderLayout.SOUTH);
        frame.setVisible(true);

        String[] relation2 = relation.split(" ");
        int n = 0;
        //put the relations of criteria into the PC matrix
        if (relation2.length == 3)//3 by 3 pc matrix
        {
            for (int i = 0; i < 2; i++) {
                for (int j = i + 1; j < 3; j++) {
                    cellValue[i][j] = formatNumber(Double.parseDouble(String.valueOf(relation2[n])));
                    n++;
                }

            }

        } else if (relation2.length == 6)//4 by 4 pc matrix
        {
            for (int i = 0; i < 3; i++) {
                for (int j = i + 1; j < 4; j++) {
                    cellValue[i][j] = formatNumber(Double.parseDouble(String.valueOf(relation2[n])));
                    n++;
                }

            }

        } else if (relation2.length == 10)//5 by 5 pc matrix
        {
            for (int i = 0; i < 4; i++) {
                for (int j = i + 1; j < 5; j++) {
                    cellValue[i][j] = formatNumber(Double.parseDouble(String.valueOf(relation2[n])));
                    n++;
                }

            }

        } else if (relation2.length == 15)///6 by 6 pc matrix
        {
            for (int i = 0; i < 5; i++) {
                for (int j = i + 1; j < 6; j++) {
                    cellValue[i][j] = formatNumber(Double.parseDouble(String.valueOf(relation2[n])));
                    n++;
                }

            }

        } else if (relation2.length == 21)//7 by 7 pc matrix
        {
            for (int i = 0; i < 6; i++) {
                for (int j = i + 1; j < 7; j++) {
                    cellValue[i][j] = formatNumber(Double.parseDouble(String.valueOf(relation2[n])));
                    n++;
                }

            }

        }

        for (childNumber = 0; childNumber < childWeight.length; childNumber++) {
            if (childWeight[childNumber].equals(""))
                break;
        }
        for (int row = 0; row < childNumber; row++) {
            for (int column = 0; column < childNumber; column++) {
                //table.setValueAt(cellValue[row][column], row, column);
                if ((!cellValue[row][column].equals("")) && (column >= row))//fill up the lower triangle part of the pc matrix
                {
                    //table.setValueAt("1/"+cellValue[row][column], column, row);
                    try {
                        table.setValueAt(formatNumber(Double.parseDouble("" + 1 / unformatNumberString(cellValue[row][column]))), column, row);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        for (int i = 0; i < childNumber; i++) {
            table.setValueAt("1", i, i);//display 1 in the diagonal of the table
        }
    }

    //sort an array
    public static void sort(double[] data) {
        for (int j = 1; j <= data.length; j++) {
            for (int i = 0; i < data.length - 1; i++) {
                if (data[i] < data[i + 1]) {
                    double temp;
                    temp = data[i];
                    data[i] = data[i + 1];
                    data[i + 1] = temp;
                }
            }
        }
    }

    public double min(double a, double b)//find the smaller one between a and b
    {
        return Math.min(a, b);
    }

    //compute inconsistency of a triad
    public void computeInconsistency(String[][] cellValue1) throws Exception {
        num = 0;
        for (int i = 0; i < 5; i++) {
            for (int k = i + 1; k < 6; k++) {
                for (int j = k + 1; j < 7; j++) {
                    if ((!cellValue1[i][j].equals("")) && (!cellValue1[i][k].equals("")) && (!cellValue1[k][j].equals(
                            ""))) {
                        double a = unformatNumberString(cellValue1[i][j]);//obtain value from the matrix
                        double b = unformatNumberString(cellValue1[i][k]);//obtain value from the matrix
                        double c = unformatNumberString(cellValue1[k][j]);//obtain value from the matrix
                        inconsistency[num] = (int) Math.round(
                                min(Math.abs(1 - a / (b * c)), Math.abs(1 - (b * c) / a)) * 100) / 100f;//calculate the inconsistency value and store in the inconsistency array
                        num++;
                    }
                }
            }
        }
        sort(inconsistency);
    }

    //display the triad with maximal inconsistency
    public void showMaxInconsistency(String[][] cellValue1) throws Exception {
        labelMaxInconsistency.setText("Inconsistency: " + inconsistency[0] + "             ");//display the first element which is the largest number of the inconsistency matrix
        for (int i = 0; i < 5; i++) {
            for (int k = i + 1; k < 6; k++) {
                for (int j = k + 1; j < 7; j++) {
                    if ((!cellValue1[i][j].equals("")) && (!cellValue1[i][k].equals("")) && (!cellValue1[k][j].equals(""))) {
                        double a = unformatNumberString(cellValue1[i][j]);//obtain value from the matrix
                        double b = unformatNumberString(cellValue1[i][k]);//obtain value from the matrix
                        double c = unformatNumberString(cellValue1[k][j]);//obtain value from the matrix
                        if (inconsistency[0] == (int) Math.round(
                                min(Math.abs(1 - a / (b * c)), Math.abs(1 - (b * c) / a)) * 100) / 100f)//find the triple elements with the largest inconsistency
                        {
                            highlightedI = i;
                            highlightedJ = j;
                            highlightedK = k;
                            table.setDefaultRenderer(Object.class, new EvenOddRenderer());    //highlight the triple elements
                        }
                    }
                }
            }
        }
    }

    //button anction listener
    public void actionPerformed(ActionEvent e) {
        try {
            handler(e);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void handler(ActionEvent e) throws Exception {
        if (e.getSource() == buttonOk)//apply the changes in pc matrix
        {
            int acount = 0;
            for (int i = 0; i < cellValue.length; i++) {
                if ((!cellValue[0][i].equals("")))
                    acount++;
            }
            StringBuilder relation = new StringBuilder();
            for (int i = 0; i < acount; i++) {
                for (int j = i + 1; j < acount; j++) {
                    relation.append(unformatNumberString(cellValue[i][j])).append(" ");
                }
            }
            graph.getNode(ananode).set(3, relation.toString());
            if (file != null) {
                int childnumber = graph.getNode(ananode).getChildCount();
                Weight_Reevaluator we = new Weight_Reevaluator(graph.getNode(ananode), null, viewMode);
                we.reevaluate();
                for (int i = 0; i < childnumber; i++) {
                    Weight_Redistributor rw = new Weight_Redistributor(viewMode);
                    rw.Redistribute_From_Root(graph.getNode(ananode).getChild(i));
                }
                new View_Mode_Administrator(viewMode, graph);
            }
            frame.setVisible(false);
            frame.dispose();
        } else if (e.getSource() == buttonMaxInconsistency)//display the triad with the maximal inconsistency
        {
            computeInconsistency(cellValue);//compute the inconsistency
            showMaxInconsistency(cellValue);//display the triad with maximal inconsistency
        } else if (e.getSource() == buttonNextInconsistency)//display the next triad by decending inconsistency order
        {
            if (buttonNextInconsistencyCounter > num - 1)//the number user clicked the next button beyond the total number of the inconsistency value
            {
                buttonNextInconsistencyCounter = 0;//go back to the first element of the inconsistency array
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 3; j++) {
                        sameInconsistencyTriad[i][j] = 0;
                    }
                }
                samecount = 0;
            } else if (buttonNextInconsistencyCounter == 0)//it's the first time for user to click next button
            {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 3; j++) {
                        sameInconsistencyTriad[i][j] = 0;
                    }
                }
                samecount = 0;
            }
            labelMaxInconsistency.setText("inconsistency: " + inconsistency[buttonNextInconsistencyCounter] + "             ");//display inconsistency value
            //find the triad having the specific inconsistency
            A:
            for (int i = 0; i < 5; i++) {
                for (int k = i + 1; k < 6; k++) {
                    for (int j = k + 1; j < 7; j++) {
                        if ((!cellValue[i][j].equals("")) && (!cellValue[i][k].equals("")) && (!cellValue[k][j].equals(""))) {
                            int flag = 0;
                            double a = unformatNumberString(cellValue[i][j]);//obtain value from the matrix
                            double b = unformatNumberString(cellValue[i][k]);//obtain value from the matrix
                            double c = unformatNumberString(cellValue[k][j]);//obtain value from the matrix
                            if (inconsistency[buttonNextInconsistencyCounter] == (int) Math.round(
                                    min(Math.abs(1 - a / (b * c)), Math.abs(1 - (b * c) / a)) * 100) / 100f)//find the triple elements with the same inconsistency
                            {
                                for (int n1 = 0; n1 < 10; n1++) {
                                    if ((sameInconsistencyTriad[n1][0] == i) && (sameInconsistencyTriad[n1][1] == j) && (
                                            sameInconsistencyTriad[n1][2] == k)) {
                                        flag = 1;//there exists another triple elements with the same value of inconsistency
                                    }
                                }
                                if (flag == 0)//the inconsistency value is unique
                                {
                                    highlightedI = i;
                                    highlightedJ = j;
                                    highlightedK = k;
                                    table.setDefaultRenderer(Object.class, new EvenOddRenderer());//display the triple elements
                                    sameInconsistencyTriad[samecount][0] = i;//store the position of the triple elements
                                    sameInconsistencyTriad[samecount][1] = j;//store the position of the triple elements
                                    sameInconsistencyTriad[samecount][2] = k;//store the position of the triple elements
                                    samecount++;
                                    break A;
                                }
                            }
                        }
                    }
                }
            }
            buttonNextInconsistencyCounter++;
        } else if (e.getSource() == buttonReduceInconsistency)//reduce inconsistency automatically according to weight-based inconsistency reduction method
        {
            int execute = 0;
            B:
            while (inconsistency[0] > 0.333333) {
                execute++;
                for (int i = 0; i < 5; i++) {
                    for (int k = i + 1; k < 6; k++) {
                        for (int j = k + 1; j < 7; j++) {
                            if ((!cellValue[i][j].equals("")) && (!cellValue[i][k].equals("")) && (!cellValue[k][j].equals(""))) {
                                double a = unformatNumberString(cellValue[i][j]);//obtain value from the matrix
                                double b = unformatNumberString(cellValue[i][k]);//obtain value from the matrix
                                double c = unformatNumberString(cellValue[k][j]);//obtain value from the matrix
                                if (inconsistency[0] == (int) Math.round(
                                        min(Math.abs(1 - a / (b * c)), Math.abs(1 - (b * c) / a)) * 100) / 100f)//find the triple elements with the largest inconsistency
                                {
                                    a = (Math.pow(b, (double) 1 / 3) * Math.pow(c, (double) 1 / 3) * Math.pow(
                                            a, (double) 2 / 3));
                                    b = (Math.pow(b, (double) 2 / 3) * Math.pow(c, (double) -1 / 3) * Math.pow(
                                            a, (double) 1 / 3));
                                    c = (Math.pow(b, (double) -1 / 3) * Math.pow(c, (double) 2 / 3) * Math.pow(
                                            a, (double) 1 / 3));
									/* old incon reduce algprithm
									if((b*c)<a)
									{
										System.out.print("a:"+a+"\n");
									    System.out.print("b:"+b+"\n");
									    System.out.print("c:"+c+"\n");
										double A=(b*c)/((a+b+c)*(a+b+c));
										double B=(a+2*b*c)/(a+b+c);
										double C=b*c-a;
										double m=B*B-4*A*C;
										if(m<0)
										{
											warnWindow("No solution for triad {"+i+j+","+i+k+","+k+j+"}!",400,100);
											System.out.print("error");
											break B;
										}else
										{
										    double x1=(-1*B+Math.sqrt(m))/(2*A);
										    double x2=(-1*B-Math.sqrt(m))/(2*A);
										    System.out.print("x1:"+x1+"\n");
										    System.out.print("x2:"+x2+"\n");
										    if((x1>0)&&(x2<0))
										    {
										    	b=(float) (b+(b*x1)/(a+b+c));
										    	c=(float) (c+(c*x1)/(a+b+c));
										    	a=(float) (a-(a*x1)/(a+b+c));
										    	System.out.print("a':"+a+"\n");
											    System.out.print("b':"+b+"\n");
											    System.out.print("c':"+c+"\n");
										    }else if((x1<0)&&(x2>0))
										    {
										    	b=(float) (b+(b*x2)/(a+b+c));
										    	c=(float) (c+(c*x2)/(a+b+c));
										    	a=(float) (a-(a*x2)/(a+b+c));
										    	System.out.print("a':"+a+"\n");
											    System.out.print("b':"+b+"\n");
											    System.out.print("c':"+c+"\n");
										    }else if((x1>0)&&(x2>0))
										    {
										    	double x=Min((float)x1,(float)x2);
										    	b=(float) (b+(b*x)/(a+b+c));
										    	c=(float) (c+(c*x)/(a+b+c));
										    	a=(float) (a-(a*x)/(a+b+c));
										    }else if((x1<0)&&(x2<0))
										    {
										    	warnWindow("No solution for triad {"+i+j+","+i+k+","+k+j+"}!",400,100);
										    	break B;
										    }
										}
										
									}else if((b*c)>a)
									{
										System.out.print("a:"+a+"\n");
									    System.out.print("b:"+b+"\n");
									    System.out.print("c:"+c+"\n");
										double A=(b*c)/((a+b+c)*(a+b+c));
										double B=-1*(a+2*b*c)/(a+b+c);
										double C=b*c-a;
										double m=B*B-4*A*C;
										if(m<0)
										{
											warnWindow("No solution for triad {"+i+j+","+i+k+","+k+j+"}!",400,100);
											System.out.print("error");
											break B;
										}else
										{
										    double x1=(-1*B+Math.sqrt(m))/(2*A);
										    double x2=(-1*B-Math.sqrt(m))/(2*A);
										    System.out.print("x1:"+x1+"\n");
										    System.out.print("x2:"+x2+"\n");
										    if((x1>0)&&(x2<0))
										    {
										    	b=(float) (b-(b*x1)/(a+b+c));
										    	c=(float) (c-(c*x1)/(a+b+c));
										    	a=(float) (a+(a*x1)/(a+b+c));
										    	System.out.print("a':"+a+"\n");
											    System.out.print("b':"+b+"\n");
											    System.out.print("c':"+c+"\n");
										    }else if((x1<0)&&(x2>0))
										    {
										    	b=(float) (b-(b*x2)/(a+b+c));
										    	c=(float) (c-(c*x2)/(a+b+c));
										    	a=(float) (a+(a*x2)/(a+b+c));
										    	System.out.print("a':"+a+"\n");
											    System.out.print("b':"+b+"\n");
											    System.out.print("c':"+c+"\n");
										    }else if((x1>0)&&(x2>0))
										    {
										    	double x=Min((float)x1,(float)x2);
										    	b=(float) (b-(b*x)/(a+b+c));
										    	c=(float) (c-(c*x)/(a+b+c));
										    	a=(float) (a+(a*x)/(a+b+c));
										    }else if((x1<0)&&(x2<0))
										    {
										    	warnWindow("No solution for triad {"+i+j+","+i+k+","+k+j+"}!",400,100);
										    	break B;
										    }
										}
									}*/

                                }
                                if (a < 0 || b < 0 || c < 0) {
                                    warnWindow("No solution for triad {" + i + j + "," + i + k + "," + k + j + "}!", 400, 100);
                                    break B;
                                } else {
                                    table.setValueAt(String.valueOf(formatNumber(a)), i, j);
                                    table.setValueAt(String.valueOf(formatNumber(b)), i, k);
                                    table.setValueAt(String.valueOf(formatNumber(c)), k, j);
                                    table.setValueAt(formatNumber(1 / Double.parseDouble(String.valueOf(a))), j, i);
                                    table.setValueAt(formatNumber(1 / Double.parseDouble(String.valueOf(b))), k, i);
                                    table.setValueAt(formatNumber(1 / Double.parseDouble(String.valueOf(c))), j, k);
                                }
                            }
                        }
                    }
                }
                computeInconsistency(cellValue);
                showMaxInconsistency(cellValue);
            }
            table.setDefaultRenderer(Object.class, new EvenOddRenderer());
        } else if (e.getSource() == buttonReduceTriadInconsistency)//reduce the inconsistency of one triad by weight-based inconsistency reduction method
        {
            if ((!cellValue[highlightedI][highlightedJ].equals("")) && (!cellValue[highlightedI][highlightedK].equals("")) && (!cellValue[highlightedK][highlightedJ].equals(
                    ""))) {
                double a = unformatNumberString(cellValue[highlightedI][highlightedJ]);//obtain value from the matrix
                double b = unformatNumberString(cellValue[highlightedI][highlightedK]);//obtain value from the matrix
                double c = unformatNumberString(cellValue[highlightedK][highlightedJ]);//obtain value from the matrix

                double tempA;
                double tempB;
                double tempC;

                tempA = (Math.pow(b, (double) 1 / 3) * Math.pow(c, (double) 1 / 3) * Math.pow(a, (double) 2 / 3));
                tempB = (Math.pow(b, (double) 2 / 3) * Math.pow(c, (double) -1 / 3) * Math.pow(a, (double) 1 / 3));
                tempC = (Math.pow(b, (double) -1 / 3) * Math.pow(c, (double) 2 / 3) * Math.pow(a, (double) 1 / 3));
                a = tempA;
                b = tempB;
                c = tempC;
					
					/* old incon reduce algorithm
					if((b*c)<a)
						{
							double A=(b*c)/((a+b+c)*(a+b+c));
							double B=(a+2*b*c)/(a+b+c);
							double C=b*c-a;
							double m=B*B-4*A*C;

							if(m<0)
							{
								System.out.print("error");
								warnWindow("No solution!",400,100);
							}else
							{
							    double x1=(-1*B+Math.sqrt(m))/(2*A);
							    double x2=(-1*B-Math.sqrt(m))/(2*A);

							    if((x1>0)&&(x2<0))
							    {
							    	b=(float) (b+(b*x1)/(a+b+c));
							    	c=(float) (c+(c*x1)/(a+b+c));
							    	a=(float) (a-(a*x1)/(a+b+c));

							    }else if((x1<0)&&(x2>0))
							    {
							    	b=(float) (b+(b*x2)/(a+b+c));
							    	c=(float) (c+(c*x2)/(a+b+c));
							    	a=(float) (a-(a*x2)/(a+b+c));
							    }else if((x1>0)&&(x2>0))
							    {
							    	double x=Min((float)x1,(float)x2);
							    	b=(float) (b+(b*x)/(a+b+c));
							    	c=(float) (c+(c*x)/(a+b+c));
							    	a=(float) (a-(a*x)/(a+b+c));
							    }else if((x1<0)&&(x2<0))
							    {
							    	warnWindow("No solution!",400,100);
							    }
							}
							
						}else if((b*c)>a)
						{
							double A=(b*c)/((a+b+c)*(a+b+c));
							double B=-1*(a+2*b*c)/(a+b+c);
							double C=b*c-a;
							double m=B*B-4*A*C;
							if(m<0)
							{
								warnWindow("No solution!",400,100);
							}else
							{
							    double x1=(-1*B+Math.sqrt(m))/(2*A);
							    double x2=(-1*B-Math.sqrt(m))/(2*A);
							    if((x1>0)&&(x2<0))
							    {
							    	b=(float) (b-(b*x1)/(a+b+c));
							    	c=(float) (c-(c*x1)/(a+b+c));
							    	a=(float) (a+(a*x1)/(a+b+c));
							    }else if((x1<0)&&(x2>0))
							    {
							    	b=(float) (b-(b*x2)/(a+b+c));
							    	c=(float) (c-(c*x2)/(a+b+c));
							    	a=(float) (a+(a*x2)/(a+b+c));
							    }else if((x1>0)&&(x2>0))
							    {
							    	double x=Min((float)x1,(float)x2);
							    	b=(float) (b-(b*x)/(a+b+c));
							    	c=(float) (c-(c*x)/(a+b+c));
							    	a=(float) (a+(a*x)/(a+b+c));
							    }else if((x1<0)&&(x2<0))
							    {
							    	warnWindow("No solution!",400,100);
							    }
							}
							
																
					}*/
                if (a < 0 || b < 0 || c < 0) {
                    warnWindow("No solution!", 400, 100);
                } else {
                    table.setValueAt(formatNumber(a), highlightedI, highlightedJ);
                    table.setValueAt(formatNumber(b), highlightedI, highlightedK);
                    table.setValueAt(formatNumber(c), highlightedK, highlightedJ);

                    //table.setValueAt("1/"+String.valueOf(df.format(a)),highlighted_j,highlighted_i);
                    //table.setValueAt("1/"+String.valueOf(df.format(b)),highlighted_k,highlighted_i);
                    //table.setValueAt("1/"+String.valueOf(df.format(c)),highlighted_j,highlighted_k);

                    table.setValueAt("" + formatNumber(1 / Double.parseDouble(String.valueOf(a))),
                                     highlightedJ,
                                     highlightedI);
                    table.setValueAt("" + formatNumber(1 / Double.parseDouble(String.valueOf(b))),
                                     highlightedK,
                                     highlightedI);
                    table.setValueAt("" + formatNumber(1 / Double.parseDouble(String.valueOf(c))),
                                     highlightedJ, highlightedK);

                }

            }
            computeInconsistency(cellValue);
            showMaxInconsistency(cellValue);
            table.setDefaultRenderer(Object.class, new EvenOddRenderer());
        } else if (e.getSource() == buttonLocateElement)//locate the most inconsistent element in a triad
        {
            double[] incon_aij = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};//store all inconsistency values of triads involving element aij
            double[] incon_aik = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};//store all inconsistency values of triads involving element aik
            double[] incon_akj = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};//store all inconsistency values of triads involving element akj
            int count_aij = 0;//total number of triads involving aij
            int count_aik = 0;//total number of triads involving aik
            int count_akj = 0;//total number of triads involving akj
            //System.out.print(ai+" "+ak+" "+aj+"\n");
            //compute all inconsistency values of triads involving aij, aik or akj
            for (int i = 0; i < 5; i++) {
                for (int k = i + 1; k < 6; k++) {
                    for (int j = k + 1; j < 7; j++) {
                        if ((!cellValue[i][j].equals("")) && (!cellValue[i][k].equals("")) && (!cellValue[k][j].equals(""))) {
                            double a = unformatNumberString(cellValue[i][j]);//obtain value from the matrix
                            double b = unformatNumberString(cellValue[i][k]);//obtain value from the matrix
                            double c = unformatNumberString(cellValue[k][j]);//obtain value from the matrix
                            count_aij = getCount_aij(incon_aij, count_aij, i, k, j, a, b, c, highlightedJ, j == highlightedJ);
                            count_aik = getCount_aij(incon_aik, count_aik, i, k, k, a, b, c, highlightedK, j == highlightedK);
                            if ((i == highlightedK && i == highlightedI) || (i == highlightedK && k == highlightedJ) ||
                                (k == highlightedK && j == highlightedJ)) {
                                //System.out.print(i+","+j+" "+i+","+k+" "+k+","+j+"\n");
                                incon_akj[count_akj] = (int) Math.round(
                                        min(Math.abs(1 - a / (b * c)), Math.abs(1 - (b * c) / a)) * 100) / 100f;
                                count_akj++;
                            }
                        }
                    }
                }
            }
            //sort the inconsistency values
            sort(incon_aij);
            sort(incon_aik);
            sort(incon_akj);
				/*System.out.print(count_aij+" "+count_aik+" "+count_akj+"\n");
				for(int i=0;i<incon_aij.length;i++)
				{
					System.out.print(incon_aij[i]+"\n");
				}
				for(int i=0;i<incon_aik.length;i++)
				{
					System.out.print(incon_aik[i]+"\n");
				}
				for(int i=0;i<incon_akj.length;i++)
				{
					System.out.print(incon_akj[i]+"\n");
				}*/
            //find the elements among aij, aik and akj who involves in another triad having the highest inconsistency
            if (incon_aij[1] >= incon_aik[1] && incon_aij[1] >= incon_akj[1]) {
                highlightedK = 100;//disable ak(allows jc to highlight aij)
            } else if (incon_aik[1] >= incon_aij[1] && incon_aik[1] >= incon_akj[1]) {
                highlightedJ = 100;//disable aj(allows jc to highlight aik)
            } else if (incon_akj[1] >= incon_aik[1] && incon_akj[1] >= incon_aij[1]) {
                highlightedI = 100;//disable ai(allows jc to highlight akj)
            }
        }
        for (int i = 0; i < childNumber; i++) {
            table.setValueAt("1", i, i);//display 1 in the diagnal of the table
        }
    }

    private int getCount_aij(double[] incon_aij, int count_aij, int i, int k, int j, double a, double b, double c, int highlightedJ, boolean b2) {
        if ((i == highlightedI && j == highlightedJ) || (i == highlightedI && k == highlightedJ) || (k ==
                                                                                                     highlightedI && b2)) {
            //System.out.print(i+","+j+" "+i+","+k+" "+k+","+j+"\n");
            incon_aij[count_aij] = (int) Math.round(
                    min(Math.abs(1 - a / (b * c)), Math.abs(1 - (b * c) / a)) * 100) / 100f;
            count_aij++;
        }
        return count_aij;
    }

    private void close() {
        // TODO Auto-generated method stub
        frame.setVisible(false);
        frame.dispose();
        System.exit(0);
    }

    //create a warn window
    public void warnWindow(String warnMSG, int width, int height) {
        final JFrame warn = new JFrame();
        JLabel label = new JLabel(warnMSG);
        JButton ok = new JButton("Ok");
        Panel p1 = new Panel();
        Panel p2 = new Panel();
        p1.add(label);
        p2.add(ok);
        warn.setSize(width, height);
        warn.setLocation(500, 250);
        warn.add(p1, BorderLayout.NORTH);
        warn.add(p2, BorderLayout.SOUTH);
        warn.setVisible(true);
        ok.addActionListener(e -> warn.dispose());
    }

    //table anction listener
    class TableModelListener1 implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String tableValueAt = (String) table.getValueAt(row, column);
            if (row != column) {
                if (!tableValueAt.equals(",,") && !tableValueAt.equals("")) {
                    String[] strs = new String[3];
                    strs = tableValueAt.split(",");
                    cellValue[row][column] = strs[0];//store the input data from the table
                }
            } else {
                cellValue[row][column] = "1";//store the input data from the table
            }
        }
    }
}