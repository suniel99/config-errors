package edu.washington.cs.conf.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class MutatedConf {

	private final Map<String, String> mutatedConfValues;
	private final Set<String> onOffOptions;
	private final String mutatedConf;
	private final String originalConfValue;
	
	public static String PREFIX="-";
	
	public MutatedConf(Map<String, String> confValues, Set<String> onOffOptions,
			String mutatedConf, String originalConfValue) {
		Utils.checkNotNull(confValues);
		Utils.checkNotNull(onOffOptions);
		Utils.checkNotNull(mutatedConf);
		Utils.checkNotNull(originalConfValue);
		
		this.mutatedConfValues = confValues;
		this.onOffOptions = onOffOptions;
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
	
	//return command line like: -option1 value1 -option2 value2 ...
	@Deprecated
	public String createCmdLine() {
		StringBuilder sb = new StringBuilder();
		for(String option : mutatedConfValues.keySet()) {
			String v = mutatedConfValues.get(option); 
			if(this.onOffOptions.contains(option)) {
				Utils.checkTrue(v.toLowerCase().equals("true") || v.toLowerCase().equals("false"));
				if(v.toLowerCase().equals("true")) {
					sb.append(" ");
					sb.append(PREFIX);
					sb.append(option);
				}
			} else {
			    //process other normal options
				sb.append(" ");
				sb.append(PREFIX);
				sb.append(option);
				sb.append(" ");
				sb.append(v);
			}
		}
		return sb.toString();
	}
	
	public String[] createCmdLineAsArgs() {
		List<String> list = new ArrayList<String>();
		
		for(String option : mutatedConfValues.keySet()) {
			//PREFIX here is like: -, --, or nothing, user-settable
			String v = mutatedConfValues.get(option); 
			
			if(this.onOffOptions.contains(option)) {
				Utils.checkTrue(v.toLowerCase().equals("true") || v.toLowerCase().equals("false"));
				if(v.toLowerCase().equals("true")) {
					list.add(PREFIX + option);
				}
			} else {
			    //process other normal options
			    list.add(PREFIX + option);
			    //empty value
			    if(!v.equals("")) {
				    list.add(v);
			    }
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
