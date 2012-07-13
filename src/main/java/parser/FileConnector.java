package parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
/** 
 * File content management
 * 
 * + Open a File with 
 * - Tex_FileContent(path)
 * - or
 * - CTex_FileContent() and openFile (String filePath, boolean create) if you want to crate a file if it dosen't exists
 * 
 * ++ Write into a File 
 *  - First openStreamOut() then write (String) how often you want and then to Close Strem closeStreamOut ()
 * 
 * ++ Read out of a File 
 *  - First readFileContent() and then getFileContent() return the File content
 *  
 * 
 * @author Lars Lischke
 * @version 0.2
 */
public class FileConnector {
	private String content = new String ();
	private File f = null;
	private FileOutputStream streamOut = null;
	private String textEncoding = new String ("UTF-8");
	
	public FileConnector(){
	}
	
	/**
	 * 
	 * @param filePath
	 * 
	 */
	public FileConnector(String filePath){
		f = new File(filePath);
		
	}
	
	/**
	 * 
	 * @param filePath Path of the file, which should be read
	 * @param textEncod Encodingtype of the file (US-ASCII, ISO646-US, ISO-8859-1, ISO-LATIN-1, UTF-8, UTF-16BE,
	 * UTF-16LE) 
	 * @author lischkls
	 */
	public FileConnector(String filePath, String textEncod){
		f = new File(filePath);
		textEncoding = textEncod;
	}
	
	/**
	 * open a File with given path
	 * @param filePath
	 * @param create true if you want to create a file if it dosen't exists
	 * @return
	 * @author SamyStyle
	 */
	public boolean openFile (String filePath, boolean create){
	     f = new File(filePath);
	     if (!f.exists() && create == true){
	    	 try {
				f.createNewFile();
				return true;
			} catch (IOException e) {
				System.out.println(e.toString());
				return false;
			}
	     }
	     return true;
	}
	
	/**
	 * Creates a new file with a given path
	 * 
	 * @param filePath
	 * @return true if successful
	 */
	public boolean createFile(String filePath) {
		f = new File(filePath);
		try {
			f.createNewFile();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/** 
	 * Reads the content of a text file. 
	 * 
	 * @author lischkls
	 * @since 0.1
	 */
  	public void readFileContent (){
  		
		String line;
		try {
  		    BufferedReader in = new BufferedReader (new InputStreamReader(new FileInputStream(f), textEncoding));
			 
			if ((line = in.readLine()) != null){
				content = line;
			}
			while ((line = in.readLine()) != null) {
				  content = content + '\n' + line;
			}
			in.close();
		  }catch (IOException FileNotFoundException) {
		        System.out.println("File not found!");
		  }
	  }
  	
	/** 
	 * Get the content string back
	 * @return The hole content of the read file
	 * 
	 * @author lischkls
	 * @since 0.1
	 */
  	public String getFileContent (){
  		return content;
  	}
  	
  	/**
	 * before you can write() in a file you have to open a output Stream
	 * @return
	 * 
	 * @author SamyStyle
	 */
	public boolean openStreamOut (){
		try {
			streamOut = new FileOutputStream(f);
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}
  	
	
  	/**
  	 * 
  	 * @param text
  	 * @return
  	 * 
  	 * @author SamyStyle
  	 */
  	public boolean write (String text){
		try {
			text = text + "\n";
			streamOut.write(text.getBytes());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
  	
  	/**
  	 * Colse on the and of a output stream
  	 * @author SamyStyle
  	 */
  	public boolean closeStreamOut (){
  		try {
			streamOut.flush();
			streamOut.close();
			return true;
		} catch (IOException e) {
			return false;
		}	
  	}
}
