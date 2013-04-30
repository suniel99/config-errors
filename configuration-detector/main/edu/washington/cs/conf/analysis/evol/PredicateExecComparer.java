package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Utils;

/**
 * Compare two predicate pools to get the most behavior-deviated
 * predicates.
 * */
public class PredicateExecComparer {

	public final PredicateExecPool pool1;
	public final PredicateExecPool pool2;
	
	public PredicateExecComparer(PredicateExecPool pool1, PredicateExecPool pool2) {
		Utils.checkNotNull(pool1);
		Utils.checkNotNull(pool2);
		this.pool1 = pool1;
		this.pool2 = pool2;
	}
	
	public List<PredicateExecInfo> getMostDifferenceBehaviors() {
		
		List<PredicateExecInfo> rankedList = new LinkedList<PredicateExecInfo>();
//		
//		Map<PredicateExecInfo>
		
//		1. get the code, predicate
//		2. match it across versions
//		3. 
		
		return rankedList;
	}
}
