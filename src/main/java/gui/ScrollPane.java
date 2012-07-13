package gui;

import javax.swing.JScrollPane;
import javax.swing.JTable;
public class ScrollPane extends JScrollPane {
	
	private static final long serialVersionUID = 1L;
	private JTable dataTable = null;
	private Model model = null;
	
	public ScrollPane(JTable dataTable, Model model){
		super();
		this.dataTable = dataTable;
		this.model = model;
		
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setCorner(JScrollPane.UPPER_LEFT_CORNER, dataTable);
		setViewportView(dataTable);
	}
	
	public JTable getTable() {
		return dataTable;
	}
	
	public Model getModel() {
		return model;
	}
}
