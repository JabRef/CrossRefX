package database;

/**
 * 
 * Oracle driver for the Oracle 10.x database
 * 
 * Driver Location 	Oracle Database 10g Release 2 JDBC Drivers
 * http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/htdocs/jdbc_10201.html
 * Driver Versions 	Oracle Database 10g Release 2 (10.2.0.3)
 * Driver JAR File 	ojdbc14.jar
 * Driver Classnames 	oracle.jdbc.driver.OracleDriver
 * URL Formats 	- 	Example URL 	jdbc:oracle:thin:@localhost:1521:ora9i
 * 
 * @author SamyStyle
 */

public class Oracle10 extends Db {
	
	public Oracle10(String user, String password, String hostName, String portNumber, String dbName) {
		super("oracle.jdbc.driver.OracleDriver",
			  "jdbc:oracle:thin:@" + hostName + ":" + portNumber + ":" + dbName,
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
