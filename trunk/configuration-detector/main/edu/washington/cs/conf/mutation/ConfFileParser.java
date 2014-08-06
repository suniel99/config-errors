package edu.washington.cs.conf.mutation;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.mutation.ConfValueTypeInferrer.ConfType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class ConfFileParser {

	//the re-implementation of Java property parser
	private String confFile = null;
	//the file content
	private final List<String> lines;
	
	//the configuration keys
	List<String> keys = new ArrayList<String>();
	Map<String, List<Integer>> keyIndex = new LinkedHashMap<String, List<Integer>>();
	List<String> values = new ArrayList<String>();
	
	//types of each option
	final Map<String, Set<ConfType>> typeMap = new LinkedHashMap<String, Set<ConfType>>();
	
	//an on/off option does not require a concrete value after it
	//like: --verbose
	private final Set<String> onOffOptions = new LinkedHashSet<String>();
	
	public ConfFileParser(String confFile) {
		this.confFile = confFile;
		lines = Files.readWholeNoExp(confFile);
	}
	
	public ConfFileParser(List<String> lines) {
		Utils.checkNotNull(lines);
		this.lines = lines;
	}
	
	public List<String> getAllConfLines() {
		return this.lines;
	}
	
	public List<String> getConfOptionNames() {
		return this.keys;
	}
	
	public List<String> getConfOptionValues() {
		return this.values;
	}
	
	public String getConfOptionValue(int index) {
		Utils.checkTrue(index >= 0 && index < this.values.size());
		return this.values.get(index);
	}
	
	public Set<String> getOnOffOptions() {
		return this.onOffOptions;
	}
	
	public Set<ConfType> getTypes(String optionName) {
		return typeMap.get(optionName);
	}
	
	public List<String> getConfValues(String optionName) {
		List<String> values = new LinkedList<String>();
		List<Integer> indices = this.keyIndex.get(optionName);
		for(Integer index : indices) {
			values.add(this.values.get(index));
		}
		return values;
	}
	
	public List<Integer> getConfOptionIndices(String optionName) {
		return this.keyIndex.get(optionName);
	}
	
	public boolean isOnOffOption(String optionName) {
		return this.onOffOptions.contains(optionName);
	}
	
	public void parse() {
		//parse each line
		for(int index = 0; index < lines.size(); index++) {
			String line = lines.get(index).trim();
			if(line.equals("")) {
				continue;
			}
			if(line.startsWith("#")) {
				continue;
			}
			//parse it
			int splitIndex = line.indexOf("=");
			String key = splitIndex == -1 ? line : line.substring(0, splitIndex).trim();
			String value = splitIndex == -1 ? "" : line.substring(splitIndex + 1).trim();
			//add to the internal data structure
			if(!keyIndex.containsKey(key)) {
				keyIndex.put(key, new ArrayList<Integer>());
			}
			keyIndex.get(key).add(index);
			keys.add(key);
			values.add(value);
			
			//infer the possible types
			Set<ConfType> types = ConfValueTypeInferrer.inferPossibleTypes(value);
			if(!this.typeMap.containsKey(key)) {
				this.typeMap.put(key, new LinkedHashSet<ConfType>());
			}
			this.typeMap.get(key).addAll(types);
		}
	}
	
	public void dumpFile() {
		for(String line : lines) {
			System.out.println(line);
		}
	}
	
	private static int count = 1;
	public String getNextMutatedFileName() {
		File f = new File(this.confFile);
		return "mutated-" + (count++) + "-" + f.getName();
	}
	
}
