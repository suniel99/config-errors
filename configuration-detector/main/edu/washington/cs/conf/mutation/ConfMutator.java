package edu.washington.cs.conf.mutation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.mutation.ConfValueTypeInferrer.ConfType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

/**
 * Mutate the configuration file, and write the mutated conf to the disk
 * */
public class ConfMutator {

	private final ConfFileParser parser;
	
	private final ValueGenerator valueGenerator = new ValueGenerator();
	
	private boolean addNonExistentOption = true;
	
	//mutate each part of the configuration property file, and output
	//the mutated results to disk
	
	public ConfMutator(String filePath) {
		this.parser = new ConfFileParser(filePath);
		this.parser.parse();
	}
	
	public ConfFileParser getParser() {
		return this.parser;
	}
	
	public void setNonExistentOption(boolean add) {
		this.addNonExistentOption = add;
	}
	
	//insert a non-existent configuration option
	public MutatedConf createNonExistentConf() {
		String nonExistentConf = "NO_EXISTENT_CONF_OPTION";
		String nonExistentValue = "NO_EXISTENT_VALUE";
		
		return MutatedConf.createNonExistentMutatedConf(this.parser, nonExistentConf, nonExistentValue);
	}
	
	public List<MutatedConf> mutateConfFile() {
		List<MutatedConf> mutatedConfList = new LinkedList<MutatedConf>();
		List<String> confNameList = this.parser.getConfOptionNames();
		for(int index = 0; index < confNameList.size(); index++) {
			String optionName = confNameList.get(index);
			String origValue = this.parser.getConfOptionValue(index);
			List<String> mutatedValues = this.createMutatedValues(optionName);
			Utils.removeRedundant(mutatedValues);
			//set the mutated optionName
//			System.out.println("size of mutated values: " + mutatedValues.size());
			for(String mutatedValue : mutatedValues) {
				//if the mutated value is the same as the original one
				if(mutatedValue.equals(origValue)) {
					continue;
				}
			    MutatedConf mutatedConf = new MutatedConf(this.parser, optionName, mutatedValue, index);
			    //add to the list
			    mutatedConfList.add(mutatedConf);
			}
		}
		
		//add the non-existent conf options
		if(this.addNonExistentOption) {
			MutatedConf nonExistent = this.createNonExistentConf();
			mutatedConfList.add(nonExistent);
		}
		
		return mutatedConfList;
	}
	
	//start to mutate the read conf file
	//produce a list of values
	List<String> createMutatedValues(String optionName) {
		List<String> arrayList = new ArrayList<String>();
		
		//start to mutate
		Set<ConfType> types = this.parser.getTypes(optionName);
		List<String> confValues = this.parser.getConfValues(optionName);
		for(ConfType type : types) {
			List<Object> mutatedValues = this.valueGenerator.generateMutatedValues(confValues, type);
			for(Object v : mutatedValues) {
				arrayList.add(v.toString());
			}
		}
//		System.out.println("Mutating: " + optionName + ", type: " + type);
//		System.out.println(" Original value: " + currValue);
//		System.out.println(" Mutated values: " + mutatedValues);
//		
		return arrayList;
	}
	
	@Deprecated
	public static void writeConfFile(String filePath, Map<String, String> confValues) {
		StringBuilder sb = new StringBuilder();
		
		for(String option : confValues.keySet()) {
			sb.append(option);
			sb.append("=");
			sb.append(confValues.get(option));
			sb.append(Globals.lineSep);
		}
		
		Files.writeToFileNoExp(sb.toString(), filePath);
	}
	
	public static void main(String[] args) {
		
	}
}
