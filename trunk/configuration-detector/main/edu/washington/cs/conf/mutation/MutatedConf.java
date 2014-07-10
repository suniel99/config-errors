package edu.washington.cs.conf.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class MutatedConf {

	private final Map<String, String> mutatedConfValues;
	private final String mutatedConf;
	private final String originalConfValue;
	
	public static String PREFIX="-";
	
	public MutatedConf(Map<String, String> confValues, String mutatedConf, String originalConfValue) {
		Utils.checkNotNull(confValues);
		Utils.checkNotNull(mutatedConf);
		Utils.checkNotNull(originalConfValue);
		
		this.mutatedConfValues = confValues;
		this.mutatedConf = mutatedConf;
		this.originalConfValue = originalConfValue;
	}
	
	//by default, it is "-", sometimes, it might be "--"
	//or "" (empty space)
	public static void setConfPrefix(String prefix) {
		PREFIX = prefix;
	}
	
	public String getMutatedConfOption() {
		return this.mutatedConf;
	}
	
	public String getMutatedConfValue() {
		return this.mutatedConfValues.get(this.mutatedConf);
	}
	
	public String createCmdLineForMutatedOptions() {
		return PREFIX + this.mutatedConf + "=" + this.mutatedConfValues.get(this.mutatedConf);
	}
	
	//return command line like: -option1=value1 -option2=value2 ...
	public String createCmdLine() {
		StringBuilder sb = new StringBuilder();
		for(String option : mutatedConfValues.keySet()) {
			sb.append(" ");
			sb.append(PREFIX);
			sb.append(option);
			sb.append(" ");
			sb.append(mutatedConfValues.get(option));
		}
		return sb.toString();
	}
	
	public String[] createCmdLineAsArgs() {
		List<String> list = new ArrayList<String>();
		
		for(String option : mutatedConfValues.keySet()) {
			list.add(PREFIX + option);
			String v = mutatedConfValues.get(option); 
			if(!v.equals("")) {
				list.add(v);
			}
		}
		
		return list.toArray(new String[0]);
	}
	
	public void writeToFile(String filePath) {
        StringBuilder sb = new StringBuilder();
		
		for(String option : mutatedConfValues.keySet()) {
			//append comments
			if(option.equals(mutatedConf)) {
			    sb.append("##--");
			    sb.append("original value: " + originalConfValue);
			    sb.append(Globals.lineSep);
			}
			sb.append(option);
			sb.append("=");
			sb.append(mutatedConfValues.get(option));
			sb.append(Globals.lineSep);
		}
		
		Files.writeToFileNoExp(sb.toString(), filePath);
	}
	
	@Override
	public String toString() {
		return "mutate: " + this.mutatedConf + " with value: " + mutatedConfValues.get(this.mutatedConf);
	}
}
