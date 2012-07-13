package container;

public class ContainerIni {
	
	private String var, value;
	
	public ContainerIni (String var, String value){
		this.var = var;
		this.value = value;
	}
	
	public String getVar (){ return var;}
	public String getValue (){ return value;}
	public void setValue (String value){ this.value = value;}
	
}
