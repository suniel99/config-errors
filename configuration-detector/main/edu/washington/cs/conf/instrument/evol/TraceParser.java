package edu.washington.cs.conf.instrument.evol;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class TraceParser {
	
	//Parse the sig mapping files
	//from the integer to the instruction signature
	public static Map<Integer, String> parseSigNumMapping(String fileName) {
		Utils.checkNotNull(fileName);
		Map<Integer, String> sigMap = new LinkedHashMap<Integer, String>();
		for(String line : Files.readWholeNoExp(fileName)) {
			String[] splits = line.split(PredicateInstrumenter.sigSep);
			Utils.checkTrue(splits.length == 2);
			Integer num = Integer.parseInt(splits[1]);
			String sig = splits[0];
			Utils.checkTrue(!sigMap.containsKey(num));
			sigMap.put(num, sig);
		}
		return sigMap;
	}
	
}