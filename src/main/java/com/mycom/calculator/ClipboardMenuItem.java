package com.mycom.calculator;

import java.awt.event.ActionListener;

public class ClipboardMenuItem extends AbstractMenuItem {

    //-----
    public ClipboardMenuItem(String tooltip, String title, int keycode, int mask, Mediator mediator, ActionListener al) {
        super(title, keycode, mask, mediator, al);
        setToolTipText(tooltip);
    }

    //-----
    public void execute() {
		String command = getActionCommand();
        System.out.println("ClipboardMenuItem.execute() - " + getActionCommand());
		if (command.equals("Copy")) {
			mediator.copyContentsToClipboard();
		} else if (command.equals("Paste")) {
			mediator.pasteFromClipboard();
		}
    }		
}