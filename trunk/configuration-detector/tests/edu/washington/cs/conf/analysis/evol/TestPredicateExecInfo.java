package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;

import junit.framework.TestCase;

public class TestPredicateExecInfo extends TestCase {

	public void testParsingTraces() {
		String fileName = "./evol-experiments/randoop/randoop-1.2.1.txt";
		Collection<PredicateExecInfo> coll = PredicateExecInfo.createPredicateExecInfoList(fileName);
		
		System.out.println(coll.size());
		
		for(PredicateExecInfo info : coll) {
			System.out.println(info);
		}
	}
	
}
