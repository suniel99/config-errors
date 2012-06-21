package edu.washington.cs.conf.experiments.synoptic;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingSynopticTraces extends TestCase {

	public void test1() {
	    String goodRunTrace = "./experiments/synoptic-database/2pc_3nodes_100tx_bad-injected.txt";
	    String badRunTrace = "./experiments/synoptic-database/2pc_3nodes_100tx_good.txt";
//	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.0f);
	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.2584772f);
	}
	
}