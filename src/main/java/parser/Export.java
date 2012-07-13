package parser;

import java.util.Iterator;
import java.util.Vector;

import container.ContainerInclude;
import container.ContainerObject;


import database.Db;

public class Export {
	
	/**
	 * @see Export # open_File
	 * only if a file with this name does not yet exist it will create a new one otherwiese it will open the old one
	 * @author SamyStyle
	 * @version 0.1
	 * @param File_Path
	 * @return OutputStream
	 * @throws java.io.FileNotFoundException
	 */
	
	FileConnector f = new FileConnector();
	String data;
	boolean returnValue;
	
	public boolean exportToFile (Db theDb, String to, String file_Path){
		// Adds a file ending if there is none yet
		if (file_Path.endsWith(".bib")) {
		} else if (!file_Path.endsWith(".xtx")) {
			file_Path = file_Path.concat(".xtx");
		}
		f.createFile(file_Path);
		f.openStreamOut();
		
		if (to == "xtx") {
			returnValue =  exportCrosstexFile (theDb);
		} else if (to == "bib") {
			returnValue =  exportBibtexFile (theDb);
		}
		else { 
			returnValue =  false; 
		}
		f.closeStreamOut();
		
		return returnValue;
	}
	
	
	private boolean exportCrosstexFile (Db theDb){
		//include
		Vector<ContainerInclude> allInclude = theDb.getAllInclude();
		Iterator<ContainerInclude> includeIterator = allInclude.iterator();
		while (includeIterator.hasNext()){
			f.write("@include " + includeIterator.next().getValue());
		}
			
		Vector<ContainerObject> allObject = theDb.getAllObject();
		Iterator<ContainerObject> myIterator = allObject.iterator();
		ContainerObject obj  = null;
	
		
		while (myIterator.hasNext()){
			obj = myIterator.next();
			f.write((new ParseToString(theDb, obj)).getXtx() + "\n");
		}			
		return true;
	}
	
	private boolean exportBibtexFile (Db theDb){
		//include
		Vector<ContainerInclude> allInclude = theDb.getAllInclude();
		Iterator<ContainerInclude> includeIterator = allInclude.iterator();
		while (includeIterator.hasNext()){
			f.write("@include " + includeIterator.next().getValue());
		}
			
		Vector<ContainerObject> allObject = theDb.getAllObject();
		Iterator<ContainerObject> myIterator = allObject.iterator();
		ContainerObject obj  = null;
	
		
		while (myIterator.hasNext()){
			obj = myIterator.next();
			f.write((new ParseToString(theDb, obj)).getBib() + "\n");
		}			
		return true;
	}

}
