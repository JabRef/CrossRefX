package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import ctex.Main;

/**
 * About dialog showing logo, version number and credits
 *  
 * @author strohmsn
 *
 */

public class AboutDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private GridBagConstraints cons = new GridBagConstraints();
	
	private ImageIcon logo;
	private JLabel logoLabel;
	private JLabel infoLabel;
	private JButton okButton;
	
	/**
	 * Constructor initializing the JDialog
	 */
	public AboutDialog() {
		setTitle(Main.myLang.getString("dialog.about.title"));
		// Close dialog when pressing ESC
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new GridBagLayout());
		
		cons.gridx = 0;
		cons.gridy = 0;
		cons.anchor = GridBagConstraints.CENTER;
		cons.insets = new Insets(5, 5, 5, 5);
		logo = new ImageIcon("images/logo.jpeg");
		logoLabel = new JLabel(logo);
		getContentPane().add(logoLabel);
		
		cons.gridy = 1;
		infoLabel = new JLabel("<html>Authors: Dmitrijs Allendorfs, Lars Lischke,<br>" +
				"Sven Andre Mayer, Stefan Strohmaier</html>");
		getContentPane().add(infoLabel, cons);
		
		cons.gridy = 2;
		infoLabel = new JLabel("Version: " + Main.getIniData("Version"));
		getContentPane().add(infoLabel, cons);
		
		cons.gridy = 3;
		infoLabel = new JLabel("Iconset by Mark James (http://www.famfamfam.com/lab/icons/silk/)");
		getContentPane().add(infoLabel, cons);
		
		cons.gridy = 4;
		okButton = new JButton(Main.myLang.getString("button.ok"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		getContentPane().add(okButton, cons);
		
		setResizable(false);		
		setModal(true);
		pack();
		setVisible(true);
	}
	

}
