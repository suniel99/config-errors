package edu.washington.cs.conf.diagnosis;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class ConfDiagnosisOutput {

	private final ConfEntity conf;
	
	//need to keep some (meta) histrocal analysis information to
	//show users why it output the rank
	private List<String> explanations = new LinkedList<String>();
	
	private Float finalScore = Float.NaN; //A place holder
	
	public ConfDiagnosisOutput(ConfEntity conf) {
		Utils.checkNotNull(conf);
		this.conf = conf;
	}
	
	public ConfEntity getConfEntity() {
		return this.conf;
	}
	
	public void addExplain(String explanation) {
		this.explanations.add(explanation);
	}
	
	public void addAllExplain(Collection<String> explanations) {
		this.explanations.addAll(explanations);
	}
	
	public List<String> getExplanations() {
		return this.explanations;
	}
	
	public String getBriefExplanation() {
		return "Number of explanations: " + this.explanations.size()
		    + Globals.lineSep
		    + "     , with the first piece: "
		    + (explanations.isEmpty() ? "N/A" : explanations.get(0) )
		    + Globals.lineSep
		    + "     , with the last piece: "
		    + (explanations.isEmpty() ? "N/A" : explanations.get(explanations.size() - 1) );
	}
	
	private String errorReport = "NOT-GENERATED-YET";
	
	public void setErrorReport(String errorReport) {
		Utils.checkNotNull(errorReport);
		this.errorReport = errorReport;
	}
	
	public String getErrorReport() {
		return errorReport;
	}
	
	/**
	 * a rough outline of recovery:
	 * 
	 * String confName = this.conf.getFullConfName();
		String context = null;
		String predicateText = null;
		int lineNum = -1;
		int goodRunNum = -1;
		int goodEnter = -1;
		int badRunNum = -1;
		int badEnter = -1;
		String expl = ExplanationGenerator.createWellFormattedExpanation(confName, context, predicateText, lineNum,
				goodRunNum, goodEnter, badRunNum, badEnter);
		return expl;
	 * */
	
	public Float getFinalScore() {
		return this.finalScore;
	}
	
	public void setFinalScore(Float score) {
		this.finalScore = score;
	}
	
	public void showExplanations(PrintStream out) {
		String s = "";
		for(String ex : this.getExplanations()) {
			s =  s + ex + Globals.lineSep;
		}
		out.println(s);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ConfDiagnosisOutput)) {
			return false;
		}
		ConfDiagnosisOutput conf = (ConfDiagnosisOutput)o;
		return conf.conf.equals(this.conf);
	}
	
	@Override
	public int hashCode() {
		return this.conf.hashCode();
	}
	
	@Override
	public String toString() {
		return this.conf.toString();
	}
}