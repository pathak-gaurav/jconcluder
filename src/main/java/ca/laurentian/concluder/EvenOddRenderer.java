package ca.laurentian.concluder;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * FOR WHAT THIS ACTUALLY DOES, THIS IS A DISASTER
 * OF AN IMPLEMENTATION
 * IS FAR TOO COMPLEX FOR WHAT IT IS DOING
 *
 * @author tylerjessup
 */
class EvenOddRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color background;

        //highlight an element or a triad

        if (Inconsistency.highlightedK == 100) {
            //only highlight element aij
            if (row == Inconsistency.highlightedI && column == Inconsistency.highlightedJ) {
                background = Color.CYAN;
            } else {
                background = Color.DARK_GRAY;
            }
        } else if (Inconsistency.highlightedJ == 100) {
            //only highlight element aik
            if (row == Inconsistency.highlightedI && column == Inconsistency.highlightedK) {
                background = Color.CYAN;
            } else {
                background = Color.DARK_GRAY;
            }
        } else if (Inconsistency.highlightedI == 100) {
            //only highlight element akj
            if (row == Inconsistency.highlightedK && column == Inconsistency.highlightedJ) {
                background = Color.CYAN;
            } else {
                background = Color.DARK_GRAY;
            }
        } else//highlight a triad with 3 elements(aij,aik,akj)
        {
            if ((row == Inconsistency.highlightedI && column == Inconsistency.highlightedJ) || (row == Inconsistency.highlightedI && column == Inconsistency.highlightedK) || (row == Inconsistency.highlightedK && column == Inconsistency.highlightedJ)) {
                background = Color.CYAN;
            } else {
                background = Color.DARK_GRAY;
            }
        }
        renderer.setBackground(background);
        return renderer;
    }
}
