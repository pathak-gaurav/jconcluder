package ca.laurentian.concluder;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * FOR WHAT THIS ACTUALLY DOES, THIS IS A DISASTER
 * OF AN IMPLEMENTATION
 * IS FAR TOO COMPLEX FOR WHAT IT IS DOING
 *
 * @author tylerjessup
 */
class EvenOddRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color background;

        //highlight an element or a triad

        if (inconsistency.highlighted_k == 100) {
            //only highlight element aij
            if (row == inconsistency.highlighted_i && column == inconsistency.highlighted_j) {
                background = Color.CYAN;
            } else {
                background = Color.DARK_GRAY;
            }
        } else if (inconsistency.highlighted_j == 100) {
            //only highlight element aik
            if (row == inconsistency.highlighted_i && column == inconsistency.highlighted_k) {
                background = Color.CYAN;
            } else {
                background = Color.DARK_GRAY;
            }
        } else if (inconsistency.highlighted_i == 100) {
            //only highlight element akj
            if (row == inconsistency.highlighted_k && column == inconsistency.highlighted_j) {
                background = Color.CYAN;
            } else {
                background = Color.DARK_GRAY;
            }
        } else//highlight a triad with 3 elements(aij,aik,akj)
        {
            if ((row == inconsistency.highlighted_i && column == inconsistency.highlighted_j) || (row == inconsistency.highlighted_i && column == inconsistency.highlighted_k) || (row == inconsistency.highlighted_k && column == inconsistency.highlighted_j)) {
                background = Color.CYAN;
            } else {
                background = Color.DARK_GRAY;
            }
        }
        renderer.setBackground(background);
        return renderer;
    }
}
