package container;

import java.util.Vector;

public class ContainerAttribute {
	private int id = -1;
	private int typeId = -1;
	private String value = "";
	private boolean link = false;
	Vector<ContainerAttribute> condition = new Vector<ContainerAttribute>();
	
	public ContainerAttribute (){};
	public ContainerAttribute (int idToSet, int typeIdToSet, String valueToSet, boolean linkToSet, Vector<ContainerAttribute>  attributToSet){ 
		id = idToSet;
		typeId = typeIdToSet;
		value = valueToSet;
		link = linkToSet;
		condition = attributToSet;
	}	
	
	public int getId() {return id;}
	public int getObjectTypeId() {return typeId;}
	public String getValue() {return value;}
	public boolean getLink() {return link;}
	public Vector<ContainerAttribute> getCondition(){return condition;};
}
