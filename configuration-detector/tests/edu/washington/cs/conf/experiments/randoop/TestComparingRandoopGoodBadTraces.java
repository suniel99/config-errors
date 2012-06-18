package edu.washington.cs.conf.experiments.randoop;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingRandoopGoodBadTraces extends TestCase {
	
	public void test1() {
		//compare the same
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.0f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.0f);
	}
	
	public void test2() {
		//2 good traces
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.01677453f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.120200634f);
	}
	
	public void test3() {
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.11405811f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.27575958f);
	}
	
	public void test4() {
		
		String goodRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.2617045f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.117307134f);
	}
	
	//this sounds  incorrect
	public void test5() {
		String goodRunTrace = "./experiments/randoop-database/show_help.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.9330544f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.85614043f);
	}
	
	public void test6() {
		String goodRunTrace = "./experiments/randoop-database/show_help.txt";
		String badRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.9373041f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.85133606f);
	}
	
	public void test7() {
		String goodRunTrace = "./experiments/randoop-database/good-binarysearchtree-60s-pruned.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.11985898f);
		
		goodRunTrace = "./experiments/randoop-database/good-binomialheap-60s-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.12303108f);
		
		goodRunTrace = "./experiments/randoop-database/gentests_help-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 1.0f);
		
		goodRunTrace = "./experiments/randoop-database/good-treeset-collections-60s-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.14591342f);
		
		goodRunTrace = "./experiments/randoop-database/good-treeset-collections-60s-myclasses-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.17543972f);
		
		goodRunTrace = "./experiments/randoop-database/show_help-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 1.0f);
	}
	
	

}
