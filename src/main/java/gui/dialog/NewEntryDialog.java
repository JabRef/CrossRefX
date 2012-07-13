package gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import container.ContainerType;


import ctex.Main;
import database.Db;

public class NewEntryDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private Vector<ContainerType> allEntryTypes = new Vector<ContainerType>();
	private JPanel buttonPanel = new JPanel(new GridLayout(0, 3, 5, 5));
	private JLabel entryTypesLabel = new JLabel(Main.myLang.getString("newentrydialog.entrytypeslabel"));
	private Vector<JButton> buttons = new Vector<JButton>();
	private JButton cancelButton = new JButton(Main.myLang.getString("button.cancel"));
	private GridBagConstraints constraints = new GridBagConstraints();
	private Insets inset = new Insets(3, 3, 3, 3);
	
	private String pressedButton;
	
	/**
	 * Constructor to setup the swing components for this dialog
	 * 
	 * @param theDb
	 */
	public NewEntryDialog(Db theDb) {
		setTitle(Main.myLang.getString("newentrydialog.title"));
		// Close dialog when pressing ESC
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		setResizable(false);
		setLayout(new GridBagLayout());
		setModal(true);
		
		// Getting types from database
		allEntryTypes = theDb.getAllTypes();
			
		Vector<ContainerType> entryTypes = new Vector<ContainerType>(); 
		for (int i = 0; i < allEntryTypes.size(); i++) {
			if (allEntryTypes.get(i).isEntryType()) {
				entryTypes.add(allEntryTypes.get(i));
			}
		}
		
		// Generating the buttons for the possible entry types
		for (int i = 0; i < entryTypes.size(); i++) {
			buttons.add(new JButton(entryTypes.get(i).getType()));
			buttons.get(i).setPreferredSize(new Dimension(170, 24));
			buttons.get(i).addActionListener(this);
			buttons.get(i).setActionCommand(buttons.get(i).getText());
			buttonPanel.add(buttons.get(i));
		}			
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = inset;
		getContentPane().add(entryTypesLabel, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		getContentPane().add(buttonPanel, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		// Closes the window without setting a string
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		getContentPane().add(cancelButton, constraints);
		pack();
		setVisible(true);
	}
	
	/**
	 *  Sets a string, so it can then be accessed from outside the class.
	 *  Then closes the window
	 */
	public void actionPerformed(ActionEvent e) {
		pressedButton = new String(e.getActionCommand());
		dispose();
	}
	
	/**
	 * Returns the name of the pressed button, so the JFrame knows which
	 * TextFields to load.
	 * 
	 * @return The name of the pressed button
	 */
	public String getPressedButton() {
		return pressedButton;
	}

}
