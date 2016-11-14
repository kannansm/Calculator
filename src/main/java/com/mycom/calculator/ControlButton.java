package com.mycom.calculator;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.logging.Logger;

public class ControlButton extends AbstractButton {
	private Logger log = Logger.getLogger(getClass().getName());
	
    //-----
    public ControlButton(String tooltip, String title, Mediator md, ActionListener al, MouseListener ml) {
        super(title, md, al, ml);
        setToolTipText(tooltip);
    }

    //-----
    public void execute() {
        mediator.calculateResult(getActionCommand());
        log.info("ControlButton.Execute(): " + getActionCommand());
    }
}