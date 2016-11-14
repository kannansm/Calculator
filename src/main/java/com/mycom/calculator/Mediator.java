package com.mycom.calculator;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Mediator {
	private JTextField txtInput;
	private JDialog aboutDialog;
	private ImageIcon icon;

	private boolean isNew = true;
	private boolean hasDot = false;
	private NumberFormat integerFormat = new DecimalFormat("###0"); // NumberFormat.getIntegerInstance();
	private NumberFormat decimalFormat = new DecimalFormat("###0.##########"); // NumberFormat.getNumberInstance();
	private NumberFormat currentFormat;

	private String operationCode = "";
	private double result = 0.0;
	private double memoryValue = 0.0;
	private Clipboard clipboard = Toolkit.getDefaultToolkit()
			.getSystemClipboard();
	private Logger log = Logger.getLogger(getClass().getName());

	public Mediator(JTextField txtInput) {
		this.txtInput = txtInput;
		URL url = getClass().getResource("/images/calculator.gif");
		icon = new ImageIcon(url);
		currentFormat = integerFormat;
		if (integerFormat instanceof DecimalFormat) {
			log.info("Integer Format: "
					+ ((DecimalFormat) integerFormat).toPattern());
		}
		if (decimalFormat instanceof DecimalFormat) {
			log.info("Decimal Format: "
					+ ((DecimalFormat) decimalFormat).toPattern());
		}
	}

	public void executeCommand(String command) {

		if (command.length() == 1) {
			char keyCharacter = command.charAt(0);
			log.info("Mediator.executeCommand() ==> " + keyCharacter);
			if (keyCharacter >= '0' && keyCharacter <= '9') {
				((Calculator4MBean) txtInput.getTopLevelAncestor())
						.playAudio(command + ".wav");

				if (isNew)
					txtInput.setText(command);
				// else if (hasDot == true &&
				// Double.parseDouble(txtInput.getText()) == 0)
				// txtInput.setText(command);
				else if (operationCode.equals("=")
						&& Double.parseDouble(txtInput.getText()) == 0)
					txtInput.setText(command);
				else
					txtInput.setText(txtInput.getText() + command);
				isNew = false;
				return;
			}

			switch (keyCharacter) {
			case '+':
			case '-':
			case '/':
			case '*': {
				calculateResult(command);
				break;
			}
			case '.': {
				addDot();
				break;
			}
			case '%': {
				calculatePercentage();
				break;
			}
			case '=': {
				completeOperation();
				break;
			}
			case 'C': {
				break;
			}
			default: {
			}
			}
		} else {
			if (command.equals("+/-")) {
				changeSign();
			} else if (command.equals("1/x")) {
				currentFormat = decimalFormat;
				calculateInverse();
			} else if (command.equals("sqrt")) {
				currentFormat = decimalFormat;
				calculateSquareRoot();
			} else if (command.indexOf("M") != -1) {
				memoryAction(command);
			} else if (command.trim().equals("Backspace")) {
				backspaceOperation();
			} else if (command.trim().equals("C")) {
				currentFormat = integerFormat;
				clearCurrentValue();
			} else if (command.trim().equals("CE")) {
				currentFormat = integerFormat;
				clearAllValues();
			}
		}
	}

	public void calculateResult(String command) {
		// Checking whether the user has entered a value
		if (operationCode.equals("/")
				&& Double.parseDouble(txtInput.getText()) == 0) {
			showErrorMessage("Error: Division by zero.", "Arithmetic Error..");
		} else {
			double d = Double.parseDouble(txtInput.getText());
			double oldResult = result;

			if (operationCode.equals("+"))
				result += d;
			else if (operationCode.equals("-"))
				result -= d;
			else if (operationCode.equals("*"))
				result *= d;
			else if (operationCode.equals("/"))
				result /= d;
			else
				// if (operationCode.equals(""))
				result = d;

			log.info("Mediator.calculateResult() ==> " + result);

			if (txtInput.getText().indexOf(".") != -1) {
				hasDot = true;
				currentFormat = decimalFormat;
			}
			if (oldResult % d != 0.0) {
				System.out
						.println("Mediator.calculateResult() ==> (oldResult % d != 0.0) is "
								+ (oldResult % d != 0.0));
				hasDot = true;
				currentFormat = decimalFormat;
			}
			log.info("-------------------------");

			txtInput.setText(currentFormat.format(result));
			if (operationCode.equals("=")) {
				txtInput.setText("0");
			}
			operationCode = command;
			isNew = true;
		}
	}

	public void calculatePercentage() {
		// t1.beep();
		// Checking whether the user has entered a value
		if (Double.parseDouble(txtInput.getText()) == 0) {
			showErrorMessage("Error: Division by zero.", "Arithmetic Error..");
		} else {
			// lblMessage.setText("Percent Calculation Success");
			result = (result / Double.parseDouble(txtInput.getText())) * 100;
			log.info("Mediator.calculatePercentage() ==> " + result);
			log.info("-------------------------");
			txtInput.setText(String.valueOf(result));

			operationCode = "";
			isNew = true;
			// hasDot = false;
		}
	}

	public void completeOperation() {
		((Calculator4MBean) txtInput.getTopLevelAncestor()).playAudio("enter.au");
		double d = Double.parseDouble(txtInput.getText());
		double oldResult = result;

		// Checking whether the user has entered a value
		if (operationCode.equals("+"))
			result += d;
		else if (operationCode.equals("-"))
			result -= d;
		else if (operationCode.equals("*"))
			result *= d;
		else if (operationCode.equals("/"))
			result /= d;

		log.info("Mediator.completeOperation() ==> " + result);
		if (oldResult % d != 0.0) {
			log
					.info("Mediator.completeOperation() ==> (oldResult % d != 0.0) is "
							+ (oldResult % d != 0.0));
			hasDot = true;
			currentFormat = decimalFormat;
		}
		log.info("-------------------------");

		if (operationCode.equals("")) {
			txtInput.setText(txtInput.getText());
		} else {
			// txtInput.setText(String.valueOf(result));
			txtInput.setText(currentFormat.format(result));
			operationCode = "=";
			isNew = true;
			// hasDot = false;
		}
	}

	public void addDot() {
		if (isNew) {
			if (operationCode.equals("+"))
				txtInput.setText("0");
			if (operationCode.equals("-"))
				txtInput.setText("0");
			if (operationCode.equals("*"))
				txtInput.setText("0");
			if (operationCode.equals("/"))
				txtInput.setText("0");
		}

		if (txtInput.getText().indexOf(".") == -1) {
			txtInput.setText(txtInput.getText() + '.');
			hasDot = true;
			currentFormat = decimalFormat;
		}
		isNew = false;
		log.info("Mediator.addDot() ==> " + txtInput.getText());
		log.info("-------------------------");
	}

	public void changeSign() {
		// Checking whether the user has entered a value
		if ((Double.parseDouble(txtInput.getText())) == 0.0 && result == 0.0) {
			txtInput.setText("0");
		} else {
			double tempResult = Double.parseDouble(txtInput.getText()) * -1;
			log.info("Mediator.changeSign() ==> " + tempResult);
			log.info("-------------------------");
			txtInput.setText(currentFormat.format(tempResult));
		}
	}

	public void calculateInverse() {
		if (Double.parseDouble(txtInput.getText()) == 0.0) {
			showErrorMessage("Error: Division by zero.", "Arithmetic Error..");
		} else {
			double invResult = 1 / Double.parseDouble(txtInput.getText());
			txtInput.setText(currentFormat.format(invResult));
			log.info("Mediator.calculateInverse() ==> " + invResult);
			log.info("-------------------------");
			isNew = true;
			// hasDot = false;
		}
	}

	public void calculateSquareRoot() {
		if (Double.parseDouble(txtInput.getText()) == 0) {
		} else {
			if (Double.parseDouble(txtInput.getText()) < 0) {
				showErrorMessage("Square root of Negative Number is \n"
						+ "is Not a Number(NaN)", "Error..");
				return;
			}
			double sqResult = Math.sqrt(Double.parseDouble(txtInput.getText()));
			txtInput.setText(currentFormat.format(sqResult));
			isNew = true;
		}
	}

	public void memoryAction(String command) {
		// Memory Clear Button - Clears the contents of Memory
		if (command.equals("MC")) {
			// t1.beep();
			((Calculator4) txtInput.getTopLevelAncestor())
					.setMemoryButtonText("    ");
			memoryValue = 0.0;
		}

		// Memory Recall Button - Recalls the contents of Memory
		if (command.equals("MR")) {
			txtInput.setText(String.valueOf(memoryValue));
			isNew = true;
		}

		// Memory Set Button - Sets the value of Memory
		if (command.equals("MS")) {
			((Calculator4) txtInput.getTopLevelAncestor())
					.setMemoryButtonText("M");
			memoryValue = Double.parseDouble(txtInput.getText());
			isNew = true;
		}
		// Memory Plus Button - Adds to the contents of Memory
		if (command.equals("M+")) {
			((Calculator4) txtInput.getTopLevelAncestor())
					.setMemoryButtonText("M");
			memoryValue += Double.parseDouble(txtInput.getText());
		}
		log.info("Mediator.memoryAction() ==> " + memoryValue);
		log.info("-------------------------");
	}

	public void clearCurrentValue() {
		isNew = true;
		txtInput.setText("0");
	}

	public void clearAllValues() {
		isNew = true;
		txtInput.setText("0");
		result = 0.0f;
		operationCode = "";
		log.info("Mediator.clearAllValues() ==> " + result);
		log.info("-------------------------");
	}

	public void backspaceOperation() {
		String bs = txtInput.getText();
		// StringBuffer sb = new StringBuffer(bs);
		int i = bs.length();
		if (i == 1) {
			txtInput.setText("0");
			isNew = true;
			currentFormat = integerFormat;
		} else {
			// txtInput.setText(sb.deleteCharAt(sb.length()-1));
			txtInput.setText(bs.substring(0, bs.length() - 1));
		}
		if (txtInput.getText().indexOf(".") == -1) {
			hasDot = false;
			currentFormat = integerFormat;
		}
	}

	public void showErrorMessage(Object message, String title) {
		Container container = txtInput.getTopLevelAncestor();
		log.info("Container ==> " + container);
		JOptionPane.showMessageDialog(container, message, title,
				JOptionPane.ERROR_MESSAGE);
		// lblMessage.setText("Invalid Parameter");
		// txtInput.setText("Infinity");
	}

	public void openAboutDialog() {
		JFrame owner = null;
		if (txtInput.getTopLevelAncestor() instanceof JFrame) {
			owner = (JFrame) txtInput.getTopLevelAncestor();
		}
		aboutDialog = new AboutDialog(owner, "About Calculator", true, icon);
		Point location = txtInput.getLocationOnScreen();
		Dimension size = txtInput.getSize();
		int x = (location.x)// - popupCalendar.getPreferredSize().width)
				+ size.width / 2;
		int y = location.y + size.height;
		aboutDialog.setLocation(x, y);
		aboutDialog.setVisible(true);
		log.info("Mediator.openAboutDialog(): " + owner);
		log.info("-------------------------");
	}

	public void openHelpPane() {
		String urlString = "";
		Calculator4 calculator = (Calculator4) txtInput.getTopLevelAncestor();

		try {
			if (calculator.getIsApplet()) {
				urlString = calculator.getCodeBase() + "user_guide.html";
				URL urlobj = new URL(urlString);

				calculator.getAppletContext().showDocument(urlobj, "_blank");
				log.info("Help Doc from Applet shown successfully..");
				calculator.showStatus(urlString
						+ ", Help Doc opened from Applet successfully!!");
			} else {
				HelpPane hpane = new HelpPane();
				log.info("Help Pane created successfully..");
				Point location = txtInput.getLocationOnScreen();
				Dimension size = txtInput.getSize();
				int x = (location.x)// - popupCalendar.getPreferredSize().width)
						+ size.width / 2;
				int y = location.y + size.height;

				hpane.setLocation(x, y);
				hpane.setVisible(true);
			}
		} catch (Exception eH) {
			log.warning("Error in executing help menu..." + eH);
		}
		log.info("Mediator.openHelpPane()...");
		log.info("-------------------------");
	}

	public void copyContentsToClipboard() {
		String selection = txtInput.getText();
		StringSelection data = new StringSelection(selection);
		clipboard.setContents(data, data);
		log.info("Mediator.copyContentsToClipboard(): " + selection);
		log.info("-------------------------");
	}

	public void pasteFromClipboard() {
		Transferable clipData = clipboard.getContents(clipboard);
		String s = null;
		if (clipData != null) {
			try {
				s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
				double d = Double.parseDouble(s);
				txtInput.setText(s);
			} catch (UnsupportedFlavorException ee) {
				log.warning("Unsupported flavor: " + ee);
			} catch (IOException ee) {
				log.warning("Unable to get data: " + ee);
			} catch (NumberFormatException nfe) {
				log.warning("Error: Invalid input = " + nfe);
				showErrorMessage("Invalid input : " + nfe.getMessage(),
						"Invalid Input Error..");
			}
		}
		log.info("Mediator.pasteFromClipboard(): " + s);
		log.info("-------------------------");
	}

	public void exit() {
		Object[] options = { "OK", "Cancel" };
		((Calculator4MBean) txtInput.getTopLevelAncestor()).playAudio("exit.wav");

		int opt = JOptionPane.showOptionDialog(txtInput.getTopLevelAncestor(),
				"Are you sure you want to quit?", "Quitting..",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[1]);
		if (opt == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
}