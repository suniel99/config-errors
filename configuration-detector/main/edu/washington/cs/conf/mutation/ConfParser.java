package edu.washington.cs.conf.mutation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.washington.cs.conf.mutation.ConfValueTypeInferrer.ConfType;
import edu.washington.cs.conf.util.Utils;

//parse a property file
public class ConfParser {

	private final String propertyFile;
	
	//store the key-value pair, and key-type pair
	private final Map<String, String> valueMap = new LinkedHashMap<String, String>();
	private final Map<String, Set<ConfType>> typeMap = new LinkedHashMap<String, Set<ConfType>>();
	
	public ConfParser(String propertyFile) {
		Utils.checkNotNull(propertyFile);
		this.propertyFile = propertyFile;
	}
	
	public Map<String, String> getOptionValueMap() {
		Map<String, String> mapCopy = new LinkedHashMap<String, String>();
		mapCopy.putAll(valueMap);
		return mapCopy;
	}
	
	public Set<String> getOptions() {
		return valueMap.keySet();
	}
	
	public Set<ConfType> getTypes(String confOption) {
		return typeMap.get(confOption);
	}
	
	//assuming at least one type for an option
	public ConfType getType(String confOption) {
		return typeMap.get(confOption).iterator().next();
	}
	
	public String getValue(String confOption) {
		return valueMap.get(confOption);
	}
	
	//parse into a set key-value pairs
	public void parseFile() {
		Properties prop = new Properties();
		 
    	try {
            //load a properties file
    		prop.load(new FileInputStream(this.propertyFile));
 
            for(Object key : prop.keySet()) {
            	Object value = prop.get(key);
            	Set<ConfType> type = ConfValueTypeInferrer.inferPossibleTypes(value.toString());
            	
            	valueMap.put(key.toString(), value.toString());
            	typeMap.put(key.toString(), type);
            	
//            	System.out.println(key + ",  " + value);
//            	System.out.println("   " + type);
            }
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
	
	private static int count = 1;
	public String getNextMutatedFileName() {
		File f = new File(this.propertyFile);
		return "mutated-" + (count++) + "-" + f.getName();
	}
	
	public static void main(String[] args) {
		String filepath = "./sample-config-files/jmeter.properties";
		ConfParser cp = new ConfParser(filepath);
		cp.parseFile();
	}
	
}
