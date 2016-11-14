package com.mycom.calculator;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

/**
 * Panel whose background can be set to any image.
 *
 * @author Kannan S
 * @version 4.0 04/09/2003
 */

public class BackgroundPanel extends JPanel
{
	private ImageIcon bgIcon;

	public BackgroundPanel() {
		super();
	}

	protected void paintComponent(Graphics g) {
		if (bgIcon == null) {
			super.paintComponent(g);
		} else {
			bgIcon = new ImageIcon(bgIcon.getImage().getScaledInstance(this.getSize().width,
				this.getSize().height, Image.SCALE_DEFAULT));
			g.drawImage(bgIcon.getImage(), 0, 0, this);
		}
	}

	public ImageIcon getBackgroundImage() {
        return bgIcon;
    }

    public void setBackgroundImage(ImageIcon icon) {
        bgIcon = icon;
		repaint();
    }
}
