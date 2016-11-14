/**
 * @version 1.00 2003-08-10
 * @author S Kannan
 */
package com.mycom.calculator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutDialog extends JDialog {
    private JPanel logoPanel;

    public AboutDialog(Frame owner, String title, boolean modal, ImageIcon icon) {
        super(owner, title, modal);

        logoPanel = new JPanel();
        logoPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        JPanel labelPanel = new JPanel();
        labelPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel okPanel = new JPanel();
        okPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        JPanel okPanel1 = new JPanel();

        JLabel logoLabel = new JLabel(icon, JLabel.LEFT);
        //JLabel logoLabel = new JLabel(new ImageIcon("C:\\Java_Codes\\Calculator3S.ico"));

        JLabel nameLabel1 = new JLabel("Java2-Swing Calculator Version 4.0", JLabel.LEFT);
        JLabel nameLabel2 = new JLabel("Developed by S. Kannan", JLabel.LEFT);
        JLabel nameLabel3 = new JLabel("Copyright(C) 2001 - 2005 SK Corp.", JLabel.LEFT);
        JLabel nameLabel4 = new JLabel("   ", JLabel.LEFT);

        JLabel licenseLabel = new JLabel("This product is licensed to:     ", JLabel.RIGHT);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(75, 25));
        okButton.setForeground(Color.blue);
        okButton.setToolTipText("Click to close");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                //setVisible(false);
                dispose();
            }
        });
        okButton.requestFocus(); //requestFocusInWindow();

        logoPanel.add(logoLabel);
        labelPanel.add(nameLabel1);
        labelPanel.add(nameLabel2);
        labelPanel.add(nameLabel3);
        labelPanel.add(nameLabel4);

        //okPanel.setLayout(new BorderLayout());
        okPanel.setLayout(new GridLayout(3, 1, 1, 1));
        okPanel.add(licenseLabel);
        okPanel.add(new JLabel("                    ", JLabel.RIGHT));
        okPanel1.add(okButton);
        okPanel.add(okPanel1);

        getContentPane().add("West", logoPanel);
        getContentPane().add("Center", labelPanel);
        getContentPane().add("South", okPanel);

        setResizable(false);
        setSize(350, 250);
        //setLocation(280, 100);
        //setVisible(true);
    }
}