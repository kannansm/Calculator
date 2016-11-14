package com.mycom.calculator;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public abstract class AbstractMenuItem extends JMenuItem
        implements Command {

	protected Mediator mediator;

    //-----
    public AbstractMenuItem(String title, int keycode, int mask, Mediator mediator, ActionListener al) {
        super(title);
		this.mediator = mediator;
        //setForeground(Color.blue);
		if (mask == -1) {
			setMnemonic(keycode);
		} else {
			setAccelerator(KeyStroke.getKeyStroke(keycode, mask));
		}
        setActionCommand(title);
        addActionListener(al);
    }
}