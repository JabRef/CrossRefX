package gui;

import java.util.Vector;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import ctex.Main;

public class ShortcutB extends JButton {
	
	private static final long serialVersionUID = 1L;
	
	public Vector<ShortcutM> keys = new Vector<ShortcutM>();
	
	/*  
	 * 	Dieser Vector wird benutzt um die schortcuts nach dem Einstellungen
	 *  uebername updaiten zu konnen, ohne neustart des Programms
	 *   
	*/
	public static Vector<ShortcutB> updait = new Vector<ShortcutB>();
	
	//  My Menue Buttons & Items
	private ShortcutM newWindow = new ShortcutM(Main.myLang.getString("menu.file.newwindow"));		// id 0
	private ShortcutM newDb 	 = new ShortcutM(Main.myLang.getString("menu.file.newdb"));		// id 1
	private ShortcutM openFile  = new ShortcutM(Main.myLang.getString("menu.file.openfile"));		// id 2
	private ShortcutM openDb    = new ShortcutM(Main.myLang.getString("menu.file.opendb"));		// id 3
	private ShortcutM save      = new ShortcutM(Main.myLang.getString("menu.file.savedb"));		// id 4
	private ShortcutM saveAs    = new ShortcutM(Main.myLang.getString("menu.file.savedbas"));		// id 5
	private ShortcutM importPDF = new ShortcutM(Main.myLang.getString("menu.edit.importfrompdf"));		// id 6
	private ShortcutM exit      = new ShortcutM(Main.myLang.getString("menu.file.exit"));		// id 7
	
	//  My Edit Buttons & Items
	/*	
	 * 	Undo und Redo Button und Item werden aus UndoManager geladen  
	 * 	Undo hat id 8; Redo hat id 9;
	 */
	private ShortcutM newEntry  = new ShortcutM(Main.myLang.getString("menu.edit.newentry"));		// id 10
	
	private ShortcutM editEntryItem  = new ShortcutM(Main.myLang.getString("menu.edit.editentry"));		// id 11
	private ShortcutM editWithEditorItem  = new ShortcutM(Main.myLang.getString("menu.edit.editwitheditor"));		// id 12
	private ShortcutM customizeEntryItem  = new ShortcutM(Main.myLang.getString("menu.edit.customizetype"));		// id 13
	
	private ShortcutM citeKey   = new ShortcutM(Main.myLang.getString("menu.edit.citekey"));		// id 14
	private ShortcutM copyKey   = new ShortcutM(Main.myLang.getString("menu.edit.copykey"));		// id 15
	private ShortcutM help      = new ShortcutM(Main.myLang.getString("menu.help.howto"));		// id 16
	
	//  weitere Buttons & Items
	private ShortcutM newFile  		= new ShortcutM(Main.myLang.getString("menu.file.newfile"));		// id 17
	private ShortcutM search    		= new ShortcutM(Main.myLang.getString("menu.extra.search"));		// id 18
	private ShortcutM preferencesItem  = new ShortcutM(Main.myLang.getString("menu.extra.preferences"));		// id 19

	ShortcutB(Undomanager myUndoManager){
		// New Window
		keys.add(newWindow);
		// New Database
		keys.add(newDb);
		// Open File
		keys.add(openFile);
		// Open Database
		keys.add(openDb);
		// Save Database
		keys.add(save);
		// Save as Database
		keys.add(saveAs);
		// import PDF
		importPDF.setEnabled(false);
		keys.add(importPDF);
		// exit
		keys.add(exit);
		// Undo Button & Item
		keys.add(myUndoManager.getUndoItem());
		// Redo Button & Item
		keys.add(myUndoManager.getRedoItem());
		// new Entry type
		keys.add(newEntry);
		
		// edit Entry
		keys.add(editEntryItem);
		// edit With Editor
		keys.add(editWithEditorItem);
		// customize EntryItem
		keys.add(customizeEntryItem);
		// Cite Key
		keys.add(citeKey);
		// Copy Key
		keys.add(copyKey);
		// help
		keys.add(help);
		// New File Button
		//newFile.setVisible(false);
		keys.add(newFile);
		// Search Button
		// search.setVisible(false);
		keys.add(search);
		// Shortcut
		keys.add(preferencesItem);
		
		try {
			loadKeys(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setShortcut(this);
		//Audgabetest();
		updait.add(this);
		
	}
	
	
	/*   
	 * 	Shorcuts belegungen aus der Datei laden und uebernehmen
	 */
	public void loadKeys (ShortcutB myshortcut) throws IOException {
	
		int index = 0, length = 0, ID = 0;
		String theConfig = Main.getIniData("HotKey");
		String gelesen = "";
		
		while (index >= 0) {
			  if (theConfig.charAt(index) != ',' && theConfig.charAt(index) != ';') {
				  gelesen += theConfig.charAt(index);
			  }
			  else if (theConfig.charAt(index) != ',' && theConfig.charAt(index) == ';'){
		  		  myshortcut.keys.get(length).Var = gelesen.charAt(0);
				  gelesen = "";
				  ID = 0;
				  
			  }
			  else {
				  switch (ID) {
				  		case 0:
				  			length = Integer.valueOf(gelesen);
				  			myshortcut.keys.get(length).ID = length;
				  			break;
				  		case 1:
				  			myshortcut.keys.get(length).Strg = gelesen.charAt(0);
				  			break;
				  		default:
				             System.out.println("Invalid ID.");
				             break;
				  }
				  gelesen = "";
				  ID++;
			  }
			  
			  if ((theConfig.length()-1) == index){
				  index = -1;
			  } else {index++;}
		}
		
		// Einstellungen setzen
		String myStrg = "";
		for (int i = 0; i < myshortcut.keys.size(); i++){
			
			switch (myshortcut.keys.get(i).Strg) {
				// Ctrl
				case 's' :
					myStrg = "Ctrl";
					break;
				// Shift
				case 'u' :
					myStrg = "Shift";
					break;
				// Alt
				case 'a' :
					myStrg = "Alt";
					break;
				// Ctrl+Shift
				case 't' :
					myStrg = "Ctrl+Shift";
					break;
				// Ctrl+Alt
				case 'r' :
					myStrg = "Ctrl+Alt";
					break;
				default:
					System.out.print(i + " ");
					System.out.println("Invalid Variable.");
					break;	
			}
			myshortcut.keys.get(i).myStrgBox.setSelectedItem(myStrg);
			
			String Key = "";
			switch (myshortcut.keys.get(i).Var) {
			case '1' :
				Key = "F1";
				break;
			case '2' :
				Key = "F2";
				break;
			case '3' :
				Key = "F3";
				break;
			case '4' :
				Key = "F4";
				break;
			case '5' :
				Key = "F5";
				break;
			case '6' :
				Key = "F6";
				break;
			case '7' :
				Key = "F7";
				break;
			case '8' :
				Key = "F8";
				break;
			case '9' :
				Key = "F9";
				break;
			case '0' :
				Key = "F10";
				break;
			case '!' :
				Key = "F11";
				break;
			case '?' :
				Key = "F12";
				break;
			default:
				Key = Character.toString(myshortcut.keys.get(i).Var);
				break;	
		}
			
			myshortcut.keys.get(i).myBuchBox.setSelectedItem(Key);
			
		}
		setShortcut(myshortcut);
		
	}
	
	
	/* 
	 * 	Das Procedure speichert die Einstellungen des Schortcuts 
	 *  in die Datei und setzt die Enderungen an Menue Item
	 */
	public void storeKeys () throws IOException {
		
		String text = "";
	    String myStrg = "";
	    String value = "";
	    String key = "";
	    
	    for (int i = 0; i < keys.size(); i++) {
	    	
	    	if (keys.get(i).myStrgBox.getSelectedItem() == "Ctrl") {
	    		myStrg = "s";
	    	} else if (keys.get(i).myStrgBox.getSelectedItem() == "Alt") {
	    		myStrg = "a";
	    	} else if (keys.get(i).myStrgBox.getSelectedItem() == "Shift") {
	    		myStrg = "u";	
	    	} else if (keys.get(i).myStrgBox.getSelectedItem() == "Ctrl+Shift"){
	    		myStrg = "t";	
	    	} else if (keys.get(i).myStrgBox.getSelectedItem() == "Ctrl+Alt"){
	    		myStrg = "r";	
	    	} else {
	    		System.out.println("");
	    	}
	    	
	    	if (keys.get(i).myBuchBox.getSelectedItem() == "F1"){
	    		key = "1";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F2"){
	    		key = "2";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F3"){
	    		key = "3";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F4"){
	    		key = "4";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F5"){
	    		key = "5";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F6"){
	    		key = "6";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F7"){
	    		key = "7";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F8"){
	    		key = "8";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F9"){
	    		key = "9";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F10"){
	    		key = "0";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F11"){
	    		key = "!";
	    	} else if (keys.get(i).myBuchBox.getSelectedItem() == "F12"){
	    		key = "?";
	    	} else {
	    		key = (String) keys.get(i).myBuchBox.getSelectedItem();
	    	}
	    	
	    	text = i + "," + myStrg + "," + key + ";";
	    	value += text;
	    }
	    
	    Main.setIniData("HotKey", value);
	    for (int i = 0; i < ShortcutB.updait.size(); i++){
	    	loadKeys(ShortcutB.updait.get(i));
		}
	}
	
	
	/*
	 * 	wendet set_Key_Event auf alle Shortcuts
	 */
	public void setShortcut(ShortcutB myshortcut) {
		for (int i = 0; i < myshortcut.keys.size(); i++) {
			setKeyEvent(myshortcut.keys.get(i));
		}
	}
	
	/*
	 * 	setzt Die KeyStroke von Menue Item
	 */
	public void setKeyEvent (ShortcutM key) {
		
		if (key.Strg == 's') {
			switch (key.Var) {
				case 'a':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
					break;
				case 'b':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK));
					break;
				case 'c':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
					break;
				case 'd':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
					break;
				case 'e':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
					break;
				case 'f':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
					break;
				case 'g':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK));
					break;
				case 'h':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK));
					break;
				case 'i':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK));
					break;
				case 'j':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.CTRL_MASK));
					break;
				case 'k':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, Event.CTRL_MASK));
					break;
				case 'l':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
					break;
				case 'm':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK));
					break;
				case 'n':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
					break;
				case 'o':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
					break;
				case 'p':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
					break;
				case 'q':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
					break;
				case 'r':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
					break;
				case 's':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
					break;
				case 't':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK));
					break;
				case 'u':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK));
					break;
				case 'v':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
					break;
				case 'w':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));
					break;
				case 'x':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
					break;
				case 'y':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
					break;
				case 'z':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
					break;
				/* F-Tasten*/
					
				case '1':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, Event.CTRL_MASK));
					break;
				case '2':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, Event.CTRL_MASK));
					break;
				case '3':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, Event.CTRL_MASK));
					break;
				case '4':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK));
					break;
				case '5':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.CTRL_MASK));
					break;
				case '6':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, Event.CTRL_MASK));
					break;
				case '7':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, Event.CTRL_MASK));
					break;
				case '8':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, Event.CTRL_MASK));
					break;
				case '9':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, Event.CTRL_MASK));
					break;
				case '0':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, Event.CTRL_MASK));
					break;
				case '!':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, Event.CTRL_MASK));
					break;
				case '?':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, Event.CTRL_MASK));
					break;
				
				default:
		             System.out.println("Invalid Variable.");
		             break;	
			}
			
		} else if (key.Strg == 'a') {
			switch (key.Var) {
				case 'a':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.ALT_MASK));
					break;
				case 'b':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.ALT_MASK));
					break;
				case 'c':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.ALT_MASK));
					break;
				case 'd':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.ALT_MASK));
					break;
				case 'e':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.ALT_MASK));
					break;
				case 'f':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.ALT_MASK));
					break;
				case 'g':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.ALT_MASK));
					break;
				case 'h':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.ALT_MASK));
					break;
				case 'i':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.ALT_MASK));
					break;
				case 'j':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.ALT_MASK));
					break;
				case 'k':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, Event.ALT_MASK));
					break;
				case 'l':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.ALT_MASK));
					break;
				case 'm':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.ALT_MASK));
					break;
				case 'n':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.ALT_MASK));
					break;
				case 'o':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.ALT_MASK));
					break;
				case 'p':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.ALT_MASK));
					break;
				case 'q':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.ALT_MASK));
					break;
				case 'r':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.ALT_MASK));
					break;
				case 's':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.ALT_MASK));
					break;
				case 't':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.ALT_MASK));
					break;
				case 'u':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.ALT_MASK));
					break;
				case 'v':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.ALT_MASK));
					break;
				case 'w':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.ALT_MASK));
					break;
				case 'x':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.ALT_MASK));
					break;
				case 'y':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.ALT_MASK));
					break;
				case 'z':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.ALT_MASK));
					break;
				/* F-Tasten*/
					
				case '1':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, Event.CTRL_MASK));
					break;
				case '2':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, Event.CTRL_MASK));
					break;
				case '3':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, Event.CTRL_MASK));
					break;
				case '4':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK));
					break;
				case '5':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.CTRL_MASK));
					break;
				case '6':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, Event.CTRL_MASK));
					break;
				case '7':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, Event.CTRL_MASK));
					break;
				case '8':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, Event.CTRL_MASK));
					break;
				case '9':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, Event.CTRL_MASK));
					break;
				case '0':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, Event.CTRL_MASK));
					break;
				case '!':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, Event.CTRL_MASK));
					break;
				case '?':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, Event.CTRL_MASK));
					break;
				default:
					System.out.println("Invalid Variable.");
					break;	
			}
		} else if (key.Strg == 'u') {
			switch (key.Var) {
				case 'a':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.SHIFT_MASK));
					break;
				case 'b':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.SHIFT_MASK));
					break;
				case 'c':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.SHIFT_MASK));
					break;
				case 'd':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.SHIFT_MASK));
					break;
				case 'e':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.SHIFT_MASK));
					break;
				case 'f':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.SHIFT_MASK));
					break;
				case 'g':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.SHIFT_MASK));
					break;
				case 'h':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.SHIFT_MASK));
					break;
				case 'i':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.SHIFT_MASK));
					break;
				case 'j':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.SHIFT_MASK));
					break;
				case 'k':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, Event.SHIFT_MASK));
					break;
				case 'l':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.SHIFT_MASK));
					break;
				case 'm':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.SHIFT_MASK));
					break;
				case 'n':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.SHIFT_MASK));
					break;
				case 'o':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.SHIFT_MASK));
					break;
				case 'p':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.SHIFT_MASK));
					break;
				case 'q':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.SHIFT_MASK));
					break;
				case 'r':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.SHIFT_MASK));
					break;
				case 's':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.SHIFT_MASK));
					break;
				case 't':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.SHIFT_MASK));
					break;
				case 'u':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.SHIFT_MASK));
					break;
				case 'v':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.SHIFT_MASK));
					break;
				case 'w':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.SHIFT_MASK));
					break;
				case 'x':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.SHIFT_MASK));
					break;
				case 'y':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.SHIFT_MASK));
					break;
				case 'z':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.SHIFT_MASK));
					break;
				/* F-Tasten*/
					
				case '1':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, Event.CTRL_MASK));
					break;
				case '2':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, Event.CTRL_MASK));
					break;
				case '3':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, Event.CTRL_MASK));
					break;
				case '4':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK));
					break;
				case '5':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.CTRL_MASK));
					break;
				case '6':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, Event.CTRL_MASK));
					break;
				case '7':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, Event.CTRL_MASK));
					break;
				case '8':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, Event.CTRL_MASK));
					break;
				case '9':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, Event.CTRL_MASK));
					break;
				case '0':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, Event.CTRL_MASK));
					break;
				case '!':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, Event.CTRL_MASK));
					break;
				case '?':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, Event.CTRL_MASK));
					break;
				default:
					System.out.println("Invalid Variable.");
					break;	
			}
		} else if (key.Strg == 't'){
			switch (key.Var) {
				case 'a':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'b':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'c':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'd':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'e':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'f':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'g':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'h':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'i':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'j':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'k':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'l':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'm':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'n':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'o':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'p':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'q':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'r':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 's':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 't':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'u':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'v':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'w':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'x':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'y':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				case 'z':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
					break;
				/* F-Tasten*/
					
				case '1':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, Event.CTRL_MASK));
					break;
				case '2':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, Event.CTRL_MASK));
					break;
				case '3':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, Event.CTRL_MASK));
					break;
				case '4':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK));
					break;
				case '5':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.CTRL_MASK));
					break;
				case '6':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, Event.CTRL_MASK));
					break;
				case '7':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, Event.CTRL_MASK));
					break;
				case '8':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, Event.CTRL_MASK));
					break;
				case '9':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, Event.CTRL_MASK));
					break;
				case '0':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, Event.CTRL_MASK));
					break;
				case '!':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, Event.CTRL_MASK));
					break;
				case '?':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, Event.CTRL_MASK));
					break;
				default:
					System.out.println("Invalid Variable.");
					break;
			}
		} else if (key.Strg == 'r'){
			switch (key.Var) {
				case 'a':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'b':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'c':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'd':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'e':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'f':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'g':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'h':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'i':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'j':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'k':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'l':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'm':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'n':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'o':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'p':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'q':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'r':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 's':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 't':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'u':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'v':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'w':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'x':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'y':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				case 'z':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
					break;
				/* F-Tasten*/
					
				case '1':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, Event.CTRL_MASK));
					break;
				case '2':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, Event.CTRL_MASK));
					break;
				case '3':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, Event.CTRL_MASK));
					break;
				case '4':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK));
					break;
				case '5':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.CTRL_MASK));
					break;
				case '6':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, Event.CTRL_MASK));
					break;
				case '7':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, Event.CTRL_MASK));
					break;
				case '8':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, Event.CTRL_MASK));
					break;
				case '9':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, Event.CTRL_MASK));
					break;
				case '0':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, Event.CTRL_MASK));
					break;
				case '!':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, Event.CTRL_MASK));
					break;
				case '?':
					key.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, Event.CTRL_MASK));
					break;
				default:
					System.out.println("Invalid Variable.");
					break;
			}
		} else {
			System.out.println("ungueltige Struerung");
		}
		
	}
	
	public boolean exeption (){
		boolean fehler = false;
		for (int i = 0; i < keys.size(); i++) {
			for (int j = i + 1; j < keys.size(); j++) {
				if (i != j &&
					keys.get(i).myBuchBox.getSelectedItem() == keys.get(j).myBuchBox.getSelectedItem() &&
					keys.get(i).myStrgBox.getSelectedItem() == keys.get(j).myStrgBox.getSelectedItem()){
					System.out.print(i + " " + keys.get(i).myBuchBox.getSelectedItem() + " ");
					System.out.println(j + " " + keys.get(i).myBuchBox.getSelectedItem() + " ");
					
					System.out.print(i + " " + keys.get(i).myStrgBox.getSelectedItem() + " ");
					System.out.println(j + " " + keys.get(i).myStrgBox.getSelectedItem() + " ");
					
					fehler = true;
				}
			}
		}
		return fehler;
	}
	
	/*
	 *  das Procedure gibt Buttons zurueck
	 */
	Vector<ShortcutM> getButtons (){
		return keys;
	}
	
}
