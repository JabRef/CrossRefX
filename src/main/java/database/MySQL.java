package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * MySQL drivers for the MySQL database
 * 
 * Driver Location 	Bundled with NetBeans 6.0
 * http://dev.mysql.com/downloads/connector/j/5.0.html
 * Also available from http://dev.mysql.com/downloads/connector/j/5.0.html
 * Driver Version 	MySQL Connector/J 5.0.7
 * Driver JAR Files 	mysql-connector-java-5.0.7-bin.jar
 * Driver Classname 	com.mysql.jdbc.Driver
 * URL Formats 	- 
 * Example URL 	jdbc:mysql://localhost:3306/sample
 * 
 * 
 * you need mysql-connector-java-5.1.13-bin.jar or higher included as user liebery
 * Download at http://dev.mysql.com/downloads/connector/j/
 * 
 * 
 * @author SamyStyle
 * @version 0.2
 */

public class MySQL extends Db {
	
	public MySQL(String user, String password, String hostName, String portNumber, String dbName) {
		super("com.mysql.jdbc.Driver",
				"jdbc:mysql://" + hostName + ":" + portNumber + "/" + dbName,
				hostName, portNumber, dbName);
		setSettings(user, password);
	}
	
	protected boolean createTableAttribut (boolean overwrite) {
		String myTableName = prifix + tableAttribut;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableAttributColum[0] + " INTEGER NULL AUTO_INCREMENT, " + tableAttributColum[1] + " int not null, " + tableAttributColum[2] + " TEXT NOT NULL, " + tableAttributColum[3] + " int not null, PRIMARY KEY (ID))";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	protected boolean createTableAttributLink (boolean overwrite) {
		String myTableName = prifix + tableAttributLink;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableAttributLinkColum[0] + " INTEGER NULL AUTO_INCREMENT, " + tableAttributLinkColum[1] + " int not null, " + tableAttributLinkColum[2] + " int not null, PRIMARY KEY (ID))";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	 protected boolean createTableFile (boolean overwrite) {
		String myTableName = prifix + tableFile;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableFileColum[0] + " INTEGER NULL AUTO_INCREMENT, " + tableFileColum[1] + " TEXT NOT NULL, " + tableFileColum[2] + " TEXT NOT NULL, " + tableFileColum[3] + " TEXT NOT NULL, " + tableFileColum[4] + " blob, " + tableFileColum[5] + " int not null, PRIMARY KEY (" + tableFileColum[0] + "))";
		System.out.print(sqlCode);
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	 
	protected boolean createTableInclude (boolean overwrite) {
		String myTableName = prifix + tableInclude;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableIncludeColum[0] + " INTEGER NULL AUTO_INCREMENT, " + tableIncludeColum[1] + " TEXT NOT NULL, PRIMARY KEY (ID))";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	protected boolean createTableLink (boolean overwrite) {
		String myTableName = prifix + tableObjectLink;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableObjectLinkColum[0] + " INTEGER NULL AUTO_INCREMENT, " + tableObjectLinkColum[1] + " int not null, " + tableObjectLinkColum[2] + " int not null, PRIMARY KEY (ID))";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	protected boolean createTableObject (boolean overwrite) {
		String myTableName = prifix + tableObject;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableObjectColum[0] + " INTEGER NULL AUTO_INCREMENT, " + tableObjectColum[1] + " int not null, " + tableObjectColum[2] + " TEXT NOT NULL, PRIMARY KEY (ID))";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	protected boolean createTableType (boolean overwrite) {
		String myTableName = prifix + tableType;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableTypeColum[0] + " INTEGER NULL AUTO_INCREMENT, " + tableTypeColum[1] + " TEXT NOT NULL, " + tableTypeColum[2] + " tinyint(1) NOT NULL, " + tableTypeColum[3] + " tinyint(1) not null, " + tableTypeColum[4] + " tinyint(1) not null, PRIMARY KEY (ID))";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	protected boolean createTableTypeLink (boolean overwrite) {
		String myTableName = prifix + tableTypeLink;
		String sqlCode = "CREATE TABLE " + myTableName + " (" + tableTypeLinkColum[0] + " INTEGER NULL AUTO_INCREMENT, " + tableTypeLinkColum[1] + " int not null, " + tableTypeLinkColum[2] + " int not null, " + tableTypeLinkColum[3] + " tinyint(1) not null, PRIMARY KEY (ID))";
		return createNewTable (sqlCode, myTableName, overwrite);
	}
	
	public Connection getConnection() throws SQLException {
		Statement st = null;
		try {
			Class.forName(driver);
			DriverManager.setLoginTimeout(60); // fail after 60 seconds
			try{
				connection = DriverManager.getConnection(url, this.properties);
				this.connected = true;
				return  connection; 
			} catch (Exception e) {
				url = "jdbc:mysql://" + hostName + ":" + portNumber + "/";
				connection = null;
				try {
					connection = DriverManager.getConnection(url, this.properties);
					st = connection.createStatement();
					st.executeUpdate("CREATE DATABASE " + dbName);
					url = "jdbc:mysql://" + hostName + ":" + portNumber + "/" + dbName;
					this.connected = true;
					connection = DriverManager.getConnection(url, this.properties);
					return  connection; 					
				} catch (SQLException e1) {
					this.connected = false;
					return null;
				}
			}
		} catch (ClassNotFoundException e) {
			this.connected = false;
			return null;
		}
	}
}
