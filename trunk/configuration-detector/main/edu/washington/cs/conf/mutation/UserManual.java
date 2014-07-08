package edu.washington.cs.conf.mutation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class UserManual {

	private Map<String, String> options = new LinkedHashMap<String, String>();
	
	//the file format
	//option name: description (split from the first : )
	static String SPLIT = ":";
	
	//read options from file
	public UserManual(String filePath) {
		List<String> lines = Files.readWholeNoExp(filePath);
		for(String line : lines) {
			if(line.trim().length() == 0) {
				continue;
			}
			int index = line.indexOf(SPLIT);
			if(index == -1) {
				throw new RuntimeException("Invalid line: " + line);
			}
			String optionName = line.substring(0, index);
			String description = (index + SPLIT.length() == line.length() - 1)
			    ? "" : line.substring(index + SPLIT.length());
			Utils.checkTrue(!options.containsKey(optionName), "Already contain: " + optionName);
			//add to the option map
			options.put(optionName, description);
		}
		
	}
	
	public String getDescription(String option) {
		Utils.checkTrue(options.containsKey(option));
		return options.get(option);
	}
	
	public Set<String> getAllOptions() {
		return this.options.keySet();
	}
}