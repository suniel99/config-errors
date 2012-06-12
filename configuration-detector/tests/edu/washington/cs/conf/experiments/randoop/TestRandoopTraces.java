package edu.washington.cs.conf.experiments.randoop;

import edu.washington.cs.conf.diagnosis.PredicateProfileTuple;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.diagnosis.TraceAnalyzer;
import junit.framework.TestCase;

public class TestRandoopTraces extends TestCase {
	
	public void test1() {
		//compare the same
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.0f);
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.0f);
	}
	
	public void test2() {
		//2 good traces
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.01677453f);
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.120200634f);
	}
	
	public void test3() {
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.11405811f);
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.27575958f);
	}
	
	public void test4() {
		
		String goodRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.2617045f);
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.117307134f);
	}
	
	//this sounds  incorrect
	public void test5() {
		String goodRunTrace = "./experiments/randoop-database/show_help.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.9330544f);
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.85614043f);
	}
	
	public void test6() {
		String goodRunTrace = "./experiments/randoop-database/show_help.txt";
		String badRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.9373041f);
		this.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.85133606f);
	}
	
	private void compareTraceDistance(String goodRunTrace, String badRunTrace, DistanceType t, Float expected) {
		PredicateProfileTuple good = TraceAnalyzer.createGoodProfileTuple(goodRunTrace, "good-run");
		PredicateProfileTuple bad = TraceAnalyzer.createBadProfileTuple(badRunTrace, "bad-run");
		
		ProfileDistanceCalculator.showAlignedVectors(good, bad);
		
		float distance = ProfileDistanceCalculator.computeDistance(good, bad, t);
		System.out.println(t + " distance: " + distance);
		assertEquals(expected, distance);
	}

}
