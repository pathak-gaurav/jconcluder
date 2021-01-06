package ca.laurentian.concluder.treemap;


import ca.laurentian.concluder.refactorState.HierarchyVisualization;
import prefuse.data.Graph;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Treemap extends Frame {
    MapModel map;
    MapLayout algorithm;
    Graph gp;
    HierarchyVisualization hierarchyVisualization;

    public Treemap(int length, Graph graph, HierarchyVisualization hv) {
        int w = 720;
        int h = 720;
        gp = graph;
        hierarchyVisualization = hv;
        //Set the division of Tree Map
        map = new RandomMap(length);
        Mappable[] items = map.getItems();

        algorithm = new BinaryTreeLayout();
        algorithm.layout(map, new Rect(0, 0, w, h));

        setBounds(100, 100, w, h);
        setVisible(true);

       // Watch for the user closing the window so we can exit gracefully
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public void paint(Graphics g) {
        Mappable[] items = map.getItems();
        Rect rect;
        Graphics2D g2d = (Graphics2D) g;
        for(int i=0;i<gp.getNodeCount();i++){
            rect = items[i].getBounds();
            int a = (int) rect.x;
            int b = (int) rect.y;
            int c = (int) (rect.x + rect.w) - a;
            int d = (int) (rect.y + rect.h) - b;
            g2d.fillRect(a, b, c, d);
            g2d.drawRect(a, b, c, d);
            g2d.setColor(new Color(112+i*4,11+i*8,22+i*12));

            //System.out.println(String.valueOf(i+1));
            //g2d.drawString(String.valueOf(i+1), (a+10), (b-5));
            //g2d.drawString(String.valueOf(i+1), a+1, b);
            g2d.setFont(new Font("Comic Sans",Font.BOLD,15));
        }
    }

}
