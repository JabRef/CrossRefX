package gui;

import gui.dialog.AboutDialog;
import gui.dialog.CustomizeEntryDialog;
import gui.dialog.HelpDialog;
import gui.dialog.IncludeDialog;
import gui.dialog.NewEntryDialog;
import gui.dialog.OpenFileDialog;
import gui.dialog.PlainTextDialog;
import gui.dialog.PreferencesDialog;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import parser.FileConnector;
import parser.Parser;
import parser.ParserException;

import container.ContainerObject;

import ctex.Entity;
import ctex.Main;

public class Frame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final Frame thisFrame;
	private JPanel jContentPane       = null;
	private JToolBar toolBar          = new JToolBar();			// ToolBar
	private JPanel toolBarPanel       = new JPanel();
	private Statusbar statusBar  = new Statusbar(4); 
	private JLabel logo               = new JLabel(new ImageIcon("images/logo.jpeg"));   // Logo
		
	// Instance of CTex_NewEntryDialog, so the JFramacsac.xtxe knows which
	// Button was pressed there
	private NewEntryDialog entryDialog;
	
	private boolean itemOff = true;
	
	//	Undo Redo Manager 
	private Undomanager myUndoManager = new Undomanager();
	
	private TabPanel tableTabPanel;	
	
	//Variables for the menuBar
	private JMenuBar menuBar             = new JMenuBar();
	private JMenu fileMenu, editMenu, viewMenu, extraMenu, helpMenu;
	//This Item of File	
	
	//This Item of Edit	
	private JMenuItem editInclude, viewRefresh;
	//This Item of View
	private ButtonGroup viewItemGroup;
	private JRadioButtonMenuItem viewItem1, viewItem2;
	private JCheckBoxMenuItem togglePreviewItem;
	//This Item of Extra
	//private JMenuItem preferencesItem;
	//This Item of Help	
	private JMenuItem aboutItem, importFile;
	
	//This Item with Shortcuts
	private ShortcutB myShortcut;
	
	//	Variables for the toolBar
	private JButton newEntryButton, newDatabaseButton, openButton, saveButton, saveAsButton, pdfButton, howToButton, 
		searchButton, nextSearchButton, prevSearchButton;
	private JTextField searchField;
	
	// Contains the resulting indices from the performed search
	private Vector<Integer> searchResults = new Vector<Integer>();
	private JLabel searchResultsLabel;
    
	/**
	 * 
	 * @param indexFrame
	 * @param frameName
	 */
	public Frame(int indexFrame, String frameName, final boolean mainwindow) {
		super(frameName);
		thisFrame = this;
		tableTabPanel = new TabPanel(myUndoManager, thisFrame);
		myShortcut = new ShortcutB(myUndoManager);
		Image icon = Toolkit.getDefaultToolkit().getImage("images/icon.gif");
		setIconImage(icon);
		setPreferredSize(new Dimension(800, 600));
		setSize(new Dimension(800, 600));
		// Clicking on X will call closeFrame(boolean isMainWindow) method
		addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent arg0) {
				closeFrame(mainwindow);
			}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
		setExtendedState(JFrame.MAXIMIZED_BOTH); // Frame opens maximize
		// Menu begins here
		initMenuItems();
		menuBar.setPreferredSize(new Dimension(600, 20));
		menuBar.add(getFileMenu());
		menuBar.add(getEditMenu());
		menuBar.add(getViewMenu());
		menuBar.add(getExtraMenu());
		menuBar.add(getHelpMenu());
		
		// Exit option on File -> Exit
		myShortcut.keys.get(7).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeFrame(mainwindow);
			}
		});			
		
		// Frame settings
		setJMenuBar(menuBar);
		setContentPane(getContenPane());	
		// Turn off items that can't be used without a database
		setIconsEnabled(false);
		pack();
		setVisible(true);
	}
	
	/**
	 * 
	 * @param indexFrame
	 * @param frameName
	 * @param name
	 * @param tableScrollPane
	 */
	public Frame(int indexFrame, String frameName, String name, String compTitle, JSplitPane model){
		super (frameName + " - " + (indexFrame + 1));
		thisFrame = this;
		tableTabPanel = new TabPanel(myUndoManager, thisFrame);
		myShortcut = new ShortcutB(myUndoManager);
		// Clicking on X will call closeFrame(boolean isMainWindow) method
		addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent arg0) {
				closeFrame(false);
			}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
		setPreferredSize(new Dimension(800, 600));
		setSize(new Dimension(800, 600));
		// Menu begins here
		initMenuItems();
		menuBar.add(getFileMenu());
		menuBar.add(getEditMenu());
		menuBar.add(getViewMenu());
		menuBar.add(getExtraMenu());
		menuBar.add(getHelpMenu());
		// Frame settings
		setJMenuBar(menuBar);
		setContentPane(getContenPane());			
		pack();
		setVisible(true);
		
		//add new TabelTab in new Frame
		addTableTab(name, ((ScrollPane) model.getTopComponent()).getModel());
		
		if (model.getBottomComponent() != null){
			if (model.getBottomComponent().getClass().getName() == "gui.CTex_EditorPanel"){
				// That is a new Editor
				// object hollen
				ContainerObject obj = (((EditorPanel) model.getBottomComponent()).getDb().getObject(
						((EditorPanel) model.getBottomComponent()).getObject().getKey()));
				tableTabPanel.setBottomComponentofSplitPane(new EditorPanel(((EditorPanel) model.getBottomComponent()).getDb(), 
																				  tableTabPanel, 
																				  myUndoManager, 
																				  obj));
			} else if (model.getBottomComponent().getClass().getName() == "gui.CTex_EntryPanel") {
				// That is a new Text Area Tab
				if (((EntryPanel) model.getBottomComponent()) != null) {
					// object hollen
					ContainerObject obj;
					try {
						obj = getSelectedEntity().getDb().getObject(
								((EntryPanel) model.getBottomComponent()).getObject().getKey());
						tableTabPanel.setBottomComponentofSplitPane(new EntryPanel(getSelectedEntity().getDb(), 
								tableTabPanel, 
								myUndoManager, 
								obj,
								thisFrame));
					} catch (entityIsNull e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					((EntryPanel) tableTabPanel.getBottomComponentofSplitPane()).requestFocusInWindow();
					((EntryPanel) tableTabPanel.getBottomComponentofSplitPane()).setFocus();
					
				}
			}
		}
		
	}
	
	
	private void initMenuItems(){
		fileMenu               = new JMenu(Main.myLang.getString("menu.file"));
		fileMenu.setMnemonic(Main.myLang.getString("menu.file").charAt(0));
		editMenu               = new JMenu(Main.myLang.getString("menu.edit"));
		editMenu.setMnemonic(Main.myLang.getString("menu.edit").charAt(0));
		viewMenu               = new JMenu(Main.myLang.getString("menu.view"));
		viewMenu.setMnemonic(Main.myLang.getString("menu.view").charAt(0));
		extraMenu              = new JMenu(Main.myLang.getString("menu.extra"));
		extraMenu.setMnemonic(Main.myLang.getString("menu.extra").charAt(0));
		helpMenu               = new JMenu(Main.myLang.getString("menu.help"));
		helpMenu.setMnemonic(Main.myLang.getString("menu.help").charAt(0));
		editInclude        = new JMenuItem(Main.myLang.getString("menu.edit.editinclude"));
		//preferencesItem    = new JMenuItem(CTex_Main.myLang.getString("menu.extra.preferences"));
		aboutItem          = new JMenuItem(Main.myLang.getString("menu.help.about"));
		importFile			= new JMenuItem(Main.myLang.getString("menu.file.import"));
		
	}
	
	/**
	 * This method initializes File Menue	
	 * @return javax.swing.JToolBar	
	 */
	private JMenu getFileMenu () {
		fileMenu.setLocation(new Point(0, 0));
		fileMenu.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		myShortcut.keys.get(0).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.addFrame();
			}
		});
				
		myShortcut.keys.get(1).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Item aktivieren
	            if (itemOff){
	            	setIconsEnabled(true);
	            	itemOff = false;
	            }
				new OpenFileDialog(thisFrame, 1);
			}
		});
		myShortcut.keys.get(2).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Item aktivieren
	            if (itemOff){
	            	setIconsEnabled(true);
	            	itemOff = false;
	            }
				new OpenFileDialog(thisFrame, 2);
			}
		});
		myShortcut.keys.get(3).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Item aktivieren
	            if (itemOff){
	            	setIconsEnabled(true);
	            	itemOff = false;
	            }
				new OpenFileDialog(thisFrame, 3);
			}
		});
		myShortcut.keys.get(4).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		myShortcut.keys.get(5).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		myShortcut.keys.get(7).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		importFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setCursorWait();
				Entity entity;
				try {
					entity = getSelectedEntity();
					File f;
					JFileChooser openFile = new JFileChooser();
					openFile.setFileFilter(new FilenameFilter("CrossTeX (*.xtx) and BibTeX (*.bib) files", new String[] {"xtx", "bib"}));
					openFile.setAcceptAllFileFilterUsed(false);
					int returnVal = openFile.showOpenDialog(importFile);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
			            f = openFile.getSelectedFile();
						String filePath = f.getPath();
						FileConnector theFile = new FileConnector(filePath);
						theFile.readFileContent();
						Parser theParser = new Parser(theFile.getFileContent());
						try {
							theParser.setFrame(thisFrame);
							theParser.proof(entity.getDb(), false);
							theParser.finalize();		
						} catch (ParserException exc){
							JOptionPane.showMessageDialog(thisFrame, e.toString(), "Parser", JOptionPane.OK_OPTION);
						}
						
						// Model u. Table update
						entity.getTableModel().setChanged(true);
						//tableTab.setTabTitleMarked();
						entity.getTableModel().refreshModel();
						entity.getTableModel().fireTableStructureChanged();
						entity.getTableModel().fireTableDataChanged();
					}
				} catch (entityIsNull e1) {
				}
				
				setCursorNormal();
			}
		});
		
		fileMenu.add(myShortcut.keys.get(0));
		fileMenu.add(myShortcut.keys.get(14));
		fileMenu.add(myShortcut.keys.get(1));
		fileMenu.add(myShortcut.keys.get(2));
		fileMenu.add(myShortcut.keys.get(3));
		fileMenu.addSeparator();
		fileMenu.add(myShortcut.keys.get(4));
		fileMenu.add(myShortcut.keys.get(5));
		fileMenu.add(importFile);
		fileMenu.addSeparator();
		fileMenu.add(myShortcut.keys.get(7));
		return fileMenu;
	}
	
	private JMenu getEditMenu() {
		editMenu.setLocation(new Point(0, 0));
		editMenu.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		editMenu.add(myShortcut.getButtons().get(8));
		editMenu.add(myShortcut.getButtons().get(9));
		editMenu.addSeparator();
		// Opens JDialog called CTex_NewEntryDialog
		myShortcut.keys.get(10).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newEntry();
			}
		});
		editMenu.add(myShortcut.keys.get(10));
		editMenu.add(myShortcut.keys.get(6));
		editMenu.addSeparator();
		
		myShortcut.keys.get(6).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					entryDialog = new NewEntryDialog(getSelectedEntity().getDb());
				} catch (entityIsNull e1) {

				}
				
				if (entryDialog.getPressedButton() != null) {
					try {
						int obj = -1;
						PlainTextDialog plainEditor = new PlainTextDialog(entryDialog.getPressedButton(), getSelectedEntity().getDb());
						obj = plainEditor.getObj();
						
						if(obj >= 0){
							
							getSelectedEntity().getTableModel().setChanged(true);
							getSelectedEntity().getTableModel().refreshModel(getSelectedEntity().getDb().getObject(obj));
							getSelectedEntity().getTableModel().fireTableStructureChanged();
							getSelectedEntity().getTableModel().fireTableDataChanged();
							
							/*
							 * Bottom Component init
							 */
							// TabOffnen
							tableTabPanel.setBottomComponentofSplitPane(new EntryPanel(getSelectedEntity().getDb(), 
																								tableTabPanel, 
																								myUndoManager, 
																								getSelectedEntity().getDb().getObject(obj),
																								thisFrame));
							((EntryPanel) tableTabPanel.getBottomComponentofSplitPane()).requestFocusInWindow();
							((EntryPanel) tableTabPanel.getBottomComponentofSplitPane()).setFocus();
						}
					
					} catch (entityIsNull e1) {
						
					}
				}
			}
		});
		
		// editEntryItem is number 11
		myShortcut.keys.get(11).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (tableTabPanel.getTable().getSelectedRow() != -1) {
						int index = tableTabPanel.getTable().getRowSorter().convertRowIndexToModel(tableTabPanel.getTable().getSelectedRow());
						tableTabPanel.setBottomComponentofSplitPane(new EntryPanel(
								getSelectedEntity().getDb(),
								tableTabPanel.getTab(),
								myUndoManager,
								getSelectedEntity().getTableModel().getTableContent().get(index),
								thisFrame
								)
						);
						((EntryPanel) tableTabPanel.getBottomComponentofSplitPane()).requestFocusInWindow();
						((EntryPanel) tableTabPanel.getBottomComponentofSplitPane()).setFocus();
					}
				} catch (entityIsNull e1) {
					
				}
				
			}
		});
		editMenu.add(myShortcut.keys.get(11));
		
		// editWithEditorItem is number 12
		myShortcut.keys.get(12).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tableTabPanel.getTable().getSelectedRow() != -1) {
					int index = tableTabPanel.getTable().getRowSorter().convertRowIndexToModel(tableTabPanel.getTable().getSelectedRow());
					try {
						ContainerObject selectedObject = getSelectedEntity().getTableModel().getTableContent().get(index);
						tableTabPanel.setBottomComponentofSplitPane(new EditorPanel(getSelectedEntity().getDb(),
																	  tableTabPanel.getTab(),
																	  myUndoManager,
																	  selectedObject));
						((EditorPanel) tableTabPanel.getBottomComponentofSplitPane()).requestFocusInWindow();
						((EditorPanel) tableTabPanel.getBottomComponentofSplitPane()).setFocus();
						
						
						
					} catch (entityIsNull e1) {
						
					}
				}
			}
		});
		
		editMenu.add(myShortcut.keys.get(12));
		editMenu.addSeparator();
		// ActionListener to cite a selected key
		myShortcut.keys.get(14).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (getSelectedEntity() != null) {
						if (tableTabPanel.getTable().getSelectedRow() != -1) {
							int index = tableTabPanel.getTable().getRowSorter().convertRowIndexToModel(tableTabPanel.getTable().getSelectedRow());
							String key = getSelectedEntity().getTableModel().getTableContent().get(index).getKey();
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\\cite{" + key + "}"), null);
						}
					}
					
				} catch (entityIsNull e1){

				}
			}
		});
		// cite key is number 14
		editMenu.add(myShortcut.keys.get(14));
		// ActionListener just to copy a selected key
		// Info: Same as above, but just copies the key to clipboard (without \cite{})
		myShortcut.keys.get(15).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (getSelectedEntity() != null) {
						if (tableTabPanel.getTable().getSelectedRow() != -1) {
							int index = tableTabPanel.getTable().getRowSorter().convertRowIndexToModel(tableTabPanel.getTable().getSelectedRow());
							String key = getSelectedEntity().getTableModel().getTableContent().get(index).getKey();
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(key), null);
						}
					}
					
				} catch (entityIsNull e1){

				}
			}
		});
		// copy key is number 15
		editMenu.add(myShortcut.keys.get(15));
		editMenu.addSeparator();
		
		// Opens JDialog called CTex_CustomizeEntryDialog
		// customizeEntryItem is number 13
		myShortcut.keys.get(13).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new CustomizeEntryDialog(getSelectedEntity().getDb());
					getSelectedEntity().getTableModel().refreshTypes();
				} catch (entityIsNull e1) {

				}
			}
		});
		editMenu.add(myShortcut.keys.get(13));
		editInclude.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new IncludeDialog(thisFrame, getSelectedEntity().getDb());
				} catch (entityIsNull e1) {

				}
			}
		});
		editMenu.add(editInclude);
			
		return editMenu;
	}
	
	/* Menu to t between different views of the JTable */
	private JMenu getViewMenu() {
		viewMenu.setLocation(new Point(0, 0));
		viewMenu.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		// Opens JDialog called CTex_NewEntryDialog

		viewRefresh = new JMenuItem(Main.myLang.getString("menu.view.refresh")); 
		viewRefresh.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		//TODO:
	    		try {
					getSelectedEntity().getDb().getTablePrifix();
				} catch (entityIsNull e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					getSelectedEntity().getTableModel().refreshModel();
				} catch (entityIsNull e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	    });
		viewMenu.add(viewRefresh);
		
	    viewItemGroup = new ButtonGroup();
	    viewItem1 = new JRadioButtonMenuItem(Main.myLang.getString("menu.view.viewitem1"), true);
	    viewItem1.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		try {
					getSelectedEntity().getTableModel().setView(true);
				} catch (entityIsNull e1) {

				}
	    	}
	    });
	    viewMenu.add(viewItem1);
	    viewItemGroup.add(viewItem1);
	    viewItem2 = new JRadioButtonMenuItem(Main.myLang.getString("menu.view.viewitem2"));
	    viewItem2.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		try {
					getSelectedEntity().getTableModel().setView(false);
				} catch (entityIsNull e1) {

				}
	    		
	    	}
	    });
	    viewMenu.add(viewItem2);
	    viewItemGroup.add(viewItem2);
		viewMenu.addSeparator();
		togglePreviewItem = new JCheckBoxMenuItem(Main.myLang.getString("menu.view.togglepreview"));
		togglePreviewItem.setSelected(true);
		togglePreviewItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					boolean isShowPreview = getSelectedEntity().getTableModel().getShowPreview();
					togglePreviewItem.setSelected(!isShowPreview);
					getSelectedEntity().getTableModel().setShowPreview(!isShowPreview);
				} catch (entityIsNull e1) {
					e1.printStackTrace();
				}
			}
		});
		viewMenu.add(togglePreviewItem);
		
		return viewMenu;
	}
	
	/**
	 * This method initializes Extra Menu	
	 * @return javax.swing.JToolBar	
	 * @author SamyStyle
	 */
	private JMenu getExtraMenu() {
		extraMenu.setLocation(new Point(0, 0));
		extraMenu.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		extraMenu.add(myShortcut.keys.get(18));
		extraMenu.add(myShortcut.keys.get(19));
		// searchItem is number 18
		myShortcut.keys.get(18).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchField.requestFocus();
				searchField.selectAll();
			}
		});
		// preferenceItem is number 19
		myShortcut.keys.get(19).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PreferencesDialog(myShortcut, tableTabPanel);
			}
		});
	
		return extraMenu;
	}
	
	
	/**
	 * This method initializes Help Menu	
	 * @return javax.swing.JToolBar	
	 */
	private JMenu getHelpMenu() {
		helpMenu.setLocation(new Point(0, 0));
		helpMenu.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		//helpItem is number 16
		myShortcut.keys.get(16).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HelpDialog();
			}
		});
		helpMenu.add(myShortcut.keys.get(16));
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AboutDialog();
			}
		});
		helpMenu.add(aboutItem);
		
		return helpMenu;
	}
	
	/**
	 * 
	 * @return javax.swing.JToolBar	
	 */
	
	private JToolBar getToolBar() {
	    newEntryButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/report_add.png")));
		pdfButton      = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/page_white_acrobat.png")));
		newDatabaseButton  = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/page_white_add.png")));
		openButton     = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/folder.png")));
	    saveButton     = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/disk.png")));
	    saveAsButton   = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/disk_multiple.png")));
	    searchButton   = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/find.png")));
	    howToButton    = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/help.png")));
	    searchField = new JTextField("");
		searchResultsLabel = new JLabel(Main.myLang.getString("menu.bar.searchresults") + "0");
	    prevSearchButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/resultset_previous.png")));
		nextSearchButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/resultset_next.png")));
	    newEntryButton.setToolTipText(Main.myLang.getString("menu.edit.newentry") +  " [" + myShortcut.keys.get(1).getAccelerator().toString().replaceAll("pressed", "+") + "]");
	    pdfButton.setToolTipText(Main.myLang.getString("menu.edit.importfrompdf") +  " [" + myShortcut.keys.get(6).getAccelerator().toString().replaceAll("pressed", "+") + "]");
		newDatabaseButton.setToolTipText(Main.myLang.getString("menu.file.newdb") +  " [" + myShortcut.keys.get(1).getAccelerator().toString().replaceAll("pressed", "+") + "]");
		openButton.setToolTipText(Main.myLang.getString("menu.file.open") +  " [" + myShortcut.keys.get(2).getAccelerator().toString().replaceAll("pressed", "+") + "]");
		saveButton.setToolTipText(Main.myLang.getString("menu.file.savedb") +  " [" + myShortcut.keys.get(4).getAccelerator().toString().replaceAll("pressed", "+") + "]");
		saveAsButton.setToolTipText(Main.myLang.getString("menu.file.savedbas") +  " [" + myShortcut.keys.get(5).getAccelerator().toString().replaceAll("pressed", "+") + "]");
		searchButton.setToolTipText(Main.myLang.getString("menu.extra.search") +  " [" + myShortcut.keys.get(18).getAccelerator().toString().replaceAll("pressed", "+") + "]");
		howToButton.setToolTipText(Main.myLang.getString("menu.help.howto") +  " [" + myShortcut.keys.get(16).getAccelerator().toString().replaceAll("pressed", "+") + "]");
		
		newEntryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!itemOff) {
					newEntry();
				}
			}
		});
		
		pdfButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActionListener[] actions = myShortcut.keys.get(6).getActionListeners();
				if (actions[0] != null) {
					actions[0].actionPerformed(e);
				}
			}
		});
		
		newDatabaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Item aktivieren
	            if (itemOff){
	            	setIconsEnabled(true);
	            	itemOff = false;
	            }
				new OpenFileDialog(thisFrame, 1);
			}
		});
		
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser openFile = new JFileChooser();
				openFile.setFileFilter(new FilenameFilter("CrossTeX (*.xtx) and BibTeX (*.bib) files", new String[] {"xtx", "bib"}));
				int returnVal = openFile.showOpenDialog(myShortcut.getButtons().get(1));
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = openFile.getSelectedFile();
		            Main.addEntityFileStandart(file.getPath(), thisFrame, "UTF-8");
		            try {
		            	// Setting the path of the opened file
		            	if (file.getPath().endsWith(".xtx")) {
		            		getSelectedEntity().getTableModel().setSavePath(file.getPath());
		            	}
		            } catch (entityIsNull e1) {
		            	
		            }
		            // Item aktivieren
		            if (itemOff){
		            	setIconsEnabled(true);
		            	itemOff = false;
		            }
				}
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		
		saveAsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		
		howToButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HelpDialog();
			}
		});
		
		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchResults.clear();
				searchResultsLabel.setText(Main.myLang.getString("menu.bar.searchresults") + "0");
				try {
					getSelectedEntity().getTableModel().search(searchField.getText());
					// Getting the search results
					searchResults = getSelectedEntity().getTableModel().getSearchResults();
					
					searchResultsLabel.setText(Main.myLang.getString("menu.bar.searchresults") + searchResults.size());
					
					if (searchResults.size() > 0) {
						int newIndex = tableTabPanel.getTable().getRowSorter().convertRowIndexToView(searchResults.get(0));
						ListSelectionModel selection = tableTabPanel.getTable().getSelectionModel();
						selection.setSelectionInterval(newIndex, newIndex);
						tableTabPanel.getTable().setSelectionModel(selection);
						tableTabPanel.getTable().scrollRectToVisible(tableTabPanel.getTable().getCellRect(newIndex, 0, false));
					}
				} catch (entityIsNull e1) {
				}
					
			}
		});
		
		
		
		// Exact copy of searchField
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchResults.clear();
				searchResultsLabel.setText(Main.myLang.getString("menu.bar.searchresults") + "0");
				try {
					getSelectedEntity().getTableModel().search(searchField.getText());
					// Getting the search results
					searchResults = getSelectedEntity().getTableModel().getSearchResults();
					searchResultsLabel.setText(Main.myLang.getString("menu.bar.searchresults") + searchResults.size());
					
					if (searchResults.size() > 0) {
						int newIndex = tableTabPanel.getTable().getRowSorter().convertRowIndexToView(searchResults.get(0));
						ListSelectionModel selection = tableTabPanel.getTable().getSelectionModel();
						selection.setSelectionInterval(newIndex, newIndex);
						tableTabPanel.getTable().setSelectionModel(selection);
						tableTabPanel.getTable().scrollRectToVisible(tableTabPanel.getTable().getCellRect(newIndex, 0, false));
					}
				} catch (entityIsNull e1) {

				}
					
			}
		});
		
		prevSearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					
				try {
					int selectedRow = -1;
					searchResults = getSelectedEntity().getTableModel().getSearchResults();
					if (tableTabPanel.getTable().getSelectedRow() != -1) {
						selectedRow = tableTabPanel.getTable().getRowSorter().convertRowIndexToModel(tableTabPanel.getTable().getSelectedRow());
						if (searchResults.size() > 0) {
							if (selectedRow > searchResults.firstElement()) {
								int newIndex =  tableTabPanel.getTable().getRowSorter().convertRowIndexToView(getSelectedEntity().getTableModel().getPreviousIndex(selectedRow));
								if (newIndex != -1) {
									ListSelectionModel selection = tableTabPanel.getTable().getSelectionModel();
									selection.setSelectionInterval(newIndex, newIndex);
									tableTabPanel.getTable().setSelectionModel(selection);
									tableTabPanel.getTable().scrollRectToVisible(tableTabPanel.getTable().getCellRect(newIndex, 0, false));
								}
							}
						}
					}
				} catch (entityIsNull e1) {

				}
					
				
			}
		});
		
		nextSearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int selectedRow = -1;
					searchResults = getSelectedEntity().getTableModel().getSearchResults();
					if (tableTabPanel.getTable().getSelectedRow() != -1) {
						selectedRow = tableTabPanel.getTable().getRowSorter().convertRowIndexToModel(tableTabPanel.getTable().getSelectedRow());
						if (searchResults.size() > 0) {
							if (selectedRow < searchResults.lastElement()) {
								int newIndex =  tableTabPanel.getTable().getRowSorter().convertRowIndexToView(getSelectedEntity().getTableModel().getNextIndex(selectedRow));
								if (newIndex != -1) {
									ListSelectionModel selection = tableTabPanel.getTable().getSelectionModel();
									selection.setSelectionInterval(newIndex, newIndex);
									tableTabPanel.getTable().setSelectionModel(selection);
									tableTabPanel.getTable().scrollRectToVisible(tableTabPanel.getTable().getCellRect(newIndex, 0, false));
								}
							}
						}
					}
				} catch (entityIsNull e1) {

				}
			}
		});
		toolBar.add(newEntryButton);
		toolBar.add(pdfButton);
		toolBar.add(newDatabaseButton);
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.add(saveAsButton);
		toolBar.add(myUndoManager.getUndoButton());
		toolBar.add(myUndoManager.getRedoButton());
		toolBar.add(howToButton);
		toolBar.add(searchField);
		toolBar.add(searchButton);
		toolBar.add(prevSearchButton);
		toolBar.add(nextSearchButton);
		toolBar.add(searchResultsLabel);
		
		toolBar.setFloatable(false);
		return toolBar;
	}

	/**
	 * This method initializes jContentPane	
	 * @return javax.swing.JPanel
	 */
	private JPanel getContenPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getToolbarPane(), BorderLayout.NORTH);
			jContentPane.add(tableTabPanel, BorderLayout.CENTER);
			jContentPane.add(getStatusBar(), BorderLayout.SOUTH); 
		}
		return jContentPane;
	}
	
	private JPanel getStatusBar(){ 
		statusBar.setText(0, "Status Bar"); 
		return statusBar; 
	}
	
	/**
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getToolbarPane(){
		toolBarPanel.setPreferredSize(new Dimension(790, 30));
		toolBarPanel.setLayout(new BorderLayout());
		toolBarPanel.add(getToolBar(), BorderLayout.CENTER);
		toolBarPanel.add(logo, BorderLayout.EAST);
		return toolBarPanel;
	}

	
	/**
	 * 
	 * @param name
	 * @param model
	 */
	public void addTableTab(String name, Model model) {
		tableTabPanel.addTable(name, model);
		tableTabPanel.setVisible(true);
		tableTabPanel.setSelectedIndex(tableTabPanel.getTabCount() - 1);
	}
	
	// Method is called by saveButton and by Shortcut #4
	private void save() {
		try {
			String savePath = getSelectedEntity().getTableModel().getSavePath();
			if (savePath.compareTo("") == 0) {
				saveAs();
			} else {
				getSelectedEntity().exportToFile("xtx", savePath);
				getSelectedEntity().getTableModel().setChanged(false);
				tableTabPanel.setTabTitleUnmarked();
			}
		} catch (entityIsNull e) {
			
		}
		// Item aktivieren
		if (itemOff){
        	setIconsEnabled(true);
			itemOff = false;
		}
	}
	
	// Method is called by saveAsButton and by Shortcut #5
	private void saveAs() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FilenameFilter("BibTeX (*.bib)", "bib"));
		fc.setFileFilter(new FilenameFilter("CrossTeX (*.xtx)", "xtx"));
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showSaveDialog(Frame.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {  
			try {
				String extensions = ((FilenameFilter)fc.getFileFilter()).getExtensions();
				getSelectedEntity().exportToFile(extensions, fc.getSelectedFile().getAbsolutePath());
				getSelectedEntity().getTableModel().setSavePath(fc.getSelectedFile().getAbsolutePath());
				getSelectedEntity().getTableModel().setChanged(false);
				if (tableTabPanel.getSelectedIndex() != -1) {
					String fileName = fc.getSelectedFile().getAbsolutePath();
					fileName = fileName.substring(fileName.lastIndexOf(System.getProperty("file.separator")) + 1);
					if (!fileName.endsWith(extensions)) {
						fileName = fileName + "." + extensions;
					}
					System.out.print(fileName);
					tableTabPanel.setTitleAt(tableTabPanel.getSelectedIndex(), fileName);
				}
				tableTabPanel.setTabTitleUnmarked();
			} catch (entityIsNull e1) {
			}
		}
		// Item aktivieren
		if (itemOff){
        	setIconsEnabled(true);
			itemOff = false;
		}
	}
	
	// Method is called by addEntryButton and by Shortcut #10
	private void newEntry() {
		try {
			entryDialog = new NewEntryDialog(getSelectedEntity().getDb());
		} catch (entityIsNull e1) {

		}
		if (entryDialog.getPressedButton() != null) {
			try {
				tableTabPanel.setBottomComponentofSplitPane(
						new EntryPanel(
								getSelectedEntity().getDb(), tableTabPanel,
								entryDialog.getPressedButton(),
								myUndoManager,
								thisFrame
						)
						
				);
				((EntryPanel) tableTabPanel.getBottomComponentofSplitPane()).requestFocusInWindow();
				((EntryPanel) tableTabPanel.getBottomComponentofSplitPane()).setFocus();
			} catch (entityIsNull e1) {
				
			}
		}
	}	
	
	/*
	 * 	Enables or disables all needed icons/menu entries
	 */
	public void setIconsEnabled(boolean enabled){
		saveButton.setEnabled(enabled);
		myShortcut.keys.get(4).setEnabled(enabled);
		saveAsButton.setEnabled(enabled);
		myShortcut.keys.get(5).setEnabled(enabled);
		pdfButton.setEnabled(enabled);
		myShortcut.keys.get(6).setEnabled(enabled);
		myShortcut.keys.get(10).setEnabled(enabled);
		newEntryButton.setEnabled(enabled);
		myShortcut.keys.get(11).setEnabled(enabled);
		myShortcut.keys.get(12).setEnabled(enabled);
		myShortcut.keys.get(14).setEnabled(enabled);
		myShortcut.keys.get(15).setEnabled(enabled);
		myShortcut.keys.get(13).setEnabled(enabled);
		editInclude.setEnabled(enabled);
		viewRefresh.setEnabled(enabled);
		viewItem1.setEnabled(enabled);
		viewItem2.setEnabled(enabled);
		importFile.setEnabled(enabled);
		togglePreviewItem.setEnabled(enabled);
	}	
	
	/**
	 * Destruktor
	 */
	@Override
	protected void finalize() throws Throwable
	{
		System.out.print("test");
		super.finalize(); //not necessary if extending Object.
	}
	
	// Method to prevent the frame from closing when there are unsaved changes
	public void closeFrame(final boolean isMainWindow) {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		if (isMainWindow) {
			System.exit(0);
		} else {
			dispose();	
		}
	}
	
	/**
	 * @author SamyStyle
	 */	
	public void setCursorWait() { this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); }
	
	public void setCursorNormal() { this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); }
	
	private Entity getSelectedEntity() throws entityIsNull {
		if (((Model)tableTabPanel.getTheModel()) != null) {
			Entity theEntity = ((Model)tableTabPanel.getTheModel()).getEntity();
			if (theEntity == null){
				throw new entityIsNull();
			}
			return theEntity;
		} else {
			throw new entityIsNull();
		}
	}
	
	public void setItemOff (){
		itemOff = true;
	}
	
	public boolean ItemOff(){
		return itemOff;
	}
	
	// Exception for getSelectedEntity() method
	private class entityIsNull extends Exception{
		private static final long serialVersionUID = 1L;
		
		public entityIsNull(){
			super();
		}
	}
	
	public void setBarText(int num, String text) {
		this.statusBar.setText(num, text);
	}
}
