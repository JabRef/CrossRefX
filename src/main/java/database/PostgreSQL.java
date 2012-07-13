package database;


/**
 * 
 * PostgreSQL driver for the PostgreSQL database
 * 
 * Driver Location 	Bundled with NetBeans 6.0
 * Also available from http://jdbc.postgresql.org/download.html
 * Driver Version 	8.2 Build 506
 * Driver JAR File 	postgresql-8.2-506.jdbc3.jar
 * Driver Classname 	org.postgresql.Driver
 * URL Format   jdbc:postgresql://<host>:<port>/<database>
 * Example URL 	jdbc:postgresql://jbrave-pc1.sfbay.sun.com:5432/postgres
 * 
 * @author SamyStyle
 */

public class PostgreSQL extends Db {
	
	public PostgreSQL(String user, String password, String hostName, String portNumber, String dbName) {
		super("org.postgresql.Driver", "jdbc:postgresql://" + hostName + ":" + portNumber + "/" + dbName, hostName, portNumber, dbName);
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
