package edu.washington.cs.conf.analysis.evol;

import java.util.Comparator;

public class PredicateExecInfoComparators {

	public static Comparator<PredicateExecInfo> getFreqComparator() {
		Comparator<PredicateExecInfo> comparator = new Comparator<PredicateExecInfo>() {
			@Override
			public int compare(PredicateExecInfo o1, PredicateExecInfo o2) {
				return o1.evalFreqCount > o2.evalFreqCount ? 1 : 0;
			}
			
		};
		return comparator;
	}
	
}