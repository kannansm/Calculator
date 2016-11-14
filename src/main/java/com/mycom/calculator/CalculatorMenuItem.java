package com.mycom.calculator;

import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class CalculatorMenuItem extends AbstractMenuItem {
	private Logger log = Logger.getLogger(getClass().getName());
	
    //-----
    public CalculatorMenuItem(String tooltip, String title, int keycode, int mask, Mediator mediator, ActionListener al) {
        super(title, keycode, mask, mediator, al);
        setToolTipText(tooltip);
    }

    //-----
    public void execute() {
		String command = getActionCommand();
        log.info("CalculatorMenuItem.execute() - " + getActionCommand());
		if (command.equals("Exit")) {
			mediator.exit();
		} else if (command.equals("About Calculator")) {
			mediator.openAboutDialog();
		} else if (command.equals("User Guide")) {
			mediator.openHelpPane();
		} else if (command.equals("Start Calc")) {
			runCalculator();
		}
    }	

	private void runCalculator() {
		Runtime r = Runtime.getRuntime();
		Process p = null;
		try {
			p = r.exec("Calc");
			//p.waitFor();
		} catch (Exception e) {
			mediator.showErrorMessage("Error in executing Calc.exe.", "Error");
		}
	}	
}