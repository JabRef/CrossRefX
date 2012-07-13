package gui.dialog;

import gui.Frame;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import container.ContainerInclude;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import ctex.Main;

import database.Db;

/**
 * 
 * To Edit the Include Variabls of a File/DB
 * 
 * @version 1.0
 * @author mayersn
 *
 */

public class IncludeDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JTable table = null;
	private final CTex_IncludeMenuModel model;
	private Db db = null;
	private JButton bAdd, bEdit, bRemove, bOk, bCancel;
	private JTextField textField = new JTextField("", 15);
	
	private ContainerInclude openedData = null;
	private int iSelectedRow = 0;
	
	public IncludeDialog(Frame owner, Db db){
		setLocationRelativeTo(owner);
		// Close dialog when pressing ESC
		getRootPane().registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		setResizable(false);
		
		this.db = db;
		model = new CTex_IncludeMenuModel(this.db.getAllInclude());
		table = getTable();
		this.add(setOnScrollPane(table));
		add(getPanel1());
		add(getPanel2());
		add(getPanel3());
		pack();
		setModal(true);
		setVisible(true);
	}
	
	private JTable getTable() {
		table = new JTable(model);
		
		table.addMouseListener(new MouseAdapter(){
		     public void mouseClicked(MouseEvent e){
		         if (e.getClickCount() == 2){
		        	 iSelectedRow = table.getSelectedRow();
		        	 openedData = (ContainerInclude) model.getDataAt(iSelectedRow);
		        	 textField.setText(openedData.getValue());

		         }
		     }
		});
		return table;
	}
	
	private JPanel getPanel1(){
		JPanel panel = new JPanel();
		panel.add(new JLabel("Label: "));
		panel.add(textField);

		return panel;
	}
	
	private JPanel getPanel2(){
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		bAdd = new JButton(Main.myLang.getString("button.add"));
		bAdd.addActionListener(this);
	    bEdit = new JButton(Main.myLang.getString("button.edit"));
		bEdit.addActionListener(this);
	    bRemove = new JButton(Main.myLang.getString("button.remove"));
		bRemove.addActionListener(this);
		panel.add(bAdd);
		panel.add(bEdit);
		panel.add(bRemove);
		return panel;
	}
	private JPanel getPanel3(){
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		bOk = new JButton(Main.myLang.getString("button.ok"));
		bOk.addActionListener(this);
	    bCancel = new JButton(Main.myLang.getString("button.cancel"));
	    bCancel.addActionListener(this);
		panel.add(bOk);
		panel.add(bCancel);
		return panel;
	}
	
	private JScrollPane setOnScrollPane (JComponent comp){
		JScrollPane pane = new JScrollPane(comp);
		pane.setPreferredSize(new Dimension (300, 200));
		return pane;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (bAdd.getText() == e.getActionCommand()) {
			ContainerInclude obj = new ContainerInclude(-1, textField.getText());
			model.addData(obj);
		}
		else if (bEdit.getText() == e.getActionCommand() && openedData != null) {
			ContainerInclude o = new ContainerInclude(openedData.getId(), textField.getText());
			model.editData(o, iSelectedRow);
		}
		else if (bRemove.getText() == e.getActionCommand() && openedData != null) {
			model.editData(new ContainerInclude(openedData.getId(), textField.getText(), true), iSelectedRow);
		}
		else if (bOk.getText() == e.getActionCommand()) {
			db.setAllInclude(model.getData());
			this.dispose();
		}
		else if (bCancel.getText() == e.getActionCommand()) {
			this.dispose();
		}
		else if ((bEdit.getText() == e.getActionCommand() && openedData == null) || 
				  (bRemove.getText() == e.getActionCommand() && openedData != null)) {
			JOptionPane.showMessageDialog(null,
					Main.myLang.getString("=/"),
				    Main.myLang.getString("Kein Object Ausgewählt"),
				    JOptionPane.ERROR_MESSAGE);
		}
		else {
			System.out.println(e.getActionCommand());
		}
	}
}

/** TableModel implementation for the CTex_IncludeMenu Class above 
 * */
class CTex_IncludeMenuModel extends AbstractTableModel{
	private static final long serialVersionUID = 6692626588737966136L;
	private Vector<ContainerInclude> data = new Vector<ContainerInclude>();
	private Vector<TableModelListener> listeners = new Vector<TableModelListener>();
	    
    public CTex_IncludeMenuModel(Vector<ContainerInclude> v){
    	super();
    	Iterator<ContainerInclude> pointer = v.iterator();
  		while (pointer.hasNext()){
  			addData(pointer.next());
  		}
    }
    public CTex_IncludeMenuModel(){
    	super();
    }
    
    
    
    public void addData(ContainerInclude obj){
        // Das wird der Index des Vehikels werden
        int index = data.size();
        data.add(obj);
        
        // Jetzt werden alle Listeners benachrichtigt
        
        // Zuerst ein Event, "neue Row an der Stelle index" herstellen
        TableModelEvent e = new TableModelEvent( this, index, index, 
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT );
        
        // Nun das Event verschicken
        for( int i = 0, n = listeners.size(); i<n; i++ ){
            ((TableModelListener)listeners.get( i )).tableChanged(e);
        }
    }
    
    public void editData (ContainerInclude obj, int iSelectedRow){
    	data.remove(iSelectedRow);
    	if (obj.getId() == -1 && obj.getDelete() == true){
    		
    	} else {
    		data.add(iSelectedRow, obj);
    	}
    	
    	// Zuerst ein Event, "neue Row an der Stelle index" herstellen
        TableModelEvent e = new TableModelEvent( this, iSelectedRow, iSelectedRow, 
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT );
        
        // Nun das Event verschicken
        for(int i = 0, n = listeners.size(); i<n; i++ ){
            ((TableModelListener)listeners.get( i )).tableChanged(e);
        }
    }
    
    public Vector<ContainerInclude> getData(){
    	return data;
    }
    
    // Die Anzahl Columns
    public int getColumnCount() {
        return 3;
    }
    
    // Die Anzahl Vehikel
    public int getRowCount() {
        return data.size();
    }
    
    // Die Titel der einzelnen Columns
    public String getColumnName(int column) {
        switch( column ){
            case 0: return "ID";
            case 1: return "Value";
            case 2: return "Delete";
            default: return null;
        }
    }
    
    // Der Wert der Zelle (rowIndex, columnIndex)
    public Object getValueAt(int rowIndex, int columnIndex) {
        ContainerInclude obj = (ContainerInclude)data.get( rowIndex );
        
        switch( columnIndex ){
            case 0: return new Integer(obj.getId());
            case 1: return obj.getValue();
            case 2: return obj.getDelete(); 
            default: return null;
        }
    }
 
    // Eine Angabe, welchen Typ von Objekten in den Columns angezeigt werden soll
    /*public Class getColumnClass(int columnIndex) {
        switch( columnIndex ){
            case 0: return Integer.class;
            case 1: return String.class;
            case 2: return Boolean.class;
            default: return null;
        }   
    }*/
    
    public void addTableModelListener(TableModelListener l) {
        listeners.add( l );
    }
    
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove( l );
    }
    
 
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // nicht beachten
    }
    
    public Object getDataAt (int rowIndex){
    	return (ContainerInclude)data.get(rowIndex);
    }
}
