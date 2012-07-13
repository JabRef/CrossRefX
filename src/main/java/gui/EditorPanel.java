package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import parser.ParseToString;
import parser.Parser;
import parser.ParserException;

import container.ContainerObject;

import ctex.Main;
import database.Db;

public class EditorPanel extends JPanel implements KeyListener{

	private static final long serialVersionUID = 1L;

	private UndoManager myEditor = new UndoManager();
	private CompoundEdit myEditorCom = new CompoundEdit();
	
	private JScrollPane editScroller = new JScrollPane();
	private JTextPane editPane = new JTextPane();
	private JButton checkAndSaveButton = new JButton(Main.myLang.getString("button.checkandsave"));
	private JButton checkButton = new JButton(Main.myLang.getString("button.check"));
	private JButton cancelButton = new JButton(Main.myLang.getString("button.cancel"));
	private Clipboard clipboard = Toolkit.getDefaultToolkit()
			.getSystemClipboard();

	private JPanel buttonPanel = new JPanel(new GridBagLayout());

	private GridBagConstraints constraints = new GridBagConstraints();
	private final Db theDb;
	private TabPanel myTab;
	private ContainerObject myObj;

	public EditorPanel(final Db theDb, TabPanel inTab,
			final Undomanager manager, ContainerObject obj) {

		this.theDb = theDb;
		this.myTab = inTab;
		this.myObj = obj;
		this.setLayout(new GridBagLayout());
		
		// Close dialog when pressing ESC
		inTab.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myTab.removeBottomComponentofSplitPane();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		final JPopupMenu popup = new JPopupMenu();
		JMenuItem cutItem = new JMenuItem(
				Main.myLang.getString("button.cut"));
		JMenuItem copyItem = new JMenuItem(
				Main.myLang.getString("button.copy"));
		final JMenuItem pasteItem = new JMenuItem(
				Main.myLang.getString("button.paste"));
		cutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection selected = new StringSelection(editPane
						.getSelectedText());
				clipboard.setContents(selected, selected);
				editPane.replaceSelection("");
			}
		});
		popup.add(cutItem);
		copyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection selected = new StringSelection(editPane
						.getSelectedText());
				clipboard.setContents(selected, selected);
			}
		});
		popup.add(copyItem);
		pasteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Transferable data = clipboard.getContents(clipboard);
				if (data.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					try {
						editPane.replaceSelection((String) (data
								.getTransferData(DataFlavor.stringFlavor)));
					} catch (UnsupportedFlavorException e1) {
						// TODO DIMI Auto-generated method stub
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO DIMI Auto-generated method stub
						e1.printStackTrace();
					}
				}

			}
		});
		popup.add(pasteItem);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.fill = GridBagConstraints.BOTH;
		editScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editScroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// Setting the CrossTeX file content
		// Inhalt vom Objekt in editPane setzen
		setObjtoText();
		editPane.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO DIMI Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO DIMI Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO DIMI Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// Showing popup menu on right click
				if (e.getButton() == MouseEvent.BUTTON3) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO DIMI Auto-generated method stub
			}

		});
		// UndoManager setzen
		addTextFieldOpt(editPane, manager);
		editScroller.setViewportView(editPane);
		
		this.add(editScroller, constraints);
		constraints.fill = GridBagConstraints.NONE;

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.insets = new Insets(5, 5, 5, 5);
		buttonPanel.add(checkAndSaveButton, constraints);

		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		buttonPanel.add(checkButton, constraints);

		constraints.gridx = 2;
		constraints.anchor = GridBagConstraints.LINE_START;
		
		buttonPanel.add(cancelButton, constraints);

		// Adding the button panel to the layout
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		this.add(buttonPanel, constraints);

		checkAndSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Parser p = new Parser(editPane.getText());
				theDb.deleteObject(myObj);
				try {
					p.proof(theDb, false);
					//TODO:: Alte Zeile löschen
					((Model)myTab.getTheModel()).setChanged(true);
					((Model)myTab.getTheModel()).refreshModel(theDb.getObject(myObj.getKey()));
					//JOptionPane.showMessageDialog(null, "Everyting is ok", "Parser", JOptionPane.OK_OPTION);
					myTab.removeBottomComponentofSplitPane();
				} catch (ParserException exc) {
					JOptionPane.showMessageDialog(null, exc.toString(), "Parser", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		checkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Parser p = new Parser(editPane.getText());
				try {
					p.proof(theDb, true);
					JOptionPane.showMessageDialog(null, "Everyting is ok", "Parser", JOptionPane.OK_OPTION);
				} catch (ParserException exc) {
					JOptionPane.showMessageDialog(null, exc.toString(), "Parser", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myTab.removeBottomComponentofSplitPane();
			}
		});
		
		
		setVisible(true);
	}

	private void setObjtoText() {
		editPane.setText((new ParseToString(theDb, this.myObj)).getXtx());
	}
	
	public ContainerObject getObject(){
		return myObj;
	}
	
	public Db getDb(){
		return theDb;
	}
	
	public UndoManager getEditor(){
		return myEditor;
	}
	
	public CompoundEdit getEditorCompound(){
		return myEditorCom;
	}
	
	public void setFocus(){
		editPane.setCaretPosition(0);
		editPane.requestFocusInWindow();
	}
	/**
	 *  Die Methode ueberwacht die Actionen in Optional Text Felder und speichert 
	 *  die Anderungen dummy Compound
	 *  
	 */
	public void addTextFieldOpt(final JTextPane myField,final Undomanager manager){
		myField.addKeyListener(this);
		myField.getDocument().addUndoableEditListener(new UndoableEditListener() {
			
			public void undoableEditHappened(UndoableEditEvent e) {
				UndoableEdit ue = e.getEdit();
				if (!myEditorCom.isInProgress()){
					myEditorCom = new CompoundEdit();
				}
				myEditorCom.addEdit(ue);
				manager.setActivManager(null, null, myEditor, null, null, myEditorCom, false, true);
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
			closeCompoundEdit();
		}
	}
	
	/**
	 *  Die Methode schliest den dummy Compound
	 *  und ubergibt die Inhalt an Undo Manager
	 */
	public void closeCompoundEdit() {
		
		// Editor Compound schliessen und an
		// Editor Manager uebergeben
		if (myEditorCom.isInProgress()) {
			myEditorCom.end();
			myEditor.addEdit(myEditorCom);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO DIMI Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO DIMI Auto-generated method stub
		
	}

}
