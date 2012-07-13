package parser;

import javax.swing.JOptionPane;

import com.mysql.jdbc.PacketTooBigException;

import ctex.Main;
import gui.Frame;
import database.Db;

/** 
 * Parser-functions for CrossTex-Files
 * @param input - String to analyse
 * 
 * @author lischkls
 * @version 0.1
 */

public class Parser  {
	
	private Frame frame = null;
	
	private enum states {START, INCDEFORENTRY, ENTRYTYPE, KEY, CONDORATTTYPE, 
		ATTTYPE, INFOTYPE, INFOQUATE, INFOPARAGRAPH1, INFOPARAGRAPH2, 
		INFOWITHOUT, COND, CONDTYPE, CONDVAL, CONDATTTYPE, CONDATTVAL, INCLUDE,
		DEFAULTtyp, DEFAULTval, DEFAULTvaltype, DEFAULTobj, DEFAULTobjval, COMMENT};
	private states state;
	private String Text;

	public Parser(String input){
		Text = input;
		state = states.START;
	}
	
	final boolean debug = false;
	final boolean debug2 = false;
	
	protected void print (String value){
    	if (debug){
    		System.out.println(value);
    	}
    }
	protected void print2 (String value){
    	if (debug2){
    		System.out.println(value);
    	}
    }

	/** 
	 * Delete doube spaces, new lines or tabs
	 * @param String information: String which have to be without double spaces, new lines or tabs. 
	 * @return String without multible spaces, new lines or tab
	 * 
	 * @author lischkls
	 * @version 0.1
	 */

	protected String textOnly(String information){
				
		String text = new String ();
		//trim to eliminate spaces at the begining and the end
		information = information.trim();
		//space is false if last sign is not a sign space, new line or tab 
		Boolean space = false;
		for (int l = 0; l < information.length(); l ++ ) {
			if (information.charAt(l) == ' ' || information.charAt(l) == '\n' || information.charAt(l) == '\t') {
				// replace new line and tab by space and set space to true - one space 
				if (space == false){
					text = text.concat(" ");
					space = true;
				}
			// reset last sign is not a space
			}else{
				space = false;
				text = text.concat(Character.toString(information.charAt(l)));
			}
		}
		return text;	
	}
	
	/** 
	 * proofs if the next sign without the outs charcters is the should charcater
	 * @param String text: String to proof
	 * @param char should: The character which should be the next without looking at outs
	 * @param char outs: Characters which should not looked at it 
	 * @return return the position of the sign if it is true, if not -1
	 * 
	 *  @author lischkls
	 * @version 0.1
	 */
	protected int isNextSignWithout(String text,char should, char ... outs){
	
	int i = 0;
	//look to the hole string
		while (i < text.length()){
			// if the characters match, return position
			if (text.charAt(i) == should){
				return i;
			}
			//otherwise check outs
			else{
				int n = 0;
				boolean out=false;
				//goto all outs, until chracter is one of the outs
				while ((n < outs.length) &&  out == false){
					if (text.charAt(i) == outs[n]){
						out = true;
					}else{
						n++;
					}
				}
				// if it is a out, go next, else return -1
				if (out == true){
					i++;
				}else{
					return -1;
				}
			}
		}
		// if in the hole string only outs, return -1
		return -1;
	}
	
	/** 
	 * proofs if the string is only numeric with or without dots
	 * @param String text: String to proof
	 * @param boolean dotted: true if it could be a floatingpoint number
	 * @return true if it is only a number
	 * 
	 *  @author lischkls
	 * @version 0.1
	 */
	protected boolean isStringNumeric(String text, boolean dotted){
		boolean dot=false;
		//look to the hole string
		for (int i = 0; i< text.length(); i++){
			//if the character at position i is not a number:
			if (!Character.isDigit(text.charAt(i))){
				//if it have to be an integer, the floating dot is already found or it is not a dot, it can not be a number
				if (!dotted || dot || text.charAt(i) !='.'){
					return false;
				//if it can be a floatingpoint number, the floatingpoint is not found until jet, an it is a dot, set dot to true
				}else if (dotted && !dot && text.charAt(i) =='.'){
					dot=true;
				}
			}
		}
		return true;
	}

	//Edits information, defVec, theDb
	/** 
	 * Write attribute with to database.
	 * if the string is numeric it is not a link. otherwise it is.
	 * @param Db theDb: Database to load data in it
	 * @param String information: Information 
	 * @param DefaultHandler defVec: default handling
	 * @param int counter: Position at global text string
	 * @param int entryId: ID of the entry to which add the attribute
	 * @param int typeId: ID of the attribute type
	 * @param int comma: End position of given attribute, if it ends with a comma, otherwise -1
	 * @param int bracket: End position of given attribute, if it ends with a bracket, otherwise -1
	 * @return the position of the next information
	 * attention: Manipulates state, defVec and theDb!
	 * 
	 * @author lischkls
	 * @version 0.1
	 */
	protected int setAttributeValueToDbGoNext (Db theDb, boolean checkOnly, String information, DefaultHandler defVec, int counter, int entryTypeId,int entryId, int typeId, int comma, int bracket){
		int attributeId, ObjectLinkId;
	
		information = textOnly(information);
		if (checkOnly == false){
			if (theDb.convTypeIdToTypeName(typeId).equals(Main.fileField)){
				try {
					theDb.addFile(information, entryId);
				} catch (PacketTooBigException e) {
					JOptionPane.showMessageDialog(null,
							Main.myLang.getString(Main.myLang.getString("textFildTab.FileSizeToSmall.message")),
							Main.myLang.getString(Main.myLang.getString("textFildTab.FileSizeToSmall.title")),
							JOptionPane.ERROR_MESSAGE);
				}
			}else{
				// Add Information (e.g. the name of the author) to database
				print2 ("addAttribute " + information);
				attributeId = theDb.addAttribute(typeId, information, false);
	
				//Link Attribute to Entry
				print2 ("ObjectLinkId: " + " ObejktId " + entryId +" attributeId "+ attributeId);
				ObjectLinkId=theDb.addObjectLink(entryId, attributeId);
				print2 ("OID:" + ObjectLinkId);
			}
		}
		//if the entry is at the end, add defaults, set state at START and counter to the next position
		
		if (bracket != -1) {
			int nextComma=isNextSignWithout(Text.substring(counter+bracket+1), ',', ' ', '\n');
			if (nextComma==-1){
				if (checkOnly == false){
					defVec.addDefaulsToDb(theDb,entryTypeId, entryId);
					defVec.resetLink();					
				}
				state = states.START;
				return counter +  bracket+1;
			}
			else{
				state=states.CONDORATTTYPE;
				return counter + nextComma + bracket+1;
			}
			
		}
		//selct next state, q5 if the entry is not finish and more attributes will follow
		// set counter to the right position

		else{
			state = states.CONDORATTTYPE;
			return counter + comma+1;
		}	
	}
	/** 
	 * Write attribute without paragraphs to database.
	 * if the string is numeric it is not a link. otherwise it is.
	 * @param Db theDb: Database to load data in it
	 * @param String information: Information 
	 * @param DefaultHandler defVec: default handling
	 * @param int entryId: ID of the entry to which add the attribute
	 * @param int typeId: ID of the attribute type
	 * @return true
	 * 
	 * @author lischkls
	 * @version 0.1
	 */
	protected void setAttribueValueWithoutLimits(Db theDb, boolean checkOnly, String information, DefaultHandler defVec, int entryId, int typeId){
		if (checkOnly == false){
			int attributeId;
			information = textOnly(information);
			print2("addAttribute " + information);
			if (isStringNumeric(information, true)){
				attributeId = theDb.addAttribute(typeId, information, false);
		
			}else{
				attributeId = theDb.addAttribute(typeId, information, true);
			}
			theDb.addObjectLink(entryId, attributeId);
		}
	}

	/** 
	 * Proof of xtx-syntax 
	 * @return true if the given string is a correct xtx-database
	 * @param Db theDb: Database to load data in it
	 * @author lischkls
	 * @version 0.1
	 */
	public boolean proof(Db theDb, boolean checkOnly) throws ParserException{
		DefaultHandler defVec = new DefaultHandler();
		String information = new String();
		int counter = 0;
		int entryTypeId = -1, entryId = -1, typeId = -1, attributeId =-1,
		condAttributeId =-1, defType=-1, defAttribute=-1, defObj=-1;
		int bracketopencounter=0;
		int sign = 0;
		int line = 1;
		if (frame != null){
			frame.setBarText(3, "0.0");
		}
		int proCounter = 0, proMax = Text.length(), proSave = 0;
		float proTest;
		while (counter != Text.length()){
			
			//counting lines and signs for errors
			if (Text.charAt(counter) == '\n'){
				line++;
				sign = 1;
			}else sign++;
			
			print(counter + " : " + Text.charAt(counter)+  " : " + state);
			//Ignore comments, only they are not in strings
			if (Text.charAt(counter) =='%' && 
					(state != states.INFOPARAGRAPH1 &&
					state != states.INFOPARAGRAPH2 &&
					state != states.INFOPARAGRAPH2)){
				if(counter>0){
					if (Text.charAt(counter-1)!='\\'){				
						counter = Text.indexOf(Character.toString('\n'), counter);
					}
				}else counter = Text.indexOf(Character.toString('\n'), counter);
			}
			
			if (state == states.START){

				entryTypeId = entryId = typeId = -1;
				information = new String ();
				if (Text.charAt(counter) == '@') state = states.INCDEFORENTRY;
				else if (Text.charAt(counter) != ' ' && 
						Text.charAt(counter) != '\n'){
					throw new ParserException("error at file line/ sign: " + line + "/ " + sign + Text.substring(counter-4, counter-4) + " \n");
				}
			}
			
			else if (state == states.INCDEFORENTRY){
				//@include
				if (Text.substring(counter, (counter+7)).toLowerCase().compareTo("include") == 0){
					//counter increase about 7 steps, because "include" have 7 characters
					counter += 7;
					sign += 7;
					state= states.INCLUDE;
				}
				//@default
				else if (Text.substring(counter, (counter+7)).toLowerCase().compareTo("default") == 0){
					//counter increase about 7 steps, because "default" have 7 characters
					counter += 7;
					sign += 7;
					state= states.DEFAULTtyp;
				} 
				//@comment
				else if (Text.substring(counter, (counter+7)).toLowerCase().compareTo("comment") == 0){
					counter += 7;
					sign += 7;
					int bracket = isNextSignWithout (Text.substring(counter), '{', ' ', '\n');
					if (bracket != -1){
						bracketopencounter=1;
						counter += bracket + 1;
						state = states.COMMENT;
					}
					
				}
				//Error if illegal character {
				else if (Text.charAt(counter) == '{') throw new ParserException("error at file line/ sign: " + line + "/ " + sign + " } is not allowed jet.");
				//Normal case
				else if (Text.charAt(counter) != ' ' && 
						Text.charAt(counter) != '\n'){
						information = new String(Character.toString(Text.charAt(counter)));
					
						state = states.ENTRYTYPE;
				}
			}

			else if (state == states.ENTRYTYPE){
				//End of entrytype:
				if (Text.charAt(counter) == '{'){

					// Add Entrytype (like book) to database
					if(checkOnly == false){
						entryTypeId = theDb.addType(textOnly(information).toLowerCase(), false, true, true);
						print2 ("addType " + textOnly(information) + " => " + entryTypeId);						
					}
					information= new String();
					state = states.KEY;
				}
				//Error - @
				else if (Text.charAt(counter) == '@') throw new ParserException("error at file line/ sign: " + line + "/ " + sign + " @ is not allowed jet.");
				//Character of the entrytype name
				else {
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
			}
			//COMMENT
			else if (state == states.COMMENT){
				if (Text.charAt(counter) == '{') bracketopencounter++;
				else if(Text.charAt(counter) == '}') bracketopencounter--;
				
				if (bracketopencounter == 0){
					state = states.START;
				}
			}

			//Entrykey
			else if (state == states.KEY){
				
				if (counter + 1 < Text.length()){
					int comma = isNextSignWithout(Text.substring(counter), ',', ' ', '\n');
					if (comma != -1){
						counter += comma+1;

						//  Add Entrykey to database:
						if (checkOnly == false){
							entryId = theDb.addObject(entryTypeId, textOnly(information));
							print2("addObject " + information);							
						}
						state = states.CONDORATTTYPE;
						
					}else information = information.concat(Character.toString(Text.charAt(counter)));
		
				}else throw new ParserException("error at file line/ sign: " + line + "/ " + sign);
			}

			else if (state == states.CONDORATTTYPE){
				//if it is a condition go to q14
				if (Text.charAt(counter) == '[') state = states.COND;
				//Error
				else if (Text.charAt(counter) == '=') throw new ParserException("error at file line/ sign: " + line + "/ " + sign + " = not allowd jet");
				else if (Text.charAt(counter) != ' ' && 
						Text.charAt(counter) != '\n')
				{
					state = states.ATTTYPE;
					information = new String(Character.toString(Text.charAt(counter)));
					
				}
			}

			else if (state == states.ATTTYPE){
				if (Text.charAt(counter) == '='){
					if (checkOnly == false){
						//Add new information type (e.g. AUTHOR) to database
						typeId = theDb.addType(textOnly(information), false, false, true);
						defVec.setNotToLink(typeId);
						//Add link between Entrytype and Type to catch all optionals
						theDb.addTypeLink(entryTypeId, typeId, false);

						print2 ("addType " + textOnly(information) + " => " + typeId);						
					}
					
					state = states.INFOTYPE;
				}
				else {
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
			}
		
			else if (state == states.INFOTYPE){
				information = new String ();
				if (Text.charAt(counter) == '\"'){
					state = states.INFOQUATE;
				}
				else if (Text.charAt(counter) == '{'){
					bracketopencounter=1;
					state = states.INFOPARAGRAPH1;
				}
				else if (Text.charAt(counter) == '('){
					bracketopencounter=1;
					state = states.INFOPARAGRAPH2;
				}
				else if (Text.charAt(counter) != ' ' && 
						Text.charAt(counter) != '\n'){
						state = states.INFOWITHOUT;
						information = new String (Character.toString(Text.charAt(counter)));
	
				}

			}
			
			else if (state == states.INFOQUATE){
				if (counter + 1 < Text.length()){

					if (Text.charAt(counter) == '\"'){
						int comma = isNextSignWithout(Text.substring(counter+1), ',', ' ', '\n');
						int bracket = isNextSignWithout(Text.substring(counter+1), '}', ' ', ',' ,'\n');
					
						if (comma != -1 || bracket !=-1){

							//the information is only complite if  the last charcter is an quoat
							counter = 
								setAttributeValueToDbGoNext (theDb, checkOnly, information, defVec, counter, entryTypeId, entryId, typeId, comma, bracket);
		
						}else information = information.concat(Character.toString(Text.charAt(counter))); 
					}else information = information.concat(Character.toString(Text.charAt(counter)));
		
				}else throw new ParserException("error at file line/ sign: " + line + "/ " + sign + Text.substring(counter-4, counter-4));
			}

			else if (state == states.INFOPARAGRAPH1){
				if (counter + 1 < Text.length()){
					if (Text.charAt(counter) == '}') {
						bracketopencounter--;
						if (bracketopencounter == 0){
							int comma = isNextSignWithout(Text.substring(counter+1), ',', ' ', '\n');
							int bracket = isNextSignWithout(Text.substring(counter+1), '}', ' ', ',' ,'\n');

							if (comma != -1 || bracket !=-1){
								counter = setAttributeValueToDbGoNext (theDb, checkOnly, information, defVec, counter, entryTypeId, entryId, typeId, comma, bracket);
							
							}else throw new ParserException("error at file line/ sign: " + line + "/ " + sign);
						}else information = information.concat(Character.toString(Text.charAt(counter)));
					}else{
						information = information.concat(Character.toString(Text.charAt(counter)));
						if (Text.charAt(counter) == '{'){
							bracketopencounter++;
						}
						 
					}
				}else throw new ParserException("error at file line/ sign: " + line + "/ " + sign);
			}
		
			else if (state == states.INFOPARAGRAPH2){
				if (counter + 1 < Text.length()){
					if (Text.charAt(counter) == ')'){
						bracketopencounter--;
						if (bracketopencounter == 0){
							int comma = isNextSignWithout(Text.substring(counter+1), ',', ' ', '\n');
							int bracket = isNextSignWithout(Text.substring(counter+1), '}', ' ', ',' ,'\n');

							if (comma != -1 || bracket !=-1){
								counter = setAttributeValueToDbGoNext (theDb, checkOnly, information, defVec, counter, entryTypeId, entryId, typeId, comma, bracket);
							
							}else throw new ParserException("error at file line/ sign: " + line + "/ " + sign);
						}else information = information.concat(Character.toString(Text.charAt(counter)));
					}else{
						information = information.concat(Character.toString(Text.charAt(counter)));
						if (Text.charAt(counter) == '('){
							bracketopencounter++;
						}
						 
					}
				}else throw new ParserException("error at file line/ sign: " + line + "/ " + sign);
			}
			
			else if (state == states.INFOWITHOUT){
				//It is the end of the entry
				int bracket = isNextSignWithout(Text.substring(counter), '}', ' ', ',' , '\n');
				if (bracket != -1 && bracketopencounter == 0){
					counter += bracket;
					
						setAttribueValueWithoutLimits(theDb, checkOnly, information, defVec, entryId, typeId);
					if (checkOnly == false){
						defVec.addDefaulsToDb(theDb,entryTypeId, entryId);
						defVec.resetLink();						
					}
					state = states.START;
				}
				else if (counter + 1 < Text.length()){
					int comma = isNextSignWithout(Text.substring(counter), ',', ' ', '\n');
					if (comma != -1){
						counter += comma;
						setAttribueValueWithoutLimits(theDb, checkOnly, information, defVec, entryId, typeId);		
						bracketopencounter = 0;
						state = states.CONDORATTTYPE;
						
					} else{
						if (Text.charAt(counter) == '{') bracketopencounter++;
						else if (Text.charAt(counter) == '}') bracketopencounter--;
						information = information.concat(Character.toString(Text.charAt(counter)));
					}
				} else{
					if (Text.charAt(counter) == '{') bracketopencounter++;
					else if (Text.charAt(counter) == '}') bracketopencounter--;
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
			}

			//Conditions
			else if (state == states.COND){
				print2("q14");
				if (Text.charAt(counter) == '=') throw new ParserException("error at file line/ sign: " + line + "/ " + sign + Text.substring(counter-4, counter-4) + " q14 \n");
				else{
					state = states.CONDTYPE;
					information = new String(Character.toString(Text.charAt(counter)));
				}
			}

			else if (state == states.CONDTYPE){
				print2("CONDSECEND");
				if (Text.charAt(counter) == '='){
					if (checkOnly == false){
						//Add new information type (e.g. AUTHOR) to database
						typeId = theDb.addType(textOnly(information), false, false, true);
						//Add link between Entrytype and Type to catch all optionals
						theDb.addTypeLink(entryTypeId, typeId, false);
						print2("inforation: "+ information + " typeId " + typeId + " entryTypeId: "+ entryTypeId);						
					}

					information= new String();
					state = states.CONDVAL;
				}
				else {
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
			}
			

			//Condition Value
			else if (state == states.CONDVAL){
				if (Text.charAt(counter) == ']'){
					if (checkOnly == false){
						attributeId = theDb.addAttribute(typeId, textOnly(information), false);
						theDb.addObjectLink(entryId, attributeId);
						print2("information: " + information + " typeID: " + typeId+ " attributeId: " + attributeId);						
					}
					state = states.CONDATTTYPE;
					information=new String();
				}
				else
				{
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
			}

			//Type
			else if (state == states.CONDATTTYPE){

				if (Text.charAt(counter) == '='){
					if (checkOnly == false){
						typeId = theDb.addType(textOnly(information), false, false, true);
						//Add link between Entrytype and Type to catch all optionals
						theDb.addTypeLink(entryTypeId, typeId, false);
						print2("information: " + information + " typeId " +typeId + " entyTypeId " + entryTypeId);	
					}
					information = new String();
					state = states.CONDATTVAL;
				}
				else{
					information= information.concat(Character.toString(Text.charAt(counter)));
				}
			}
		    //Value

			else if (state == states.CONDATTVAL){

				int comma = isNextSignWithout (Text.substring(counter), ',', ' ');
				if (comma != -1){
					counter = counter + comma + 1;
					int bracked = isNextSignWithout (Text.substring(counter), '}', ' ', '\n');
					if (information.charAt(0)=='\"' && information.charAt(information.length()-1)=='\"'){
						information=information.substring(1, information.length()-1);
					}
					if (checkOnly == false){
						condAttributeId = theDb.addAttribute(typeId, textOnly(information), true);
						theDb.addAttributeLink(attributeId, condAttributeId);
						print2("information " + information + " condAttributeId " + condAttributeId);						
					}
					information = new String ();

					int findnewline = isNextSignWithout(Text.substring(counter), '\n', ' ');
					//End of Entry
					if (bracked != -1){
						state = states.START;
						counter = counter+bracked;

					}
					//End of condition

					else if (findnewline != -1){
						counter = counter+findnewline;
						state = states.CONDORATTTYPE;
						print2("goto CONDORATTTYPE");
					//End of End of fact
					}else{
						state = states.CONDATTTYPE;
						print2("goto CONDATTTYPE");
					}
				}else {
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
			}
			else if(state == states.INCLUDE){
				if (Text.charAt(counter)=='\n' || Text.length() == counter){
					if (checkOnly == false) theDb.addInclude(textOnly(information));

					state = states.START;
				}
				else if(Text.charAt(counter) == '@') throw new 
					ParserException("error at file line/ sign: " + line + "/ " + sign);	
				else if (Text.length()-1 == counter){
					information = information.concat(Character.toString(Text.charAt(counter)));
					if (checkOnly == false) theDb.addInclude(textOnly(information));
					state = states.START;
				}else information = information.concat(Character.toString(Text.charAt(counter)));
	
			}
			else if (state==states.DEFAULTtyp){
				if (Text.charAt(counter) == '='){
					//Save into the database
					if (checkOnly == false) defType=theDb.addType(textOnly(information), false, false, true);
					
					state=states.DEFAULTvaltype;
					information = new String();
				}else{
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
			}
			// decide if author= "Max Muster" or author={mm, "Max Muster"}
			else if (state == states.DEFAULTvaltype){
				if (Text.charAt(counter) == '\n' || Text.charAt(counter) == ' '){
					state = states.DEFAULTvaltype;
				}
				else if (Text.charAt(counter) == '{'){
					state = states.DEFAULTobj;
				}
				else {
					if(Text.charAt(counter) == '\"'){
						state=states.DEFAULTval;
					}
					else{
						information = new String(String.valueOf(Text.charAt(counter)));
						state=states.DEFAULTval;						
					}
				}
			}
			
			//author={mm, "Max Muster"}
			else if (state == states.DEFAULTobj){
				if (Text.charAt(counter) == ','){
					if (checkOnly == false) {
						//create an object from type deftype, with shortname information
						information = textOnly(information);
						defObj = theDb.addObject(defType, information);

						//add attribute name=link
						defAttribute = theDb.addAttribute(defType, information, true);
						defVec.addDefault(defType, defAttribute);
					}
					
					state = states.DEFAULTobjval;
				}else{
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
			}
			
			//Default Object value
			else if (state == states.DEFAULTobjval){
				if (Text.charAt(counter) == '}'){
					//Add attribute default-value to default-type, with attribute-type "name"
					if (checkOnly == false) 
						theDb.addObjectLink
							(defObj, theDb.addAttribute
									(theDb.addType
											("name", false, false, false), textOnly(information), true));
					state = states.START;

				}else{
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
				
			}
			// author= "Max Muster"
			else if (state == states.DEFAULTval){
				if (Text.charAt(counter) == '\n'){
					information = textOnly(information);
					if (information.charAt(information.length()-1) == '\"'){
						information = information.substring(0, information.length()-1);
					}
					if (checkOnly == false){
						defAttribute = theDb.addAttribute(defType, information, false);
						defVec.addDefault(defType, defAttribute);						
					}
					state=states.START;
				}
				else{
					information = information.concat(Character.toString(Text.charAt(counter)));
				}
			}

			counter++;
			//Prozess Counter
			proCounter++;
			proTest = (proCounter*100)/proMax;
			if (frame != null && proTest != proSave){
				proSave = (int)proTest;
				frame.setBarText(3, Main.myLang.getString("parser.progress") + proSave + "%");
			}
		}
		//DEBUG	
		print(state.name());
		print2("Parser END");
		if (frame != null){
			frame.setBarText(3, "");
		}
		return state == states.START;
	}
	
	public void setFrame(Frame frame){
		this.frame = frame;
	}
	
	public void finalize(){
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}