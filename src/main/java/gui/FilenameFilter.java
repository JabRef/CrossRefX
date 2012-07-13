package gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/** 
 * Very simple file filter so the JFileChooser doesn't show all kinds of files
 *  
 * @author mayersn
 */



public class FilenameFilter extends FileFilter {
	private String extensions[];
	
	private String description;
	public FilenameFilter(String description, String extension) {
		this(description, new String[] { extension });
	}
	
	public FilenameFilter(String description, String extensions[]) {
		this.description = description;
		this.extensions = (String[]) extensions.clone();
	}


	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		int count = extensions.length;
		for (int i = 0; i < count; i++) {
			String ext = extensions[i];
			if (file.isFile() && file.getName().endsWith(ext) && (file.getName().charAt(file.getName().length() - ext.length() -1) == '.')) {
				return true;
			}
		}
		return false;
	}

	public String getDescription() {
		return (description == null ? extensions[0] : description);
	}
	    	
	public String getExtensions() { return extensions[0];}
}
