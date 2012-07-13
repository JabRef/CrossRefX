package parser;

import java.util.Vector;

import container.ContainerIni;



public class ParserIni {
	private enum myStates {START, EQL, VAR, BRAKE};
	private myStates theState;
	FileConnector file;
	private String text = "", info = "", info1 = "", info2 = "";
	
	
	public ParserIni () {
		file = new FileConnector("app.ini");
		file.readFileContent();
		text = file.getFileContent();
	}
	
	public Vector<ContainerIni> readIni() {
		Vector<ContainerIni> returnVector = new Vector<ContainerIni>();
		int counter = 0;
		theState = myStates.START;
		while (counter != text.length()){
			if (theState == myStates.START){
				if (text.charAt(counter) != '=') {
					info += text.charAt(counter);
				}
				else {
					info1 = info;
					info = "";
					theState = myStates.VAR; 
				}
			}
			else if (theState == myStates.VAR){
				if (text.charAt(counter) == '\n' || counter == text.length()){
					info2 = info;
					info = "";
					theState = myStates.START; 
					returnVector.add( new ContainerIni(info1, info2));
				}
				else {
					
					info += text.charAt(counter);
				}
			}
			
			counter++;
		}
		returnVector.add( new ContainerIni(info1, info));
		return returnVector;	
	}
}
