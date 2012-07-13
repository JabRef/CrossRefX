package ctex;


import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import container.ContainerIni;
import database.Db;
import database.Derby;
import database.MySQL;
import gui.Frame;
import gui.dialog.OpenFileDialog;
import parser.FileConnector;
import parser.ParserIni;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main {

	public final static String fileField  = "file";
	public final static String entryTypeKeyFild = "CrossTeX-Key";
	
	private static String frameName;
	private static int counterDerby = 0;
	private static Vector<ContainerIni> cIni = null;
	
	public static MultilLanguage myLang = new MultilLanguage();
	
	/**
	 * Application start
	 * @param args
	 * @throws Exception
	 * @author mayersn
	 */
	public static void main(String[] args) throws Exception {
	    // do default log4j initialization
		// see http://logging.apache.org/log4j/1.2/manual.html
		BasicConfigurator.configure();
		// overwrite level for PDF Box
		Logger pdfboxLogger = Logger.getRootLogger().getLoggerRepository().getLogger("org.apache.pdfbox.util.PDFStreamEngine");
		pdfboxLogger.setLevel(Level.OFF);

	    // Creating a file lock
		FileLocking runProgramOnce = new FileLocking("crossrefx");
		ParserIni parsIni = new ParserIni();
		cIni = parsIni.readIni();
		myLang.setLocale(getIniData("Language"));
		if (runProgramOnce.isProgramRunning()) {
			// Program is already running
			JOptionPane.showMessageDialog(null,
					Main.myLang.getString(Main.myLang.getString("main.running.message")),
				    Main.myLang.getString(Main.myLang.getString("main.running.title")),
				    JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} else {
			// Program is not running and will be started
			
			// TODO: Choose some nice look and feel by Stefan
			/*try {
			      UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
			} catch (Exception e) {
				System.out.println("Unable to set Look and Feel " + e);
			}*/
			JFrame.setDefaultLookAndFeelDecorated(false);
			frameName = getIniData("ProgramName") + " - v" + getIniData("Version");		
			new Frame(1, frameName, true);
		}
	}
	
	/**
	 * 
	 * Creates an Entry + Table + Connection to Any DB
	 * 
	 * @param dbType where you want to Connect with e.g. mysql
	 * @param user to log in into database
	 * @param password to log in into database
	 * @param ip database address
	 * @param port database port
	 * @param name Table name
	 * @param prefix of the right Data in the Tabel
	 * @param frame where result will be shown
	 * @param open ture if you open a Server false if its a new Server
	 * @return true if no error
	 * @author mayersn 
	 */
	public static boolean addEntity (Db mydb, String prefix, Frame frame, boolean open){
		frame.setCursorWait();
		Entity newEntity = new Entity();
		boolean isCreated = newEntity.init(mydb, prefix, frame, open);
		if (isCreated == true){
			frame.addTableTab(prefix, newEntity.getTableModel());
			frame.setCursorNormal();
			return true;
		} else {
			frame.setCursorNormal();
			return false;
			//TODO:
			/*JOptionPane.showMessageDialog(null,
					Main.myLang.getString(Main.myLang.getString("main.errorCreateEntity.message")),
				    Main.myLang.getString(Main.myLang.getString("main.errorCreateEntity.title")),
				    JOptionPane.ERROR_MESSAGE);
			frame.setCursorNormal();
			return false;*/
		}
	}
	
	/**
	 * 
	 * Creates an Entry + Table + Connection to Any DB from a file
	 * 
	 * @param mydb
	 * @param prefix of the right Data in the Tabel
	 * @param frame where result will be shown
	 * @param filePath of the file you will load
	 * @param encodingType EncodingType of file
	 * @return true if no error
	 * @author mayersn, lischkls
	 */
	public static boolean addEntityFile (Db mydb, String prefix, Frame frame, String filePath, String encodingType){
		frame.setCursorWait();
		Entity newEntity = new Entity();
		boolean isCreated = newEntity.init(mydb, prefix, frame, filePath, encodingType);
		// For .bib files the path is not saved, because we can't save in that format
		if (filePath.endsWith(".xtx")) {
			newEntity.getTableModel().setSavePath(filePath);
		}
		if (isCreated == true){
			frame.addTableTab(prefix, newEntity.getTableModel());
			frame.setCursorNormal();
			return true;
		} else {
			frame.setCursorNormal();
			return false;
		}
	}
	
	/**
	 * 
	 * Creates an Entry + Table + Connection to Derby DB from a file
	 * 
	 * @param filePath of the file you will load
	 * @param frame where result will be shown
	 * @return true if no error
	 * @author mayersn
	 * @deprecated 
	 */
	public static boolean addEntityFileStandart (String filePath, Frame frame, String encodingType){
		frame.setCursorWait();
		Entity newEntity = new Entity();
		Db myDb = Main.connect("derby", "user1", "user1", "", "", "./TexDB", frame);
		boolean isCreated = newEntity.init(myDb, "Tex" + counterDerby + "_", frame,  filePath, encodingType);
		// For .bib the path is not saved, because we can't save in that format
		if (filePath.endsWith(".xtx")) {
			newEntity.getTableModel().setSavePath(filePath);
		}
		if (isCreated == true){
			// System.getProperty("file.separator") returns "/" for Unix Systems, "\" for Windows Systems
			frame.addTableTab(filePath.substring(filePath.lastIndexOf(System.getProperty("file.separator")) + 1), newEntity.getTableModel());
			frame.setCursorNormal();
			counterDerby++;
			return true;
		} else {
			frame.setCursorNormal();
			return false;
		}
	}
	
	/**
	 * Creates an Entry + Table + Connection to Derby DB
	 * @param frame where result will be shown
	 * @return true if no error
	 * @author mayersn
	 * @deprecated
	 */
	public static boolean  addEntityStandart (Frame frame){
		frame.setCursorWait();
		Entity newEntity = new Entity();
		Db myDb = Main.connect("derby", "user1", "user1", "", "", "./TexDB", frame);
		boolean isCreated = newEntity.init(myDb, "Tex" + counterDerby + "_", frame, false);
		if (isCreated == true){
			frame.addTableTab("Tex" + counterDerby + "_", newEntity.getTableModel());
			frame.setCursorNormal();
			counterDerby++;
			return true;
		} else {
			frame.setCursorNormal();
			return false;
			/*JOptionPane.showMessageDialog(null,
					Main.myLang.getString(Main.myLang.getString("main.errorCreateEntity.message")),
				    Main.myLang.getString(Main.myLang.getString("main.errorCreateEntity.title")),
				    JOptionPane.ERROR_MESSAGE);
			frame.setCursorNormal();
			return false;*/
		}
	}
	
	/**
	 * Creates an empty Frame
	 * @author mayersn
	 */
	public static void addFrame (){
		new Frame(2, frameName, false);
	}
	
	/**
	 * Creates an Frame with one filled Table in it
	 * @author mayersn
	 */
	public static void addFrame (String name, String compTitle, JSplitPane compContext){
		new Frame(2, frameName, name, compTitle, compContext);
	}
	
	/**
	 * 
	 * @param dbType
	 * @param user
	 * @param password
	 * @param ip
	 * @param port
	 * @param name
	 * @param frame
	 * @return
	 */
	public static Db connect (String dbType, String user, String password, String ip, String port, String name, OpenFileDialog frame){
		Db myDb;
		if (dbType == "derby"){
			myDb = new Derby(user, password, "localhost", "1527", name);
		}
		else{
			myDb = new MySQL(user, password, ip, port, name);
		}
		try {
			myDb.getConnection();
			if (myDb.isConnected() == true){
				return myDb;
			} else {
				throw new SQLException();
			}
			
		} catch (SQLException e) {
			// TODO: Bessere Erkl�rung f�r Connection Error
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					Main.myLang.getString("dialog.db.connection.message"),
				    Main.myLang.getString("dialog.db.connection.famename"),
				    JOptionPane.ERROR_MESSAGE);
				return null;
		}
		
	}
	
	/**
	 * 
	 * @param dbType
	 * @param user
	 * @param password
	 * @param ip
	 * @param port
	 * @param name
	 * @param frame
	 * @return
	 */
	public static Db connect (String dbType, String user, String password, String ip, String port, String name, Frame frame){
		Db myDb;
		if (dbType == "derby"){
			myDb = new Derby(user, password, "localhost", "1527", name);
		}
		else{
			myDb = new MySQL(user, password, ip, port, name);
		}
		
		try {
			myDb.getConnection();
			if (myDb.isConnected() == true){
				return myDb;
			} else {
				throw new SQLException();
			}
		} catch (SQLException e) {
			// TODO: Bessere Erkl�rung f�r Connection Error
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					Main.myLang.getString("dialog.db.connection.message"),
				    Main.myLang.getString("dialog.db.connection.famename"),
				    JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	/**
	 * Gets application variable with the identifier key
	 * @author mayersn
	 */
	public static String getIniData (String key){
		Iterator<ContainerIni> iIni = cIni.iterator();
		ContainerIni element = null;
		while (iIni.hasNext()){
			element = iIni.next();
			String testVar = element.getVar();
			if (testVar.equalsIgnoreCase(key)){
				return element.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Save the application variables in to the Vector and into app.ini
	 * @author mayersn
	 */
	public static void setIniData (String var, String data){
		if (var.equalsIgnoreCase("Language")){
			myLang.setLocale(data);
		}
		Iterator<ContainerIni> iIni = cIni.iterator();
		ContainerIni element = null;
		while (iIni.hasNext()){
			element = iIni.next();
			String testVar = element.getVar();
			if (testVar.equalsIgnoreCase(var)){
				element.setValue(data);
				saveIni();
				return;
			}
		}
		cIni.add(new ContainerIni(var, data));
		
		saveIni();
	}
	
	
	/**
	 * Save the application variables in app.ini
	 * @author mayersn
	 */
	private static void saveIni()
	{
		FileConnector fc = new FileConnector();
		fc.openFile("app.ini", true);
		fc.openStreamOut();
		Iterator<ContainerIni> iIni = cIni.iterator();
		ContainerIni element = null;
		while (iIni.hasNext()){
			element = iIni.next();
			fc.write(element.getVar() + "=" + element.getValue());
		}
		fc.closeStreamOut();
	} 

}
