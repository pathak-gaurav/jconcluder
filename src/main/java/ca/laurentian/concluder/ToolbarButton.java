package ca.laurentian.concluder;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToolbarButton extends JButton {
    private static final long serialVersionUID = 1L;

    public ToolbarButton(String text, ImageIcon icon, String tooltip) {
        setText(text);

        //new
        Image img = icon.getImage();
        Image newImage = img.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        icon = new ImageIcon(newImage);

        setIcon(icon);
        setToolTipText(tooltip);

        setVerticalTextPosition(BOTTOM);
        setHorizontalTextPosition(CENTER);
        setBorderPainted(false);
        addMouseListener(new MouseEventButton());
    }

    public class MouseEventButton extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton) e.getComponent();
            button.setBorderPainted(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton button = (JButton) e.getComponent();
            button.setBorderPainted(false);
        }
    }
}