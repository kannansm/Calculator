package com.mycom.calculator;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public abstract class AbstractMenu extends JMenuItem
        implements Command {

	protected Mediator mediator;

    //-----
    public AbstractMenu(String title, int keycode, int mask, Mediator mediator, ActionListener al) {
        super(title);
		this.mediator = mediator;
        //setForeground(Color.blue);
		setAccelerator(KeyStroke.getKeyStroke(keycode, mask));
        setActionCommand(title);
        addActionListener(al);
    }
}