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

	private final ConfParser parser;
	
	private final ValueGenerator valueGenerator = new ValueGenerator();
	
	private boolean addNonExistentOption = true;
	
	//mutate each part of the configuration property file, and output
	//the mutated results to disk
	
	public ConfMutator(String filePath) {
		this.parser = new ConfParser(filePath);
		this.parser.parseFile();
	}
	
	public void setNonExistentOption(boolean add) {
		this.addNonExistentOption = add;
	}
	
	//insert a non-existent configuration option
	public MutatedConf createNonExistentConf() {
		String nonExistentConf = "NO_EXISTENT_CONF_OPTION";
		String nonExistentValue = "NO_EXISTENT_VALUE";
		
		Map<String, String> confValues = parser.getOptionValueMap();
		confValues.put(nonExistentConf, nonExistentValue);
		
		Set<String> onOffOptions = parser.getOnOffOptions();
		String originalValue = "NO_VALUE"; //there is no original value
		
		return new MutatedConf(confValues, onOffOptions, nonExistentConf, originalValue);
	}
	
	public List<MutatedConf> mutateConfFile() {
		List<MutatedConf> mutatedConfList = new LinkedList<MutatedConf>();
		for(String optionName : this.parser.getOptions()) {
			String origValue = this.parser.getValue(optionName);
			List<String> mutatedValues = this.createMutatedValues(optionName);
			Utils.removeRedundant(mutatedValues);
			//set the mutated optionName
//			System.out.println("size of mutated values: " + mutatedValues.size());
			for(String mutatedValue : mutatedValues) {
				//if the mutated value is the same as the original one
				if(mutatedValue.equals(origValue)) {
					continue;
				}
			    Map<String, String> optionValues = this.parser.getOptionValueMap();
			    Set<String> onOffOptions = this.parser.getOnOffOptions();
			    optionValues.put(optionName, mutatedValue); //mutated the value
			    MutatedConf mutatedConf = new MutatedConf(optionValues, onOffOptions,
			    		optionName, origValue);
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
	public List<String> createMutatedValues(String optionName) {
		List<String> arrayList = new ArrayList<String>();
		
		//start to mutate
		ConfType type = this.parser.getType(optionName);
		String currValue = this.parser.getValue(optionName);
		//the mutated values
		List<Object> mutatedValues = this.valueGenerator.generateMutatedValues(currValue, type);
//		
//		System.out.println("Mutating: " + optionName + ", type: " + type);
//		System.out.println(" Original value: " + currValue);
//		System.out.println(" Mutated values: " + mutatedValues);
//		
		for(Object v : mutatedValues) {
			arrayList.add(v.toString());
		}
		
		return arrayList;
	}
	
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
		String filePath = "./sample-config-files/jmeter.properties";
		ConfMutator mutator = new ConfMutator(filePath);
		List<MutatedConf> mutatedConfList = mutator.mutateConfFile();
		System.out.println(mutatedConfList.size());
//		for(String optionName : mutator.parser.getOptions()) {
//			mutator.createMutatedValues(optionName);
//		}
		String outputDir = "./sample-config-files-after-mutated";
		int i = 0;
		for(MutatedConf mConf : mutatedConfList) {
			String outputFileName = outputDir + "/" + mutator.parser.getNextMutatedFileName();
			mConf.writeToFile(outputFileName);
			i++;
			if(i > 10) {
				break;
			}
		}
	}
}
