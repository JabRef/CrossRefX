package ctex;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

public class MultilLanguage {
	
	private Vector<Locale> SupportedLanguage = new Vector<Locale>();
	
	private Locale myLocale;
	private ResourceBundle myBundle;
	
    public Vector<Locale> getSupportedLanguage(){
    	SupportedLanguage.clear();
    	Locale[] ltest = Locale.getAvailableLocales();    	
    	int count = 0;
    	File file = null;
		while (count != ltest.length){
		    URL resource = getClass().getResource("Lang_" + ltest[count].toString() + ".properties");
			file = new File(resource.toString());
			if (file.exists()) {
				SupportedLanguage.add(ltest[count]);
			}
			count++;
		}
		return SupportedLanguage;
    }
	
	public MultilLanguage (){
		Locale.setDefault(Locale.ENGLISH);
	}
	
	public void setLocale(String lang){
		myLocale = Locale.ENGLISH;
		
		Locale[] ltest = Locale.getAvailableLocales();
		int count = 0;
		while (count != ltest.length){
			if (lang.toLowerCase().equals(ltest[count].toString().toLowerCase())){
				myLocale = ltest[count];
			}
			count++;
		}
		
		myBundle = ResourceBundle.getBundle("Lang", myLocale);
	}
	
	public String getString (String Key){
		try {
			return myBundle.getString(Key);
		} catch (Throwable e) {
			return Key;
		}
	}
	
	public void showAllKeys() {
		Enumeration<String> bundleKeys = myBundle.getKeys();

		while (bundleKeys.hasMoreElements()) {
			String key = (String)bundleKeys.nextElement();
	        String value  = myBundle.getString(key);
	        System.out.println("key = " + key + ", " + "value = " + value);
	    }

	}
}