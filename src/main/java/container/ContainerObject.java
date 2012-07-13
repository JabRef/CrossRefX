package container;

import java.util.Vector;


public class ContainerObject {
	
	private int id;
	private int typeId;
	private String key;
	private Vector<ContainerAttribute> attribut = null;
	
	public ContainerObject (int idToSet, int typeIdToSet, String keyToSet, Vector<ContainerAttribute> myAttributToSet){ 
		id = idToSet;
		typeId = typeIdToSet;
		key = keyToSet;
		attribut = myAttributToSet;
	}	
	
	public Vector<ContainerAttribute> getAttributes (){
		return attribut;
	}
	
	public int getId() {return id;}
	public int getTypeId() {return typeId;}
	public String getKey() {return key;}
}
