package edu.washington.cs.conf.experiments.randoop;

import edu.washington.cs.conf.diagnosis.PredicateProfileTuple;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.diagnosis.TraceAnalyzer;
import junit.framework.TestCase;

public class TestRandoopTraces extends TestCase {
	
	public void testComparingTraces() {
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		
		PredicateProfileTuple good = TraceAnalyzer.createGoodProfileTuple(goodRunTrace, "good-run");
		PredicateProfileTuple bad = TraceAnalyzer.createBadProfileTuple(badRunTrace, "bad-run");
		
		float distance = ProfileDistanceCalculator.computeDistance(good, bad, DistanceType.INTERPRODUCT);
		System.out.println("interproduct distance: " + distance);
		assertEquals(0.0f, distance);
		
		distance = ProfileDistanceCalculator.computeDistance(good, bad, DistanceType.SUBTRACTION);
		System.out.println("substraction distance: " + distance);
		
		goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		badRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		good = TraceAnalyzer.createGoodProfileTuple(goodRunTrace, "good-run");
		bad = TraceAnalyzer.createBadProfileTuple(badRunTrace, "bad-run");
		
		distance = ProfileDistanceCalculator.computeDistance(good, bad, DistanceType.INTERPRODUCT);
		System.out.println("interproduct distance good1 to good2: " + distance);
		
		goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		good = TraceAnalyzer.createGoodProfileTuple(goodRunTrace, "good-run");
		bad = TraceAnalyzer.createBadProfileTuple(badRunTrace, "bad-run");
		
		distance = ProfileDistanceCalculator.computeDistance(good, bad, DistanceType.INTERPRODUCT);
		System.out.println("interproduct distance good1 to bad: " + distance);
		
		goodRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		good = TraceAnalyzer.createGoodProfileTuple(goodRunTrace, "good-run");
		bad = TraceAnalyzer.createBadProfileTuple(badRunTrace, "bad-run");
		
		distance = ProfileDistanceCalculator.computeDistance(good, bad, DistanceType.INTERPRODUCT);
		System.out.println("interproduct distance good2 to bad: " + distance);
		
		goodRunTrace = "./experiments/randoop-database/show_help.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		good = TraceAnalyzer.createGoodProfileTuple(goodRunTrace, "good-run");
		bad = TraceAnalyzer.createBadProfileTuple(badRunTrace, "bad-run");
		
		distance = ProfileDistanceCalculator.computeDistance(good, bad, DistanceType.INTERPRODUCT);
		System.out.println("interproduct distance irrelevant to bad: " + distance);
		
	}

}
