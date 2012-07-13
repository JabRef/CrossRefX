package parser;

import java.util.Iterator;
import java.util.Vector;

import container.ContainerAttribute;
import container.ContainerFile;
import container.ContainerObject;
import database.Db;

public class ParseToString {
	private String data;
	private final ContainerObject obj;
	private final Db theDb;
	
	public ParseToString (Db theDb, ContainerObject obj){
		this.theDb = theDb;
		this.obj = obj;
	}
	
	public String getXtx (){
		ContainerAttribute att, con;
		Vector<ContainerAttribute> attAll, conAll;
		Iterator<ContainerAttribute> attI, conI;
		Vector<ContainerFile> f = theDb.getAllFiles(obj.getId());
		data = "";
		data = data.concat("@" + theDb.convTypeIdToTypeName(obj.getTypeId()) + "{" + obj.getKey() + ",\n");				

		attAll = obj.getAttributes();
		attI = attAll.iterator();
		while (attI.hasNext()){
			att = attI.next();
			if (!att.getValue().equalsIgnoreCase("")){
				if (att.getCondition().size() == 0){
					if (att.getLink()){
						data = data.concat("   " + theDb.convTypeIdToTypeName(att.getObjectTypeId()) + " = " + att.getValue() + "");
					} else {
						data = data.concat("   " + theDb.convTypeIdToTypeName(att.getObjectTypeId()) + " = \"" + att.getValue() +"\"");
					}
					
						
				} else {
					data = data.concat("   [ " + theDb.convTypeIdToTypeName(att.getObjectTypeId()) + " = \"" + att.getValue() +"\" ]");
					conAll = att.getCondition();
					conI = conAll.iterator();
					while (conI.hasNext()){
						con = conI.next();
						data = data.concat(" " + theDb.convTypeIdToTypeName(con.getObjectTypeId()) + " = \"" + con.getValue() +"\"");
						if (conI.hasNext()){
							data = data.concat(", ");
						}
					}
				}
				
				if (attI.hasNext()|| f.size() != 0){
					data = data.concat(",\n");
				} else{
					data = data.concat("\n");
				}
			}
			
		}
		//if Files are avliebale add
		Iterator<ContainerFile> fi = f.iterator();
		if (f.size() != 0){
			data = data.concat("   file = \"");
			while (fi.hasNext()){
				ContainerFile e = fi.next();
				data = data.concat(e.getDescription() + ":" + e.getLink() + ":" + e.getType());
				if (fi.hasNext()){
					data = data.concat(";");
				}
			}
			data = data.concat("\"");
		}
		
		data = data.concat("\n}\n");
		return data;
	}
	
	public String getBib (){
		ContainerAttribute att, con;
		Vector<ContainerAttribute> attAll, conAll;
		Iterator<ContainerAttribute> attI, conI;
		data = "% Generated with Crosstex Tool \n";
		data = data.concat("@" + theDb.convTypeIdToTypeName(obj.getTypeId()) + "{" + obj.getKey() + ",\n");				

		attAll = obj.getAttributes();
		attI = attAll.iterator();
		while (attI.hasNext()){
			att = attI.next();
			if (!att.getValue().equalsIgnoreCase("")){
				if (att.getCondition().size() == 0){
					if (att.getLink()){
						data = data.concat("   " + theDb.convTypeIdToTypeName(att.getObjectTypeId()) + " = " + att.getValue() + "");
					}
				    else {
						data = data.concat("   " + theDb.convTypeIdToTypeName(att.getObjectTypeId()) + " = \"" + att.getValue() +"\"");
					}
					if (attI.hasNext()){
						data = data.concat(",\n");
					} else{
						data = data.concat("\n");
					}
						
				} else {
					data = data.concat("   " + theDb.convTypeIdToTypeName(att.getObjectTypeId()) + " = \"" + att.getValue() +"\"");
					conAll = att.getCondition();
					conI = conAll.iterator();
					while (conI.hasNext()){
						con = conI.next();
						data = data.concat(" " + theDb.convTypeIdToTypeName(con.getObjectTypeId()) + " = \"" + con.getValue() +"\"");
						if (conI.hasNext()){
							data = data.concat(",\n");
						} else{
							data = data.concat("\n");
						}
					}
					data = data.concat("\n");
				}
			}
			
		}
		data = data.concat("}\n");
		return data;
	}
}
