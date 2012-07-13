package database;

/**
 * 
 * IBM driver for the DB2 database
 * 
 * Driver Location 	DB2 Personal Developer's Edition: Redistributable JDBC Type 4 Driver (requires registration)
 * Driver Version 	Redistributable DB2 JDBC Type 4 driver v8 fixpack 13
 *  * db2_jdbc_t4_fp13.zip
 * Driver JAR Files 	Type 4 db2jcc.jar, Type 2 jcc_license_cu.jar
 * Driver Classname 	com.ibm.db2.jcc.DB2Driver
 * URL Format (Type 4) 	- 	Example URL 	jdbc:db2://localhost:50002/sample
 * 
 * Note: The IBM drivers support both Type 2 (native) and Type 4 (pure Java). To force the drivers to run in Type 4, add a property: driverType = 4 when connecting to the database. Note2: To execute application, copy db2jcct2.dll or similar to the application server classpath. For Glassfish that would be $GLASSFISH_INSTALL_DIR\domains\domain1\lib 
 * 
 * @author SamyStyle
 */

public class Db2 extends Db {
	
	public Db2 (String user, String password, String hostName, String portNumber, String dbName) {
		super("com.ibm.db2.jcc.DB2Driver",
				"dbc:db2://" + hostName + ":" + portNumber + "/" + dbName,
				  hostName, portNumber, dbName);
    	setSettings(user, password);  
	}

	@Override
	protected boolean createTableType(boolean overwrite) {
		return false;
	}

	@Override
	protected boolean createTableTypeLink(boolean overwrite) {
		return false;
	}

	@Override
	protected boolean createTableObject(boolean overwrite) {
		return false;
	}

	@Override
	protected boolean createTableAttribut(boolean overwrite) {
		return false;
	}

	@Override
	protected boolean createTableLink(boolean overwrite) {
		return false;
	}

	@Override
	protected boolean createTableAttributLink(boolean overwrite) {
		return false;
	}
	
	@Override
	protected boolean createTableFile (boolean overwrite) {
		return false;
	}

	@Override
	protected boolean createTableInclude(boolean overwrite) {
		
		return false;
	}

}
