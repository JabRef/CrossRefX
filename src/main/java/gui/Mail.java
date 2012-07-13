package gui;

/**
 * 
 */

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.swing.JOptionPane;

import parser.ParseToString;

import container.ContainerFile;
import container.ContainerObject;
import ctex.Main;

import database.Db;
public class Mail {
	
	
	/**
	 * 
	 * @param theDb
	 * @param obj
	 */
	Mail (Db theDb, ContainerObject obj){
		Desktop desktop = null;
		String mailTo = "?BODY="+ ((new ParseToString(theDb, obj)).getXtx());
        
        mailTo = mailTo.concat("&subject=CrossTeX " + obj.getKey() + " by CrossRefX");
        
        //File anhaengen
        Iterator<ContainerFile> i = theDb.getAllFiles(obj.getId()).iterator();
        while (i.hasNext()){
        	File f = theDb.getFile(i.next().getId());
        	mailTo = mailTo.concat("&attachment="+f.getAbsolutePath());
        }
        mailTo = mailTo.concat("&attachment="+getXtxLink(theDb, obj));
        URI uriMailTo = null;
        
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            // Now enable buttons for actions that are supported.
        } 
        try {
			uriMailTo = new URI("mailto", mailTo, null);
			//TODO:: Jungs so löchen macht keinen sinn. Wenn die mail noch nicht abgeschickt ist, und die Datei schon wieder weg ist?
			//File file = new File(CTex_Main.workspace + getFileName(obj));
			//file.delete();
			//
			//Vorschlag von Oliver:
            //tmp = File.createTempFile("jabrefCb", ".tmp");
            //tmp.deleteOnExit();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			desktop.mail(uriMailTo);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Main.myLang.getString("mail.errormessage"),
						Main.myLang.getString("mail.errortitle"), JOptionPane.OK_OPTION);
		}    
	}
	
	/**
	 * 
	 *  liefert die xtx Link aus dem workspace
	 *
	 * @param theDb
	 * @param obj
	 * @return
	 */
	private String getXtxLink (Db theDb, ContainerObject obj) {
		String dirName = Main.getIniData("MailDir") + "/";
		String mailPath = getFileName(obj);
		
		//Create Folder
		File fDir = new File(dirName);
		if (!fDir.isDirectory()) {
			fDir.mkdir();
		}
		
		// Create file 
		File f = new File (dirName + mailPath);
		FileWriter fstream;
		try {
			fstream = new FileWriter(f.getAbsoluteFile());
			BufferedWriter out = new BufferedWriter(fstream);
		    out.write(((new ParseToString(theDb, obj)).getXtx()));
		    out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f.getAbsolutePath();
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	
	private String getFileName (ContainerObject obj){
		String file = null;
		String dummy = obj.getKey().concat("/");
		while (dummy.length() != 0){
			file = dummy.substring(0, dummy.indexOf("/"));
			dummy = dummy.substring(dummy.indexOf("/") + 1);
		}
		return file + ".xtx";
	}
}