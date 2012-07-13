package database;

/**
 * 
 * Oracle driver for the Oracle 11.x database
 * 
 * Driver Location 	Oracle Database 11g Release JDBC Drivers
 * http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/htdocs/jdbc_111060.html
 * Driver Versions 	Oracle Database 11g Release (11.1.0.6.0)
 * Driver JAR File 	ojdbc5.jar (JDK 1.5), ojdbc6.jar (JDK 1.6)
 * Driver Classnames 	oracle.jdbc.driver.OracleDriver
 * URL Formats 	- 	Example URL 	jdbc:oracle:thin:@localhost:1521:ora11i 
 * 
 * @author SamyStyle
 */

public class Oracle11 extends Db {
	
	public Oracle11(String user, String password, String hostName, String portNumber, String dbName) {
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
