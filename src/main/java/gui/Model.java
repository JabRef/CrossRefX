package gui;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import container.ContainerAttribute;
import container.ContainerObject;
import container.ContainerType;

import ctex.Entity;
import ctex.Main;
import database.Db;


public class Model extends AbstractTableModel{
		
	private static final long serialVersionUID = 1L;
	
	private Vector<ContainerObject> tableContent = null;
	private Vector<ContainerType> allTypes;
	private Vector<ContainerType> usedTypes = new Vector<ContainerType>();
	private Vector<Integer> searchResults = new Vector<Integer>();
	private boolean showObjects = true;
	private Entity myEntity = null;
	private Db theDb = null;
	private boolean isChanged;
	private boolean showPreview;
	private String savePath = "";
	private int columntype;
	
	public Model (Entity entity) {
		this.myEntity = entity;
		this.theDb = myEntity.getDb();
		this.allTypes = myEntity.getDb().getAllTypes();
		this.tableContent = theDb.getAllObject();
		this.isChanged = false;
		this.showPreview = true;
		updateColumns();
	}
	
	public void updateColumns() {
		columntype = Integer.parseInt(Main.getIniData("columntype"));
		switch (columntype) {
			case 1:
				// Showing default columns declared in app.ini
				String columns = Main.getIniData("defaultcolumns");
				String[] splitColumns = columns.split(",");
				usedTypes.clear();
				if (new Integer(Main.getIniData("filecolumn")) == 1) {
					usedTypes.add(new ContainerType(Main.fileField, false, false));
				}
				for (int i = 0; i < splitColumns.length; i++) {
					for (int j = 0; j < allTypes.size(); j++) {
						if (splitColumns[i].compareToIgnoreCase(allTypes.get(j).getType()) == 0 &&
								splitColumns[i].compareToIgnoreCase(Main.fileField) != 0) {
							usedTypes.add(allTypes.get(j));
						}
					}
				}
				break;
			case 2:
				// Showing all possible columns, except of entry types
				usedTypes.clear();
				for (int i = 0; i < allTypes.size(); i++) {
					if (!allTypes.get(i).isEntryType()) {
						usedTypes.add(allTypes.get(i));
					}
				}
				break;
			case 3:
				// Showing only columns which contain at least 1 entry
				showOnlyUsedTypes();
				addUsedTypes();
				break;
		}
		// To refresh the table columns
		fireTableStructureChanged();
	}
	
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	
	public boolean isChanged() {
		return isChanged;
	}
	
	public void setShowPreview(boolean showPreview) {
		this.showPreview = showPreview;
	}
	
	public boolean getShowPreview() {
		return showPreview;
	}
	
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	
	public String getSavePath() {
		return savePath;
	}
	
	/** 
	 *  @procedure: refreshModel
	 * 	
	 * 	Refresh all objects of the model
	 */
	public void refreshModel() {
		tableContent.clear();
		tableContent = theDb.getAllObject();
		fireTableDataChanged();
		updateColumns();
	}
	
	/** 
	 *  @procedure: refreshModel
	 * 	
	 *  @parameter: obj = edited entry
	 *  
	 *  Do refresh object under Object ID of this model
	 *  when entry is edited
	 */
	public void refreshModel(ContainerObject obj) {
		if(theDb.getObject(obj.getId()) != null) {
			obj = theDb.getObject(obj.getId());
			int dummy = 0;
			
				while (dummy != tableContent.size() && tableContent.get(dummy).getKey().compareTo(obj.getKey()) != 0){
					dummy++;
				}
				
				if(dummy == tableContent.size()){
					tableContent.add(obj);
				} else {
					tableContent.remove(dummy);
					tableContent.add(dummy, obj);
				}
		}
		fireTableDataChanged();
		updateColumns();
	}

	public void refreshTypes() {
		allTypes.clear();
		allTypes = theDb.getAllTypes();
		updateColumns();
	}
	
	public Entity getEntity() {
		return myEntity;
	}
	
	// Decides if the model resolves links (-> false), or shows the links
	// in the table (-> true)
	public void setView(boolean showObjects) {
		// TODO: might not behave correct always
		this.showObjects = showObjects;
	}
	
	public Vector<Integer> getSearchResults() {
		return searchResults;
	}
	
	// SHOWS used Types
	public void showOnlyUsedTypes() {
		// Hiding all types at first, for restoring defaults
		for (int i = 0; i < allTypes.size(); i++) {
			allTypes.get(i).setInvisible(true);
		}
		// Only the type that are used are set visible
		for (int i = 0; i < tableContent.size(); i++) {
			for (int j = 0; j < tableContent.get(i).getAttributes().size(); j++) {
				String typeName = theDb.convTypeIdToTypeName(tableContent.get(i).getAttributes().get(j).getObjectTypeId());
				for (int k = 0; k < allTypes.size(); k++) {
					if (typeName.compareTo(allTypes.get(k).getType()) == 0) {
						allTypes.get(k).setInvisible(false);
					}
				}
			}
		}
	}
	
	// Toggles if column is shown or not
	public void toggleColumn(String type) {
		for (int i = 0; i < allTypes.size(); i++) {
			if (type == allTypes.get(i).getType()) {
				if (allTypes.get(i).isInvisible()) {
					allTypes.get(i).setInvisible(false);
				} else {
					allTypes.get(i).setInvisible(true);
				}
			}
		}
		addUsedTypes();
	}
	
	public void addUsedTypes() {
		usedTypes.clear();
		for (int i = 0; i < allTypes.size(); i++) {
			if (!allTypes.get(i).isInvisible()) {
				usedTypes.add(allTypes.get(i));
			}
		}
	}
	
	public Vector<ContainerType> getAllTypes() {
		return allTypes;		
	}
	
	public Vector<ContainerType> getUsedTypes() {
		return usedTypes;
	}
	
	public int getColumnCount() {
		// + 1 for showing keys, + 1 for showing types
		return usedTypes.size() + 2;
	}

	public int getRowCount() {
		return tableContent.size();
	}

	/* Decides what is shown in the main table of the program */
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return tableContent.get(row).getKey();
		} else if (col == 1) {
			return theDb.convTypeIdToTypeName(tableContent.get(row).getTypeId());
		} else {
			Vector<ContainerAttribute> attributesOfRow = tableContent.get(row).getAttributes();
			for (int i = 0; i < attributesOfRow.size(); i++) {
				// Current column is the file column
				if (Main.fileField.compareTo(usedTypes.get(col - 2).getType()) == 0) {
					if (theDb.getAllFiles(tableContent.get(row).getId()) != null && theDb.getAllFiles(tableContent.get(row).getId()).size() > 0) {
						return new ImageIcon(getClass().getClassLoader().getResource("images/page.png"));
					}
				}
				if (theDb.convTypeIdToTypeName(attributesOfRow.get(i).getObjectTypeId()).compareTo(usedTypes.get(col - 2).getType()) == 0) {
					if (!attributesOfRow.get(i).getLink()) {
						// The cell contains a condition
						if (attributesOfRow.get(i).getCondition().size() > 0) {
							String conditionObjectType = theDb.convTypeIdToTypeName(attributesOfRow.get(i).getObjectTypeId());
							String firstConditionLine = "[" + conditionObjectType + "=" + attributesOfRow.get(i).getValue() + "] ";
							for (int j = 0; j < tableContent.get(row).getAttributes().get(i).getCondition().size(); j++) {
								String conditionAttributeType = theDb.convTypeIdToTypeName(attributesOfRow.get(i).getCondition().get(j).getObjectTypeId());
								firstConditionLine = firstConditionLine.concat(conditionAttributeType + " = " + attributesOfRow.get(i).getCondition().get(j).getValue() + ", ");
							}
							return firstConditionLine;
						// The cell contains a normal string or a file
						} else {
							return attributesOfRow.get(i).getValue();
						}
					} else {
						if (showObjects) {
							return ">>" + attributesOfRow.get(i).getValue();
						} else {
							return resolveLink(attributesOfRow.get(i)).getValue();
						}
					}
				}
			}
			return "";
		}
	}
	
	/** 
	 * Trying to resolve Links with the following pattern:
	 * (1) Only 1 Attribute => Keep the attribute
	 * (2) More than one Attribute
	 * 	(2.1) Look for an Attribute same as the root type
	 * 	(2.2) If (2.1) fails: Look for an Attribute with the type "name"
	 * 	(2.3) If (2.2) fails: Look for an Attribute with the type "shortname"
	 * 
	 * @param rootAttribute
	 * @return resolved Attribute
	 */
	public ContainerAttribute resolveLink(ContainerAttribute rootAttribute) {
		ContainerAttribute root = rootAttribute;
		ContainerObject dummy;
		while (root.getLink()) {
			dummy = theDb.getObject(root.getValue());
			if (dummy.getAttributes().size() == 1) {
				root = dummy.getAttributes().get(0);
			} else {
				boolean foundAttribute = false;
				for (int i = 0; i < dummy.getAttributes().size(); i++) {
					if (root.getObjectTypeId() == dummy.getAttributes().get(i).getObjectTypeId()) {
						root = dummy.getAttributes().get(i);
						foundAttribute = true;
					}
				}
				if (!foundAttribute) {
					// Check if it matches name or shortname
					for (int i = 0; i < dummy.getAttributes().size(); i++) {
						if (theDb.convTypeIdToTypeName(dummy.getAttributes().get(i).getObjectTypeId()).compareTo("name") == 0) {
							root = dummy.getAttributes().get(i);
							foundAttribute = true;
						} else if (theDb.convTypeIdToTypeName(dummy.getAttributes().get(i).getObjectTypeId()).compareTo("shortname") == 0) {
							if (!foundAttribute) {
								root = dummy.getAttributes().get(i);
							}
						}
					}
				}
			}
		}
		return root;
	}
	
    public boolean isCellEditable(int row, int col){
		return false;
	}
		
	public String getColumnName(int col){
		if (col == 0) {
			return "key";
		} else if (col == 1) {
			return "type";
		} else {
			return usedTypes.get(col - 2).getType();
		}
	}
		
	
	public void search(String searchtext) {
		searchResults.clear();
		Pattern pattern = Pattern.compile(".*" + searchtext + ".*", Pattern.CASE_INSENSITIVE);
		for (int i = 0; i < tableContent.size(); i++) {
			// Search in "key"
			Matcher matcher = pattern.matcher(tableContent.get(i).getKey());
			if (matcher.matches()) {
				if (!searchResults.contains(i)) {
					searchResults.add(i);
				}
			}
			// Search in "Attributes"
			for (int j = 0; j < tableContent.get(i).getAttributes().size(); j++) {
				matcher = pattern.matcher(tableContent.get(i).getAttributes().get(j).getValue());
				if (matcher.matches()) {
					if (!searchResults.contains(i)) {
						searchResults.add(i);
					}
				}
			}
		}
	}
	
	// Returns the next smaller index of the searchResults
	public int getPreviousIndex(int compareIndex) {
		for (int i = searchResults.size() - 1; i >= 0; i--) {
			if (searchResults.get(i) < compareIndex) {
				return searchResults.get(i);
			}
		}
		return -1;
	}
	
	// Returns the next bigger index of the searchResults
	public int getNextIndex(int compareIndex) {
		for (int i = 0; i < searchResults.size(); i++) {
			if (searchResults.get(i) > compareIndex) {
				return searchResults.get(i);
			}
		}
		return -1;
	}

	
	public Vector<ContainerObject> getTableContent() {
		return tableContent;
	}
	
}
