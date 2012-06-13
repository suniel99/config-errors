package edu.washington.cs.conf.diagnosis;

import instrument.Globals;

/**
 * A class for generating well-formatted, human-readable explanation
 * */
public class ExplanationGenerator {

	public static String createWellFormattedExpanation
	    (String confName, String context, String predicate, int lineNum,
		 int goodRunNum, int goodEnter,
		 int badRunNum, int badEnter) {
		StringBuilder sb = new StringBuilder();
		sb.append("Configuration option: " + confName);
		sb.append(Globals.lineSep);
		sb.append("The predicate: \"" + predicate + "\" in \"" + context + "\" (line: " + lineNum + ") shows different behaviors.");
		sb.append(Globals.lineSep);
		sb.append("In good runs, it evaluates to true:  " + ((float)goodEnter/goodRunNum)*100 + "% of the time (" + goodEnter + " out of " + goodRunNum + " observations)");
		sb.append(Globals.lineSep);
		sb.append("In the bad run, it evaluates to true: " + ((float)badEnter/badRunNum)*100 + "% of the time (" + badEnter + " out of " + badRunNum + " observations)");
		sb.append(Globals.lineSep);
		return sb.toString();
	}
	
	
}