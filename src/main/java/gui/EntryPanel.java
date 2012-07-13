/*		Class: CTex_TextFieldTab
 * 		
 * 		Autoren: Sven, Dimi, Stefan, Lars
 * 		
 * 		Funktion: Stellt die EingabeFelder, fuer das Editieren von 
 * 				  Datenbank Eintraege. Die Classe bestehen aus 2
 * 				  Tabs "Requeds Field" u. "Optional Fields"
 * 
 */
package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.TableColumn;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.mysql.jdbc.PacketTooBigException;

import java.util.Iterator;
import java.util.Vector;

import container.ContainerAttribute;
import container.ContainerFile;
import container.ContainerObject;
import container.ContainerType;
import ctex.Main;
import gui.dialog.FileDialog;
import database.Db;


public class EntryPanel extends JTabbedPane implements KeyListener{
	
	private static final long serialVersionUID = 1L;
	
	// Undo Manager fuer "Requeds Field" u. "Optional Fields"
	public UndoManager myReqField = new UndoManager();
	public UndoManager myOptField = new UndoManager();
	
	/*
	//  dummy Compounds fuer zwischenspeichern der Inhalt
	//  "Requeds Field" u. "Optional Fields"
	*/
	public CompoundEdit myReqCompound = new CompoundEdit();
	public CompoundEdit myOptCompound = new CompoundEdit();
	
	// boolean Fleck fuer den aktuellen Undo Manager
	private boolean ReqOROpt = true;
	
	// this TextField Tab
	private EntryPanel myTabPane;
	
	// ScrollPane fuer "Requeds Field" u. "Optional Fields"
	private JScrollPane jScrollPaneReq = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JScrollPane jScrollPaneOpt = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	// Vectoren fuer "Requeds Field" u. "Optional Fields"
	private Vector<TextArea> ReqField = new Vector<TextArea>();
	private Vector<TextArea> OptField = new Vector<TextArea>();
	
	// boolean Fleck true fuer "Req" u. false fuer "Opt"
	private boolean isRequired = false;
	
	// fuer Tab Button betetigung Id von TextArea
	private int ReqAreaId = 0;
	private int OptAreaId = 0;
	
	// boolean Fleck true fuer den new Entry sonst alten Eintrag editieren
	private boolean isNewEntry = false;
	
	// String of entryType
	private final String entryTypeString;
	
	// Key of entryType
	private final String entryTypeKeyVar = Main.entryTypeKeyFild;
	
	// Datenbank entryType
	private ContainerType entryType;
	
	// Datenbank
	private Db theDb;
	
	// Object of Datenbank
	private ContainerObject obj;
	private TabPanel tableTab;
	
	private JButton apply = new JButton(Main.myLang.getString("button.apply"));
	
	/* to generate entry Key */
	private TextArea autor = null;
	private TextArea year = null;
	
	private JTable fileTable;
	private JPopupMenu tablePopupMenu;
	private ContainerFile rightClickedElement;
	private FileTableModel fileModel;
	
	private final Frame myFrame;
	
	/**
	 * 	Construktor: 
	 *  Initialisiert CTex_TextFieldTab mit dem uebergebnen Objekt
	 *  aus dem Datenbank
	 * 
	 *  @return gui.CTex_TextFieldTab
	 */
	EntryPanel(Db theDb, TabPanel inTab, final Undomanager manager, ContainerObject obj, Frame frame) {
		
		tableTab = inTab;
		isNewEntry = false;
		this.obj = obj;
		this.theDb = theDb;
		this.myTabPane = this;
		this.myFrame = frame;
		this.entryTypeString = theDb.convTypeIdToTypeName(obj.getTypeId());
		this.entryType = theDb.getType(entryTypeString);
		
		// Close dialog when pressing ESC
		tableTab.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableTab.showPreviewPane();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		// Auf Tab Mous Listner Legen. 
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				
				// Tab Nummer holen
				int tabNumber = getUI().tabForCoordinate(EntryPanel.this, e.getX(), e.getY());
				
				// Wenn Tab 0 dann Requiedfield sonst OptionsField
				if(tabNumber == 0){
					// Manager auf ReqField den feld setzen
					ReqOROpt = true;
					ReqAreaId = 0;
					manager.setActivManager(myReqField, myOptField, null, myReqCompound, myOptCompound, null, ReqOROpt, false);
					// Area Tab Einstellungen
					ReqField.get(ReqAreaId).setCaretPosition(0);
					ReqField.get(ReqAreaId).requestFocus(true);
					/* set Color of new Area */
					//ReqField.get(ReqAreaId).setFocusBackground();
					
				} else if (tabNumber == 1) {
					ReqOROpt = false;
					OptAreaId = 1;
					// Manager auf OptField setzen
					manager.setActivManager(myReqField, myOptField, null, myReqCompound, myOptCompound, null, ReqOROpt, false);
					// Area Tab Einstellungen
					OptField.get(OptAreaId).setCaretPosition(0);
					OptField.get(OptAreaId).requestFocus(true);
					/* set Color of new Area */
					//OptField.get(OptAreaId).setFocusBackground();
				}
			}
		});
		// Manager auf selekted Tab setzen
		manager.setActivManager(myReqField, myOptField, null, myReqCompound, null, myOptCompound, ReqOROpt, false);
		
		// "Requeds Field" u. "Optional Fields" Tabs einfuegen
		this.addTab("Required fields", getReqTextField(manager));
		this.addTab("Optional fields", getOptTextField(manager));
		
		this.ReqAreaId = 0;
		this.OptAreaId = 0;
		this.ReqOROpt = true;
	
	}
	
	/**
	 * 	Construktor: 
	 *  Initialisiert CTex_TextFieldTab mit neuem Entry Type
	 * 
	 *  @return gui.CTex_TextFieldTab
	 */
	EntryPanel(Db theDb, TabPanel inTab, String entryTypeString, final Undomanager manager, Frame frame) {
		
		tableTab = inTab;
		isNewEntry = true;
		this.obj = null;
		this.theDb = theDb;
		this.myTabPane = this;
		this.myFrame = frame;
		this.entryTypeString = entryTypeString;
		this.entryType = theDb.getType(entryTypeString);
		
		// Close dialog when pressing ESC
		tableTab.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableTab.showPreviewPane();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		// Auf Tab Mous Listner Legen. 
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// Tab Nummer hollen
				int tabNumber = getUI().tabForCoordinate(EntryPanel.this, e.getX(), e.getY());
				// Wenn Tab 0 dann Requiedfield sonst OptionsField
				
				
				if(tabNumber == 0){
					// Manager auf ReqField den feld setzen
					ReqOROpt = true;
					ReqAreaId = 0;
					manager.setActivManager(myReqField, myOptField, null, myReqCompound, myOptCompound, null, ReqOROpt, false);
					// Area Tab Einstellungen
					ReqField.get(ReqAreaId).setCaretPosition(0);
					ReqField.get(ReqAreaId).requestFocus(true);
					/* set Color of new Area */
					//ReqField.get(ReqAreaId).setFocusBackground();
					
				} else if (tabNumber == 1) {
					ReqOROpt = false;
					OptAreaId = 1;
					// Manager auf OptField setzen
					manager.setActivManager(myReqField, myOptField, null, myReqCompound, myOptCompound, null, ReqOROpt, false);
					// Area Tab Einstellungen
					OptField.get(OptAreaId).setCaretPosition(0);
					OptField.get(OptAreaId).requestFocus(true);
					/* set Color of new Area */
					//OptField.get(OptAreaId).setFocusBackground();
					
				}
			}
		});
		
		// Manager auf selekted Tab setzen
		manager.setActivManager(myReqField, myOptField, null, myReqCompound, null, myOptCompound, ReqOROpt, false);
		
		// "Requeds Field" u. "Optional Fields" Tabs einfuegen
		this.addTab("Required fields", getReqTextField(manager));
		this.addTab("Optional fields", getOptTextField(manager));
		
		this.ReqAreaId = 0;
		this.OptAreaId = 0;
		this.ReqOROpt = true;
		
	}

	
	/**
	 *  Diese Methode initialisiert die RequedField Panel
	 *  und fuegt die benuetigte Text Areas ein
	 *  
	 * @return javax.swing.JPanel	
	 */
	private JPanel getReqTextField(Undomanager manager) {
		
		// Requed Field Pane 
		JPanel myReq = new JPanel();
		JPanel jPanelReq = new JPanel();
		myReq.setLayout(new BorderLayout());

		isRequired = true;
		// Skalierung in Y - Richtung 
		jPanelReq.setLayout(new BoxLayout(jPanelReq, BoxLayout.Y_AXIS));
		
		// Entry Type Key Feld einfuegen
		jPanelReq.add(getTextPanel(entryTypeKeyVar, manager));
													
		// Restliche Felder einfuegen
		for (int i = 0; i < entryType.getRequiredFields().size(); i++){
			jPanelReq.add(getTextPanel(entryType.getRequiredFields().get(i), manager));
		}
		
		jScrollPaneReq.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPaneReq.getVerticalScrollBar().setUnitIncrement(12);
		jScrollPaneReq.setViewportView(jPanelReq);

		myReq.add(getTypePanel(), BorderLayout.WEST);
		myReq.add(jScrollPaneReq, BorderLayout.CENTER);
		myReq.add(getBottomPanel(), BorderLayout.PAGE_END);
		return myReq;
	
	}
	
	public void setFocus(){
		ReqField.get(ReqAreaId).setCaretPosition(0);
		ReqField.get(ReqAreaId).requestFocusInWindow();
	}
	
	/**
	 *  Diese Methode initialisiert die OptionalFields Panel
	 *  und fuegt die benuetigte Text Areas ein
	 *  
	 * @return javax.swing.JPanel	
	 */
	private JPanel getOptTextField(Undomanager manager) {
		
		// Optional Field Pane 
		JPanel myOpt = new JPanel();
		JPanel jPanelOpt = new JPanel();
		myOpt.setLayout(new BorderLayout());

		jPanelOpt = new JPanel();
		isRequired = false;
		// Skalierung in Y - Richtung 
		jPanelOpt.setLayout(new BoxLayout(jPanelOpt, BoxLayout.Y_AXIS));
		
		// Felder einfuegen
		jPanelOpt.add(getFilePanel(Main.fileField, manager));
		for (int i = 1; i < entryType.getOptionalFields().size(); i++){
			if(!entryType.getOptionalFields().get(i).equals("pdfField")){
				jPanelOpt.add(getTextPanel(entryType.getOptionalFields().get(i), manager));
			}
		}
		jScrollPaneOpt.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPaneOpt.getVerticalScrollBar().setUnitIncrement(12);
		jScrollPaneOpt.setViewportView(jPanelOpt);
	
		myOpt.add(getTypePanel(), BorderLayout.WEST);
		myOpt.add(jScrollPaneOpt, BorderLayout.CENTER);
		myOpt.add(getBottomPanel(), BorderLayout.PAGE_END);
		return myOpt;
	
	}
	
	private JPanel getFilePanel(String typeName, Undomanager manager) {
		//PopupMenu init
		tablePopupMenu = new JPopupMenu();
		JMenuItem tableOpenItem = new JMenuItem("Open");
		
		tableOpenItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile(rightClickedElement);
			}
		});
		tablePopupMenu.add(tableOpenItem);
		
		//Main Panel init
		JPanel textFieldPanel = new JPanel();
		JLabel typeLabel = new JLabel(typeName, JLabel.CENTER);
		typeLabel.setPreferredSize(new Dimension(150, 35));
		
		//Tabel Init
		if (isNewEntry == false){
			fileTable = new JTable (new FileTableModel(theDb.getAllFiles(obj.getId())));
		} else {
			fileTable = new JTable (new FileTableModel(new Vector<ContainerFile>()));
		}
		fileModel = (FileTableModel) fileTable.getModel();
		fileTable.getTableHeader().setReorderingAllowed(false);
		TableColumn col = fileTable.getColumnModel().getColumn(0);
	    col.setMinWidth(25);
	    col.setMaxWidth(50);
	    col.setPreferredWidth(25);
	    col = fileTable.getColumnModel().getColumn(3);
	    col.setMinWidth(35);
	    col.setMaxWidth(50);
	    col.setPreferredWidth(35);
		
	    //Listener f�r clicks on file Table
	    // Double-Click => Open Edit Fild
	    // Right-Click => Context Menu => Open 
		fileTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 && fileTable.rowAtPoint(e.getPoint()) != -1) {
					rightClickedElement = (ContainerFile) fileModel.getDataAt(fileTable.rowAtPoint(e.getPoint()));
					tablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
				else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && fileTable.rowAtPoint(e.getPoint()) != -1) {
					new FileDialog (fileModel,
							(ContainerFile) fileModel.getDataAt(fileTable.rowAtPoint(e.getPoint())),
							fileTable.rowAtPoint(e.getPoint()),
							myFrame);
				}
			}
		});
		
		//Scrollpane init
		JScrollPane scrollPane = new javax.swing.JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(fileTable);
		scrollPane.setPreferredSize(new Dimension(0, 100));
		
		//Button init
		JButton add = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/add.png")));
		add.setToolTipText(Main.myLang.getString("button.add"));
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new FileDialog(fileModel, ((Component)myFrame));
			}
		});
		JButton del = new JButton(new ImageIcon("images/delete.png"));
		del.setToolTipText(Main.myLang.getString("button.delete"));
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = fileTable.getSelectedRow();
				if (selectedRow != -1) {
					fileModel.removeData(selectedRow);
					//TODO:
				}
				fileModel.fireTableDataChanged();
				fileTable.repaint();
			}
		});
		JButton open = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/folder.png")));
		open.setToolTipText(Main.myLang.getString("button.open"));
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileTable.getSelectedRow() != -1){
					 openFile(((ContainerFile) fileModel.getDataAt(fileTable.getSelectedRow())));
				}
			}
		});
		
		//Toolbar init
		JToolBar buttonBar = new JToolBar();
		buttonBar.setOrientation(VERTICAL);
		buttonBar.setFloatable(false);
		buttonBar.add(add);
		buttonBar.add(del);
		buttonBar.add(open);
		
		//Zusammenf�gen
		textFieldPanel.setLayout(new BoxLayout(textFieldPanel, BoxLayout.LINE_AXIS));
		textFieldPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		textFieldPanel.add(typeLabel);
		textFieldPanel.add(scrollPane);
		textFieldPanel.add(buttonBar);
		textFieldPanel.validate();
		return textFieldPanel;
	}
	
	/**
	 *  Die Methode initialisiert die Text Panel 
	 *  mit bestimten Text Areas
	 *  
	 *  Besteht: aus Label, Text Area und Toolbar mit 2 Button "addObject, addCondition"
	 *  
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTextPanel(String typeName, Undomanager manager) {
			// Button init
			JButton addObject = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/table_relationship.png")));
			JButton addCondition = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/script.png")));
			addObject.setToolTipText(Main.myLang.getString("textArea.addObject"));
			addCondition.setToolTipText(Main.myLang.getString("textArea.addCondition"));
			
			// Label init
			JLabel typeLabel = new JLabel(typeName, JLabel.CENTER);
			typeLabel.setPreferredSize(new Dimension(150, 40));
			
			// Text Area in Text Panel einfuegen
			JScrollPane scrol = new JScrollPane();
			scrol = getTextScroll(typeName, manager, addObject, addCondition);
			
			// Fuer Entry Type Key keine Button erzeugen
			if (typeName == entryTypeKeyVar){
				addObject.setVisible(false);
				addCondition.setVisible(false);
			}
			
			// ButtonBar init
			JToolBar buttonBar = new JToolBar();
			buttonBar.setOrientation(HORIZONTAL);
			buttonBar.setFloatable(false);
			// Bottun on Toolbar einfuegen
			buttonBar.add(addObject);
			buttonBar.add(addCondition);
			
			// Label in Panel einfuegen & zusammenf�hren
			JPanel textFieldPanel = new JPanel();
			textFieldPanel.setLayout(new BoxLayout(textFieldPanel, BoxLayout.LINE_AXIS));
			textFieldPanel.setPreferredSize(new Dimension(0, 40));
			textFieldPanel.add(typeLabel);
			textFieldPanel.add(scrol);
			textFieldPanel.add(buttonBar);
			textFieldPanel.setBorder(BorderFactory.createRaisedBevelBorder());
			textFieldPanel.validate();
			return textFieldPanel;
	}
	
	private JPanel getTypePanel(){
		JLabel label = new JLabel(this.entryTypeString, JLabel.CENTER);
		label.setFont(new Font("Serif", Font.BOLD, 20));
		label.setUI(new VerticalLabelUI());
		JPanel panel = new JPanel();
		panel.add(label);
		panel.setPreferredSize(new Dimension (30, 150));
		return panel;
	}


	/**
	 *  Die Methode initialisiert die ScrollPane
	 *  fuer Text Areas
	 *  
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTextScroll(String typeName, Undomanager manager, JButton addObject, JButton addCondition) {
			JScrollPane jScrollPane = new JScrollPane();
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane.setViewportView(getTextArea(isRequired, typeName, manager, addObject, addCondition));
			return jScrollPane;
	}


	/**
	 *  Die Methode initialisiert Text Areas
	 *  
	 * @return gui.CTex_TextArea
	 */
	private TextArea getTextArea(boolean isRequired, final String typeName, Undomanager manager, final JButton addObject, final JButton addCondition) {
		
			final TextArea TextArea;
			ContainerAttribute e = null;
			Vector<ContainerAttribute> attV = new Vector<ContainerAttribute>();	
			if (obj != null){
				// wenn ein aus Objekt
				if (entryTypeKeyVar == typeName){
					attV.add(new ContainerAttribute(-1, -1, obj.getKey(), false, new Vector<ContainerAttribute>()));
					TextArea = new TextArea(theDb, entryTypeKeyVar, attV, this);
				
				} else {
					Vector<ContainerAttribute> v = obj.getAttributes();
					Iterator<ContainerAttribute> i = v.iterator();
					// load Attribute
					attV.clear();
					while(i.hasNext()){
						e = i.next();
						if (theDb.convTypeIdToTypeName(e.getObjectTypeId()).equals(typeName)){
							attV.add(e);
						}
					}
					//if has Value or not
					if (attV.size() != 0){
						TextArea = new TextArea(theDb, theDb.convTypeIdToTypeName(attV.get(0).getObjectTypeId()), attV, this);
					} else {
						attV.add(new ContainerAttribute(-1, theDb.convTypeNameToTypeId(typeName), "", false, new Vector<ContainerAttribute>()));
						TextArea = new TextArea(theDb, typeName, attV, this);
					}
				}
			} else {
				TextArea = new TextArea(theDb, typeName, attV, this);
			}			
			
			// action of addObject "Objecte werden Gelb markiert und Links Weis"
			addObject.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent obj) {
					TextArea.setObject();
				}
			});
			
			// action of addCondition "Condition werden Grun markiert"
			addCondition.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent con) {
					TextArea.addCondition();
				}
			});
						
			if (! isRequired) {
				OptField.add(TextArea);
		    	addTextFieldOpt(OptField.get(OptField.size() - 1), manager);
		    	this.OptAreaId++;
		    } else {
		    	ReqField.add(TextArea);
		    	addTextFieldReq(ReqField.get(ReqField.size() - 1), manager);
		    	this.ReqAreaId++;
			}
			
			//Save TextAreas for AutoGeneration Key
			if (typeName.equals("author")) {
				this.autor = TextArea;
			} else if (typeName.equals("year")) {
				this.year = TextArea;
			}
			
			return TextArea;
	}
	
	/**
	 *  Die Methode initialisiert Button Toolbar
	 *  
	 * @return java.swing.JToolBar
	 */
	private JPanel getBottomPanel(){
		JPanel toolBar = new JPanel(new FlowLayout());
		// Button save, applay, close
		JButton save = new JButton(Main.myLang.getString("button.ok"));
		JButton closeAll = new JButton(Main.myLang.getString("button.cancel"));
		// an Toolbar einfuegen
		apply = new JButton(Main.myLang.getString("button.apply"));
		toolBar.add(save);
		toolBar.add(apply);
		toolBar.add(closeAll);
		
		// Schliessen
		closeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Tab loeschen
				myTabPane.removeAll();
				tableTab.showPreviewPane();
			}
		});
		
		// Anderungen uebernehmen
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (addToDB()) {
					// Model u. Tabel update 
					((Model)tableTab.getTheModel()).setChanged(true);
					((Model)tableTab.getTheModel()).refreshModel(obj);
					((Model)tableTab.getTheModel()).fireTableDataChanged();
				}
			}
		});
		
		// Anderungen uebernehmen und Schliessen
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (addToDB()) {
					// Tab loeschen
					myTabPane.removeAll();
					tableTab.removeBottomComponentofSplitPane();
					// Model u. Tabel updait
					((Model)tableTab.getTheModel()).setChanged(true);
					((Model)tableTab.getTheModel()).refreshModel(obj);
					((Model)tableTab.getTheModel()).fireTableStructureChanged();
					((Model)tableTab.getTheModel()).fireTableDataChanged();
				}
			}
		});
		return toolBar;
	}
	
	/**
	 *  Die Methode speichert den Inhalt des Text Felder in die Datenbank
	 *  
	 */
	private boolean addToDB() {
		myFrame.setCursorWait();
		Vector<ContainerAttribute> myAtt = new Vector<ContainerAttribute>();
		String objKey = ReqField.get(0).getText();
		
		if (objKey.isEmpty() == false){
			for (int i = 1; i < ReqField.size(); i++){
				if (ReqField.get(i).getAtt().size() != 0 && ReqField.get(i).getText().isEmpty() == false){
					myAtt.addAll(ReqField.get(i).getAtt());
				}
			}
			for (int i = 0; i < OptField.size(); i++){
				if (OptField.get(i).getAtt().size() != 0 && OptField.get(i).getText().isEmpty() == false){
					myAtt.addAll(OptField.get(i).getAtt());
				}
			}
			int ret = -1;
			
			//New Entry
			if (isNewEntry == true) {
				if(theDb.getObject(objKey) == null){
					ret = theDb.setObject(new ContainerObject(-1,
										theDb.convTypeNameToTypeId(this.entryTypeString),
										objKey,
										myAtt));
				} else {
					JOptionPane.showMessageDialog(null,
							Main.myLang.getString(Main.myLang.getString("textFildTab.existingkey.message")),
						    Main.myLang.getString(Main.myLang.getString("textFildTab.existingkey.title")),
						    JOptionPane.ERROR_MESSAGE);
				}
			}
			//Edit Entry
			else {
				if (theDb.getObject(objKey) != null) {
				} else {
					JOptionPane.showMessageDialog(null,
							Main.myLang.getString(Main.myLang.getString("textFildTab.changedkey.message")),
						    Main.myLang.getString(Main.myLang.getString("textFildTab.changedkey.title")),
						    JOptionPane.WARNING_MESSAGE);
				}
				ret = theDb.setObject(new ContainerObject(obj.getId(),
						theDb.convTypeNameToTypeId(this.entryTypeString),
						objKey,
						myAtt));
			}
			
			//file into DB
			if (ret != -1){
				try {
					theDb.setAllFiles (this.fileModel.getData(), ret);
					obj = theDb.getObject(ret);
					myFrame.setCursorNormal();
					return true;
				} catch (PacketTooBigException e) {
					JOptionPane.showMessageDialog(null,
							Main.myLang.getString(Main.myLang.getString("textFildTab.FileSizeToSmall.message")),
							Main.myLang.getString(Main.myLang.getString("textFildTab.FileSizeToSmall.title")),
							JOptionPane.ERROR_MESSAGE);
					myFrame.setCursorNormal();
				}
			}
			myFrame.setCursorNormal();
			return false;
		}
		else {
			JOptionPane.showMessageDialog(null,
					Main.myLang.getString(Main.myLang.getString("textFildTab.missingKey.message")),
					Main.myLang.getString(Main.myLang.getString("textFildTab.missingKey.title")),
					JOptionPane.ERROR_MESSAGE);
			myFrame.setCursorNormal();
			return false;
		}
	}
	
	
	/**
	 *  Die Methode ueberwacht die Actionen in Requed Text Felder und speichert 
	 *  die Anderungen dummy Compound
	 *  
	 */
	private void addTextFieldReq(final JTextArea myField, final Undomanager manager){
		myField.addKeyListener(this);
		myField.getDocument().addUndoableEditListener(new UndoableEditListener() {
			
			public void undoableEditHappened(UndoableEditEvent e) {
				UndoableEdit ue = e.getEdit();
				if (!myReqCompound.isInProgress()){
					myReqCompound = new CompoundEdit();
				}
				myReqCompound.addEdit(ue);
				manager.setActivManager(myReqField, myOptField, null, myReqCompound, myOptCompound, null, true, false);
				manager.updateButtons();
			}
		});
	}
	
	/**
	 *  Die Methode ueberwacht die Actionen in Optional Text Felder und speichert 
	 *  die Anderungen dummy Compound
	 *  
	 */
	private void addTextFieldOpt(final JTextArea myField,final Undomanager manager){
		myField.addKeyListener(this);
		myField.getDocument().addUndoableEditListener(new UndoableEditListener() {
			
			public void undoableEditHappened(UndoableEditEvent e) {
				UndoableEdit ue = e.getEdit();
				if (!myOptCompound.isInProgress()){
					myOptCompound = new CompoundEdit();
				}
				myOptCompound.addEdit(ue);
				manager.setActivManager(myReqField, myOptField, null, myReqCompound, myOptCompound, null, false, false);
				manager.updateButtons();
			}
		});
	}
	
		
	/**
	 *  Die Methode schliest den dummy Compound
	 *  falls es ein "Space oder Enter" gedrueckt wird
	 *  
	 */
	public void keyTyped(KeyEvent e) {
		char keyChar = e.getKeyChar();
		if (keyChar == ' ' || keyChar == '\n') {
			// Falls Req Req Compound schliessen und an
			// Req Manager uebergeben
			if (ReqOROpt) {
				if (myReqCompound.isInProgress()) {
					myReqCompound.end();
					myReqField.addEdit(myReqCompound);
				}
			// sonst Opt Compound schliessen und an
			// Opt Manager uebergeben
			} else {
				if (myOptCompound.isInProgress()) {
					myOptCompound.end();
					myOptField.addEdit(myOptCompound);
				}
			}
		}
	}
	
	private void openFile(ContainerFile f){
		Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
          desktop = Desktop.getDesktop();
        }
	    try {
	         desktop.open(new File(theDb.getFile(f.getId()).getAbsolutePath()));
	      } catch (Exception ioe) {
	    	  try {
				desktop.open(new File(f.getLink()));
			} catch (IOException e) {
				// TODO File ist gelaescht worden
			}
	      }
	}
	
	public ContainerObject getObject(){
		return obj;
	}
	
	public JButton getapplayButton(){
		return apply;
	}
	
	/*
	 * 	@Proceder: genetrate the Entry Key in abhangigkeit Von Autor Name + Jahr
	 */
	public void generateKey(){
		
		String author = this.autor.getText().concat(" ");
		String year = this.year.getText();
		String dummy = null;
		Vector <String> Vornamen = new Vector <String>();
		Vector <String> Namen = new Vector <String>();
		int count = 0;
		
		/* 
		 * 	analysieren vom TextArea of author
		 *  sortieren Nach Name und Vorname 
		 */
		while (author.length() != 0){
			dummy = author.substring(0, author.indexOf(" "));
			if (dummy.length() > 3){
				if (count % 2 == 0){
					Vornamen.add(dummy);
				} else {
					Namen.add(dummy);
				}
				count++;
			}
			author = author.substring(author.indexOf(" ") + 1);
		}
		
		/*
		 *  generieren vom Key
		 */
		if(Namen.size() > 1) {
			this.ReqField.get(0).setText("");
			// falls mehrere Authoren, die ersten Buchstaben von der Name nehmen
			for(int i = 0; i < Namen.size(); i++){
				this.ReqField.get(0).setText(this.ReqField.get(0).getText() + Namen.get(i).charAt(0));	
			}
			this.ReqField.get(0).setText(this.ReqField.get(0).getText() + year);
		
		} else if (Namen.size() == 1) {
			// falls 1 Authoren, die Name nehmen
			this.ReqField.get(0).setText("");
			this.ReqField.get(0).setText(this.ReqField.get(0).getText() + Namen.get(0) + year);
		
		} else {
			// sonst was im Feld steht
			this.ReqField.get(0).setText("");
			this.ReqField.get(0).setText(this.ReqField.get(0).getText() + this.autor.getText() + this.year.getText());
		}
		
	}
	
	/*
	 * 	legt die Funktion auf die Tab button
	 * 	Corsor spring zu nachsten Text Area
	 */
	public void keyPressed(KeyEvent key) {
		/* mit strg + g autogenerate Key */
		// TODO:
		if(key.getKeyCode() == 71 && key.getModifiersEx() == 128){
			generateKey();
		}		
		
		/* mit Tab TextArea vorwaerts gehen */
		if (key.getKeyCode() == 9 && key.getModifiersEx() == 0){
			setNextArea();
			//closeCompoundEdit();
		}

		/* mit shift+Tab TextArea rueckwaerts gehen */
		if(key.getKeyCode() == 9 && key.getModifiersEx() == 64){
			setBackArea();
			//closeCompoundEdit();
		}
	}
	
	/*
	 * 	Setzt den Cursor auf nachsten Text Area
	 */
	private void setNextArea(){
		
		// Actives Tab Req oder Opt
		if (this.ReqOROpt){
			
			// wenn es letzte Area ist dann zum ersten
			if (this.ReqAreaId == this.ReqField.size() - 1) {
				this.ReqAreaId = 0;
				this.jScrollPaneReq.getVerticalScrollBar().setValue(0);
				
			// sonst NextArea
			} else {
				this.ReqAreaId++;
				this.jScrollPaneReq.getVerticalScrollBar().setValue(this.jScrollPaneReq.getVerticalScrollBar().getValue() + 35);
			}
			
			// Area setzen
			ReqField.get(this.ReqAreaId).setCaretPosition(0);
			ReqField.get(this.ReqAreaId).requestFocus(true);
			
			if (this.ReqField.get(this.ReqAreaId).getBackground() != Color.WHITE){
				setNextArea();
			}
			
			/* set Color of new Area */
			//OptField.get(this.OptAreaId).setFocusBackground();
			
			
		} else {
			// wenn es letzte Area ist dann zum ersten
			if (this.OptAreaId == this.OptField.size() - 1) {
				this.OptAreaId = 1;
				this.jScrollPaneOpt.getVerticalScrollBar().setValue(0);
				
			// sonst NextArea
			} else {
				this.OptAreaId++;
				this.jScrollPaneOpt.getVerticalScrollBar().setValue(this.jScrollPaneOpt.getVerticalScrollBar().getValue() + 35);
			}
			// Area setzen
			OptField.get(this.OptAreaId).setCaretPosition(0);
			OptField.get(this.OptAreaId).requestFocus(true);
			
			if (this.OptField.get(this.OptAreaId).getBackground() != Color.WHITE){
				setNextArea();
			}
			
			/* set Color of new Area */
			//OptField.get(this.OptAreaId).setFocusBackground();
		}
	}
	
	/*
	 * 	Setzt den Cursor auf vorherigen Text Area
	 */
	private void setBackArea(){
		
		// Actives Tab Req oder Opt
		if (this.ReqOROpt){
			// wenn es erste Area ist dann zum letzen
			if (this.ReqAreaId == 0) {
				this.ReqAreaId = this.ReqField.size() - 1;
				this.jScrollPaneReq.getVerticalScrollBar().setValue(this.jScrollPaneReq.getVerticalScrollBar().getValue() + this.ReqField.size()*35);
			// sonst NextArea
			} else {
				this.ReqAreaId--;
				this.jScrollPaneReq.getVerticalScrollBar().setValue(this.jScrollPaneReq.getVerticalScrollBar().getValue() - 35);
			}
			
			// Area setzen
			ReqField.get(this.ReqAreaId).setCaretPosition(0);
			ReqField.get(this.ReqAreaId).requestFocus(true);
			
			if (this.ReqField.get(this.ReqAreaId).getBackground() != Color.WHITE){
				setBackArea();
			}
			
			/* set Color of new Area */
			//OptField.get(this.OptAreaId).setFocusBackground();
			
		} else {
			// wenn es erste Area ist dann zum letzen
			if (this.OptAreaId == 1) {
				this.OptAreaId = this.OptField.size() - 1;
				this.jScrollPaneOpt.getVerticalScrollBar().setValue(this.jScrollPaneOpt.getVerticalScrollBar().getValue() + this.OptField.size()*35);
				
			// sonst NextArea
			} else {
				this.OptAreaId--;
				this.jScrollPaneOpt.getVerticalScrollBar().setValue(this.jScrollPaneOpt.getVerticalScrollBar().getValue() - 35);
			}
			// Area setzen
			OptField.get(this.OptAreaId).setCaretPosition(0);
			OptField.get(this.OptAreaId).requestFocus(true);
			
			if (this.OptField.get(this.OptAreaId).getBackground() != Color.WHITE){
				setBackArea();
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	
}

