package database;

/**
 * 
 * Microsoft driver for the SQL Server database
 * 
 * Driver Location 	" Microsoft SQL Server 2005 JDBC Driver 1.1
 * http://www.microsoft.com/downloads/details.aspx?FamilyId=6D483869-816A-44CB-9787-A866235EFC7C&displaylang=en
 * Driver Version 	Microsoft SQL Server 2005 JDBC Driver 1.1 sqljdbc_1.1.1501.101_enu.tar.gz
 * Driver JAR File 	sqljdbc.jar
 * Driver Classname 	com.microsoft.sqlserver.jdbc.SQLServerDriver
 * URL Format 	- 	
 * Example URL 	jdbc:sqlserver://localhost:1433;databaseName=travel;selectMethod=cursor 
 * 
 * @author SamyStyle
 */

public class SQLServer extends Db {

	public SQLServer(String user, String password, String hostName, String portNumber, String dbName) {
		super("com.microsoft.sqlserver.jdbc.SQLServerDriver",
			  "jdbc:sqlserver:" + hostName + ":" + portNumber + ";databaseName=" + dbName + ";selectMethod=cursor",
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
