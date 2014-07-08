package edu.washington.cs.conf.mutation;

import java.util.Map;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class MutatedConf {

	private final Map<String, String> mutatedConfValues;
	private final String mutatedConf;
	private final String originalConfValue;
	
	public MutatedConf(Map<String, String> confValues, String mutatedConf, String originalConfValue) {
		Utils.checkNotNull(confValues);
		Utils.checkNotNull(mutatedConf);
		Utils.checkNotNull(originalConfValue);
		
		this.mutatedConfValues = confValues;
		this.mutatedConf = mutatedConf;
		this.originalConfValue = originalConfValue;
	}
	
	public String getMutatedConfOption() {
		return this.mutatedConf;
	}
	
	public String getMutatedConfValue() {
		return this.mutatedConfValues.get(this.mutatedConf);
	}
	
	//return command line like: -option1=value1 -option2=value2 ...
	public String createCmdLine() {
		StringBuilder sb = new StringBuilder();
		for(String option : mutatedConfValues.keySet()) {
			sb.append(" -");
			sb.append(option);
			sb.append("=");
			sb.append(mutatedConfValues.get(option));
		}
		return sb.toString();
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
}
