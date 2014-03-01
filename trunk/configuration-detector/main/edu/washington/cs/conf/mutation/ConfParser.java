package edu.washington.cs.conf.mutation;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import edu.washington.cs.conf.util.Utils;

//parse a property file
public class ConfParser {

	private final String propertyFile;
	
	public ConfParser(String propertyFile) {
		Utils.checkNotNull(propertyFile);
		this.propertyFile = propertyFile;
	}
	
	//parse into a set key-value pairs
	public void parseFile() {
		Properties prop = new Properties();
		 
    	try {
            //load a properties file
    		prop.load(new FileInputStream(this.propertyFile));
 
            for(Object key : prop.keySet()) {
            	Object value = prop.get(key);
            	System.out.println(key + ",  " + value);
            	System.out.println("   " + ConfValueTypeInferrer.inferPossibleTypes(value.toString()));
            }
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
 
	}
	
	public static void main(String[] args) {
		String filepath = "./sample-config-files/jmeter.properties";
		ConfParser cp = new ConfParser(filepath);
		cp.parseFile();
	}
	
}
