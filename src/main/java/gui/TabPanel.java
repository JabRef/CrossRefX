package gui;

import gui.dialog.NewEntryDialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import container.ContainerObject;

import ctex.Main;


public class TabPanel extends JTabbedPane {
	
	private static final long serialVersionUID = 1L;
	
	// Popupmenu
	private JPanel tabLablePanel = null;
	private JMenuItem MenuItem = null;
	private JLabel tabLable = null;	
	
	// dragging Tab
	private boolean dragging = false;
	private Image tabImage = null;
	private Point MouseLocation = null;
	private int draggedTabIndex = 0;

	private JTable dataTable = null;
	private Model dataModel = null;
	private ScrollPane tableScrollPane = null;
	
    private JPopupMenu tablePopupMenu = new JPopupMenu();
    private JMenuItem tableGoToObjectItem = new JMenuItem(Main.myLang.getString("tabpanel.table.gotoobject"));
    private JMenuItem tableNewEntryItem = new JMenuItem(Main.myLang.getString("menu.edit.newentry"));
    private JMenuItem tableEditEntryItem = new ShortcutM(Main.myLang.getString("menu.edit.editentry"));
    private JMenuItem tableEditWithEditorItem  = new ShortcutM(Main.myLang.getString("menu.edit.editwitheditor")); // id 12
    private JMenuItem tableMailtoItem  = new ShortcutM(Main.myLang.getString("menu.edit.mail")); // id 12
    private JMenuItem tableCiteKeyItem = new JMenuItem(Main.myLang.getString("menu.edit.citekey"));
    private JMenuItem tableCopyKeyItem = new JMenuItem(Main.myLang.getString("menu.edit.copykey"));
    private JMenuItem tableDeleteItem = new JMenuItem(Main.myLang.getString("tabpanel.table.deleteobject"));
    
    private Undomanager Undomanager;
    private Frame myFrame;
    private TabPanel myTab;
    
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	TabPanel(final Undomanager manager, Frame frame) {
		myTab = this;
		this.Undomanager = manager;
		this.myFrame = frame;
		
		/* Tab schliesen */
		this.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove(getSelectedIndex());
				// restliche Tabs updaiten
				int existTab = getSelectedIndex();
				// if Tabs exist gibt
				if (existTab >= 0){
					UpdaitTab();
					final JSplitPane split = (JSplitPane) myTab.getSelectedComponent();
					final ScrollPane scrol = (ScrollPane) split.getComponent(1);
					// DataTabel und Model auf dem tab setzen
					dataTable = scrol.getTable();
					dataModel = scrol.getModel();
				} else {
					// falls keine Tabs Button deaktivieren
					Undomanager.resetButton();
					myFrame.setIconsEnabled(false);
					myFrame.setItemOff();
				}
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		tableGoToObjectItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Step 1: Getting the key that is in the marked cell
				String keyOfObject = null;
				if (dataTable.getSelectedRow() != -1 && dataTable.getSelectedColumn() != -1) {
					ContainerObject selectedObject = dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow()));
					String selectedColumnName = dataTable.getColumnName(dataTable.getSelectedColumn());
					for (int i = 0; i < selectedObject.getAttributes().size(); i++) {
						if ((dataModel.getEntity().getDb().convTypeIdToTypeName(selectedObject.getAttributes().get(i).getObjectTypeId()).compareTo(selectedColumnName) == 0)
								&& selectedObject.getAttributes().get(i).getLink()) {
							keyOfObject = selectedObject.getAttributes().get(i).getValue();
						}
					}
					
					// Step 2: Searching for the object that belongs to the found key
					boolean objectFound = false;
					if (keyOfObject != null) {
						for (int i = 0; i < dataModel.getTableContent().size(); i++) {
							if (dataModel.getTableContent().get(i).getKey().compareTo(keyOfObject) == 0) {
								objectFound = true;
								int newIndex = dataTable.getRowSorter().convertRowIndexToView(i);
								ListSelectionModel selection = dataTable.getSelectionModel();
								selection.setSelectionInterval(newIndex, newIndex);
								dataTable.setSelectionModel(selection);
								dataTable.scrollRectToVisible(dataTable.getCellRect(newIndex, 0, false));
							}
						}
						if (!objectFound) {
							JOptionPane.showMessageDialog(null, Main.myLang.getString("tabpanel.table.objectdoesntexist.message"), Main.myLang.getString("tabpanel.table.objectdoesntexist.title"), JOptionPane.OK_OPTION);
						}
						
					}
				}
				
				
			}
		});
		tablePopupMenu.add(tableGoToObjectItem);
		tablePopupMenu.addSeparator();
		tableNewEntryItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewEntryDialog entryDialog = new NewEntryDialog(dataModel.getEntity().getDb());
				if (entryDialog.getPressedButton() != null) {
					setBottomComponentofSplitPane(
						new EntryPanel(
							dataModel.getEntity().getDb(), getTab(),
							entryDialog.getPressedButton(),
							Undomanager,
							myFrame
						)
					);
					((EntryPanel) getBottomComponentofSplitPane()).requestFocusInWindow();
					((EntryPanel) getBottomComponentofSplitPane()).setFocus();
				}
			}
		});
		tablePopupMenu.add(tableNewEntryItem);
		tableEditEntryItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dataTable.getSelectedRow() != -1) {
					ContainerObject selectedObject = dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow()));
					setBottomComponentofSplitPane(
							new EntryPanel(
									dataModel.getEntity().getDb(),
									getTab(),
									Undomanager,
									selectedObject,
									myFrame
							)
					);
				}
			}
		});
		tablePopupMenu.add(tableEditEntryItem);
		tableEditWithEditorItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dataTable.getSelectedRow() != -1) {
					ContainerObject selectedObject = dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow()));
					// Open selected Object in TextEditor
					setBottomComponentofSplitPane(new EditorPanel(dataModel.getEntity().getDb(),
																	  getTab(),
																	  Undomanager,
																	  selectedObject));
					((EditorPanel)((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent()).setFocus();
					
				}
			}
		});
		
		tablePopupMenu.add(tableEditWithEditorItem);
		
		tableMailtoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dataTable.getSelectedRow() != -1) {
					ContainerObject selectedObject = dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow()));
					new Mail(dataModel.getEntity().getDb(), selectedObject);
				}
			}
		});
		tablePopupMenu.add(tableMailtoItem);
		
		// Copying "\cite{<key>}" to clipboard
		tableCiteKeyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dataTable.getSelectedRow() != -1) {
					int index = dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow());
					String key = dataModel.getTableContent().get(index).getKey();
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\\cite{" + key + "}"), null);
				}
			}			
		});
		tablePopupMenu.add(tableCiteKeyItem);
		// Copying "<key>" to clipboard
		tableCopyKeyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dataTable.getSelectedRow() != -1) {
					int index = dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow());
					String key = dataModel.getTableContent().get(index).getKey();
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(key), null);
				}
			}				
		});
		tablePopupMenu.add(tableCopyKeyItem);		
		tablePopupMenu.addSeparator();
		tableDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dataTable.getSelectedRow() != -1) {
					// Deleting the object that is selected now
					// At the moment deleting in database & model separately (faster)
					// Copy of this code is used on KeyListener on myTable
					dataModel.getEntity().getDb().deleteObject(dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow())));
					dataModel.getTableContent().remove(dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow())));
					dataModel.setChanged(true);
					dataModel.fireTableDataChanged();
				}
			}
		});
		tablePopupMenu.add(tableDeleteItem);		
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(!dragging) {
					// tabsIndex holen auf der die maus zeigt
					int tabNumber = getUI().tabForCoordinate(TabPanel.this, e.getX(), e.getY());
					// wenn Tab existiert
					if(tabNumber >= 0) {
						draggedTabIndex = tabNumber;  // Tabindex speichern
						Rectangle bounds = getUI().getTabBounds(TabPanel.this, tabNumber);
						// speichere Tabepane in Buffer
						Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics totalGraphics = totalImage.getGraphics();
						totalGraphics.setClip(bounds);
						setDoubleBuffered(false);
						paintComponent(totalGraphics);
						tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
						Graphics graphics = tabImage.getGraphics();
						graphics.drawImage(totalImage, 0, 0, bounds.width, bounds.height, bounds.x, bounds.y, 
								       bounds.x + bounds.width, bounds.y+bounds.height, TabPanel.this);
						dragging = true;
						repaint();
					}
				} else {
					MouseLocation = e.getPoint();
					// Need to repaint
					repaint();
				}
				super.mouseDragged(e);
			}
		});

		addMouseListener(new MouseAdapter() {
			// Mouse halten und bewegen
			public void mouseReleased(MouseEvent e) {
				if(dragging) {
					int tabNumber = getUI().tabForCoordinate(TabPanel.this, e.getX(), e.getY());
						if(tabNumber >= 0) {
							// Tab schieben
							Component comp = getComponentAt(draggedTabIndex);
							Component Tabcomp = getTabComponentAt(draggedTabIndex);
							removeTabAt(draggedTabIndex);
							insertTab(null, null, comp, null, tabNumber);
							setTabComponentAt(tabNumber, Tabcomp);
							setSelectedIndex(tabNumber);
							
						} else {
							
							// Tab rausziehen
							JPanel LabelPanel = new JPanel();
							LabelPanel = (JPanel) getTabComponentAt(getSelectedIndex());
							JSplitPane myTablepane = new JSplitPane();
							myTablepane = (JSplitPane) getSelectedComponent();
							
							// das ist ein CTex_TextFieldTab
							if (myTablepane.getBottomComponent() != null 
									&& myTablepane.getBottomComponent().getClass().getName() != "gui.CTex_PreviewPane"){
								if (!EditorORTab(myTablepane)) {
									EntryPanel tab;
									tab = (EntryPanel) ((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent();
									// Falls Tab nicht leer
									if (tab != null){
										// save all components in DB
										tab.getapplayButton().doClick();
										// new Frame with all component of SelectetTab
										Main.addFrame(LabelPanel.getName(), LabelPanel.getName() , myTablepane);
									}
									remove(getSelectedIndex());
									// restlichen Tabs Updaiten
									UpdaitTab();
								// ist ein CTex_EditorPane
								} else {
									EditorPanel tab;
									tab = (EditorPanel) ((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent();
									// Falls Tab nicht leer
									if (tab != null){
										Main.addFrame(LabelPanel.getName(), LabelPanel.getName() , myTablepane);
									}
									remove(getSelectedIndex());
									// restlichen Tabs Updaiten
									UpdaitTab();
								}
							} else {
								Main.addFrame(LabelPanel.getName(), LabelPanel.getName() , myTablepane);
								remove(getSelectedIndex());
								
								
								// restlichen Tabs Updaiten
								UpdaitTab();
								
								if ((JSplitPane) myTab.getSelectedComponent() != null){
									final JSplitPane split = (JSplitPane) myTab.getSelectedComponent();
									final ScrollPane scrol = (ScrollPane) split.getComponent(1);
									// DataTabel und Model auf dem tab setzen
									dataTable = scrol.getTable();
									dataModel = scrol.getModel();
								}
								
							}
							
										
						}	
				}
				dragging = false;
				tabImage = null;
			}
			
			// Mouse Taste druecken
			public void mousePressed(MouseEvent e) {
				// Tab Nummer holen
				int tabNumber = getUI().tabForCoordinate(TabPanel.this, e.getX(), e.getY());
				// Tab nicht leer
				if (tabNumber >= 0) {
					
					final JSplitPane split = (JSplitPane) myTab.getSelectedComponent();
					final ScrollPane scrol = (ScrollPane) split.getComponent(1);
					// DataTabel und Model auf dem tab setzen
					dataTable = scrol.getTable();
					dataModel = scrol.getModel();
					
					if (((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent() != null){
						UpdaitTab();
					} else {
						Undomanager.resetButton();
					}
				}	
			}
		});
		
	}
	
	
	/**
	 * Adds a new Table Tab
	 * @param name
	 * @param model
	 */
	public void addTable(final String name, Model model) {
		dataModel = model;
		MenuItem = new JMenuItem("Open in new window");
		tabLablePanel = new JPanel();
		tabLable = new JLabel(name); //Name of Key Tab
		final JSplitPane splitPane = new JSplitPane(); //new SplitPane for dataTabel and TextFildTab
		JPopupMenu Popup = new JPopupMenu();
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		dataTable = new JTable(model);
		dataTable.setRowSorter(sorter);
		dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataTable.getTableHeader().setToolTipText(Main.myLang.getString("tabpanel.tablesorttooltip"));
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			public void setValue(Object value) {
				if (value instanceof ImageIcon) {
					setIcon((ImageIcon)value);
					setText("");
				} else {
					setIcon(null);
					setText(value.toString());
				}
			}
		};
		dataTable.setDefaultRenderer(Object.class, cellRenderer);
		
		dataTable.getColumnModel().getColumn(1).setPreferredWidth(50);
		tableScrollPane = new ScrollPane(dataTable, model);
		
		JButton closeButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/cross.png")));
		tabLablePanel.add(tabLable);
		closeButton.setPreferredSize(new Dimension(12,12)); 
		// Tab schliessen
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* TODO: Still needed? Changes automatically saved in database.
				if (dataModel.isChanged()) {
					int response = JOptionPane.showConfirmDialog(
							null,
							CTex_Main.myLang.getString("unsavedchanges.message"),
							CTex_Main.myLang.getString("unsavedchanges.title"),
							JOptionPane.YES_NO_OPTION);
					if (response == JOptionPane.YES_OPTION) {
						if (dataModel.getSavePath().compareTo("") == 0) {
							JFileChooser fc = new JFileChooser();
							fc.setFileFilter(new CTex_FileFilter("CrossTeX (*.xtx) and BibTeX (*.bib) files", new String[] {"xtx", "bib"}));
	
							int returnVal = fc.showSaveDialog(CTex_TabPanel.this);
				            if (returnVal == JFileChooser.APPROVE_OPTION) {
				            	dataModel.setSavePath(fc.getSelectedFile().getAbsolutePath());
								dataModel.setChanged(false);
								dataModel.getEntity().exportToFile("xtx", fc.getSelectedFile().getAbsolutePath());
				            }
						} else {
							dataModel.setChanged(false);
							dataModel.getEntity().exportToFile("xtx", dataModel.getSavePath());
						}
					}
				}*/
				remove(getSelectedIndex());
				// restliche Tabs updaiten
				int existTab = getSelectedIndex();
				// if Tabs exist gibt
				if (existTab >= 0){
					UpdaitTab();
					final JSplitPane split = (JSplitPane) myTab.getSelectedComponent();
					final ScrollPane scrol = (ScrollPane) split.getComponent(1);
					// DataTabel und Model auf dem tab setzen
					dataTable = scrol.getTable();
					dataModel = scrol.getModel();
				} else {
					// falls keine Tabs Button deaktivieren
					Undomanager.resetButton();
					myFrame.setIconsEnabled(false);
					myFrame.setItemOff();
				}
			}
		});
		
		tabLablePanel.setName(name);
		tabLablePanel.add(closeButton);
		tabLablePanel.setOpaque(false);
		
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(tableScrollPane);
		splitPane.setBottomComponent(null);
		
		addTab(tabLable.getText(), splitPane);   //add splitPane on TabPane 
		setTabComponentAt(getTabCount() - 1, tabLablePanel);
		this.setComponentPopupMenu(Popup);
		MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.addFrame(tabLablePanel.getName(), name,  splitPane);
				remove(getSelectedIndex());
			}
		});
		Popup.add(MenuItem);
		Popup.addPopupMenuListener(new PopupMenuListener() {
		      public void popupMenuCanceled(final PopupMenuEvent evt) {}
		      public void popupMenuWillBecomeInvisible(final PopupMenuEvent evt) {}
		      public void popupMenuWillBecomeVisible(final PopupMenuEvent evt) {
		        tabLablePanel = (JPanel) getTabComponentAt(getSelectedIndex());
		        if (tabLablePanel.getMousePosition() != null) {
		            MenuItem.setVisible(true);
		        }else{
		        	MenuItem.setVisible(false);
		        }
		      }
		});
		
		listener(dataTable, model);
		
	}
	
	/**
	 * 
	 * @param name
	 * @param scrollPane
	 */
	public void addScrollPane(String name, Component scrollPane){
		tabLable = new JLabel(name);
		JSplitPane x = new JSplitPane();
		x.setTopComponent(scrollPane);
		addTab(tabLable.getText(), x);
		setTabComponentAt(getTabCount() - 1, tabLable);
	}
	
	public void addPane(Component compTitle, Component compContext){
		add(compContext);
		setTabComponentAt(getTabCount() - 1, compTitle);
	}
	
	public TableModel getTheModel() {
		if (getTabCount() != 0) {
			return ((ScrollPane)((JSplitPane)getComponentAt(getSelectedIndex())).getTopComponent()).getModel();
		} else {
			return null;
		}
	}
	
    // Marking the name of the tab if there are unsaved changes
	// NOT USED AT THE MOMENT, BECAUSE NO MORE LOCAL SAVING
	public void setTabTitleMarked() {
		if (getTabCount() != 0) {
			String oldTitle = getTitleAt(getSelectedIndex());
			// Adding the *
			if (!oldTitle.contains("*")) {
				tabLable.setText(oldTitle + "*");
			}
		}
	}
	
	// Unmarking the name of the tab if there are no unsaved changes
	public void setTabTitleUnmarked() {
		if (getTabCount() != 0) {
			String oldTitle = getTitleAt(getSelectedIndex());
			// Deleting the added *
			tabLable.setText(oldTitle.substring(0, oldTitle.length()));
		}
	}
	
	// Checks if there are unsaved changes in any model
	public boolean changesInAnyModel() {
		Model model = null;
		for (int i = 0; i < getTabCount(); i++) {
			model = ((ScrollPane)((JSplitPane)getComponentAt(getSelectedIndex())).getTopComponent()).getModel();
			if (model != null) {
				if (model.isChanged()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Refreshes all existing models (note: takes some time)
	 * @author stefan
	 */
	public void refreshAllModels() {
		Model model = null;
		for (int i = 0; i < getTabCount(); i++) {
			model = ((ScrollPane)((JSplitPane)getComponentAt(getSelectedIndex())).getTopComponent()).getModel();
			if (model != null) {
				model.refreshModel();
			}
		}
	}
	
	public JTable getTable() {
		if (getTabCount() != 0) {
			return ((ScrollPane)((JSplitPane)getComponentAt(getSelectedIndex())).getTopComponent()).getTable();
		} else {
			return null;
		}
	}
	
	public void setComponent (Component comp){
		setTabComponentAt(0, comp);
	}
	
	
	/*
	 * 	set the Bottom Component of SplitPane
	 */
	public void setBottomComponentofSplitPane(Component comp){
		((JSplitPane)getComponentAt(getSelectedIndex())).setBottomComponent(comp);
		((JSplitPane)getComponentAt(getSelectedIndex())).setDividerLocation(0.65);
	}
	
	public Component getBottomComponentofSplitPane(){
		return ((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent();
	}
	
	/*
	 * 	remove the Bottom Component of SplitPane
	 */
	public void removeBottomComponentofSplitPane(){
		if (((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent() != null) {
			((JSplitPane)getComponentAt(getSelectedIndex())).remove(2);
		}
	}
	
	public void showPreviewPane() {
		// Clearing the bottom component of the splitpane
		removeBottomComponentofSplitPane();
		// Adding the preview instead
		if (dataModel.getShowPreview()) {
			
			//TODO: strohmsn: hier wird ein fehler geworden wenn die datenbank leer ist
			//und man bei einem neuen eintrag auf Abbrechen drueckt!
			//Exception in thread "AWT-EventQueue-0" java.lang.IndexOutOfBoundsException: Invalid index
			ContainerObject selectedObject = dataModel.getTableContent()
				.get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow()));
			if (selectedObject != null) {
				setBottomComponentofSplitPane(new PreviewPanel(getTab(), selectedObject));
			}
		}
	}
	
	/*
	 *  return this Tab of TabelTab;
	 */
	public TabPanel getTab(){
		return this;
	}
	
	/**
	 * 
	 * @return the model of the currently selected tab
	 */
	public Model getTabelModel(){
		return dataModel;
	}
	
	// zeichnen von Graphics
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(dragging && MouseLocation != null && tabImage != null) {
			// zeichne the dragged tab
			g.drawImage(tabImage, MouseLocation.x, MouseLocation.y, this);
		}
	}
	
	protected void listener (final JTable myTable, final Model myModel){
		myTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER && myTable.getSelectedRow() != -1) {
					ContainerObject selectedObject = dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow()));
					setBottomComponentofSplitPane(
							new EntryPanel(
									myModel.getEntity().getDb(),
									getTab(),
									Undomanager,
									selectedObject,
									myFrame
							)
					);

					((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent().requestFocus();
					((EntryPanel)((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent()).setFocus();
					e.consume();
				}
				
				if (e.getKeyCode() == KeyEvent.VK_DELETE && myTable.getSelectedRow() != -1) {
					// Deleting the object that is selected now
					// At the moment deleting in database & model separately (faster)
					// Copy of this code is used on tableDeleteItem action
					dataModel.getEntity().getDb().deleteObject(dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow())));
					dataModel.getTableContent().remove(dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow())));
					dataModel.setChanged(true);
					dataModel.fireTableDataChanged();
					
				}
			}
		});
		
		myTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int rowNumber = myTable.rowAtPoint(e.getPoint());
					int colNumber = myTable.columnAtPoint(e.getPoint());
					myTable.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);
					myTable.getColumnModel().getSelectionModel().setSelectionInterval(colNumber, colNumber);
					myTable.scrollRectToVisible(myTable.getCellRect(rowNumber, 0, false));
					if (myTable.getSelectedRow() != -1 && myTable.getSelectedColumn() != -1) {
						if (myTable.getCellRect(myTable.getSelectedRow(), myTable.getSelectedColumn(), true).getBounds().contains(myTable.getMousePosition())) {
							// This part just enables/disables the "Go to object" button if the marked
							// part is an object/is no object
							tableGoToObjectItem.setEnabled(false);
							ContainerObject selectedObject = dataModel.getTableContent().get(myTable.getRowSorter().convertRowIndexToModel(myTable.getSelectedRow()));
							String selectedColumnName = myTable.getColumnName(myTable.getSelectedColumn());
							for (int i = 0; i < selectedObject.getAttributes().size(); i++) {
								if (myModel.getEntity().getDb().convTypeIdToTypeName(selectedObject.getAttributes().get(i).getObjectTypeId()).compareTo(selectedColumnName) == 0) {
									tableGoToObjectItem.setEnabled(selectedObject.getAttributes().get(i).getLink());
								}
							}
							// Showing popup menu
							tablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
				
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					if (dataTable.getSelectedRow() != -1) {
						ContainerObject selectedObject = dataModel.getTableContent().get(myTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow()));
						//Doppelklick auf key
						setBottomComponentofSplitPane(
								new EntryPanel(
										myModel.getEntity().getDb(),
										getTab(),
										Undomanager,
										selectedObject,
										myFrame
								)
						);
					}
					((EntryPanel)((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent()).requestFocusInWindow();
					((EntryPanel)((JSplitPane)getComponentAt(getSelectedIndex())).getBottomComponent()).setFocus();
					
				}
			}
		}); 
		
		/*This listener is just for showing the conditions in a tooltip box */
		myTable.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				int row = myTable.rowAtPoint(p);
				int col = myTable.columnAtPoint(p);
				String toolTip = "";
				boolean objectHasCondition = false;
				
				if (row != -1 && col != -1) {
					ContainerObject selectedObject = dataModel.getTableContent().get(dataTable.getRowSorter().convertRowIndexToModel(row));
					String selectedColumnName = myTable.getColumnName(col);					
					// Need html to perform linebreaks
					toolTip = "<html>";
					// Looking for all attributes
					for (int i = 0; i < selectedObject.getAttributes().size(); i++) {
						String selectedObjectType = myModel.getEntity().getDb().convTypeIdToTypeName(selectedObject.getAttributes().get(i).getObjectTypeId());
						// Attributes must fit to the column on which the mouse is set
						if (selectedObjectType.compareTo(selectedColumnName) == 0) {
							// Concatenating the value of the whole condition-line
							toolTip = toolTip.concat("[" + selectedObjectType + "="  + selectedObject.getAttributes().get(i).getValue() + "] ");
							if (selectedObject.getAttributes().get(i).getCondition().size() != 0) {
								for (int j = 0; j < selectedObject.getAttributes().get(i).getCondition().size(); j++) {
									// Here it is clear, that at least one condition exists
									objectHasCondition = true;
									String conditionType = myModel.getEntity().getDb().convTypeIdToTypeName(selectedObject.getAttributes().get(i).getCondition().get(j).getObjectTypeId());
									// Concatenating the content of the condition-line
									toolTip = toolTip.concat(conditionType + " = " + selectedObject.getAttributes().get(i).getCondition().get(j).getValue() + ", ");
								}
								toolTip = toolTip.concat("<br>");
							}					
						}
					}
					toolTip = toolTip.concat("</html>");
				}
				
				// No tooltip text is shown, if there are no conditions
				if (objectHasCondition) {
					myTable.setToolTipText(toolTip);
				} else {
					myTable.setToolTipText(null);
				}
			}
		});
		
		myTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getSource() == myTable.getSelectionModel() && myTable.getRowSelectionAllowed()) {
					if (dataModel.getShowPreview() && dataTable.getSelectedRow() != -1) {
						ContainerObject selectedObject = dataModel.getTableContent().get(myTable.getRowSorter().convertRowIndexToModel(dataTable.getSelectedRow()));
						setBottomComponentofSplitPane(new PreviewPanel(getTab(), selectedObject));
					}
				}	
			}
		});
	}
	
	
	/*
	 * 	gibt true falls der Splitpane Component ein CTex_EditoPane ist
	 *  und false falls ein CTex_TextFieldTab
	 */
	protected boolean EditorORTab (JSplitPane mySplitPane){
			if (mySplitPane.getBottomComponent().getClass().getName() == "gui.TextFieldTab"){
				return false;
			} else if (mySplitPane.getBottomComponent().getClass().getName() == "gui.EditorPane") {
				return true;
			} else {
				System.out.println("Class Error");
				return false;
			}
		
	}
	
	protected void UpdaitTab(){
		// restliche Tabs updaiten
		int existTab = getSelectedIndex();
		
		// if Tabs exist gibt
		if (existTab >= 0 && getComponentAt(getSelectedIndex()) != null && 
				((JSplitPane) getComponentAt(getSelectedIndex())).getBottomComponent() != null
				&& ((JSplitPane) getComponentAt(getSelectedIndex())).getBottomComponent().getClass().getName() != "gui.CTex_PreviewPane"){
			
			JSplitPane mySplitPane = (JSplitPane) getComponentAt(getSelectedIndex());
			if (mySplitPane.getBottomComponent() != null){
				
				// falls es ein CTex_TextFieldTab ist
				if (!EditorORTab(mySplitPane)){
					EntryPanel tab = (EntryPanel) mySplitPane.getBottomComponent();
					if (tab.getSelectedIndex() == 0){
						// Manager auf ReqField setzen
						Undomanager.setActivManager(tab.myReqField, tab.myOptField, null, tab.myReqCompound, tab.myOptCompound, null, true, false);
					} else {
						Undomanager.setActivManager(tab.myReqField, tab.myOptField, null, tab.myReqCompound, tab.myOptCompound, null, false, false);
					}
				// ist ein Editor Tab	
				} else if (EditorORTab(mySplitPane)){
					EditorPanel tab = (EditorPanel) mySplitPane.getBottomComponent();
					Undomanager.setActivManager(null, null, tab.getEditor(), null, null, tab.getEditorCompound(), false, true);
				} else {
					Undomanager.resetButton();
				}
			} else {
				// falls keine Tabs Button deaktivieren
				Undomanager.resetButton();
				//myFrame.setIconOff();
			}
		} else {
			Undomanager.resetButton();
		}

	}

}


