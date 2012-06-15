package edu.washington.cs.conf.diagnosis;

import instrument.Globals;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
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
	
	public static List<ConfDiagnosisOutput> rankByAvgRanking(Collection<List<ConfDiagnosisOutput>> coll) {
		Map<ConfDiagnosisOutput, List<Integer>> outputAndRanks = new LinkedHashMap<ConfDiagnosisOutput, List<Integer>>();
		Map<ConfDiagnosisOutput, ConfDiagnosisOutput> selfMapping = new LinkedHashMap<ConfDiagnosisOutput, ConfDiagnosisOutput>();
		int listNo = 0;
		for(List<ConfDiagnosisOutput> list : coll) {
			listNo++;
			int rank = 0;
			for(ConfDiagnosisOutput output : list) {
				rank++;
				if(!outputAndRanks.containsKey(output)) {
					Utils.checkTrue(!selfMapping.containsKey(output));
					ConfDiagnosisOutput copy = new ConfDiagnosisOutput(output.conf);
					outputAndRanks.put(copy, new LinkedList<Integer>());
					selfMapping.put(copy, copy);
				}
				Utils.checkTrue(outputAndRanks.size() == selfMapping.size());
				outputAndRanks.get(output).add(rank);
				//selfMapping.get(output).addExplain("From the: " + listNo + ", trace");
				selfMapping.get(output).addAllExplain(output.getExplanations());
			}
		}
		//do the ranking
		Map<ConfDiagnosisOutput, Float> rankMap = new LinkedHashMap<ConfDiagnosisOutput, Float>();
		for(ConfDiagnosisOutput output : outputAndRanks.keySet()) {
			Float avgRanking = Utils.average(outputAndRanks.get(output));
			rankMap.put(output, avgRanking);
		}
		
		List<ConfDiagnosisOutput> sortedList = Utils.sortByValueAndReturnKeys(rankMap, true); //note, the lower, the better
		Utils.checkTrue(sortedList.size() == rankMap.size());
		
//		System.err.println(rankMap);
//		System.err.println(sortedList);
		
		return sortedList;
	}
	
}