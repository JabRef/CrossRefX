package database;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.mysql.jdbc.PacketTooBigException;

import container.ContainerAttribute;
import container.ContainerFile;
import container.ContainerInclude;
import container.ContainerObject;
import container.ContainerType;


import ctex.Main;

//driver = "org.apache.derby.jdbc.EmbeddedDriver";   //DerbyDB driver
//final String Driver = "org.gjt.mm.mysql.Driver";   //MySQL Driver
//final String Driver = "oracle.jdbc.driver.OracleDriver";   //Oracle Driver

public abstract class Db {

	protected final boolean debug = false;
	protected final String   tableType				= "TYPE";
	protected final String[] tableTypeColum			= {"ID", "VALUE", "DELETABLE", "ISENTRYTYPE", "VISIBLE"};
	protected final String   tableTypeLink			= "TYPE_LINK";
	protected final String[] tableTypeLinkColum		= {"ID", "TYPE_1_ID", "TYPE_2_ID", "REQUIRED"};
	protected final String   tableObject			= "OBJECT";
	protected final String[] tableObjectColum		= {"ID", "TYPE_ID", "VALUE"};
	protected final String   tableObjectLink		= "LINK";
	protected final String[] tableObjectLinkColum	= {"ID", "OBJ_ID", "ATT_ID"};
	protected final String   tableAttribut			= "ATTRIBUTE";
	protected final String[] tableAttributColum		= {"ID", "TYPE_ID", "VALUE", "LINK"};
	protected final String   tableAttributLink		= "ATTRIBUTE_LINK";
	protected final String[] tableAttributLinkColum	= {"ID", "ATT_1_ID", "ATT_2_ID"};
	protected final String   tableInclude			= "INCLUCE";
	protected final String[] tableIncludeColum		= {"ID", "VALUE"};
	protected final String   tableFile              = "FILE";
	protected final String[] tableFileColum		    = {"ID", "DESCRIPTION", "LINK", "TYPE", "VALUE", "OBJ_ID"};
	//protected final String   pdfField			    = CTex_Main.pdfField;
	protected String driver;
	protected String url;
	protected String hostName;
	protected String portNumber;
	protected String dbName;
	protected String prifix;
	protected boolean connected = false;
	protected Connection connection = null;
	protected Properties properties = new Properties();
	
	public Db (String driver, String url, String hostName, String portNumber, String dbName){
		this.driver = driver;
		this.url = url;
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.dbName = dbName;
	}
	
	public boolean setPrifix (String prifix){
		this.prifix = prifix;
		return true;
	}
	
	public boolean createDatabase(Component frame){
		boolean overwrite = false, returnValue, isAsked = false;
		
		returnValue = createTableType(overwrite);
		if (returnValue == false){
			overwrite = showDialog (frame, Main.myLang.getString("dialog.db.empty.question"));
			isAsked = true;
			if (overwrite == true){
				returnValue = createTableType(overwrite);
			}			
		} 
		
		returnValue = createTableTypeLink(overwrite);
		if (returnValue == false && isAsked == false){
			overwrite = showDialog (frame, Main.myLang.getString("dialog.db.empty.question"));
			isAsked = true;
			if (overwrite == true){
				returnValue = createTableTypeLink(overwrite);
			}
		} 
		returnValue = createTableObject(overwrite);
		if (returnValue == false && isAsked == false){
			overwrite = showDialog (frame, Main.myLang.getString("dialog.db.empty.question"));
			isAsked = true;
			if (overwrite == true){
				returnValue = createTableObject(overwrite);
			}
		} 
		returnValue = createTableAttribut(overwrite);
		if (returnValue == false  && isAsked == false){
			overwrite = showDialog (frame, Main.myLang.getString("dialog.db.empty.question"));
			isAsked = true;
			if (overwrite == true){
				returnValue = createTableAttribut(overwrite);
			}
		} 
		returnValue = createTableLink(overwrite);
		if (returnValue == false && isAsked == false){
			overwrite = showDialog (frame, Main.myLang.getString("dialog.db.empty.question"));
			isAsked = true;
			if (overwrite == true){
				returnValue = createTableLink(overwrite);
			}
		} 
		returnValue = createTableAttributLink(overwrite);
		if (returnValue == false && isAsked == false){
			overwrite = showDialog (frame, Main.myLang.getString("dialog.db.empty.question"));
			isAsked = true;
			if (overwrite == true){
				returnValue = createTableAttributLink(overwrite);
			}
		}
		returnValue = createTableFile(overwrite);
		if (returnValue == false && isAsked == false){
			overwrite = showDialog (frame, Main.myLang.getString("dialog.db.empty.question"));
			isAsked = true;
			if (overwrite == true){
				returnValue = createTableFile(overwrite);
			}
		}
		returnValue = createTableInclude(overwrite);
		if (returnValue == false && isAsked == false){
			overwrite = showDialog (frame, Main.myLang.getString("dialog.db.empty.question"));
			isAsked = true;
			if (overwrite == true){
				returnValue = createTableInclude(overwrite);
			}
		} 
		return returnValue;
	}
	
	
	
	
	
	/**
	 * 
	 * @param code will be execute
	 * @param tableName 
	 * @param overwrite true if existing table can be delete
	 * @return
	 * @author SamyStyle
	 * @version v1.0
	 */

	protected boolean createNewTable (String code, String tableName, boolean overwrite) {
		try {
			//fals Taelle vorhanen pruefen
			ResultSet resultSet = connection.getMetaData().getTables(null, null, null, new String[] {"TABLE"});
			while (resultSet.next()) {
				if (resultSet.getString("TABLE_NAME").equalsIgnoreCase(tableName)) {
					if (overwrite == true){
						deleteTable (tableName);
					}
					else {
						return false;
					}
				}
				else {
				}
			}
			resultSet.close();
			//neue tabelle erstellen
			Statement State = connection.createStatement();
			State.execute(code);
			State.close();
			return true;
		} catch (SQLException exc) {
			print ("executeNewTable: " + exc.toString());
			return false;
		} 
	}
	
	protected abstract boolean createTableAttribut (boolean overwrite);
	protected abstract boolean createTableAttributLink (boolean overwrite);
	protected abstract boolean createTableFile (boolean overwrite);	
	protected abstract boolean createTableInclude (boolean overwrite);	
	protected abstract boolean createTableObject (boolean overwrite);
	protected abstract boolean createTableLink (boolean overwrite);
	protected abstract boolean createTableType (boolean overwrite);
	protected abstract boolean createTableTypeLink (boolean overwrite);
	
	
		
	/**
	 * 
	 * Add Attribute
	 * 
	 * @see Derby # addAttribute
	 * @param objectId
	 * @param attriubtId
	 * @return generated id, if error -1, -2 if key is empty
	 */
	public int addAttribute (int typeId, String key, boolean link){
		int generadtedId = 0;
		Statement stat;
		ResultSet res;
		if (key.isEmpty() == false) { 
			try {	
				stat = connection.createStatement();
				String sql = "INSERT INTO " + prifix + tableAttribut + " (" + tableAttributColum[1]+ ", " + tableAttributColum[2]+ ", " + tableAttributColum[3]+ ") VALUES (" + typeId + ", '" + key.replaceAll("'", "''") + "', " + this.convBooleanToInt(link) + ")";
				stat.execute(sql, Statement.RETURN_GENERATED_KEYS);
				res = stat.getGeneratedKeys(); 
			   	res.next();
			   	generadtedId = res.getInt(1); 
				return generadtedId;
			} catch (SQLException exc) {
				print ("addAttribute: " + exc.toString());
				return -1;
			}
		} else {
			print ("addAttribute: is Empty");
			return -2;
		}

			
	};
	
	public int addAttribute (String objKey, int typeId, String key, boolean link){
		int attId, objId;
		String sql;
		if (getObject(objKey) != null){
			objId = getObject(objKey).getId();
			try {
				Statement stat = this.connection.createStatement();
				sql = "SELECT " + tableAttribut + "." + tableAttributColum [0] + 
				" FROM " + tableAttribut + " INNER JOIN " + tableObjectLink + 
				" ON " + tableAttribut + "." + tableAttributColum[0] + " = " + tableObjectLink + "." + tableObjectLinkColum[2] + 
				" WHERE " + tableObjectLink + "." + tableObjectLinkColum[1] + " = " + objId +
				" AND " + tableAttribut + "." + tableAttributColum[1]  + " = " + typeId +
				" AND " + tableAttribut + "." + tableAttributColum[2]  + " = " + key +
				" AND " + tableAttribut + "." + tableAttributColum[3]  + " = " + this.convBooleanToInt(link);
				ResultSet resultSet = stat.executeQuery(sql);
				resultSet.next();
				attId = resultSet.getInt(1);
				resultSet.close();
				stat.close();
				//attId for Update
			} catch (SQLException exc1){
				attId = addAttribute (typeId, key, link);
				addAttributeLink (objId, attId);
			}
			return attId;
		} else {
			//objKey dose not exists
			return -1;
		}
	}
	
	
	/**
	 * Add Link between an Object and 
	 * 
	 * @see Derby # addAttrbuteLink 
	 * @param obj1ID form
	 * @param obj2ID to
	 * @return generated id, if error -1
	 * @author SamyStyle
	 */
	public int addAttributeLink (int attributId1, int attributId2) {
		int id = 0, generadtedid = 0;
		try {
			Statement stat = this.connection.createStatement();
			String sql = "SELECT " + tableAttributLinkColum[0] + " FROM " + prifix + tableAttributLink + " WHERE " + tableAttributLinkColum[1] + " = " + attributId1 + " AND "+ tableAttributLinkColum[2] + " = " + attributId2 + "";
			ResultSet resultSet = stat.executeQuery(sql);
			resultSet.next();
			id = resultSet.getInt(1);
			resultSet.close();
			stat.close();	   
			return id;
		} catch (SQLException exc1) {
			//print ("addAttributeLink: T: " + exc1.toString());
			try {
				Statement stat = connection.createStatement();
				String sql = "INSERT INTO " + prifix + tableAttributLink + " (" + tableAttributLinkColum[1] + ", " + tableAttributLinkColum[2] + ") VALUES (" + attributId1 + ", " + attributId2 + ")";
				stat.execute(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet res = stat.getGeneratedKeys(); 
				res.next();
				generadtedid = res.getInt(1);
				stat.close();
				return generadtedid;
			} catch (SQLException exc2) {
				print ("addAttributeLink: " + exc2.toString());
				return -1;
			}
		}		
	}
	
	/**
	 * 
	 * @param e
	 * @param objId
	 * @return -2 Size of files is to tiny
	 */
	public int addFile (ContainerFile e, int objId) throws PacketTooBigException{ 
		int generadtedid = 0;
		FileInputStream fis;
		Statement stat;
		String sql;
		try {
			stat = connection.createStatement();
			connection.setAutoCommit(false);
			sql = "INSERT INTO " + prifix + tableFile + " (" + tableFileColum[1] + ", " + tableFileColum[2] + ", " + tableFileColum[3] + ", " + tableFileColum[4] + ", " + tableFileColum[5] + ") VALUES (?, ?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, e.getDescription());
			stmt.setString(3, e.getType());
			File f = new File(e.getLink());
			stmt.setString(2, f.getName());
			
			fis = new FileInputStream(f);
			stmt.setBinaryStream(4, fis, (int) f.length());
			stmt.setInt(5, objId);
			stmt.execute();
			try {
				fis.close();
			} catch (IOException e1) {
				print ("addFile e1: " + e1.toString());
			}
			connection.commit();
			stat.close();
			connection.setAutoCommit(true);
			return generadtedid;
		} catch (FileNotFoundException exc1){
			try {
				stat = connection.createStatement();			
				sql = "INSERT INTO " + prifix + tableFile + " (" + tableFileColum[1] + ", " + tableFileColum[2] + ", " + tableFileColum[3] + ", " + tableFileColum[5] + ") VALUES ('" + e.getDescription() + "', '" + e.getLink() + "', '" + e.getType() + "', " + objId + ")";
				stat.execute(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet res = stat.getGeneratedKeys(); 
				res.next();
				generadtedid = res.getInt(1);
				stat.close();
				return generadtedid;
			} catch (SQLException exc2) {
				print ("addFile exc2: " + exc2.toString());
				return -1;
			}
		} catch (PacketTooBigException exc4){
			throw new PacketTooBigException(0,0);
		}catch (SQLException exc3) {
			print ("addFile exc3: " + exc3.toString());
			return -1;
		}
	}

	public void addFile (String s, int id) throws PacketTooBigException{
		String[] a = s.split(";");
		for (int i = 0; i < a.length; i++){
			String[] a1 = a[i].split(":");
			ContainerFile e = new ContainerFile(a1[1], a1[0], a1[2]);
			addFile(e, id);
		}
	}
	
	/**
	 * Add Value into include table
	 * @param value 
	 * @return generated id, if error -1
	 * @author SamyStyle
	 */
	public int addInclude (String value)  {
		try {
			int generadtedid = 0;
			Statement stat = connection.createStatement();
			String sql = "INSERT INTO " + prifix + tableInclude + " (VALUE) VALUES ('" + value.replaceAll("'", "''") + "')";
		  	stat.execute(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet res = stat.getGeneratedKeys(); 
			res.next(); 
		   	generadtedid = res.getInt(1);
		   	stat.close();
			return generadtedid;
		} catch (SQLException exc) {
			print ("addInclude:" + exc.toString());
			return -1;
		}
	}

	/**
	 * 
	 * Add an Object into Object table
	 * 
	 * @see Derby # addObject
	 * @param typeId the Type id of the Key
	 * @param Key to set
	 * @return number of the object if error -1, -2 if key is empty
	 * @author SamyStyle
	 */
	public int addObject (int typeId, String key)  {
		if (key.isEmpty() == false) { 
			if (getObject(key) == null){
				try {
					int generadtedid = 0;
					Statement stat = connection.createStatement();
					String sql = "INSERT INTO " + prifix + tableObject + " (TYPE_ID, VALUE) VALUES (" + typeId + ", '" + key.replaceAll("'", "''") + "')";
				  	stat.execute(sql, Statement.RETURN_GENERATED_KEYS);
					ResultSet res = stat.getGeneratedKeys(); 
				   	res.next();
				   	generadtedid = res.getInt(1); 
				   	stat.close();
				   	
					return generadtedid;
				} catch (SQLException exc) {
					print("addObject:" + exc.toString());
					return -1;
				}
			} else {
				return getObject(key).getId();
		}
		} else {
			return -2;
		}
		
	}

	/**
	 * 
	 * Add Link between Object and an Attribute into ObjectLinkTable
	 * 
	 * @see Derby # addObjectLink
	 * @param objectId
	 * @param attriubtId
	 * @return number of the link if error -1
	 * @author SamyStyle
	 */
	public int addObjectLink (int objectId, int attriubtId) {
		Statement stat;
		int generadtedid = 0;
		try {
			stat = connection.createStatement();
			String sql = "INSERT INTO " + prifix + tableObjectLink + " (OBJ_ID, ATT_ID) VALUES (" + objectId + ", " + attriubtId + ")";
			stat.execute(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet res = stat.getGeneratedKeys(); 
			res.next();
			generadtedid = res.getInt(1);			
			stat.close();
			return generadtedid;
		} catch (SQLException exc2) {
			print ("addObjectLink: " + exc2.toString());
			return -1;
		}
	}

	/**
	 * Add a type into Type Table
	 * 
	 * @param Value of the Type
	 * @param deleteble if its deleteble
	 * @param isEntryType if its a EntryType
	 * @return number of the type if error -1
	 * @author SamyStyle
	 */
	public int addType (String Value, boolean deleteble, boolean isEntryType, boolean visible) {
		
		if (isExistingTyp(Value) != -1){
			return isExistingTyp(Value);
		}
		else {
			try {
				int generadtedid = 0;
				Statement stat = connection.createStatement();
				String sql = "INSERT INTO " + prifix + tableType + " (" + tableTypeColum[1] + ", " + tableTypeColum[2] + ", " + tableTypeColum[3] + ", " + tableTypeColum[4] + ") VALUES ('" + Value.replaceAll("'", "''") + "', " + convBooleanToInt(deleteble) + ", " + convBooleanToInt(isEntryType) + ", " + convBooleanToInt(visible) + ")";
				stat.execute(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet res = stat.getGeneratedKeys(); 
				res.next();
				generadtedid = res.getInt(1);
				stat.close();
				/*if (isEntryType == true){
					addTypeLink(generadtedid, addType (pdfField, false, false, true), false);
				}*/
				return generadtedid;
			} catch (SQLException exc) {
				print("addType: " + exc.toString());
				return -2;
			}
		}
	}

	/**
	 * Add a Link between to Types for Type Optional/Required relations
	 * @param typeId1 number of type 1
	 * @param typeId2 number of type 2
	 * @param required if the typeId2 is required for type1
	 * @return generated id, if error -1
	 * @author SamyStyle
	 */
	public int addTypeLink (int typeId1, int typeId2, boolean required) {
		Statement stat;
		ResultSet set;
		try {
			stat = this.connection.createStatement();
			String sql = "SELECT * FROM " + prifix + tableTypeLink + " WHERE TYPE_1_ID = " + typeId1 + " AND TYPE_2_ID = " + typeId2 + "";
			set = stat.executeQuery(sql);	   
			set.next();
			return set.getInt(1);
		} catch (SQLException exc1) {
			try {
				int generadtedid = 0;
				stat = connection.createStatement();
				String sql = "INSERT INTO " + prifix + tableTypeLink + " (TYPE_1_ID, TYPE_2_ID, REQUIRED) VALUES (" + typeId1 + ", " + typeId2 + "," + this.convBooleanToInt(required) +")";
				stat.execute(sql, Statement.RETURN_GENERATED_KEYS);
				set = stat.getGeneratedKeys(); 
				set.next();
				generadtedid = set.getInt(1);
				stat.close();
				return generadtedid;
			} catch (SQLException exc2) {
				print ("addTypeLink: " + exc2.toString());
				return -1;
			}
		}
	}
	
	/**
	 * Close the Database Connection
	 * @author SamyStyle
	 */
	public void close () {
		try {
			connection.close();
		} catch (SQLException exc){
			print ("close: " + exc.toString());
		}
	}
	/**
	 * @param conv boolean to convert into integer
	 * @return if conv = true then 1 else 0
	 * @author SamyStyle
	 */
	protected int convBooleanToInt (boolean conv){
		if (conv) {return 1;
		}else {return 0;}
	}
	/**
	 * 
	 * @param conv integer to convert into boolean
	 * @return if conv = 1 then true else false
	 * @author SamyStyle
	 */
	protected boolean convIntToBoolean (int conv){
		if (conv >= 0.5 ) {return true;
		} else {return false;}
	}
		
	/**
	 * 
	 * @param id of the Type to return
	 * @return Type name of the id
	 * @author SamyStyle
	 */
	public String convTypeIdToTypeName (int id){
		Statement stat;
		String myReturn = "";
		try {
			stat = this.connection.createStatement();
			ResultSet resultSet = stat.executeQuery("SELECT * FROM " + prifix + tableType + " WHERE ID = " + id);
			resultSet.next();
			myReturn = resultSet.getString(2).trim();
			resultSet.close();
			stat.close();
		} catch (SQLException exc) {
			print ("convTypeIdToTypeName: " + exc.toString());
		}	   
		return myReturn;
	}
	
	/**
	 * 
	 * @param typeName
	 * @return id of typeName
	 * @author SamyStyle
	 */
	public int convTypeNameToTypeId (String typeName) {
		try {
			int id = 0;
			Statement stat = this.connection.createStatement();
			String sql = "SELECT " + tableTypeColum[0] + " FROM " + prifix + tableType + " WHERE " + tableTypeColum[1] + " = '" + typeName.replaceAll("'", "''") + "' ";
			ResultSet resultSet = stat.executeQuery(sql);
			resultSet.next();
			id = resultSet.getInt(tableTypeColum[0]);
			resultSet.close();
			stat.close();	   
			return id;
		} catch (SQLException exc) {
			print ("convTypeNameToTypeI:" + exc.toString());
			return -1;
		}
	}
	
	/**
	 * Deleate all Attributes an its Condion of the Attribute with id
	 * @param id
	 * @return false if error else true
	 * @author SamyStyle
	 */
	protected boolean deleteAttribute (int id){
		Statement stat;
		String sql;
		//TODO: delete Conditions
		//Delete Attribute Links on Object
		try {
			stat = connection.createStatement();
			sql = "DELETE FROM " + prifix + this.tableObjectLink + "  WHERE " + tableObjectLinkColum[2] + " = " + id + "";
		  	stat.executeUpdate(sql);
		  	stat.close();
		} catch (SQLException exc) {
			print ("deleteAttribute: " + exc.toString());
		}
		
		//Delete Attribute
		try {
			stat = connection.createStatement();
			sql = "DELETE FROM " + prifix + this.tableAttribut + "  WHERE " + tableAttributColum[0] + " = " + id + "";
		  	stat.executeUpdate(sql);
		  	stat.close();
		  	return true;
		} catch (SQLException exc) {
			print ("deleteAttribute: " + exc.toString());
			return false;
		}
	}
	
	/**
	 * Delete a Type out of the Type Table you the id form the Data Container
	 * @param value data to delete out of Type Table
	 * @return false if error else true
	 * @author SamyStyle
	 */
	protected boolean deleteContainerType (ContainerType value){
		try {
			Statement stat = connection.createStatement();
			String sql = "DELETE FROM " + prifix + tableType + "  WHERE " + tableTypeColum[0] + " = " + value.getId() + "";
			stat.executeUpdate(sql);
			stat.close();
			stat = connection.createStatement();
			sql = "DELETE FROM " + prifix + tableTypeLink + "  WHERE " + tableTypeLinkColum[1] + " = " + value.getId() + "";
			stat.executeUpdate(sql);
			stat.close();
			return true;
		} catch (SQLException exc) {
			print ("deleteContainerType: " + exc.toString());
			return false;
		}
	};
	
  	/**
  	 * Delete a objectLink between Object and an Attribute
  	 * the id is the identifier of the Link
  	 * @param id of the object to delete
  	 * @return false if error else true
  	 * @author SamyStyle
  	 */
	public boolean deleteFile (int id) {
		Statement stat;
		try {
			stat = this.connection.createStatement();
			String sql = "DELETE FROM " + prifix + tableFile + " WHERE " + tableFileColum[1] + " = " + id;
			stat.executeUpdate(sql);
			stat.close();
			return true;
		} catch (SQLException exc) {
			print("deleteFile: " + exc.toString());
			return false;
		}
	}
	
	
	/**
	 * Delete a include out of the Include Table you the id form the Data Container
	 * @param value data to delete out of Include Table
	 * @return false if error else true
	 * @author SamyStyle
	 */
	public boolean deleteInclude (ContainerInclude value)  {
		try {
			Statement stat = connection.createStatement();
			String sql = "DELETE FROM " + prifix + tableInclude + "  WHERE " + tableIncludeColum[0] + " = " + value.getId() + "";
			stat.executeUpdate(sql);
			stat.close();
			return true;
		} catch (SQLException exc) {
			print("deleteInclude: " + exc.toString());
			return false;
		}
	}
	/**
	 * Delete a object out of the object Table you the id form the Data Container
	 * @param value data to delete out of object Table
	 * @return false if error else true
	 * @author SamyStyle
	 */
	public boolean deleteObject (ContainerObject value) {
		Statement stat;
		//delete the attributes of the object
		try {
			stat = this.connection.createStatement();
			ResultSet set = stat.executeQuery("SELECT * FROM " + prifix + tableObjectLink + " WHERE " + tableObjectLinkColum[1] + " = " + value.getId());
			while (set.next()) {
				this.deleteAttribute(set.getInt(tableObjectLinkColum[2]));
			}
			set.close();
			stat.close();
		}	catch (SQLException exc) {
			print("deleteObject: " + exc.toString());
		}
		//delete Object 
		try {
			stat = connection.createStatement();
			String sql = "DELETE FROM " + prifix + tableObject + "  WHERE " + tableObjectColum[0] + " = " + value.getId() + "";
		  	stat.executeUpdate(sql);
		  	stat.close();
		  	return true;
		} catch (SQLException exc) {
			print("deleteObject: " + exc.toString());
			return false;
		}
	}
  	/**
  	 * Delete a objectLink between Object and an Attribute
  	 * the id is the identifier of the Link
  	 * @param id of the object to delete
  	 * @return false if error else true
  	 * @author SamyStyle
  	 */
	protected boolean deleteObjectLink (int id) {
		Statement stat;
		try {
			stat = this.connection.createStatement();
			String sql = "DELETE FROM " + prifix + tableObjectLink + " WHERE " + tableObjectLinkColum[1] + " = " + id;
			stat.executeUpdate(sql);
			stat.close();
			return true;
		} catch (SQLException exc) {
			print("deleteObjectLink: " + exc.toString());
			return false;
		}
	}
	
	/**
	 * Delete a Table out of connected Database
	 * @param tableName of the Table to delete
	 * @return false if error else true
	 * @author SamyStyle
	 */
  	protected boolean deleteTable (String tableName){
	  	Statement stat;
		try {
			stat = connection.createStatement();
			stat.execute("DROP TABLE " + tableName);
		  	stat.close();
		  	return true;
		} catch (SQLException exc) {
			print("deleteTable: " + exc.toString());
			return false;
		} 
  	}

	/**
	 * 
	 * @param id is an Object id in Object Table
	 * @return all Attribute hat linked to the id
	 * @author SamyStyle
	 */
	protected Vector<ContainerAttribute> getAllAttribut (int id){
		Vector<ContainerAttribute> attribute = new Vector<ContainerAttribute>();
		Statement stat1, stat2;
		ResultSet res1, res2;
		ContainerAttribute element;
		try {
			stat1 = this.connection.createStatement();
			res1 = stat1.executeQuery("SELECT " + tableObjectLinkColum[2] + " FROM " + prifix + tableObjectLink + " WHERE " + tableObjectLinkColum[1] + " = " + id);
			while (res1.next()) {
				stat2 = this.connection.createStatement();
				res2 = stat2.executeQuery("SELECT * FROM " + prifix + tableAttribut + " WHERE ID = " + res1.getInt(tableObjectLinkColum[2]));
				while (res2.next()) {
					element = new ContainerAttribute(res2.getInt(tableAttributColum[0]),
															res2.getInt(tableAttributColum[1]),
															res2.getString(tableAttributColum[2]).trim(),
															res2.getBoolean(tableAttributColum[3]),
															getAllConditional(res2.getInt(tableAttributColum[0])));
					attribute.add(element);
				}
				res2.close();
				stat2.close();
			}
			res1.close();
			stat1.close();
			return attribute;
		} catch (SQLException exc) {
			print ("getAllAttribut: " + exc.toString());
			return null;
		}
	}
	
	/**
	 * 
	 * @param id is an Attribute id in Attribute Table
	 * @return all Condition hat linked to the id 
	 * @author SamyStyle
	 */
	protected Vector<ContainerAttribute> getAllConditional (int id){
		Vector<ContainerAttribute> attribute = new Vector<ContainerAttribute>();
		Statement stat1, stat2;
		ResultSet res1, res2;
		ContainerAttribute element;
		String sql1, sql2;
		try {
			stat1 = this.connection.createStatement();
			sql1 = "SELECT " + tableAttributLinkColum[2] + " FROM " + prifix + tableAttributLink + " WHERE " + tableAttributLinkColum[1] + " = " + id;
			res1 = stat1.executeQuery(sql1);
			while (res1.next()) {
				stat2 = this.connection.createStatement();
				sql2 = "SELECT * FROM " + prifix + tableAttribut + " WHERE " + tableAttributColum[0] + " = " + res1.getInt("ATT_2_ID");			
				res2 = stat2.executeQuery(sql2);
				while (res2.next()) {	   
					element = new ContainerAttribute(res2.getInt(tableAttributColum[0]),
															res2.getInt(tableAttributColum[1]),
															res2.getString(tableAttributColum[2]).trim(),
															res2.getBoolean(tableAttributColum[3]),
															getAllConditional(res2.getInt(tableAttributColum[0])));
					attribute.add(element);
				}
			}
			res1.close();
			stat1.close();
			return attribute;
		} catch (SQLException exc) {
			print ("getAllConditional: " + exc.toString());
			return null;
		}
	}
	
	public Vector<ContainerFile> getAllFiles (int id){
		Vector<ContainerFile> v = new Vector<ContainerFile>();
		Statement stat;
		ResultSet res;
		try {
			stat = this.connection.createStatement();
			res = stat.executeQuery("SELECT * FROM " + prifix + tableFile + " WHERE " + tableFileColum[5] + " = " + id);
			while (res.next()) {
				v.add(new ContainerFile(res.getInt(tableFileColum[0]),
											 res.getString(tableFileColum[1]).trim(),
											 res.getString(tableFileColum[2]).trim(),
											 res.getString(tableFileColum[3]).trim()));
			}
			res.close();
			stat.close();
			return v;
		} catch (SQLException exc) {
			print ("getAllObject:" + exc.toString());
			return null;
		}
		
	}
	
	/**
	 * @return the include Table
	 * @author SamyStyle
	 */
	public Vector<ContainerInclude> getAllInclude ()  {
		Vector<ContainerInclude> vector = new Vector<ContainerInclude>();
		try {
			Statement statement = connection.createStatement();
			String sql = "SELECT * FROM " + prifix + tableInclude + "";
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) { 
				vector.add(new ContainerInclude(resultSet.getInt(1),
													 resultSet.getString(2).trim(),
													 false
													)
							);
			}
			statement.close();
			resultSet.close();
			return vector;
		} catch (SQLException exc) {
			print ("getAllInclude: " + exc.toString());
			return null;
		}
	}
	
	/**
	 * @return the Object Table with the Attribute Table in it
	 * @author SamyStyle
	 */
	public Vector<ContainerObject> getAllObject (){
		Statement stat;
		ResultSet res;
		Vector<ContainerObject> myVector = new Vector<ContainerObject>();
		try {
			stat = this.connection.createStatement();
			res = stat.executeQuery("SELECT * FROM " + prifix + tableObject);
			while (res.next()) {
				myVector.add(new ContainerObject(res.getInt(tableObjectColum[0]),
														res.getInt(tableObjectColum[1]),
														res.getString(tableObjectColum[2]).trim(),
														getAllAttribut(res.getInt(tableObjectColum[0]))));
			}
			res.close();
			stat.close();
			return myVector;
		} catch (SQLException exc) {
			print ("getAllObject:" + exc.toString());
			return null;
		}
		
	}
	
	/**
	 * @return the Type Table 
	 * @author SamyStyle
	 */
	public Vector<ContainerType> getAllTypes (){
		Vector<ContainerType> v = new Vector<ContainerType>();
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + prifix + tableType);
			while (resultSet.next()) {
				v.add(new ContainerType(resultSet.getInt(tableTypeColum[0]),
											 resultSet.getString(tableTypeColum[1]).trim(),
											 getRequiredFild(resultSet.getInt(tableTypeColum[0])),
											 getOptionalField(resultSet.getInt(tableTypeColum[0])),
											 convIntToBoolean(resultSet.getInt(tableTypeColum[2])),
											 convIntToBoolean(resultSet.getInt(tableTypeColum[3])),
											 convIntToBoolean(resultSet.getInt(tableTypeColum[4]))
											 ));
			}
			resultSet.close();
			statement.close();
			return v;
		} catch (SQLException exc) {
			print ("getAllTypes: " + exc.toString());
			return new Vector<ContainerType>();
		}
	}
	
	public File getFile (int id){
		String sql;
		File f = null;
		try {
			sql = "SELECT * FROM " + prifix + tableFile + " WHERE " + tableFileColum[0] + " = " + id;
			PreparedStatement stmt = connection.prepareStatement(sql);
		    ResultSet resultSet = stmt.executeQuery();
		    while (resultSet.next()) {
		      String link = resultSet.getString(tableFileColum[2]);
		      
		      //Create Folder
		      String dirName = Main.getIniData("FileDir") + "/";
		      File fDir = new File(dirName);
		      if (!fDir.isDirectory()) {
		    	  fDir.mkdir();
		      }
		      //TODO: test if file already exists
		      //Create File
		      f = new File(dirName + link);
		      FileOutputStream fos;
			  fos = new FileOutputStream(f);
			  	
		      byte[] buffer = new byte[1];
		      InputStream is = resultSet.getBinaryStream(tableFileColum[4]);
		      while (is.read(buffer) > 0) {
		        fos.write(buffer);
		      }
		      fos.close();
		    }
		    return f;
		} catch (SQLException exc) {
			print ("getFile1: " + exc.toString());
		} catch (FileNotFoundException exc) {
			print ("getFile2: " + exc.toString());
		} catch (IOException exc) {
			print ("getFile3: " + exc.toString());
		}
		return null;
	}
	
	/**
	 * @param id of the Object
	 * @return an Object with id of Object Table
	 * @author SamyStyle
	 */
	public ContainerObject getObject (int id){
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + prifix + tableObject + " WHERE " + tableObjectColum[0] + " = " + id);
			resultSet.next();
			Vector<ContainerAttribute> attribute = getAllAttribut(resultSet.getInt(1));
			ContainerObject element = new ContainerObject(resultSet.getInt(1),
																	resultSet.getInt(2),
																	resultSet.getString(3).trim(),
																	attribute);
			resultSet.close();
			statement.close();	   
			return element;
		} catch (SQLException exc) {
			print ("getObject (int): " + exc.toString());
			return null;
		}
	}
	
	/**
	 * 
	 * @param key of the Object
	 * @return an Object with key of Object Table
	 * @author SamyStyle
	 */
	public ContainerObject getObject (String key){
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + prifix + tableObject + " WHERE " + tableObjectColum[2] + " = '" + key + "'");
			resultSet.next();
			ContainerObject element = new ContainerObject(resultSet.getInt(1),
																		resultSet.getInt(2),
																		resultSet.getString(3).trim(),
																		getAllAttribut(resultSet.getInt(1)));
			resultSet.close();
			statement.close();
			return element;
		} catch (SQLException exc) {
			print ("getObject (string): " + exc.toString());
			return null;
		}
	}
	/**
	 * gets all fields that will be optional an the Type with the id 
	 * @param id of the Type 
	 * @return optional fields
	 * @author SamyStyle
	 */
	protected Vector<String> getOptionalField (int id){
		Vector<String> optional = new Vector<String>();
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT TYPE_2_ID FROM " + prifix + tableTypeLink + " WHERE REQUIRED = 0 and TYPE_1_ID = " + id);
			while (resultSet.next()) {
				optional.add(this.convTypeIdToTypeName(resultSet.getInt("TYPE_2_ID")));
			}
		} catch (SQLException exc) {
			print ("getOptionalFild" + exc.toString());
		}
		return optional;
	}
	
	/**
	 * gets all fields that will be required an the Type with the id 
	 * @param id of the Type 
	 * @return required fields
	 * @author SamyStyle
	 */
	protected Vector<String> getRequiredFild (int id) {
		Vector<String> required = new Vector<String>();
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT TYPE_2_ID FROM " + prifix + tableTypeLink + " WHERE REQUIRED = 1 and TYPE_1_ID = " + id);
			while (resultSet.next()) {
				required.add(this.convTypeIdToTypeName(resultSet.getInt("TYPE_2_ID")));
			}
		} catch (SQLException exc) {
			print ("getRequiredFild" + exc.toString());
		}
		return required;
	}
	
	public Vector<String> getTabelNames () {
		Vector<String> Names = new Vector<String>();
		try {
			String[] types = {"TABLE", "VIEW"}; 
			ResultSet resultSet = connection.getMetaData().getTables(null, null, "%", types); 
			while (resultSet.next()) {
				Names.add(resultSet.getString(3));
			}
			resultSet.close();
		} catch (SQLException exc) {
			print ("Error " + exc.toString());
		}
		/*Iterator<String> i = Names.iterator();
		while (i.hasNext()){
			print(i.next()+ "\n");
		}*/
		
		return Names;
	}
	
	public Vector<String> getTablePrifix () {
		Vector<String> names = getTabelNames();
		Vector<String> pre = new Vector<String>();
		Iterator<String> i = names.iterator();
		while (i.hasNext()){
			String[] thePrifix = i.next().split("_");
			if (!pre.contains(thePrifix[0])){
				pre.add(thePrifix[0]);
			}			          
		}
		/*Iterator<String> i2 = pre.iterator();
		while (i2.hasNext()){
			print(i2.next()+ "\n");
		}*/
		return pre;
	}
	
	/**
	 * Connect to database
	 * @return the connection to database
	 * @author SamyStyle
	 */
	public Connection getConnection() throws SQLException {
		try {
    		Class.forName(driver);
    		DriverManager.setLoginTimeout(60); // fail after 60 seconds
    		this.connected = true;
    		return DriverManager.getConnection(url, properties);
    	} catch (ClassNotFoundException e)  {   
    		this.connected = false;
            return null;
        }
	}
	
	/**
	 * return the type with typeName in the database
	 * @param typeName
	 * @return the type
	 * @author SamyStyle
	 */
	public ContainerType getType(String typeName) {
		ContainerType element = null;
		try {
			Statement statement = this.connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + prifix + tableType + " WHERE VALUE = '" + typeName.replaceAll("'", "''") + "'");
			while (resultSet.next()) {
				int id = resultSet.getInt(tableTypeColum[0]);
				element = new ContainerType(id,
												 resultSet.getString(tableTypeColum[1]).trim(),
												 getRequiredFild(id),
												 getOptionalField(id),
												 convIntToBoolean(resultSet.getInt(tableTypeColum[2])),
												 convIntToBoolean(resultSet.getInt(tableTypeColum[3])),
												 resultSet.getBoolean(4));
			}
			resultSet.close();
			statement.close();
		} catch (SQLException exc) {
			print ("getType" + exc.toString());
		}
		return element;
	}
	
	/**
	 * @return true if connection is available 
	 * @author SamyStyle
	 */
	public boolean isConnected(){
		return this.connected;
	};
	
	/**
	 * 
	 * @param typeName
	 * @return id if exixting else -1
	 * @author SamyStyle
	 */
	protected int isExistingTyp (String typeName){
		try {
			Statement statement = this.connection.createStatement();
			String sql = "SELECT " + tableTypeColum[0] + " FROM " + prifix + tableType + " WHERE " + tableTypeColum[1] + " = '" + typeName.replaceAll("'", "''") + "'";
			ResultSet resultSet = statement.executeQuery(sql);	   
			resultSet.next();
			return resultSet.getInt(tableTypeColum[0]);
		} catch (SQLException exc) {
			print ("isExistingTyp: " + exc.toString());
			return -1;
		}
	}
	
	protected void print (String value){
		if (debug){
			System.out.println("CTex_DB: " + value);
		}
	}
	
	/**
	 * 
	 * @author stromsen
	 */
	public boolean reloadDefaultTypes() {
		// Preparing the filepath
		Vector<String> types = new Vector<String>();
		Vector<ContainerType> availableTypes = new Vector<ContainerType>();
		
		File files = new File("types/available/");
		for (int i = 0; i < files.list().length; i++) {
			// Getting the list of files in the folder
			String dummyString = new String(files.list()[i]);
			// We want to load a type, but the constructor of CTex_EntryType
			// takes only the name of the type, such as "author", without the 
			// file ending
			if (dummyString.endsWith(".dat")) {
				dummyString = dummyString.substring(0, dummyString.lastIndexOf("."));
				types.add(dummyString);	
			}
		}
		
		String availableTypesPath;
		for (int i = 0; i < types.size(); i++) {
			boolean isRequired = true;
			Vector<String> requiredField = new Vector<String>();
			Vector<String> optionalField = new Vector<String>();
			requiredField.clear();
			optionalField.clear();
			availableTypesPath = "types/available/" + types.get(i) + ".dat";
			File file = new File(availableTypesPath);

			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String gelesen;
				
				// Lese Zeile fuer Zeile und Ausgabe
				while ((gelesen = br.readLine()) != null) {
					if (gelesen.compareTo("#") == 0) {
						isRequired = false;
					} else {
							if (isRequired) {
								print("R  " + gelesen );
								requiredField.add(gelesen);
							} else {
								print("O  " + gelesen);
								optionalField.add(gelesen);
							}
					}
				}
				
				// Freigabe der Ressourcen
				br.close();
			
			} catch (FileNotFoundException e1) {
				// die Datei existiert nicht
				System.err.println("Datei nicht gefunden: "+ file);
			} catch (IOException e2) {
				// andere IOExceptions abfangen.
				e2.printStackTrace();
			}
			print("Optional Size:" + optionalField.size());
			print("Required Size:" + requiredField.size());
			availableTypes.add(new ContainerType(-1, types.get(i), requiredField, optionalField, false, true, true));
		}
		// Saving all loaded types into the database
		return setAllTypes(availableTypes);	
	}
	
	public boolean setAllFiles (Vector<ContainerFile> v, int objId) throws PacketTooBigException{
		//Add New One
		Iterator<ContainerFile> i = v.iterator();
		ContainerFile e, e2;
		boolean ret = true;
		int retI = 0;
		while (i.hasNext() && ret == true){
			e = i.next();
			if (e.getId() == -1){
				retI = addFile(e, objId);
				if (retI == -1){
					ret = false;
				}
			} else {
				ret = setFile(e, objId);
			}
		}
		
		//Delete Old one
		Vector<ContainerFile> v2 = this.getAllFiles(objId);
		Iterator<ContainerFile> i2 = v2.iterator();
		boolean found = false;
		while (i2.hasNext()){
			e2 = i2.next();
			i = v.iterator();
			found = false;
			while (i.hasNext()){
				e = i.next();
				if (e2.getId() == e.getId()){
					found = true;
				}
			}
			if (found == false){
				this.deleteFile(e2.getId());
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param data
	 * @author SamyStyle
	 */
	public boolean setAllInclude (Vector<ContainerInclude> data){
		boolean ret = true;
		Iterator<ContainerInclude> pointer = data.iterator();
		while (pointer.hasNext() && ret == true){
			ContainerInclude e = pointer.next();
			if (e.getId() == -1){
				int x = this.addInclude(e.getValue());
				if (x == -1){
					ret = false;
				} else {
					ret = true;
				}
			} if (e.getDelete() == true) {
				ret = this.deleteInclude(e);
			}else {
				ret = this.updateInclude(e);
			}
		}
		return ret;
	}
	
	/**
	 * set all Type in the Vector into the database
	 * adds the new one
	 * edits the old one
	 * @param types to set
	 * @author SamyStyle
	 */
	public boolean setAllTypes(Vector<ContainerType> types) {
		boolean ret = true;
		Iterator<ContainerType> pointer = types.iterator();
		Vector<ContainerType> newTypes = new  Vector<ContainerType>();
		ContainerType e = null;
		
		while (pointer.hasNext() == true && ret == true){
			e = pointer.next();
			if (e.getId() == -1){
				int id = addType (e.getType().trim(),
								   e.isDeletable(),
								   e.isEntryType(),
								   e.isInvisible());
				if (id != -1){
					pointer.remove();
					newTypes.add(new ContainerType(id,
									e.getType().trim(),
									e.getRequiredFields(),
									e.getOptionalFields(),
									e.isDeletable(),
									e.isEntryType(),
									e.isInvisible()));
				} else{
					ret = false;
				}
			}
			else {
				ret = updateType(e);
				pointer.remove();
				newTypes.add(e);
			}
		}
		//Requried und Optional setzen daf�r wird die Link Tabelle komplett gel�scht und neue Links eingetragen
		if (ret == true) {
			ret = deleteTable(prifix + tableTypeLink);
			this.createTableTypeLink(true);		  
			pointer = newTypes.iterator();
			while (pointer.hasNext() && ret == true){
				ret = setType (pointer.next());
			}
		}
		return ret;
	}
	
	/**
	 * @param e Attribute to set
	 * @return id of the Attribute, -1 if error
	 * @author SamyStyle
	 */
	protected int setAttribute (ContainerAttribute e){
		int id = e.getId();
		if (id == -1){
			id = addAttribute (e.getObjectTypeId(), e.getValue(), e.getLink());
			//Set Conditions
			if (id != -2 && id != -1){
				Vector<ContainerAttribute> v = e.getCondition();
				if (v != null){
					Iterator<ContainerAttribute> i = v.iterator();
					while (i.hasNext()){
						addAttributeLink(id, setAttribute(i.next()));
					}
				}
			}
			return id;
		} else {
			if (updateAttribute(e)) {
				return e.getId();
			} else {
				return -1;
			}
		}
	}
	
	public boolean setFile(ContainerFile e, int objId){
		Statement stat;
		String sql;
		try {
			stat = connection.createStatement();
			sql = "UPDATE " + prifix + tableFile + " SET " + tableFileColum[1] + " = '" + e.getDescription() + "' WHERE " + tableIncludeColum[0] + " = " + e.getId();
			print (sql);
			stat.executeUpdate(sql);
			stat.close();
			return true;
		} catch (SQLException exc) {
			print("setFile: " + exc.toString());
			return false;
		}
	}
	
	/**
	 * @param data object to set
	 * @return id of the Attribute, -1 if error
	 * @author SamyStyle
	 */
	public int setObject (ContainerObject data){
		ContainerAttribute e = null;
		int id;
		Vector<ContainerAttribute> v = null;
		Iterator<ContainerAttribute> i = null;
			
		if (getObject(data.getId()) != null){
			this.updateObject (data);
			id  = data.getId();
		} else {
			id = addObject(data.getTypeId(), data.getKey());
		}
		
		if (id != -1 && id != -2){
			deleteObjectLink (id);
			v = data.getAttributes();
			i = v.iterator();
			while (i.hasNext()) {
				e = i.next();
				addObjectLink(id, setAttribute (e));
			}
		}
		return id;
	}
	
	/**
	 * 
	 * It sets user and password for database connection
	 * 
	 * @param name
	 * @param password
	 * 
	 * @author SamyStyle
	 * @version 0.1
	 */
	protected void setSettings (String name, String password){
		properties.setProperty("user", name);
		properties.setProperty("password", password);
		properties.setProperty("jdbcCompliantTruncation", "false");
	}

	/**
	 * @param e
	 * @return false if error else true
	 * @author SamyStyle
	 */
	protected boolean setType (ContainerType e){
		Vector<String> optionalFild = e.getOptionalFields(), requiredFild = e.getRequiredFields();
		Iterator<String> pointerRequiredString = requiredFild.iterator(), pointerOptionalString = optionalFild.iterator();
		int id = e.getId(), id2 = 0;
		String Value;
		boolean ret = true;
		
		//Set Required Fields
		while (pointerRequiredString.hasNext() && ret == true){
			Value = pointerRequiredString.next();
			id2 = isExistingTyp(Value);
			if (id2 == -1){
				id2 = addType(Value, true, false, false);
			}
			if (id2 == -1){
				ret = false;
			} else {
				id2 = addTypeLink (id, id2, true);
				if (id2 == -1){
					ret = false;
				}
			}
			
		}
		//Set Optional Fields
		while (pointerOptionalString.hasNext() && ret == true){
			Value = pointerOptionalString.next();
			id2 = isExistingTyp(Value);
			if (id2 == -1){
				id2 = addType(Value, true, false, false);
			}
			if (id2 == -1){
				ret = false;
			} else {
				id2 = addTypeLink (id, id2, false);
				if (id2 == -1){
					ret = false;
				}
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * @param aframe
	 * @param Text
	 * @return
	 * @author SamyStyle
	 */
	protected boolean showDialog (Component aframe, String Text){
		int n = JOptionPane.showConfirmDialog(
				aframe,
				Text,
				Main.myLang.getString("dialog.db.empty.famename"),
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}
	

	protected boolean updateAttribute (ContainerAttribute e){
		Statement stat;
		String sql;
		try {
			stat = connection.createStatement();
			sql = "UPDATE " + prifix + tableAttribut + " SET " + tableAttributColum[1] + " = " + e.getObjectTypeId() + ", "  + tableAttributColum[2] + " = '" + e.getValue().replaceAll("'", "''") + "', " + tableAttributColum[3] + " = " +  convBooleanToInt(e.getLink()) + " WHERE " + tableIncludeColum[0] + " = " + e.getId();
			print (sql);
			stat.executeUpdate(sql);
			stat.close();
			//TODO: deleate Old Conditions
			//Set Conditions
			Vector<ContainerAttribute> v = e.getCondition();
			if (v != null){
				Iterator<ContainerAttribute> i = v.iterator();
				while (i.hasNext()){
					addAttributeLink(e.getId(), setAttribute(i.next()));
				}
			}
			return true;
		} catch (SQLException exc) {
			print("updateAttribute: " + exc.toString());
			return false;
		}
	}
	
  	
  	/**
  	 * edit the include Table 
  	 * use the id of e as identifier
  	 * link the information of e to the id
  	 * @param e data to edit
  	 * @return false if error else true
  	 * @author SamyStyle
  	 */
	protected boolean updateInclude (ContainerInclude e){
		Statement stat;
		String sql;
		try {
			stat = connection.createStatement();
			sql = "UPDATE " + prifix + tableInclude + " SET " + tableIncludeColum[1] + " = '" + e.getValue().replaceAll("'", "''") + "' WHERE " + tableIncludeColum[0] + " = " + e.getId();
			stat.executeUpdate(sql);
			stat.close();
			return true;
		} catch (SQLException exc) {
			print("editInclude: " + exc.toString());
			return false;
		}
	}
	
	protected boolean updateObject (ContainerObject e){
		Statement stat;
		try {
			stat = connection.createStatement();
			String sql = "UPDATE " + prifix + tableObject + " SET " + tableObjectColum[2] + " = '" + e.getKey().replaceAll("'", "''") + "' WHERE " + tableObjectColum[0] + " = " + e.getId();
			stat.executeUpdate(sql);
			stat.close();
			return true;
		} catch (SQLException exc) {
			print("editObject: " + exc.toString());
			return false;
		}
	}

	/**
	 * edit the type Table 
  	 * use the id of e as identifier
  	 * link the information of e to the id
	 * @param id
	 * @param value
	 * @param deleteble
	 * @return true if it works else false
	 */ 
	protected boolean updateType (ContainerType e){
		Statement stat;
		try {
			stat = connection.createStatement();
			String sql = "UPDATE " + prifix + tableType + " SET " + tableTypeColum[1] + " = '" + e.getType().replaceAll("'", "''") + "', " + tableTypeColum[4] + " = " + convBooleanToInt(e.isInvisible()) + ", " + tableTypeColum[2] + " = " + convBooleanToInt(e.isDeletable()) + " WHERE ID = " + e.getId();
			stat.executeUpdate(sql);
			stat.close();
			return true;
		} catch (SQLException exc) {
			print ("editType: " + exc.toString());
			return false;
		}
	}
	
	
	
}