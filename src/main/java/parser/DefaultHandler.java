package parser;

import java.util.Vector;

import container.ContainerDefault;
import database.Db;

public class DefaultHandler {
	private Vector<ContainerDefault> defaultVec;
	
	/**
	 * @author lischkls
	 * Constructor for CTex_DefaultHandler
	 * Defaults are attributes, which are significant for all entries
	 */
	public DefaultHandler(){
		defaultVec = new Vector<ContainerDefault>();
	}

	/**
	 * @author lischkls
	 * @param typeId Type ID - ID of the Type form the attribute to add
	 * @param attID - ID of the attribute value
	 * @return position of the new default
	 * Adds an new default to the Defaultvector
	 * please notice: an other Default with the same typeId will be overwritten
	 */
	public int addDefault(int typeId, int attID){
		ContainerDefault def = new  ContainerDefault(typeId, attID);
		return addDefault (def);
	}
	
	/**
	 * @author lischkls
	 * @param def ContainerDefault to add to defaultVec
	 * @return position of the new default in the defaultVec
	 * Adds an new default to the Defaultvector
	 * please notice: an other Default with the same typeId will be overwritten
	 */
	public int addDefault(ContainerDefault def){
		for (int i = 0; defaultVec.size() > i; i++){
			if (defaultVec.elementAt(i).getType() == def.getType()){
				defaultVec.setElementAt(def, i);
				return i;
			}
		}
		defaultVec.add(def);
		return defaultVec.indexOf(def);
	}

	/**
	 * @author lischkls
	 * @param typeId  to add not default with typeID to an entry
	 * by entry defined types should no default added
	 */
	public void setNotToLink (int typeID){
		for (int i=0; defaultVec.size() > i; i++){
			if (defaultVec.elementAt(i).getType() == typeID){
				defaultVec.setElementAt(defaultVec.elementAt(i).setLink(false), i);
			}
		}
	}
	
	/**
	 * @author lischkls
	 * Set all defaults to be set
	 */
	public void resetLink(){
		for (int i=0; defaultVec.size() < i; i++){
			if (defaultVec.elementAt(i).getLink() == false){
				defaultVec.setElementAt(defaultVec.elementAt(i).setLink(true), i);
			}
		}
	}

	/**
	 * @author lischkls
	 * @param theDb Database
	 * @param entryTypeId ID of Entry which should get defaults
	 * @param ObjID 
	 * Add links between defaults and entry in db
	 */
	public void addDefaulsToDb(Db theDb,int entryTypeId, int ObjID){
		for (int i=0; defaultVec.size() > i; i++){
			if (defaultVec.elementAt(i).getLink()){
				theDb.addTypeLink(entryTypeId, defaultVec.elementAt(i).getType(), false);
				theDb.addObjectLink(ObjID, defaultVec.elementAt(i).getAttribute());
			}
		}
	}
	
}
