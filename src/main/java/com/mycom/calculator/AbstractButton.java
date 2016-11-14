package com.mycom.calculator;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.logging.Logger;

public abstract class AbstractButton extends JButton
        implements Command {
    private Border bRaised = BorderFactory.createRaisedBevelBorder();

    protected Mediator mediator;    
	protected Logger log = Logger.getLogger(getClass().getName());

    //-----
    public AbstractButton(String title, Mediator mediator, ActionListener al, MouseListener ml) {
        super(title);
        this.mediator = mediator;
        setForeground(Color.blue);
        setBorder(bRaised);
        setActionCommand(title);
        addActionListener(al);
        addMouseListener(ml);
    }
}