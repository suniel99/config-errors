package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Utils;

public class PredicateProfileAnalyzer {
	
	public enum RankMethod {TFIDF, RATIO}
	
	public final PredicateProfileTuple badRun;
	
	public final List<PredicateProfileTuple> goodRuns
	    = new LinkedList<PredicateProfileTuple>();
	
	public PredicateProfileAnalyzer(PredicateProfileTuple badRun,
			Collection<PredicateProfileTuple> goodRuns) {
		Utils.checkNotNull(badRun);
		Utils.checkNotNull(goodRuns);
		Utils.checkTrue(goodRuns.size() > 0);
		this.badRun = badRun;
		this.goodRuns.addAll(goodRuns);
	}
	
	public List<String> getResponsibleConfigurations(RankMethod m) {
		return getResponsibleConfigurations(Integer.MAX_VALUE, m);
	}
	
	public List<String> getResponsibleConfigurations(int num, RankMethod m) {
		List<String> rankedList = null;
		if(m.equals(RankMethod.TFIDF)) {
			rankedList = rankResponsibleConfigsByTFIDF();
		} else if (m.equals(RankMethod.RATIO)) {
			rankedList = rankResponsibleConfigsByRatio();
		} else {
			throw new Error("Unknown: " + m);
		}
		
		Utils.checkNotNull(rankedList);
		
		//return the top num
		if(num > rankedList.size()) {
			return rankedList;
		} else {
			return rankedList.subList(0, num);
		}
	}
	
	private List<String> rankResponsibleConfigsByRatio() {
		return new LinkedList<String>();
	}
	
	private List<String> rankResponsibleConfigsByTFIDF() {
		return new LinkedList<String>();
	}
}