package ctex;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class FileLocking {
	private File file;
	//TODO: move into app.ini
	private File maildir = new File("temp/mail");
	private FileChannel channel;
	private FileLock lock;
	private String applicationName;
	
	/**
	 * Class that provides functionality so the program can only be run once at a
	 * time, using a locking file in the home directory of the user.
	 * 
	 * @author Stefan
	 * @param applicationName
	 */
	
	public FileLocking(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public boolean isProgramRunning() {
		try {
			file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + applicationName + ".tmp");
			channel = new RandomAccessFile(file, "rw").getChannel();
			
			try {
				maildir.mkdirs();
			} catch (Exception e){
			}
			
			try {
				lock = channel.tryLock();
			} catch (OverlappingFileLockException e) {
				// File is already locked
				closeLock();
				return true;
			}
			
			if (lock == null) {
				closeLock();
				return true;
			}
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				// Release the lock when the JVM is closing
				public void run() {
					closeLock();
					deleteFile();
				}
			});
			return false;
			
		} catch(Exception e) {
			closeLock();
			return true;
		}
	}
	
	// Closes the lock
	private void closeLock() {
		try {
			lock.release();
			channel.close();
		} catch (Exception e) {
			
		}
	}
	
	// Deletes the file
	private void deleteFile() {
		try {
			file.delete();
			this.deleteDir(maildir);
		} catch (Exception e) {
			
		}
	}
	
	// Deletes the Dir
	private boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

}
