package gui.dialog;

import gui.FilenameFilter;
import gui.Frame;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import ctex.Main;
import database.Db;

public class OpenFileDialog extends JDialog implements ActionListener {
	
	private final int behavior;
	private int theGridY = 0;
	
	private static final long serialVersionUID = 1L;
	
	// Both radio buttons in a group
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton internalDbRadio = new JRadioButton(Main.myLang.getString("dialog.internaldbradio"));
	private JRadioButton sqlDbRadio = new JRadioButton(Main.myLang.getString("dialog.mysqldbradio"));
	
	
	// Inner panel containing the input elements
	private JPanel innerPanel = new JPanel(new GridBagLayout());
	
	// File
	private JLabel fileLabel = new JLabel(Main.myLang.getString("dialog.filelabel"));
	private JTextField fileText = new JTextField();
	private JButton fileButton = new JButton(Main.myLang.getString("button.browse"));
	
	// Encoding
	private JLabel encodingLabel = new JLabel(Main.myLang.getString("dialog.encodinglabel"));
	String[] encodeingStrings = 
		{"UTF-8", "US-ASCII", "ISO646-US", "ISO-8859-1", "ISO-LATIN-1", "UTF-16BE", "UTF-16LE"};
	private JComboBox encodingBox = new JComboBox (encodeingStrings);
	
	// IP
	private JLabel ipLabel = new JLabel(Main.myLang.getString("dialog.iplabel"));
	private JTextField ipText = new JTextField();
	
	// Port
	private JLabel portLabel = new JLabel(Main.myLang.getString("dialog.portlabel"));
	private JTextField portText = new JTextField();
	
	// Database
	private JLabel dbNameLabel = new JLabel(Main.myLang.getString("dialog.dbnamelabel"));
	private JTextField dbNameText = new JTextField();
	
	// Prefix
	private JLabel prefixLabel = new JLabel(Main.myLang.getString("dialog.prefixlabel"));
	private JComboBox prefixBox = new JComboBox ();
	
	// User
	private JLabel userLabel = new JLabel(Main.myLang.getString("dialog.userlabel"));
	private JTextField userText = new JTextField();
	
	// Password
	private JLabel passwordLabel = new JLabel(Main.myLang.getString("dialog.passwordlabel"));
	private JPasswordField passwordText = new JPasswordField();
	
	
	// Buttons outside the panel
	private JButton okButton = new JButton(Main.myLang.getString("button.ok"));
	private JButton cancelButton = new JButton(Main.myLang.getString("button.cancel"));
	private JButton connectButton = new JButton(Main.myLang.getString("button.connect"));
	
	
	// Variables for setting up the layout
	private GridBagConstraints cons = new GridBagConstraints();
	private Insets in = new Insets(5, 5, 5, 5);
	
	Frame myOwner;
	Db myDb;
	String dbType = null;
	
	/**
	 * Constructor for the dialog, setting up swing components
	 * 
	 * @param owner = the parent frame
	 * @param behavior decides what swing components are needed:
	 * 		1 = Components for a new database
	 * 		2 = Components for opening a file
	 * 		3 = Components for opening a database
	 */
	public OpenFileDialog(Frame owner, int behavior) {
		super(owner, true);
		this.behavior = behavior;
		this.myOwner = owner;
		// Setting the title for the dialog
		switch(behavior) {
			case 1: setTitle(Main.myLang.getString("dialog.title.newdb")); break;
			case 2: setTitle(Main.myLang.getString("dialog.title.openfile")); break;
			case 3: setTitle(Main.myLang.getString("dialog.title.opendb")); break;
		}

		// Close dialog when pressing ESC, opening on ENTER
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doOk();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		setLocationRelativeTo(owner);
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(350, 260));
		setResizable(false);
		internalDbRadio.setSelected(true);
		// Standard settings for the selected radio button
		ipText.setEnabled(false);
		portText.setEnabled(false);
		dbNameText.setText("./TexDB");
		userText.setText("user1");
		userText.setEnabled(false);
		passwordText.setText("user1");
		passwordText.setEnabled(false);
		// Layout on top
		cons.insets = in;
		cons.anchor = GridBagConstraints.CENTER;
		cons.gridx = 0;
		cons.gridy = 0;
		cons.weightx = 1.0;
		internalDbRadio.addActionListener(this);
		buttonGroup.add(internalDbRadio);
		this.getContentPane().add(internalDbRadio, cons);
		cons.gridx = 1;
		buttonGroup.add(sqlDbRadio);
		sqlDbRadio.addActionListener(this);
		this.getContentPane().add(sqlDbRadio, cons);
		cons.weightx = 0.0;
		
		// Layout inside the border
		in.set(1, 2, 1, 2);
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.anchor = GridBagConstraints.LINE_START;
		cons.gridx = 0;
		cons.gridy = 1;
		cons.gridwidth = 2;
		innerPanel.setBorder(BorderFactory.createEtchedBorder());
		this.getContentPane().add(innerPanel, cons);
		
		// For opening a file, those components are needed
		if (behavior == 2){
			cons.gridy = 0;
			cons.gridx = 0;
			innerPanel.add(fileLabel, cons);
			
			cons.gridx = 1;
			cons.gridwidth = 2;
			dbNameText.setPreferredSize(new Dimension(120, 22));
			cons.weightx = 1.0;
			innerPanel.add(fileText, cons);

			cons.weightx = 0.0;
			cons.gridx = 3;
			cons.gridwidth = 1;
			cons.anchor = GridBagConstraints.LINE_END;
			innerPanel.add(fileButton, cons);
			fileButton.addActionListener(this);
			
			cons.gridx = 0;
			cons.gridy = 2;
			cons.gridwidth = 1;
			innerPanel.add(encodingLabel, cons);
			cons.gridx = 1;
			cons.anchor = GridBagConstraints.LINE_END;
			innerPanel.add(this.encodingBox, cons);
			theGridY = 3;
		}
		
		cons.gridx = 0;
		cons.gridy = 0 + theGridY;
		cons.gridwidth = 1;
		innerPanel.add(ipLabel, cons);
		cons.gridx = 1;
		ipText.setPreferredSize(new Dimension(120, 22));
		cons.weightx = 1.0;
		innerPanel.add(ipText, cons);
		cons.weightx = 0.0;
		
		cons.gridx = 2;
		innerPanel.add(portLabel, cons);
		cons.gridx = 3;
		portText.setPreferredSize(new Dimension(45, 22));
		cons.weightx = 1.0;
		innerPanel.add(portText, cons);
		cons.weightx = 0.0;
		
		cons.gridx = 0;
		cons.gridy = 1 + theGridY;
		innerPanel.add(dbNameLabel, cons);
		cons.gridx = 1;
		cons.gridwidth = 3;
		dbNameText.setPreferredSize(new Dimension(200, 22));
		cons.weightx = 1.0;
		innerPanel.add(dbNameText, cons);
		cons.weightx = 0.0;
		
		cons.gridx = 0;
		cons.gridy = 2 + theGridY;
		cons.gridwidth = 1;
		innerPanel.add(userLabel, cons);
		cons.gridx = 1;
		cons.gridwidth = 3;
		userText.setPreferredSize(new Dimension(200, 22));
		cons.weightx = 1.0;
		innerPanel.add(userText, cons);
		cons.weightx = 0.0;
		
		cons.gridx = 0;
		cons.gridy = 3 + theGridY;
		cons.gridwidth = 1;
		innerPanel.add(passwordLabel, cons);
		cons.gridx = 1;
		cons.gridwidth = 3;
		passwordText.setPreferredSize(new Dimension(200, 22));
		// Trying to connect if enter was hit on the JPasswordField
		passwordText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (connectButton.isEnabled() && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					connectButton.doClick();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			
		});
		cons.weightx = 1.0;
		innerPanel.add(passwordText, cons);
		cons.weightx = 0.0;
		
		cons.gridx = 0;
		cons.gridy = 4 + theGridY;
		cons.gridwidth = 1;
		//innerPanel.add(passwordLabel, cons);
		cons.gridx = 1;
		cons.gridwidth = 2;
		passwordText.setPreferredSize(new Dimension(200, 22));
		cons.weightx = 1.0;
		connectButton.addActionListener(this);
		innerPanel.add(connectButton, cons);
		cons.weightx = 0.0;
		
		
		cons.gridx = 0;
		cons.gridy = 5 + theGridY;
		cons.gridwidth = 1;
		innerPanel.add(prefixLabel, cons);
		cons.gridx = 1;
		cons.gridwidth = 3;
		passwordText.setPreferredSize(new Dimension(200, 22));
		cons.weightx = 1.0;
		prefixBox.setEnabled(false);
		innerPanel.add(this.prefixBox, cons);
		cons.weightx = 0.0;
		

		
		// Layout at the bottom
		in.set(5, 5, 5, 5);
		cons.fill = GridBagConstraints.NONE;
		cons.gridx = 0;
		cons.gridy = 2;
		cons.gridwidth = 1;
		cons.anchor = GridBagConstraints.LINE_END;
		
		okButton.addActionListener(this);
		okButton.setEnabled(false);
		this.getContentPane().add(okButton, cons);
		
		cons.gridx = 1;
		cons.anchor = GridBagConstraints.LINE_START;
		cancelButton.addActionListener(this);
		this.getContentPane().add(cancelButton, cons);

		pack();
		setModal(true);
		setVisible(true);
	}
	
	

	/** 
	 * @param ActionEvent e
	 */
	public void actionPerformed(ActionEvent e) {
		// Ok-Button pressed
		if (okButton.getText() == e.getActionCommand()) {
			doOk();
		// Cancel-Button pressed, switches off icons if needed
		} else if (cancelButton.getText() == e.getActionCommand()) {
			if (!myOwner.ItemOff()){
				myOwner.setIconsEnabled(false);
				myOwner.setItemOff();
			}
			this.dispose();
		// Opening an instance of JFileChooser when the Browse-Button was pressed
		} else if (fileButton.getText() == e.getActionCommand()) {
			JFileChooser openFile = new JFileChooser();
			openFile.setFileFilter(new FilenameFilter("CrossTeX (*.xtx) and BibTeX (*.bib) files", new String[] {"xtx", "bib"}));
			openFile.setAcceptAllFileFilterUsed(false);
			int returnVal = openFile.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File f = openFile.getSelectedFile();
				fileText.setText(f.getPath());
			}
		// Internal database radio selected, some components switched off and
	    // dummy values set
		} else if (internalDbRadio.getText() == e.getActionCommand()) {
			if (internalDbRadio.isSelected()) {
				ipText.setEnabled(false);
				portText.setEnabled(false);
				userText.setEnabled(false);
				passwordText.setEnabled(false);
				dbNameText.setText("./TexDB");
				userText.setText("user1");
				passwordText.setText("user1");
			}
	    // SQL database radio selected, some components switched on and
		// dummy values set
		} else if (sqlDbRadio.getText() == e.getActionCommand()) {
			if (sqlDbRadio.isSelected()) {
				ipText.setEnabled(true);
				portText.setEnabled(true);
				userText.setEnabled(true);
				passwordText.setEnabled(true);
				ipText.setText("localhost");
				portText.setText("3306");
				dbNameText.setText("TexDB");
				userText.setText("root");
				passwordText.setText("");
			}
		// Connect-Button pressed, trying to connect to database if the required fields
		// are not empty
		} else if (connectButton.getText() == e.getActionCommand()){
			if (!dbNameText.getText().isEmpty() 
				 && !userText.getText().isEmpty()
				 && !dbNameText.getText().isEmpty()){
				
				if (sqlDbRadio.isSelected()) {
					dbType = "mysql";
				} else {
					dbType = "derby";
				}
				myDb = Main.connect(dbType, userText.getText(), new String(passwordText.getPassword()), ipText.getText(), portText.getText(), dbNameText.getText(), this);
				
				Vector<String> prifix = myDb.getTablePrifix();
				Iterator<String> i = prifix.iterator();
				while (i.hasNext()){
					prefixBox.addItem(i.next());
				}
				prefixBox.setEnabled(true);
				if (behavior != 3){
					prefixBox.setEditable(true);
					okButton.setEnabled(true);
				} else {
					if (prifix.size() != 0){
						okButton.setEnabled(true);
					}
				} 
					
				
			}
		}
	}
	
	/**
	 * Method that is invoked, when all data has been entered. Here a new entity
	 * according to the behavior is created.
	 */
	private void doOk(){
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		if (sqlDbRadio.isSelected()) {
			dbType = "mysql";
		} else {
			dbType = "derby";
		}
		
		// To check if a file exists
		new File(fileText.getText());
		
		// to detect if addEntity has worked
		boolean isCreated = false;
		switch (behavior) {
			case 1: isCreated = Main.addEntity(myDb, (String)prefixBox.getSelectedItem() + "_", myOwner, false); break;
			case 2:	isCreated = Main.addEntityFile(myDb, (String)prefixBox.getSelectedItem() + "_", myOwner, fileText.getText(), (String)encodingBox.getSelectedItem()); break;
			case 3: isCreated = Main.addEntity(myDb, (String)prefixBox.getSelectedItem() + "_", myOwner, true); break;
		}
		
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		if (isCreated == true){
			this.dispose();
		}
	}
	
}
