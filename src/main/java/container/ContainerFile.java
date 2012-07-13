package container;

public class ContainerFile {
	
	private int id = -1;
	private String description = "", link = "", type = "";
	
	public ContainerFile (int id, String description, String link, String type){
		this.id = id;
		this.link = link;
		this.description = description;
		this.type = type;
	}
	public ContainerFile (String description, String link, String type){
		this.description = description;
		this.link = link;
		this.type = type;
	}
	public int getId (){return id;}
	public String getDescription (){return description;}
	public String getLink (){return link;}
	public String getType (){return type;}
}
