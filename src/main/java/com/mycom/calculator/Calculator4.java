/**
 * started on 11 Nov 2000
 * @version 1.4 - 15 Aug 2003
 * @author Kannan S.
 *
 * Added Borders to buttons - 19/01/2001
 * Added Memory buttons & JOptionPanes - 26/01/2001
 * Added Popupmenu to change look & feel - 15/02/2001
 * Added Keyevents to key-in data - 19/02/2001
 * Added Illumination of keys - 22/02/2001
 * Added Audioclip & audio files - 04/04/2001
 * Added Help file - 10/04/2001
 * Redesigned totally - 04/09/2003 - Using Command, Mediator and Action patterns
 */
package com.mycom.calculator;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

// import calculator.HelpPane;
// import calculator.AboutDialog;

public class Calculator4 extends JApplet implements ActionListener,
		MouseListener, KeyListener, Runnable, Calculator4MBean {
	// Mediator class
	private Mediator mediator;

	// PLAF variables
	static String metalPLAF = "javax.swing.plaf.metal.MetalLookAndFeel";
	static String winPLAF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	static String motifPLAF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	static String fileSeparator = File.separator;

	// Variables for Panel, Labels, Textfield(DataEntry Control) and Buttons
	JLabel lblDate;
	JTextField txtInput;
	JButton bDummy, bBackspace, bClear, bClearAll;
	JMenuBar menuBar;
	JPopupMenu popup;

	// Variables for the borders
	Border bLowered = BorderFactory.createLoweredBevelBorder();
	Border bRaised = BorderFactory.createRaisedBevelBorder();

	private Color buttonBackground;

	private Action metalAction, winAction, motifAction, playAction, stopAction;
	private Action setBGImageAction, removeBGImageAction;
	private JFileChooser fileChooser;
	private HelpPane hpane;
	private BackgroundPanel mainpanel;
	private JDialog aboutDialog;
	private Frame topContainer;
	private ImageIcon icon;
	private Thread clockThread;
	private boolean isApplet = true;
	private SimpleDateFormat sdf = new SimpleDateFormat(
			"dd MMM yyyy  hh:mm:ss a");

	private HashMap buttonsMap = new HashMap(27);
	private HashMap audioClipMap = new HashMap(13);
	private String soundFiles[] = { "welcome.wav", "enter.au", "passport.mid",
			"0.wav", "1.wav", "2.wav", "3.wav", "4.wav", "5.wav", "6.wav",
			"7.wav", "8.wav", "9.wav", "exit.wav" };
	private static String buttonCommands[] = { "MC", "7", "8", "9", "/",
			"sqrt", "MR", "4", "5", "6", "*", "%", "MS", "1", "2", "3", "-",
			"1/x", "M+", "0", "+/-", ".", "+", "=" };
	private Logger log = Logger.getLogger(getClass().getName());

	// START OF start() >>>>>>>>
	public void start() // Method of Applet
	{
		if (clockThread == null) { // Checks if the Thread is created
			clockThread = new Thread(this);
			clockThread.start(); // Invokes start method of Thread
		}
	}

	// START OF TIME THREAD() >>>>>>>>
	public void run() {
		while (clockThread != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					lblDate.setText(sdf.format(new Date()));
				}
			});
			try {
				clockThread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	// END OF TIME THREAD >>>>>>>>>

	// START OF INIT() >>>>>>>>
	public void init() {
		// Initializing the Layout variables
		GridLayout gl = new GridLayout(4, 6, 2, 2);
		GridLayout gl2 = new GridLayout(1, 4, 2, 2);
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		BorderLayout b1 = new BorderLayout();

		icon = new ImageIcon(getClass().getResource("/images/calculator.gif"));

		JPanel topPanel, centerPanel, arithmeticButtonsPanel;
		JButton tempButton;

		// Initializing Panels
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout()); // gbl);
		topPanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
		topPanel.setOpaque(false);

		centerPanel = new JPanel();
		//centerPanel.setLayout(gl2);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		centerPanel.setOpaque(false);

		arithmeticButtonsPanel = new JPanel();
		arithmeticButtonsPanel.setLayout(gl);
		arithmeticButtonsPanel.setBorder(BorderFactory.createEmptyBorder(1, 1,
				1, 1));
		arithmeticButtonsPanel.setOpaque(false);

		mainpanel = new BackgroundPanel();// JPanel();
		mainpanel.setLayout(b1);
		mainpanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		mainpanel.add("North", topPanel);
		mainpanel.add("Center", centerPanel);
		mainpanel.add("South", arithmeticButtonsPanel);
		getContentPane().add(mainpanel);

		// Initializing Label controls, DataEntry Control(TextField) and Buttons
		lblDate = new JLabel("  ");
		lblDate.setToolTipText("System Date/Time Display");
		lblDate.setHorizontalAlignment(SwingConstants.CENTER);

		txtInput = new JTextField(22);
		txtInput.setEditable(false);
		txtInput.setForeground(Color.blue);
		txtInput.setBackground(Color.white);
		txtInput.setBorder(BorderFactory.createLoweredBevelBorder());
		txtInput.setHorizontalAlignment(JTextField.RIGHT);
		txtInput.setFont(new Font("Arial", Font.BOLD, 12));

		// Making the textfield drag enabled
		txtInput.setText("0");

		// Code for making Text field DND capable - for JRE 1.4+
		txtInput.setDragEnabled(true); // Drag enabling
		// Drop enabling
		/*
		 * txtInput.setTransferHandler(new TransferHandler("text"));12345679
		 * MouseListener ml = new MouseAdapter() { public void
		 * mousePressed(MouseEvent e) { JComponent c =
		 * (JComponent)e.getSource(); TransferHandler th =
		 * c.getTransferHandler(); th.exportAsDrag(c, e, TransferHandler.COPY); } };
		 * txtInput.addMouseListener(ml);
		 */

		mediator = new Mediator(txtInput);

		// Create MENU BAR, menus and Popup menus
		menuBar = new JMenuBar();
		createActions();
		createMenus();
		setJMenuBar(menuBar);

		createPopupMenus();
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				// popup the menu at the location where the mouse is released
				if (me.isPopupTrigger()) {
					popup.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		});

		bDummy = new JButton("    ");
		bDummy.setForeground(Color.blue);
		bDummy.setToolTipText("Memory Content Indicator");
		bDummy.setBorder(bLowered);
		bDummy.setPreferredSize(new Dimension(50, 25));

		bBackspace = new InputChangeButton("Backspace Button", " Backspace ",
				mediator, this, this);
		bClear = new InputChangeButton("Clear Field", "  C  ", mediator, this,
				this);
		bClearAll = new InputChangeButton("Clear All", " CE ", mediator, this,
				this);

		bBackspace.setPreferredSize(new Dimension(110, 25));
		bClear.setPreferredSize(new Dimension(55, 25));
		bClearAll.setPreferredSize(new Dimension(55, 25));

		// Adding buttons to Text field and date label to Top Panel
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 1;
		gbc.gridy = 1;
		// gbl.setConstraints(lblDate, gbc);
		topPanel.add(lblDate, BorderLayout.NORTH);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 1;
		gbc.gridy = 2;
		// gbl.setConstraints(txtInput, gbc);
		topPanel.add(txtInput, BorderLayout.SOUTH);

		// Adding Backspace, Clear, Clear All buttons to Center Panel
		centerPanel.add(bDummy);
		centerPanel.add(bBackspace);
		centerPanel.add(bClearAll);
		centerPanel.add(bClear);

		// Adding buttons to Arithmetic Buttons Panel
		for (int i = 0; i < buttonCommands.length; i++) {
			tempButton = new ArithmeticOperationButton(buttonCommands[i],
					mediator, this, this);
			arithmeticButtonsPanel.add(tempButton);
			tempButton.addKeyListener(this);
			buttonsMap.put(buttonCommands[i], tempButton);
			if (i == 0) {
				tempButton.setPreferredSize(new Dimension(35, 28));
			}
			if (buttonCommands[i].equals("=")) {
				getRootPane().setDefaultButton(tempButton);
			}
		}

		// Registering listeners
		lblDate.addMouseListener(this);

		// Initializing AudioClipMap
		initializeAudioClipMap();

		UIManager.LookAndFeelInfo[] installed = UIManager
				.getInstalledLookAndFeels();
		for (int i = 0; i < installed.length; i++) {
			log.info("Installed LAF: " + installed[i].getClassName());
		}
		log.info("The current look and feel is "
				+ UIManager.getLookAndFeel().getName());
		
		// Register as MBean
		registerMBean();
	}

	private void initializeAudioClipMap() {
		try {
			ClassLoader loader = getClass().getClassLoader();
			URL fileUrl = null;

			for (int i = 0; i < soundFiles.length; i++) {
				String name = "/audio/" + soundFiles[i];
				fileUrl = getClass().getResource(name);
				AudioClip clip = null;
				log.info("file Url (" + i + ") - " + soundFiles[i] + ": "
						+ fileUrl);
				if (fileUrl != null) {
					clip = Applet.newAudioClip(fileUrl);
				}
				log.info("audio clip: " + clip);
				audioClipMap.put(soundFiles[i], clip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Audio Clip Map contents: " + audioClipMap);
	}

	// END OF INIT() >>>>>>>>>
	public Component getPopupmenu() {
		return popup;
	}

	public boolean getIsApplet() {
		return isApplet;
	}

	public void setIsApplet(boolean isApplet) {
		this.isApplet = isApplet;
	}

	private void setTopContainer(JFrame frame) {
		this.topContainer = frame;
	}

	public Frame getTopContainer() {
		return topContainer;
	}

	public void setMemoryButtonText(String text) {
		bDummy.setText(text);
	}

	private void createMenus() {
		JMenu editMenu, optionsMenu, lookMenu, soundMenu, helpMenu;
		JMenuItem copyMenuItem, pasteMenuItem, exitMenuItem, winCalcItem, metalMenuItem, winMenuItem, motifMenuItem;
		JMenuItem setBGImageMenuItem, removeBGImageMenuItem;
		JMenuItem helpMenuItem, aboutMenuItem, playMenuItem, stopMenuItem;

		// a group of custom JMenuItems
		copyMenuItem = new ClipboardMenuItem("Copy", "Copy", KeyEvent.VK_C,
				ActionEvent.CTRL_MASK, mediator, this);
		pasteMenuItem = new ClipboardMenuItem("Paste", "Paste", KeyEvent.VK_V,
				ActionEvent.CTRL_MASK, mediator, this);
		winCalcItem = new CalculatorMenuItem("Run Calc", "Start Calc",
				KeyEvent.VK_R, -1, mediator, this);
		exitMenuItem = new CalculatorMenuItem("Exit", "Exit", KeyEvent.VK_X,
				ActionEvent.CTRL_MASK, mediator, this);
		helpMenuItem = new CalculatorMenuItem("User Guide", "User Guide",
				KeyEvent.VK_U, -1, mediator, this);
		aboutMenuItem = new CalculatorMenuItem("About Calculator",
				"About Calculator", KeyEvent.VK_A, -1, mediator, this);
		// winCalcItem.getAccessibleContext().setAccessibleDescription("This
		// shows VB Windows Calculator.");

		// Group of JMenuItems
		metalMenuItem = new JMenuItem("Metal(Java) Look", KeyEvent.VK_J);
		metalMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));

		winMenuItem = new JMenuItem("Windows(Native) Look", KeyEvent.VK_W);
		winMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,
				ActionEvent.ALT_MASK));

		motifMenuItem = new JMenuItem("Motif(Unix) Look", KeyEvent.VK_M);
		motifMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
				ActionEvent.ALT_MASK));

		setBGImageMenuItem = new JMenuItem("Set Background", KeyEvent.VK_T);
		setBGImageMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				ActionEvent.CTRL_MASK));
		removeBGImageMenuItem = new JMenuItem("Remove Background",
				KeyEvent.VK_R);
		removeBGImageMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, ActionEvent.CTRL_MASK));

		metalMenuItem.setAction(metalAction);
		motifMenuItem.setAction(motifAction);
		winMenuItem.setAction(winAction);
		setBGImageMenuItem.setAction(setBGImageAction);
		removeBGImageMenuItem.setAction(removeBGImageAction);

		playMenuItem = new JMenuItem("Play Music", KeyEvent.VK_P);
		playMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5,
				ActionEvent.ALT_MASK));
		stopMenuItem = new JMenuItem("Stop Music", KeyEvent.VK_S);
		stopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6,
				ActionEvent.ALT_MASK));
		stopMenuItem.setEnabled(false);

		playMenuItem.setAction(playAction);
		stopMenuItem.setAction(stopAction);

		lookMenu = new JMenu("Look and Feel");
		lookMenu.setMnemonic(KeyEvent.VK_L);
		lookMenu.getAccessibleContext().setAccessibleDescription(
				"Menu that has PLAF options");

		lookMenu.add(metalMenuItem);
		lookMenu.add(winMenuItem);
		lookMenu.add(motifMenuItem);

		soundMenu = new JMenu("Background Music");
		soundMenu.setMnemonic(KeyEvent.VK_B);
		soundMenu.add(playMenuItem);
		soundMenu.add(stopMenuItem);

		optionsMenu = new JMenu("Options");
		optionsMenu.setMnemonic(KeyEvent.VK_O);
		optionsMenu.getAccessibleContext().setAccessibleDescription(
				"Menu that has view options");
		menuBar.add(optionsMenu);

		optionsMenu.add(lookMenu);
		optionsMenu.add(setBGImageMenuItem);
		optionsMenu.add(removeBGImageMenuItem);
		optionsMenu.add(winCalcItem);
		optionsMenu.add(soundMenu);
		optionsMenu.addSeparator();
		optionsMenu.add(exitMenuItem);

		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(editMenu);

		editMenu.add(copyMenuItem);
		editMenu.add(pasteMenuItem);

		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenu.getAccessibleContext().setAccessibleDescription(
				"Menu that has Help");
		menuBar.add(helpMenu);

		helpMenu.add(helpMenuItem);
		helpMenu.addSeparator();
		helpMenu.add(aboutMenuItem);
	}

	private void createPopupMenus() {
		popup = new JPopupMenu("Look & Feel");

		// create popup menus
		JMenuItem popItem1 = new JMenuItem();
		JMenuItem popItem2 = new JMenuItem();
		JMenuItem popItem3 = new JMenuItem();

		JMenu soundPopMenu = new JMenu("Background Music");
		JMenuItem popItem4 = new JMenuItem();
		JMenuItem popItem5 = new JMenuItem();

		popItem1.setAction(metalAction);
		popItem2.setAction(winAction);
		popItem3.setAction(motifAction);
		popItem4.setAction(playAction);
		popItem5.setAction(stopAction);

		metalAction.setEnabled(false);

		// add the menus to popup
		popup.add(popItem1);
		popup.add(popItem2);
		popup.add(popItem3);
		popup.addSeparator();
		popup.add(soundPopMenu);
		soundPopMenu.add(popItem4);
		soundPopMenu.add(popItem5);
	}

	// Create actions
	private void createActions() {
		metalAction = new AbstractAction("Metal Look") {
			public void actionPerformed(ActionEvent ae) {
				changeLAF(metalPLAF);
			}
		};
		winAction = new AbstractAction("Windows Look") {
			public void actionPerformed(ActionEvent ae) {
				changeLAF(winPLAF);
			}
		};
		motifAction = new AbstractAction("Unix Look") {
			public void actionPerformed(ActionEvent ae) {
				changeLAF(motifPLAF);
			}
		};
		playAction = new AbstractAction("Play") {
			public void actionPerformed(ActionEvent ae) {
				stopAction.setEnabled(true);
				playAction.setEnabled(false);
				playAudio("passport.mid");
			}
		};
		stopAction = new AbstractAction("Stop") {
			public void actionPerformed(ActionEvent ae) {
				playAction.setEnabled(true);
				stopAction.setEnabled(false);
				stopAudio("passport.mid");
			}
		};

		setBGImageAction = new AbstractAction("Set Background Image") {
			public void actionPerformed(ActionEvent ae) {
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
					fileChooser.setMultiSelectionEnabled(false);
					fileChooser.setDialogTitle("Select Background Image");
					fileChooser.setApproveButtonText("Select");
				}

				int approvedAction = fileChooser.showOpenDialog(topContainer);
				if (approvedAction == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String urlString = null;

					if (selectedFile.exists() && selectedFile.isFile()) {
						urlString = "file:/" + selectedFile.getAbsolutePath();
						try {
							if (urlString.indexOf("gif") != -1
									|| urlString.indexOf("jpg") != -1
									|| urlString.indexOf("jpeg") != -1) {
								mainpanel.setBackgroundImage(new ImageIcon(
										new URL(urlString)));
							} else {
								throw new Exception(
										"Invalid Image File. Please select an image file \nwith gif or jpg extension.");
							}
						} catch (Exception e) {
							mediator.showErrorMessage(e.getMessage(), "Error");
						}
					}
					log.info("File URL: " + urlString);
				}
			}
		};

		removeBGImageAction = new AbstractAction("Remove Background Image") {
			public void actionPerformed(ActionEvent ae) {
				mainpanel.setBackgroundImage(null);
			}
		};
	}

	/* (non-Javadoc)
	 * @see com.mycom.calculator.Calculator4MBean#changeLAF(java.lang.String)
	 */
	public String changeLAF(String plaf) {
		String msg = "LAF changed successfully...";

		try {
			if (plaf.equals(metalPLAF)) {
				metalAction.setEnabled(false);
				winAction.setEnabled(true);
				motifAction.setEnabled(true);
			} else if (plaf.equals(winPLAF)) {
				metalAction.setEnabled(true);
				winAction.setEnabled(false);
				motifAction.setEnabled(true);
			} else if (plaf.equals(motifPLAF)) {
				metalAction.setEnabled(true);
				winAction.setEnabled(true);
				motifAction.setEnabled(false);
			} else {
				throw new Exception("Error: Unsupported Look and Feel...");
			}

			buttonBackground = UIManager.getColor("Button.background");
			log.info("Button.background: '" + buttonBackground);

			UIManager.setLookAndFeel(plaf);
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(popup);
			// call myFrame.pack() to resize frame for laf
			topContainer.pack();
		} catch (Exception eUI) {
			JOptionPane.showMessageDialog(topContainer,
					"Error: Could not change LOOK and FEEL.", "Inane Error..",
					JOptionPane.ERROR_MESSAGE);
			msg = eUI.getMessage();
		}
		return msg;
	}

	// COMMON FUNCTIONS kept here

	/* (non-Javadoc)
	 * @see com.mycom.calculator.Calculator4MBean#playAudio(java.lang.String)
	 */
	public void playAudio(String clipKey) {
		try {
			AudioClip clip = (AudioClip) audioClipMap.get(clipKey);
			if (clip != null) {
				clip.play();
			}
			log.info("Action Command:playAudio : key='" + clipKey
					+ "', clip = " + clip);
		} catch (Exception e) {
		}
	}

	/* (non-Javadoc)
	 * @see com.mycom.calculator.Calculator4MBean#stopAudio(java.lang.String)
	 */
	public void stopAudio(String clipKey) {
		try {
			AudioClip clip = (AudioClip) audioClipMap.get(clipKey);
			clip.stop();
		} catch (Exception e) {
		}
	}

	// COMMON FUNCTIONS end

	// ACTIONLISTENER INTERFACE Implementation for the buttons STARTS >>>>>>>>>
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() instanceof Command) {
			((Command) evt.getSource()).execute();
			return;
		}
	}

	// ACTIONLISTENER INTERFACE Implementation for the buttons ENDS >>>>>

	// KEYLISTENER INTERFACE Implementation STARTS >>>>>>>>>

	public void keyTyped(KeyEvent ke) {
		char keyChar = ke.getKeyChar(); // Key Character can be correctly
		// obtained only from keyTyped method.
		log.info("Key Character (keyTyped) = " + keyChar + "...");

		String command = String.valueOf(keyChar);
		JButton button = null;
		if (buttonsMap.get(command) != null) {
			button = (JButton) buttonsMap.get(command);
			button.doClick();
			log.info("Button from map (keyTyped) = "
					+ button.getActionCommand());
			return;
		}

		switch (keyChar) {
		case 'c':
		case 'C': {
			bClear.doClick();
		}
		default: {
		}
		}

	}

	public void keyPressed(KeyEvent ke) {
		int keyCode = ke.getKeyCode(); // Key code can be correctly obtained
		// only from keyPressed method.
		log.info("Key code (keyPressed) is " + keyCode + "."); // 96 - 105

		switch (keyCode) {
		case KeyEvent.VK_BACK_SPACE: {
			bBackspace.doClick();
			break;
		}
		case KeyEvent.VK_ESCAPE: {
			bClear.doClick();
			break;
		}
		case KeyEvent.VK_DELETE: {
			bClearAll.doClick();
			break;
		}
		case KeyEvent.VK_F9: {
			// valueSign();
			break;
		}
		default: {
		}
		}
	}

	public void keyReleased(KeyEvent ke) {
	}

	// KEYLISTENER INTERFACE Implementation ENDS >>>>>>>>>

	// MOUSELISTENER INTERFACE Implementation for the buttons STARTS >>>>>>>>>
	public void mouseEntered(MouseEvent me) {
		Component bMouse = me.getComponent();
		if (bMouse instanceof JButton) {
			bMouse.setForeground(Color.red);
			bMouse.setBackground(Color.yellow);
		}
	}

	public void mouseExited(MouseEvent me) {
		Component bMouse = me.getComponent();

		if (bMouse instanceof ArithmeticOperationButton) {
			bMouse.setForeground(Color.blue);
			bMouse.setBackground(buttonBackground);
		} else if (bMouse instanceof InputChangeButton) {
			bMouse.setForeground(Color.red);
			bMouse.setBackground(buttonBackground);
		}
	}

	public void mouseClicked(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
	}

	public void mouseReleased(MouseEvent me) {
	}
	
	private void registerMBean() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName name = ObjectName.getInstance("Calculator:name=calculator4");
			mbs.registerMBean(this, name);
			log.info("Registered as MBean : " + name);
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		}
	}

	// MOUSELISTENER INTERFACE Implementation for the buttons ENDS >>>>>>>>>

	// MAIN FUNCTION STARTS >>>>>>>
	public static void main(String[] commandArguments) {
		// Place Applet in Frame
		JFrame frame = new JFrame("Calculator");

		Calculator4 calc = new Calculator4();
		calc.setTopContainer(frame);
		// Run the methods that the browser normally would
		calc.init();
		calc.start();
		calc.setIsApplet(false);

		frame.getContentPane().add("Center", calc);
		// frame.setSize(275, 250);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setResizable(false);
		frame.setLocation(240, 0);

		URL url = calc.getClass().getResource("/images/calculator.gif");
		ImageIcon icon = new ImageIcon(url);
		Image image = null;
		if (icon != null) {
			image = icon.getImage();
		}
		frame.setIconImage(image);
		frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
		frame.pack();
		frame.setVisible(true);

		calc.playAudio("welcome.wav");
	}
	// MAIN FUNCTION ENDS >>>>>>>>

} // END OF Calculator4 CLASS >>>>>>>>
