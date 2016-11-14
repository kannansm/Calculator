package com.mycom.calculator;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.logging.Logger;

public class InputChangeButton extends AbstractButton {
	private Logger log = Logger.getLogger(getClass().getName());
	
    //-----
    public InputChangeButton(String tooltip, String title, Mediator md, ActionListener al, MouseListener ml) {
        super(title, md, al, ml);
        setForeground(Color.red);
        setToolTipText(tooltip);
    }

    //-----
    public void execute() {
        log.info("InputChangeButton.execute() - " + getActionCommand());
        mediator.executeCommand(getActionCommand());
    }
}