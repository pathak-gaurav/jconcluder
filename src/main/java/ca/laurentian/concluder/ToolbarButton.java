package ca.laurentian.concluder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ToolbarButton extends JButton {
    private static final long serialVersionUID = 1L;

    public ToolbarButton(String text, ImageIcon icon, String tooltip) {
        setText(text);

        //new
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);

        setIcon(icon);
        setToolTipText(tooltip);

        setVerticalTextPosition(AbstractButton.BOTTOM);
        setHorizontalTextPosition(AbstractButton.CENTER);
        setBorderPainted(false);
        addMouseListener(new MouseEventButton());
    }

    public class MouseEventButton implements MouseListener {
        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton) e.getComponent();
            button.setBorderPainted(true);
        }

        public void mouseExited(MouseEvent e) {
            JButton button = (JButton) e.getComponent();
            button.setBorderPainted(false);
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }
}