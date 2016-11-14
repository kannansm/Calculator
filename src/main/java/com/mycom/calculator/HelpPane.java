/**
 * @version 1.00 2001-04-10
 * @author S Kannan
 */
package com.mycom.calculator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Stack;
import java.util.logging.Logger;

public class HelpPane extends JFrame
        implements ActionListener, HyperlinkListener {
    private JButton backButton;
    private JEditorPane editorPane;
    private Stack urlStack = new Stack();
    private Logger log = Logger.getLogger(getClass().getName());

    public HelpPane() {
        super();
        setTitle("Calculator - Help");
        setSize(500, 425);
        //setLocation(280, 100);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });

        // set up back button and button action
        backButton = new JButton("Back");
        backButton.addActionListener(this);

        // set up editor pane and hyperlink listener
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(this);

        try {
            //codeBase = new URL("file:" + System.getProperty("user.dir") + "/");
            String fileName = "/user_guide.html";
            URL urlObj = getClass().getResource(fileName);//new URL(codeBase, fileName);
            editorPane.setPage(urlObj);
            urlStack.push(urlObj.toString());
        } catch (Exception e) {
            editorPane.setText("Error: " + e);
        }

        Container contentPane = getContentPane();
        contentPane.add(new JScrollPane(editorPane), "Center");

        // put all control components in a panel
        JPanel panel = new JPanel();
        panel.add(backButton);
        contentPane.add(panel, "North");

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/calculator.gif"));
        Image image = null;
        if (icon != null) {
            image = icon.getImage();
        }
        setIconImage(image);
    }

    public void actionPerformed(ActionEvent ae) {
        if (urlStack.size() <= 1) return;
        try {  // get URL from back button
            urlStack.pop();
            // show URL in text field
            String urlString = (String) urlStack.peek();
            editorPane.setPage(urlString);
            log.info("Web Page url: " + urlString);
        } catch (IOException e) {
            editorPane.setText("Error: " + e);
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {  // remember URL for back button
                urlStack.push(event.getURL().toString());
                editorPane.setPage(event.getURL());
            } catch (IOException e) {
                editorPane.setText("Error: " + e);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new HelpPane();
        frame.show();
    }
}
