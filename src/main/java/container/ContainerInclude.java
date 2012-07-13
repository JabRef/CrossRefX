package container;

public class ContainerInclude {
	private int id;
	private String value;
	private boolean delete;
	
	public ContainerInclude (int id, String value){ 
		this.id = id;
		this.value = value;
		this.delete = false;
	}
	
	public ContainerInclude (int id, String value, boolean delete){ 
		this.id = id;
		this.value = value;
		this.delete = delete;
	}	
	
	public int getId() {return id;}
	public String getValue() {return value;}
	public boolean getDelete() {return delete;}
	
	public void setDelete(boolean delete) {this.delete = delete;}
}
