package container;

public class ContainerDefault {
	
	private int type, attribute;
	private boolean link;
	
	public ContainerDefault(){
		type=attribute=0;
		link=true;
	}
	
	public ContainerDefault(int settype,int setattribute){
		type = settype;
		attribute = setattribute;
		link = true;
	}
	
	public ContainerDefault setLink(boolean set){
		link = set;
		return this;
	}
	
	public boolean getLink(){return link;}
	
	public int getType(){return type;}
	
	public int getAttribute(){return attribute;}
}
