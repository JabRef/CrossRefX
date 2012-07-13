/**
 * 	Class: CTex_TextArea extends JTextArea
 *  
 *  Author: Dimi, Sven, Lars, Stefan
 *  
 *  Funktion: Die Eingabe Text Felder fuer CTex_TextFieldTab
 *  
 */
package gui;

import gui.dialog.ConditionalDialog;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import container.ContainerAttribute;
import database.Db;

public class TextArea extends JTextArea implements ActionListener{
	private static final long serialVersionUID = -7190710401802304948L;
	TextArea me;
	private final String typeName;
	private Vector<ContainerAttribute> attV = new Vector<ContainerAttribute>();
	private final Db theDb;
	private enum states {TEXT, OBJECT, CONDITION};
	states state = states.TEXT;
	
	//Right Click Menu
	final JPopupMenu rightClickMenu = new JPopupMenu();
	JMenuItem cutItem = new JMenuItem("Cut");
    JMenuItem copyItem = new JMenuItem("Copy");
    JMenuItem pasteItem = new JMenuItem("Paste");
    
	/**
	 * 	Construktor: fuer CTex_ContainerAttribut att aus Tabel Model
	 * 
	 *  @return gui.CTex_TextArea
	 */
	TextArea(Db db, String typeName, Vector<ContainerAttribute> attV, final EntryPanel Tab){
		this.theDb = db;
		this.attV = attV;
		this.setTabSize(0); 
		this.me = this;
		this.typeName = typeName;
		if (attV.size() > 1){
			state = states.CONDITION;
		} else if (attV.size() != 0 && attV.get(0).getLink() == true){
			state = states.OBJECT;
		} else {
			state = states.TEXT;
		}
		/*this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// Tab Nummer an TextFieldTab uebergeben
				Tab.SetAreaId(Id, Tab.ReqorOpt());
			}
		});*/
		cutItem.addActionListener(this);
    	cutItem.setEnabled(false);
    	copyItem.addActionListener(this);
    	copyItem.setEnabled(false);
    	pasteItem.addActionListener(this);
    	pasteItem.setEnabled(false);
    	rightClickMenu.add(cutItem);
    	rightClickMenu.add(copyItem);
    	rightClickMenu.add(pasteItem);
		/*this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JOptionPane.showMessageDialog(null, "Righ Click", "Test",JOptionPane.INFORMATION_MESSAGE);
				}
			}
        });*/
		this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                switch(e.getModifiers()) {
                    case InputEvent.BUTTON3_MASK: {
                    	
                    	Clipboard systemClip = Toolkit.getDefaultToolkit().getSystemClipboard();

                        if(me.getSelectionStart() != me.getSelectionEnd())
                        {
                        	copyItem.setEnabled(true);
                        	cutItem.setEnabled(true);
                        } else {
                        	copyItem.setEnabled(false);
                        	cutItem.setEnabled(false);
                        }
                        
                        if(systemClip.isDataFlavorAvailable(DataFlavor.stringFlavor)){
                        	pasteItem.setEnabled(true);
                        } else {
                        	pasteItem.setEnabled(false);
                        }
                        
                    	rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
                    	rightClickMenu.setInvoker(e.getComponent());
                        break;
                    }
                }
            }
        });
		update();
	}
	
	
	@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cutItem) {
            this.cut();
        } else if (e.getSource() == copyItem) {
        	this.copy();
        } else if (e.getSource() == pasteItem) {
        	this.paste();
        }
	}
	
	
	public void addCondition(){
		if (state != states.OBJECT){
			new ConditionalDialog(typeName, this, theDb);
		}
	}
	
	
	/**
	* 	Methode: gibt den Attribute zurueck
	* 
	*   @return container.CTex_ContainerAttribut
	*/
	public Vector<ContainerAttribute> getAtt(){
		if (state == states.TEXT){
			attV.clear();
			attV.add(new ContainerAttribute(-1, theDb.convTypeNameToTypeId(typeName), this.getText(), false, new Vector<ContainerAttribute>()));
		}
		return attV;
	}
	
	/**
	* 	Methode: gibt den TextFeld zurueck
	* 
	*   @return gui.CTex_TextArea
	*/
	public String getTypeName(){return typeName;}
	
	/**
	 * 
	 */
	public void removeCondition (){
		state = states.TEXT;
		attV.clear();
		attV.add(new ContainerAttribute(-1, theDb.convTypeNameToTypeId(typeName), this.getText(), false, new Vector<ContainerAttribute>()));
		update();
	}
	
	
	/**
	* 	Procedure: setzt die Farbe des Text Feldes entspraechend der Einstellungen 
	*/
	
	public void setBackground (){this.setBackground(Color.WHITE);}
	@Override
	public void setBackground (Color bfg){
		if (state == states.TEXT){
			super.setBackground(Color.WHITE);
		} else if (state == states.OBJECT){
			super.setBackground(Color.YELLOW);
		} else if (state == states.CONDITION){
			super.setBackground(Color.GREEN);
		}
	}
	
	/**
	* 	Procedure: setzt Condition 
	*/
	public void setCondition (Vector<ContainerAttribute> v){
		attV = v;
		state = states.CONDITION;
		update();
	}
	
	public void setEditable() {this.setEditable(true);}
	
	@Override
	public void setEditable(boolean b) {
		if (state == states.TEXT){
			super.setEditable(true);
		} else if (state == states.OBJECT){
			super.setEditable(false);
		} else if (state == states.CONDITION){
			super.setEditable(false);
		}
	}
	
	public void setObject (){
		 if (states.TEXT == state){
			attV.clear();
			//TODO: Split Objects 
			//      and
			//      Test is Object?
			attV.add(new ContainerAttribute(-1, theDb.convTypeNameToTypeId(typeName), this.getText(), true, new Vector<ContainerAttribute>()));
			state = states.OBJECT;
		} else if (states.OBJECT == state){
			attV.clear();
			attV.add(new ContainerAttribute(-1, theDb.convTypeNameToTypeId(typeName), this.getText(), false, new Vector<ContainerAttribute>()));
			state = states.TEXT;
		}
		 update();
	}
	
	/**
	* 	Procedure: setzt den Inhalt der Text Felder enstsprechend der Attrubute 
	*/
	public void setText() {this.setText("");}
	
	@Override
	public void setText(String t) {
		super.setText("");
		
		if (state == states.TEXT){
			if (attV.size() != 0) {
				super.setText(attV.get(0).getValue());
			}
		} else if (state == states.OBJECT){
			Vector<ContainerAttribute> v = attV;
			Iterator<ContainerAttribute> i = v.iterator();
			while (i.hasNext()){
				super.setText(getText() + i.next().getValue());
				if (i.hasNext()){
					super.setText(getText() + " AND ");
				}
			}
		} else if (state == states.CONDITION) {
			Iterator<ContainerAttribute> i = attV.iterator();
			while (i.hasNext()){
				ContainerAttribute o = i.next();
				super.setText(getText().concat("[" + theDb.convTypeIdToTypeName(o.getObjectTypeId()) + "=" + o.getValue() + "]: "));
				Vector<ContainerAttribute> v2 =o.getCondition();
				Iterator<ContainerAttribute> i2 = v2.iterator();
				while (i2.hasNext()){
				
					ContainerAttribute o2 = i2.next();
					super.setText(getText().concat(theDb.convTypeIdToTypeName(o2.getObjectTypeId()) + " = " + o2.getValue() + ", "));
				}
				super.setText(getText().concat("\n"));
			}
		}
	}	
	
	private void update (){
		setEditable();
		setBackground();
		setText();
	}
}
