package edu.washington.cs.conf.analysis.evol;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

public class TestPredicateExecPool extends TestCase {

	public void testGetMostFrequent() {
		String fileName = "./evol-experiments/randoop/randoop-1.2.1.txt";
		PredicateExecPool pool = new PredicateExecPool(fileName);
		//PredicateExecInfo p = pool.getMostFrequentlyExecuted();
		//need t sort
		//System.out.println(p);
		
		Comparator<PredicateExecInfo> comparator = Comparators.getFreqComparator();
		
		Collections.sort(pool.predicates, comparator);
		Collections.reverse(pool.predicates);
		
		for(int i = 0; i < 200; i++) {
			System.out.println(pool.predicates.get(i));
		}
	}
}
