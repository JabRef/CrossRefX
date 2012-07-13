package ctex;



import javax.swing.JOptionPane;
import parser.Export;
import parser.FileConnector;
import parser.Parser;
import parser.ParserException;
import database.Db;
import gui.Frame;
import gui.Model;

public class Entity {
	
	final boolean debug = false;
	private Db myDb;
	private String prefix, filePath;
    private Model myModel; 
    
    /**
     * for Debug massages in the Console
     * @param value
     * @author mayersn
     */
	protected void print (String value){
    	if (debug){
    		System.out.println("CTex_Entity: " + value);
    	}
    }
	
	Entity (){
	}
	
	/**
	 * 
	 * @param frame
	 * @return
	 * @author mayersn
	 */
	/*private boolean connect (Component frame){
		if (dbType == "derby"){
			myDb = new CTex_Derby(user, password, "localhost", "1527", name, prefix);
		}
		else if (dbType == "mysql"){
			myDb = new CTex_MySQL(user, password, ip, port, name, prefix);
		}
		if (myDb.isConnected() == true){
			return true;
		} else {
			JOptionPane.showMessageDialog(frame,
				CTex_Main.myLang.getString("dialog.db.connection.message"),
			    CTex_Main.myLang.getString("dialog.db.connection.famename"),
			    JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}*/
	
	/**
	 * 
	 * Erstellen einer neuen Datenbank
	 * 
	 * @param dbType where you want to Connect with e.g. mysql
	 * @param user to log in into database
	 * @param password to log in into database
	 * @param ip database address
	 * @param port database port
	 * @param name Table name
	 * @param prefix of the right Data in the Tabel
	 * @param frame
	 * @param open
	 * @return false if error
	 * @author SamyStyle
	 */
	 public boolean init (Db myDb, String prefix, Frame frame, boolean open){
		this.prefix = prefix;
		this.myDb = myDb;
		myDb.setPrifix(this.prefix);
		boolean isOk = true;
		if (isOk == true){
			if (open == false){
				isOk = myDb.createDatabase(frame);
				if (isOk == true){
					isOk = myDb.reloadDefaultTypes();
				}
			}	
		
			if (isOk == true){
				/** LOADING DATABASE DATA INTO THE TABLE MODEL **/
				myModel = new Model(this);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	 /**
	  * 
	  * @param myDb
	  * @param prefix
	  * @param frame
	  * @param filePath
	  * @param encodingType
	  * @return
	  */
	public boolean init (Db myDb, String prefix, Frame frame, String filePath, String encodingType){
		this.filePath = filePath;
		this.prefix = prefix;
		myDb.setPrifix(this.prefix);
		this.myDb = myDb;
		boolean isOk = true;
		if (isOk == true) {
			isOk = myDb.createDatabase(frame);
			if (isOk == true){
				isOk = myDb.reloadDefaultTypes();
			}
			FileConnector theFile = new FileConnector(this.filePath, encodingType);
			theFile.readFileContent();
			Parser theParser = new Parser(theFile.getFileContent());
			try {
				theParser.setFrame(frame);
				theParser.proof(myDb, false);
				theParser.finalize();		
				/** LOADING DATABASE DATA INTO THE TABLE MODEL WHEN OPENING A FILE **/
				myModel = new Model(this);
				return true;
			} catch (ParserException e){
				JOptionPane.showMessageDialog(frame, e.toString(), "Parser", JOptionPane.OK_OPTION);
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public Model getTableModel() {
		return myModel;
	}
	
	/**
	 * Returns Database
	 * @return Database
	 */
	public Db getDb(){ return myDb;}
	
	/**
	 * Close Databaseconnection
	 */
	public void close(){
		myDb.close();
	}
	
	/**
	 * 
	 * @param DataType
	 * @param File_Path
	 * @return
	 */
	public boolean exportToFile (String DataType, String File_Path){
		Export export = new Export();
		return export.exportToFile  (myDb, DataType, File_Path);
	}
}
