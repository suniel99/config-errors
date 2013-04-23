package edu.washington.cs.conf.instrument;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;

public class InstrumentStats {

	static int numOfInsertedInstructions = 0;
	
	static Set<String> instrumentedPos = new LinkedHashSet<String>();
	
	public static void addInsertedInstructions(int num) {
		numOfInsertedInstructions += num;
	}
	
	public static void showInstrumentationStats() {
		System.out.println(numOfInsertedInstructions);
	}
	
	public static void addInstrumentedPositions(String pos) {
		instrumentedPos.add(pos);
	}
	
	public static void writeInstrumentedPositions(String fileName) {
		StringBuilder sb = new StringBuilder();
		for(String s : instrumentedPos) {
			sb.append(s);
			sb.append(Globals.lineSep);
		}
		Files.writeToFileNoExp(sb.toString(), fileName);
	}
}