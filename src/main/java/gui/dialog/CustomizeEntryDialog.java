package gui.dialog;

import ctex.Main;
import database.Db;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import container.ContainerType;



import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/** 
 * JDialog that makes it possible to change entry types and add new ones.
 * 
 * @author stefan
 */

public class CustomizeEntryDialog extends JDialog {
	
	boolean debug = false;
	private static final long serialVersionUID = 1L;
	
	// Entry types section
	private JLabel allTypesLabel = new JLabel(Main.myLang.getString("customizeentrydialog.alltypeslabel"));
	private JList allTypes = new JList();
	private DefaultListModel allTypesModel = new DefaultListModel();
	private JScrollPane allTypesScroller = new JScrollPane(allTypes);
	private JTextField addTypeField = new JTextField();
	private JButton addTypeButton = new JButton(Main.myLang.getString("button.add"));
	private JButton removeTypeButton = new JButton(Main.myLang.getString("button.remove"));
	private JButton defaultTypeButton = new JButton(Main.myLang.getString("button.default"));
	
	// Required fields section
	private JLabel requiredLabel = new JLabel(Main.myLang.getString("customizeentrydialog.requiredlabel"));
	private JList requiredList = new JList();
	private DefaultListModel requiredModel = new DefaultListModel();
	private JScrollPane requiredScroller = new JScrollPane(requiredList);
	private JButton requiredUpButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/arrow_up.png")));
	private JButton requiredDownButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/arrow_down.png")));
	private JButton requiredRemoveButton = new JButton(Main.myLang.getString("button.remove"));
	private JComboBox requiredComboBox = new JComboBox();
	private JButton requiredAddButton = new JButton(Main.myLang.getString("button.add"));
	
	// Optional fields section
	private JLabel optionalLabel = new JLabel(Main.myLang.getString("customizeentrydialog.optionallabel"));
	private JList optionalList = new JList();
	private DefaultListModel optionalModel = new DefaultListModel();
	private JScrollPane optionalScroller = new JScrollPane(optionalList);
	private JButton optionalUpButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/arrow_up.png")));
	private JButton optionalDownButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/arrow_down.png")));
	private JButton optionalRemoveButton = new JButton(Main.myLang.getString("button.remove"));
	private JComboBox optionalComboBox = new JComboBox();
	private JButton optionalAddButton = new JButton(Main.myLang.getString("button.add"));
	
	// Bottom buttons
	private JButton okButton = new JButton(Main.myLang.getString("button.ok"));
	private JButton applyButton = new JButton(Main.myLang.getString("button.apply"));
	private JButton cancelButton = new JButton(Main.myLang.getString("button.cancel"));
	

	// Overall contentPane, extra contentPane for the buttom buttons and GridBagConstraints
	private JPanel contentPane = new JPanel(new GridBagLayout());
	private JPanel contentPaneBottom = new JPanel(new GridBagLayout());
	private GridBagConstraints constraints = new GridBagConstraints();
	Insets insets = new Insets(0, 0, 0, 0);
	
	
	/* Values displayed on screen availableTypes is shown in the JLists
	 * fieldTypes is shown in the ComboBoxes twice */
	private Vector<ContainerType> availableTypes = new Vector<ContainerType>();
	private Vector<ContainerType> entryTypes = new Vector<ContainerType>();

	private Vector<String> fieldTypes = new Vector<String>();
	
	private Db theDb = null;

	/**
	 * Constructor for this class. Basically just initializes all the used swing
	 * components, loads the available entry types and sets everything up
	 * 
	 * @param theDb
	 */
	public CustomizeEntryDialog(final Db theDb){
		// Settings of the JDialog
		this.theDb = theDb;
		setSize(new Dimension(750, 285));
		setPreferredSize(new Dimension(750, 285));
		setTitle(Main.myLang.getString("customizeentrydialog.title"));
		// Close dialog when pressing ESC
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		setContentPane(contentPane);
		setModal(true);
		
		availableTypes = theDb.getAllTypes();
		// Loading types from files to display
		for (int i = 0; i < availableTypes.size(); i++) {
			if (availableTypes.get(i).isEntryType()) {
				entryTypes.add(availableTypes.get(i));
			}
		}
		
		// Loading all combobox fields
		loadComboBoxFields();
		
		/* 
		 * Entry types section
		 */
		allTypes.setModel(allTypesModel);
		for (int i = 0; i < entryTypes.size(); i++) {
			allTypesModel.addElement(entryTypes.get(i).getType());
		}
		allTypes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Always displaying the needed fields on the two JLists requiredList and optionalList
		allTypes.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
				
				requiredModel.clear();
				optionalModel.clear();
				
				if (!allTypes.isSelectionEmpty()) {
					for (int i = 0; i < entryTypes.get(allTypes.getSelectedIndex()).getRequiredFields().size(); i++) {
						requiredModel.add(i, entryTypes.get(allTypes.getSelectedIndex())
								.getRequiredFields().get(i));
					}
					
					for (int i = 0; i < entryTypes.get(allTypes.getSelectedIndex())
							.getOptionalFields().size(); i++) {
						optionalModel.add(i, entryTypes.get(allTypes.getSelectedIndex())
								.getOptionalFields().get(i));
					}
					
					// Activates/disables the Remove-Button
					if (entryTypes.get(allTypes.getSelectedIndex()).isDeletable()) {
							removeTypeButton.setEnabled(true);
					} else {
						removeTypeButton.setEnabled(false);
					}
				}			
			}
		});
		allTypesScroller.setPreferredSize(new Dimension(220, 150));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = insets;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.LINE_START;
		contentPane.add(allTypesLabel, constraints);
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		contentPane.add(allTypesScroller, constraints);
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		
		// Input fields and buttons of the entry types section
		addTypeField.setPreferredSize(new Dimension(145, 24));
		constraints.gridwidth = 1;
		constraints.gridy = 2;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL; // Fill stays for Add-Button
		contentPane.add(addTypeField, constraints);
		constraints.weightx = 0.0;
		constraints.gridx = 1;
		constraints.gridy = 2;

		// Adds an empty type to the availableTypes vector and to the JList model
		addTypeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((addTypeField.getText().compareTo("") != 0) &&
					(!allTypesModel.contains(addTypeField.getText()))) {
					entryTypes.add(new ContainerType(addTypeField.getText(), true, true));
					allTypesModel.addElement(addTypeField.getText());
					allTypes.setSelectedValue(addTypeField.getText(), true);
				}
			}
		});
		contentPane.add(addTypeButton, constraints);
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.NONE; // Fill ends here
		
		constraints.gridx = 0;
		constraints.gridy = 3;
		// Removes a type from the availableTypes vector and the JList model
		removeTypeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!allTypes.isSelectionEmpty()) {
					if (entryTypes.get(allTypes.getSelectedIndex()).isDeletable()) {
						entryTypes.removeElementAt(allTypes.getSelectedIndex());
						allTypesModel.removeElementAt(allTypes.getSelectedIndex());
						requiredModel.clear();
						optionalModel.clear();
					}
				}
			}
		});
		contentPane.add(removeTypeButton, constraints);
		constraints.gridx = 1;
		// Messagebox to be sure the user wants to restore defaults
		defaultTypeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int returnValue = JOptionPane.showConfirmDialog(null, 
						"Do you really want to restore the default values? This can not be undone.", 
						"Restore default values?",
						JOptionPane.YES_NO_OPTION);
				
				/* The user pressed ok, so the default values get loaded after the
				 * old values got cleared.
				 */
				if (returnValue == 0) {
					allTypesModel.clear();
					requiredModel.clear();
					optionalModel.clear();
					entryTypes.clear();
					// Reloading default types into database
					theDb.reloadDefaultTypes();
					// Getting the default types from database
					availableTypes = theDb.getAllTypes();
					// Loading types from files to display
					for (int i = 0; i < availableTypes.size(); i++) {
						if (availableTypes.get(i).isEntryType()) {
							entryTypes.add(availableTypes.get(i));
						}
					}
					for (int i = 0; i < entryTypes.size(); i++) {
						allTypesModel.addElement(entryTypes.get(i).getType());
					}
				}
			}
		});
		contentPane.add(defaultTypeButton, constraints);
		
		
		/* 
		 * Required fields section
		 */
		requiredList.setModel(requiredModel);
		requiredList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		requiredScroller.setPreferredSize(new Dimension(220, 150));
		constraints.gridwidth = 3;
		constraints.gridx = 2;
		constraints.gridy = 0;
		contentPane.add(requiredLabel, constraints);
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		contentPane.add(requiredScroller, constraints);
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.gridwidth = 1;
		
		// Input fields and buttons of the required fields section
		constraints.gridy = 2;
		// Moves a list item upward
		requiredUpButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (!requiredList.isSelectionEmpty()) {
					int position = requiredList.getSelectedIndex();
					if (position != 0) {
						swapRequiredItems(position, position - 1);
						requiredList.setSelectedIndex(position - 1);
						requiredList.ensureIndexIsVisible(position - 1);
					}
				}
			}
		});
		contentPane.add(requiredUpButton, constraints);
		constraints.gridx = 3;
		// Moves a list item downward
		requiredDownButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!requiredList.isSelectionEmpty()) {
					int position = requiredList.getSelectedIndex();
					if (position != (requiredModel.getSize() - 1)) {
						swapRequiredItems(position, position + 1);
						requiredList.setSelectedIndex(position + 1);
						requiredList.ensureIndexIsVisible(position + 1);
					}
				}
			}
		});
		contentPane.add(requiredDownButton, constraints);
		constraints.gridx = 4;
		// Removes selected item from requiredModel and requiredList
		requiredRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!requiredList.isSelectionEmpty()) {
					entryTypes.get(allTypes.getSelectedIndex())
					.getRequiredFields().remove(requiredList.getSelectedIndex());
					requiredModel.remove(requiredList.getSelectedIndex());
					}
				}
		});
		contentPane.add(requiredRemoveButton, constraints);
		
		requiredComboBox.setPreferredSize(new Dimension(145, 24));
		constraints.gridwidth = 2;
		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL; // Fill stays for Add-Button
		contentPane.add(requiredComboBox, constraints);
		constraints.weightx = 0.0;
		constraints.gridwidth = 1;
		constraints.gridx = 4;
		/* Adds the selected item from the ComboBox to the requiredList, if
		 * the current type is not a default type */
		requiredAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!allTypes.isSelectionEmpty() && 
						fieldNotUsed((String) requiredComboBox.getSelectedItem())) {
					requiredModel.addElement(requiredComboBox.getSelectedItem());
					entryTypes.get(allTypes.getSelectedIndex()).getRequiredFields().add(
						(String) requiredComboBox.getSelectedItem());
				}
			}
		});
		contentPane.add(requiredAddButton, constraints);
		constraints.fill = GridBagConstraints.NONE; // Fill ends here
		
		
		/*
		 *  Optional fields section
		 */
		optionalList.setModel(optionalModel);
		optionalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		optionalScroller.setPreferredSize(new Dimension(220, 150));
		constraints.gridwidth = 3;
		constraints.gridx = 5;
		constraints.gridy = 0;
		contentPane.add(optionalLabel, constraints);
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		contentPane.add(optionalScroller, constraints);
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.gridwidth = 1;
		
		
		// Input fields and buttons of the optional fields section
		constraints.gridy = 2;
		// Moves a list item upward
		optionalUpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!optionalList.isSelectionEmpty()) {
					int position = optionalList.getSelectedIndex();
					if (position != 0) {
						swapOptionalItems(position, position - 1);
						optionalList.setSelectedIndex(position - 1);
						optionalList.ensureIndexIsVisible(position - 1);
					}
					
				}
				
			}
		});
		contentPane.add(optionalUpButton, constraints);
		constraints.gridx = 6;
		// Moves a list item downward
		optionalDownButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!optionalList.isSelectionEmpty()) {
					int position = optionalList.getSelectedIndex();
					if (position != (optionalModel.getSize() - 1)) {
						swapOptionalItems(position, position + 1);
						optionalList.setSelectedIndex(position + 1);
						optionalList.ensureIndexIsVisible(position + 1);
					}
					
				}
				
			}
		});
		contentPane.add(optionalDownButton, constraints);
		constraints.gridx = 7;
		// Removes selected item from requiredModel and requiredList
		optionalRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!optionalList.isSelectionEmpty()) {
					entryTypes.get(allTypes.getSelectedIndex())
					.getOptionalFields().remove(optionalList.getSelectedIndex());
						optionalModel.remove(optionalList.getSelectedIndex());
					}
				}
		});
		contentPane.add(optionalRemoveButton, constraints);
		
		optionalComboBox.setPreferredSize(new Dimension(145, 24));
		constraints.gridwidth = 2;
		constraints.gridx = 5;
		constraints.gridy = 3;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL; // Fill stays for Add-Button
		contentPane.add(optionalComboBox, constraints);
		constraints.weightx = 0.0;
		constraints.gridwidth = 1;
		constraints.gridx = 7;
		/* Adds the selected item from the ComboBox to the optionalList, if
		 * the current type is not a default type
		 */
		optionalAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!allTypes.isSelectionEmpty() && 
						fieldNotUsed((String) optionalComboBox.getSelectedItem())) {
					optionalModel.addElement(optionalComboBox.getSelectedItem());
					entryTypes.get(allTypes.getSelectedIndex()).getOptionalFields().add(
						(String) optionalComboBox.getSelectedItem());
				}
			}
		});
		contentPane.add(optionalAddButton, constraints);
		constraints.fill = GridBagConstraints.NONE; // Fill ends here
		
		
		/*
		 *  Bottom buttons section (on the extra pane contentPaneBottom)
		 */
		insets.set(3, 3, 3, 3);
		constraints.weightx = 0.0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		// Saves the files and closes the JDialog
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAvailableTypes();
				dispose();	
			}
		});
		contentPaneBottom.add(okButton, constraints);
		
		constraints.gridx = 1;
		// Saves the files
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAvailableTypes();
				availableTypes = theDb.getAllTypes();
				// Loading types from files to display
				for (int i = 0; i < availableTypes.size(); i++) {
					if (availableTypes.get(i).isEntryType()) {
						entryTypes.add(availableTypes.get(i));
					}
				}
			}
		});
		contentPaneBottom.add(applyButton, constraints);
		
		constraints.gridx = 2;
		// Closes the JDialog
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		contentPaneBottom.add(cancelButton, constraints);
		
		// Adding contentPaneBottom to the other contentPane
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		contentPane.add(contentPaneBottom, constraints);
		
		// Displaying JContentPane and the JDialog
		getContentPane().setVisible(true);
		setVisible(true);
	}
	
	/** 
	 * After changes have been done in the "Customize entry type"-Dialog,
	 * the changes are saved to the database.
	 */	
	public void saveAvailableTypes(){
		theDb.setAllTypes(entryTypes);
	}
	
	/** 
	 * Loads all possible fields that can be chosen to be
	 * required or optional fields and saves them into the
	 * vector fieldTypes
	 */
	public void loadComboBoxFields() {
		try {
			FileInputStream fileStream = new FileInputStream("types/fields.dat");
			DataInputStream in = new DataInputStream(fileStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String field;
			
			while ((field = reader.readLine()) != null) {
				fieldTypes.add(field);
				requiredComboBox.addItem(field);
				optionalComboBox.addItem(field);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Checks if a field can still be used, or if some other
	 * part of the type already used it
	 * 
	 * @param field Is the field that should be checked
	 * @return boolean true if it's not used by any part of the type
	 */
	public boolean fieldNotUsed(String field) {
		return (!entryTypes.get(allTypes.getSelectedIndex())
				.getRequiredFields().contains(field)) &&
				(!entryTypes.get(allTypes.getSelectedIndex())
				.getOptionalFields().contains(field));
	}
	
	
	/** 
	 * Method that swaps two elements inside requiredList and
	 * the requiredFields vector of the entry type
	 * 
	 * @param a Index 1
	 * @param b Index 2
	 */
	public void swapRequiredItems(int a, int b) {
		Object aObject = requiredModel.getElementAt(a);
		Object bObject = requiredModel.getElementAt(b);
		requiredModel.set(a, bObject);
		requiredModel.set(b, aObject);
		entryTypes.get(allTypes.getSelectedIndex())
		.getRequiredFields().set(a, (String) bObject);
		entryTypes.get(allTypes.getSelectedIndex())
		.getRequiredFields().set(b, (String) aObject);
	}
	
	/** 
	 * Method that swaps two elements inside optionalList and
	 * the optionalFields vector of the entry type
	 * 
	 * @param a Index 1
	 * @param b Index 2
	 */
	public void swapOptionalItems(int a, int b) {
		Object aObject = optionalModel.getElementAt(a);
		Object bObject = optionalModel.getElementAt(b);
		optionalModel.set(a, bObject);
		optionalModel.set(b, aObject);
		entryTypes.get(allTypes.getSelectedIndex())
		.getOptionalFields().set(a, (String) bObject);
		entryTypes.get(allTypes.getSelectedIndex())
		.getOptionalFields().set(b, (String) aObject);
	}
}