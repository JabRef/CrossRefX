package gui;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import container.ContainerFile;

public class FileTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	protected String[] columnNames = { "id", "name", "description", "type" };
	Vector<ContainerFile> data = new Vector<ContainerFile>();
	private Vector<TableModelListener> listeners = new Vector<TableModelListener>();
	
	public FileTableModel(Vector<ContainerFile> v){
		super();
		Iterator<ContainerFile> pointer = v.iterator();
  		while (pointer.hasNext()){
  			addData(pointer.next());
  		}
	}
	
	public void addData (ContainerFile obj){
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
	
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {		
		switch(columnIndex) {
	      case 0:
	    	  return data.get(rowIndex).getId();
	     case 1:
	    	 return data.get(rowIndex).getLink();
	     case 2:
	    	 return data.get(rowIndex).getDescription();
	     case 3:
	    	 return data.get(rowIndex).getType();
	    }
		return null;
	}

    public Object getDataAt (int rowIndex){
    	return (ContainerFile)data.get(rowIndex);
    }
	
	public String getColumnName(int column) {
	    return columnNames[column];
	}
	
	public Vector<ContainerFile> getData (){
		return data;
	}
	
	public void editData (ContainerFile obj, int iSelectedRow){
    	data.remove(iSelectedRow);
    	data.add(iSelectedRow, obj);
    	
    	// Zuerst ein Event, "neue Row an der Stelle index" herstellen
        TableModelEvent e = new TableModelEvent( this, iSelectedRow, iSelectedRow, 
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT );
        
        // Nun das Event verschicken
        for(int i = 0, n = listeners.size(); i<n; i++ ){
            ((TableModelListener)listeners.get( i )).tableChanged(e);
        }
    }
	
	public void removeData (int iSelectedRow) {
		if (iSelectedRow <= data.size()) {
			data.remove(iSelectedRow);
		}
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
	   return false;
	}
    
    public void addTableModelListener(TableModelListener l) {
        listeners.add( l );
    }
    
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove( l );
    }
	
}