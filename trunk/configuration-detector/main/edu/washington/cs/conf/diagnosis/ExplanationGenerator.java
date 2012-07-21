package edu.washington.cs.conf.diagnosis;

import edu.washington.cs.conf.util.Globals;

/**
 * A class for generating well-formatted, human-readable explanation
 * */
public class ExplanationGenerator {

	public static String createWellFormattedExpanation
	    (String confName, String context, String predicate, int lineNum,
		 int goodRunNum, int goodEnter,
		 int badRunNum, int badEnter) {
		//avoid the dvide-by-zero bug
		float goodEnterRatio = goodRunNum != 0 ? ((float)goodEnter/goodRunNum)*100 : Float.NaN;
		float badEnterRatio = badRunNum != 0 ? ((float)badEnter/badRunNum)*100 : Float.NaN;
		
		//create the string below
		StringBuilder sb = new StringBuilder();
		sb.append("Suspicious configuration option: " + confName);
		sb.append(Globals.lineSep);
		sb.append(Globals.lineSep);
		sb.append("It affects the behavior of predicate: ");
		sb.append(Globals.lineSep);
		sb.append("\"" + predicate + "\" ");
		sb.append(Globals.lineSep);
		sb.append("(line: " + lineNum + ", class: " + context + ")");
		sb.append(Globals.lineSep);
		sb.append(Globals.lineSep);
		sb.append("This predicate evaluates to true: ");
		sb.append(Globals.lineSep);
		sb.append("   " + goodEnterRatio + "% of the time in normal runs (" + goodRunNum + " observations)");
		sb.append(Globals.lineSep);
		sb.append("   " + badEnterRatio + "% of the time in an undesirable run (" + badRunNum + " observations)");
		sb.append(Globals.lineSep);
		return sb.toString();
	}
	
	
}