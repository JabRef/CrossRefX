 package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * 
 * Java DB driver for the JavaDB database
 * 
 * Driver Location 	Bundled with Glassfish or Java 6
 * http://db.apache.org/derby/derby_downloads.html
 * Driver Versions 	Derby 10.3.1.4
 * Driver JAR File 	Network: derbyclient.jar
 * Embedded: derby.jar
 * Driver Classnames 	Network: org.apache.derby.jdbc.ClientDriver
 * Embedded: org.apache.derby.jdbc.EmbeddedDriver
 * URL Format (Network) 	- 	Example URL (Network) 	jdbc:derby://localhost:1527/sample
 * 
 * 
 * 
 * # Data Types
 * Source http://db.apache.org/derby/manuals/reference/sqlj02.html#ToC
 * 
	* Built-In Type Overview
	* Numeric Types
		  * Numeric Type Overview
		  * Numeric Type Promotion in Expressions
		  * Storing Values of One Numeric Data Type in Columns of Another Numeric Data Type
		  * Scale for Decimal Arithmetic
	* Data type assignments and comparison, sorting, and ordering
	* BIGINT
	* BLOB
	* CHAR
	* CHAR FOR BIT DATA
	* CLOB
	* DATE
	* DECIMAL
	* DOUBLE
	* DOUBLE PRECISION
	* FLOAT
	* INTEGER
	* LONG VARCHAR
	* LONG VARCHAR FOR BIT DATA
	* NUMERIC
	* REAL
	* SMALLINT
	* TIME
	* TIMESTAMP
	* VARCHAR
	* VARCHAR FOR BIT DATA
 * 
 * @author SamyStyle
 * @version 0.1
 */

public class Derby extends Db {
	
	/**
	 *  Konstrukor 
	 * 
	 * @param User
	 * @param PW
	 * @param Prifix_To_Set
	 * @throws Exception
	 * 
	 * @author SamyStyle
	 * @verison 0.1
	 * 
	 * "org.apache.derby.jdbc.ClientDriver"
	 * url = "jdbc:derby:";
	 * String Db_Name = "//localhost:1527/TexDB";
	 * 
	 * 
	 */
	
	public Derby(String user, String password, String hostName, String portNumber, String dbName) {
		super("org.apache.derby.jdbc.EmbeddedDriver",
			  "jdbc:derby:" + dbName + ";create=true",
			  hostName, portNumber, dbName);
			setSettings(user, password);
	}
	
	/**
	 * @param overwrite
	 */
	protected boolean createTableAttribut (boolean overwrite) {
		String myTableName = prifix + tableAttribut;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableAttributColum[0] + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " + tableAttributColum[1] + " int not null, " + tableAttributColum[2] + " CHAR (100) not null, " + tableAttributColum[3] + " int not null)";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	/**
	 * @param overwrite
	 */
	protected boolean createTableAttributLink (boolean overwrite) {
		String myTableName = prifix + tableAttributLink;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableAttributLinkColum[0] + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " + tableAttributLinkColum[1] + " int not null, " + tableAttributLinkColum[2] + " int not null)";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	/**
	 * @param overwrite
	 */
	protected boolean createTableFile (boolean overwrite) {
		String myTableName = prifix + tableFile;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableFileColum[0] + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " + tableFileColum[1] + " CHAR(100) not null, " + tableFileColum[2] + " CHAR(100) not null, " + tableFileColum[3] + " CHAR(100) not null, " + tableFileColum[4] + " blob, " + tableFileColum[5] + " int not null)";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	/**
	 * @param overwrite
	 */
	protected boolean createTableInclude (boolean overwrite) {
		String myTableName = prifix + tableInclude;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableIncludeColum[0] + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " + tableIncludeColum[1] + " CHAR(100) not null)";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	/**
	 * @param overwrite
	 */
	protected boolean createTableLink (boolean overwrite) {
		String myTableName = prifix + tableObjectLink;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableObjectLinkColum[0] + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " + tableObjectLinkColum[1] + " int not null, " + tableObjectLinkColum[2] + " int not null)";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	/**
	 * @param overwrite
	 */
	protected boolean createTableObject (boolean overwrite) {
		String myTableName = prifix + tableObject;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableObjectColum[0] + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " + tableObjectColum[1] + " int not null, " + tableObjectColum[2] + " CHAR(100) not null)";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	/**
	 * @param overwrite
	 */
	protected boolean createTableType (boolean overwrite) {
		String myTableName = prifix + tableType;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableTypeColum[0] + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " + tableTypeColum[1] + " CHAR(100) not null, " + tableTypeColum[2] + " int not null, " + tableTypeColum[3] + " int not null, " + tableTypeColum[4] + " int not null)";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	/**
	 * @param overwrite
	 */
	protected boolean createTableTypeLink (boolean overwrite) {
		String myTableName = prifix + tableTypeLink;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableTypeLinkColum[0] + " int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " + tableTypeLinkColum[1] + " int not null, " + tableTypeLinkColum[2] + " int not null, " + tableTypeLinkColum[3] + " int not null)";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	/**
	 * 
	 */
	public Connection getConnection() throws SQLException {
		try {
			Class.forName(driver);
			DriverManager.setLoginTimeout(60); // fail after 60 seconds
			this.connected = true;
			this.connection = DriverManager.getConnection(url , properties);
			return connection;
		} catch (ClassNotFoundException e) {
			this.connected = false;
			return null;
		}
		
	 }

}
