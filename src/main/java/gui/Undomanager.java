/*
 * 	Class: CTex_UndoManager
 *  
 *  Author: Dimi, Sven, Lars, Stefan
 *  
 *  Funktion: ueberfacht die Text Felder fuer Datenbank und setzt
 *  		  deren Inhalt zuruehck.		  
 *  
 */
package gui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;

import ctex.Main;

public class Undomanager {
	
    private static final long serialVersionUID = 1L;
    
    // Botton u. Item fuer Undo und Redo
    ShortcutM undoItem  = new ShortcutM(Main.myLang.getString("menu.edit.undo"));
    ShortcutM redoItem  = new ShortcutM(Main.myLang.getString("menu.edit.redo"));
	JButton undoButton  = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/arrow_undo.png")));
	JButton redoButton  = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/arrow_redo.png")));
	
	// Undo Manager fuer "Requeds Field" u. "Optional Fields"
	private UndoManager myReqField = new UndoManager();
	private UndoManager myOptField = new UndoManager();
	private UndoManager myEditor = new UndoManager();
	
	/*
	//  dummy Compounds fuer zwischenspeichern der Inhalt
	//  "Requeds Field" u. "Optional Fields"
	*/
	private CompoundEdit myReqCompound = new CompoundEdit();
	private CompoundEdit myOptCompound = new CompoundEdit();
	private CompoundEdit myEditorCompound = new CompoundEdit();
	
	// boolean Fleck fuer den aktuellen Undo Manager
	boolean ReqOROpt = true;   // Requiedfild = true;
    boolean EditorORTab = true;
	
	/**
	 * 	Construktor: 
	 *  Initialisiert CTex_UndoManager 
	 * 
	 *  @return gui.CTex_UndoManager
	 */
    Undomanager(){
    	
    	undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
		undoItem.setEnabled(false);
		
		undoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				undoTextfield();
		    }
		});
    	
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		redoItem.setEnabled(false);
		
		redoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redoTextfield();
		    }
		});
		
		undoButton.setToolTipText(Main.myLang.getString("menu.edit.undo") +  " [" + undoItem.getAccelerator().toString().replaceAll("pressed", "+") + "]");
		undoButton.setEnabled(false);
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				undoTextfield();
		    }
		});
		redoButton.setToolTipText(Main.myLang.getString("menu.edit.redo") + " [" + redoItem.getAccelerator().toString().replaceAll("pressed", "+") + "]");
		redoButton.setEnabled(false);
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redoTextfield();
		    }
		});
		
	
    }
    
    
    /**
	 * 	Procedure: Setzt Undo Manaer auf enstprechende TextFelder 
	 * 
	 */
    public void setActivManager(UndoManager Req, UndoManager Opt, UndoManager myEdit,
    							CompoundEdit ReqCom, CompoundEdit OptCom, CompoundEdit EditCom,  
    							boolean RorO, boolean Editor){
    	myReqField = Req;
    	myOptField = Opt;
    	myReqCompound = ReqCom;
    	myOptCompound = OptCom;
    	ReqOROpt = RorO;   // Requiedfild = true;
    	myEditor = myEdit;
    	myEditorCompound = EditCom;
    	EditorORTab = Editor;
    	updateButtons();
	}
    
    
	
	/*	 make undo from Undo Manager into the Text Field
	 */
	public void undoTextfield(){
		
		if (EditorORTab){
			if (myEditor.canUndo()){
				closeCompoundEdit();
				myEditor.undo();
			}
		} else {
			
			if (ReqOROpt) {
				if (myReqField.canUndo()){
					closeCompoundEdit();
					myReqField.undo();
				}
			} else {
				if (myOptField.canUndo()){
					closeCompoundEdit();
					myOptField.undo();
				}
			}
		}
		updateButtons();
	}
	
	
	/*	 set Button and Item Enabel auf false
	 */
	public void resetButton(){
		undoItem.setEnabled(false);
		redoItem.setEnabled(false); 
		undoButton.setEnabled(false);
		redoButton.setEnabled(false);
	}
	
	/*	 make redo from Redo Manager into the Text Field
	 */
	public void redoTextfield(){
		
		if (EditorORTab){
			if (myEditor.canRedo()){
				closeCompoundEdit();
				myEditor.redo();
			}
		} else {
			if (ReqOROpt) {
				if (myReqField.canRedo()){
					closeCompoundEdit();
					myReqField.redo();
				}
			} else {
				if (myOptField.canRedo()){
					closeCompoundEdit();
					myOptField.redo();
				}
			}
		}
		updateButtons();
		
	}
	
	/**
	* 	Methode: gibt den undo Item zurueck
	* 
	*  @return gui.CTex_ShortcutM
	*/
	public ShortcutM getUndoItem(){
		return undoItem;
	}
	
	/**
	* 	Methode: gibt den redo Item zurueck
	* 
	*  @return gui.CTex_ShortcutM
	*/
	public ShortcutM getRedoItem(){
		return redoItem;
	}
	
	/**
	* 	Methode: gibt den undo Button zurueck
	* 
	*  @return java.awt.JButton
	*/
	public JButton getUndoButton(){
		return undoButton;
	}
	
	/**
	* 	Methode: gibt den redo Button zurueck
	* 
	*  @return java.awt.JButton
	*/
	public JButton getRedoButton(){
		return redoButton;
	}
	
	
	/**
	* 	Procedure: macht die Buttons an oder aus
	* 			   falls es geht
	* 
	*/
	public void updateButtons() {
		
		if (EditorORTab){
			undoButton.setEnabled(myEditor.canUndo());
			redoButton.setEnabled(myEditor.canRedo());
			undoItem.setEnabled(myEditor.canUndo());
			redoItem.setEnabled(myEditor.canRedo());
		} else {
			if(ReqOROpt){
				undoButton.setEnabled(myReqField.canUndo());
				redoButton.setEnabled(myReqField.canRedo());
				undoItem.setEnabled(myReqField.canUndo());
				redoItem.setEnabled(myReqField.canRedo());
			} else {
				undoButton.setEnabled(myOptField.canUndo());
				redoButton.setEnabled(myOptField.canRedo());
				undoItem.setEnabled(myOptField.canUndo());
				redoItem.setEnabled(myOptField.canRedo());
			}
		}
	}

	
	/**
	 *  Die Methode schliest den dummy Compound
	 *  und ubergibt die Inhalt an Undo Manager
	 */
	public void closeCompoundEdit() {
		
		if (EditorORTab){
			if (myEditorCompound.isInProgress()) {
				myEditorCompound.end();
				myEditor.addEdit(myEditorCompound);
			}
		} else {
			if (ReqOROpt) {
				if (myReqCompound.isInProgress()) {
					myReqCompound.end();
					myReqField.addEdit(myReqCompound);
				}
			} else {
				if (myOptCompound.isInProgress()) {
					myOptCompound.end();
					myOptField.addEdit(myOptCompound);
				}
			}
		}
	}
	
}