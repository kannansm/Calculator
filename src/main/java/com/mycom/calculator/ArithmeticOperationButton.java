package com.mycom.calculator;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class ArithmeticOperationButton extends AbstractButton {
    //-----
    public ArithmeticOperationButton(String title, Mediator md, ActionListener al, MouseListener ml) {
        super(title, md, al, ml);
        setToolTipText(title);
    }

    //-----
    public void execute() {
        log.info("ArithmeticOperationButton.execute() - " + getActionCommand());
        mediator.executeCommand(getActionCommand());
    }
}