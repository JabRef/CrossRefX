package container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class ContainerType {
	
	private int id;
	
	// The entry type (such as book, author, etc.)
	private String type;
	
	// Two vectors to save required and optional information
	private Vector<String> required = new Vector<String>();
	private Vector<String> optional = new Vector<String>();
	
	// Decides if the type is a default type or if it is deletable
	private boolean deletable, entryType, invisible;
	
	public ContainerType (int id, String type, Vector<String> required, Vector<String> optional, boolean deletable, boolean EntryType, boolean invisible){
		this.id = id;
		this.type = type;
		this.required = required;
		this.optional = optional;
		this.deletable = deletable;
		this.entryType = EntryType;
		// On default all types are hidden
		this.invisible = invisible;
	}
	
	
	public ContainerType(String type) {
		this.id = -1;
		this.type = type;
	}
	
	public ContainerType(String type, boolean deletable, boolean entryType) {
		this.id = -1;
		this.type = type;
		this.deletable = deletable;
		this.entryType = entryType;
	}
	
	public int getId() {return id;}
	
	public String getType() {return type;}
	
	public boolean isDeletable() {return deletable;}
	
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}
	
	public boolean isEntryType() {return entryType;}
	
	public void setEntryType(boolean entryType) {
		this.entryType = entryType;
	}
	
	public boolean isInvisible() {return invisible;}
	
	public void setInvisible(boolean isVisible) {
		this.invisible = isVisible;
	}
	
	public Vector<String> getOptionalFields() {
		return this.optional;
	}
	
	public Vector<String> getRequiredFields() {
		return this.required;
	}
	
	/** Loads the required and optional fields from file. There are
	 * three options where the files can be loaded from: You can 
	 * load default field information (isDeletable = true), or
	 * the changeable files, or check for the backup files.
	 * If there doesn't exist a fitting file in the types/backup/
	 * directory, the vectors remain empty. Empty vectors are
	 * used for fresh types which don't have field info yet.
	 * 
	 * Note: backup/ only gets loaded, if def/ was cleared before.
	 */
	public void loadFieldst() {
		// Preparing the filepath
		final String defPath = "types/def/" + type + ".dat";
		final String usrPath = "types/usr/" + type + ".dat";
		final String backupPath =  "types/backup/" + type + ".dat";
		boolean canLoadFromDef = true;
		boolean canLoadFromUsr = true;
		boolean canLoadFromBackup = (new File(backupPath)).exists();
		
		// FileInputStream for reading the file, File type for checking the existence
		FileInputStream fileStream;
		File file;
				
		// Vectors only filled if the file exists, else the vectors remain empty
		try {
			/* First try is checking the default files, if the type is
			 *  not a default type, the user types are checked */
			file = new File(defPath);
			if (!file.exists()) {
				canLoadFromDef = false;				
				
				file = new File(usrPath);
				if (!file.exists()) {
					canLoadFromUsr = false;
				}
			}	
				
			if (canLoadFromDef || canLoadFromUsr || canLoadFromBackup) {
				deletable = false; // Backup files are default files and not deletable
				if (!canLoadFromDef && canLoadFromBackup) { // Load from backup/
					fileStream = new FileInputStream(backupPath);
				} else if (canLoadFromDef) { // Load from def/
					deletable = false; // Makes default files not deletable
					fileStream = new FileInputStream(defPath);
				} else { // Load from usr/
					deletable = true; // Makes user files deletable
					fileStream = new FileInputStream(usrPath);
				}
				
		    	DataInputStream in = new DataInputStream(fileStream);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
			
				// Filling the two vectors
				boolean isRequired = true;	
				while ((line = reader.readLine()) != null) {			
					if (line.compareTo("#") == 0) {
						isRequired = false;						
					} else if (isRequired) {
						required.add(line);
					} else {
						optional.add(line);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error loading file: " + e.getMessage());
		}	
	}

	
	/**
	 * Writes the recent values to types/usr/xyz.dat in the same form as
	 * they are loaded from the files. Can be saved either into types/def/
	 * or types/usr/.
	 */
	public void storeFields() {
		try {
			FileWriter fileStream;
			if (!deletable) {
				fileStream = new FileWriter("types/def/" + type + ".dat");
			} else {
				fileStream = new FileWriter("types/usr/" + type + ".dat");
			}
			BufferedWriter out = new BufferedWriter(fileStream);
			
			for (int i = 0; i < required.size(); i++) {
				out.write(required.get(i));
				out.newLine();
			}
			out.write("#");
			out.newLine();
			for (int i = 0; i < optional.size(); i++) {
				out.write(optional.get(i));
				out.newLine();
			}
			out.close();
		} catch (Exception e) {
			System.out.println("Error writing file: " + e.getMessage());
		}
		
	}
}
