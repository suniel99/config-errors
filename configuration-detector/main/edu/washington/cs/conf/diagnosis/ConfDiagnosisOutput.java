package edu.washington.cs.conf.diagnosis;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	public static List<ConfDiagnosisOutput> rankByMajorityVotes(Collection<List<ConfDiagnosisOutput>> coll) {
		
		//see the final values
		System.out.println("----------- check final values ------------");
		for(List<ConfDiagnosisOutput> l : coll) {
			for(ConfDiagnosisOutput o : l) {
				System.out.print(o.getFinalScore());
				System.out.print(", ");
			}
			System.out.println();
		}
		System.out.println("----------- end of final values ------------");
		
		int numOfObs = coll.size();
		
		Map<ConfDiagnosisOutput, List<Integer>> outputAndRanks = new LinkedHashMap<ConfDiagnosisOutput, List<Integer>>();
		Map<ConfDiagnosisOutput, ConfDiagnosisOutput> selfMapping = new LinkedHashMap<ConfDiagnosisOutput, ConfDiagnosisOutput>();
		int numOfList = 0;
		for(List<ConfDiagnosisOutput> list : coll) {
			
			//construct a rank map here, since there may be a tie
			Map<Float, List<ConfDiagnosisOutput>> confDiagnosisBuckets = new LinkedHashMap<Float, List<ConfDiagnosisOutput>>();
			for(ConfDiagnosisOutput o : list) {
				if(!confDiagnosisBuckets.containsKey(o.getFinalScore())) {
					confDiagnosisBuckets.put(o.getFinalScore(), new LinkedList<ConfDiagnosisOutput>());
				}
				confDiagnosisBuckets.get(o.getFinalScore()).add(o);
			}
			confDiagnosisBuckets = Utils.sortByKey(confDiagnosisBuckets, false);
			Map<ConfDiagnosisOutput, Integer> outputRankMap = new LinkedHashMap<ConfDiagnosisOutput, Integer>();
			int r = 1;
			for(List<ConfDiagnosisOutput> l : confDiagnosisBuckets.values()) {
				for(ConfDiagnosisOutput o : l) {
					outputRankMap.put(o, r);
				}
				r++;
			}
			
			//int rank = 0;
			for(ConfDiagnosisOutput output : list) {
				//rank++;
				if(!outputAndRanks.containsKey(output)) {
					Utils.checkTrue(!selfMapping.containsKey(output));
					ConfDiagnosisOutput copy = new ConfDiagnosisOutput(output.conf);
					copy.setFinalScore(output.getFinalScore());
					outputAndRanks.put(copy, new LinkedList<Integer>());
					selfMapping.put(copy, copy);
				}
				
				//do padding here
				int numToPad = numOfList - outputAndRanks.get(output).size();
				if(numToPad > 0) {
					for(int i = 0; i < numToPad; i++) {
						outputAndRanks.get(output).add(Integer.MIN_VALUE);
					}
				}
				
				//add others
				Utils.checkTrue(outputAndRanks.size() == selfMapping.size());
				outputAndRanks.get(output).add(outputRankMap.get(output)); //it was rank
				selfMapping.get(output).addAllExplain(output.getExplanations());
				if(output.getFinalScore() > selfMapping.get(output).getFinalScore()) {
					selfMapping.get(output).setFinalScore(output.getFinalScore());
				}
			}
			
			numOfList ++;
		}
		
		//check the ranking here
		for(List<Integer> list : outputAndRanks.values()) {
			Utils.checkTrue(list.size() == numOfObs, "Num of obs: " + numOfObs + ", size: " + list.size());
		}
		
		//do ranking below
		for(ConfDiagnosisOutput output : outputAndRanks.keySet()) {
			System.out.println(output.getConfEntity().getFullConfName());
			System.out.println("   " + outputAndRanks.get(output).toString());
		}
		
		//do the ranking
		Map<ConfDiagnosisOutput, Float> outputScoreMap = new LinkedHashMap<ConfDiagnosisOutput, Float>();
		
		for(ConfDiagnosisOutput output : outputAndRanks.keySet()) {
			//compute a score for it
			List<Integer> ranks = outputAndRanks.get(output);
			//iterate through the remaining see how many it could beat
			int numberOfBeat = 0;
			for(ConfDiagnosisOutput cmp : outputAndRanks.keySet()) {
				if(output.equals(cmp)) {
					continue;
				}
				//test if output ranks higher than cmp
				List<Integer> cmpRanks = outputAndRanks.get(cmp);
				if(rankHigher(ranks, cmpRanks)) {
					numberOfBeat ++;
				}
			}
			
			//FIXME
			output.setFinalScore((float)numberOfBeat);
			
			//add to the map
			outputScoreMap.put(output, (float)numberOfBeat);
		}
		
		//return the result
		List<ConfDiagnosisOutput> sortedList = Utils.sortByValueAndReturnKeys(outputScoreMap, false); //note, the more the better
		return sortedList;
	}
	
	
	
	//if l1 is higher than l2, returns true, otherwise, returns false
	static boolean rankHigher(List<Integer> l1, List<Integer> l2) {
		Utils.checkTrue(l1.size() == l2.size(), "l1: " + l1.size() + ",  l2: " + l2.size());
		
		Integer max1 = Collections.max(l1);
		Integer min1 = Collections.min(l1);
		Integer sum1 = Utils.sum(l1);
		
		Integer max2 = Collections.max(l2);
		Integer min2 = Collections.min(l2);
		Integer sum2 = Utils.sum(l2);
		
		return (sum1 - max1 - min1) <= (sum2 - min2 - max2);
	}
	
	@Deprecated
	public static List<ConfDiagnosisOutput> rankByAvgRanking(Collection<List<ConfDiagnosisOutput>> coll) {
		Map<ConfDiagnosisOutput, List<Integer>> outputAndRanks = new LinkedHashMap<ConfDiagnosisOutput, List<Integer>>();
		Map<ConfDiagnosisOutput, ConfDiagnosisOutput> selfMapping = new LinkedHashMap<ConfDiagnosisOutput, ConfDiagnosisOutput>();
		int listNo = 0;
		for(List<ConfDiagnosisOutput> list : coll) {
			listNo++;
			int rank = 0;
			for(ConfDiagnosisOutput output : list) {
//				System.err.println(output.getFinalScore());
				rank++;
				if(!outputAndRanks.containsKey(output)) {
					Utils.checkTrue(!selfMapping.containsKey(output));
					ConfDiagnosisOutput copy = new ConfDiagnosisOutput(output.conf);
					copy.setFinalScore(output.getFinalScore());
					outputAndRanks.put(copy, new LinkedList<Integer>());
					selfMapping.put(copy, copy);
				}
				Utils.checkTrue(outputAndRanks.size() == selfMapping.size());
				outputAndRanks.get(output).add(rank);
				//selfMapping.get(output).addExplain("From the: " + listNo + ", trace");
				selfMapping.get(output).addAllExplain(output.getExplanations());
//				System.err.println("final : " + output.getFinalScore() + ", existing final: " + selfMapping.get(output).getFinalScore());
				if(output.getFinalScore() > selfMapping.get(output).getFinalScore()) {
					selfMapping.get(output).setFinalScore(output.getFinalScore());
				}
				//selfMapping.get(output).setFinalScore(output.getFinalScore());
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