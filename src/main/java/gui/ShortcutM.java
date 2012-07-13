package gui;

import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;

public class ShortcutM extends JMenuItem {
	
	//  Vectoren von erlaubten steuerungen
	private Vector<String> Buchstaben = new Vector<String>();
	private Vector<String> Steuerung = new Vector<String>();
	
	private static final long serialVersionUID = 1L;
	int ID;
	char Strg;
	char Var;
	boolean geandert = false;
	
	// Anzeige fuer Steuerung und Buchstabe
	public JComboBox myStrgBox = new JComboBox(Steuerung);
	public JComboBox myBuchBox = new JComboBox(Buchstaben);
	
	
	ShortcutM(String txt) {
		super(txt);
		// meugliche tasten kombinationen
		Buchstaben.add("a"); Buchstaben.add("b"); Buchstaben.add("c");
		Buchstaben.add("d"); Buchstaben.add("e"); Buchstaben.add("f");
		Buchstaben.add("g"); Buchstaben.add("h"); Buchstaben.add("i");
		Buchstaben.add("j"); Buchstaben.add("k"); Buchstaben.add("l");
		Buchstaben.add("m"); Buchstaben.add("n"); Buchstaben.add("o");
		Buchstaben.add("p"); Buchstaben.add("q"); Buchstaben.add("r");
		Buchstaben.add("s"); Buchstaben.add("t"); Buchstaben.add("u");
		Buchstaben.add("v"); Buchstaben.add("w"); Buchstaben.add("x");
		Buchstaben.add("y"); Buchstaben.add("z");
		/* F-Tasten */
		Buchstaben.add("F1"); Buchstaben.add("F2");
		Buchstaben.add("F3"); Buchstaben.add("F4");
		Buchstaben.add("F5"); Buchstaben.add("F6");
		Buchstaben.add("F7"); Buchstaben.add("F8");
		Buchstaben.add("F9"); Buchstaben.add("F10");
		Buchstaben.add("F11"); Buchstaben.add("F12");
		
		Steuerung.add("Ctrl"); Steuerung.add("Alt"); //Steuerung.add("Shift");
		Steuerung.add("Ctrl+Shift"); Steuerung.add("Ctrl+Alt");
	}
	
}
