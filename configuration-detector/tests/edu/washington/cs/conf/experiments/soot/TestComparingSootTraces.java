package edu.washington.cs.conf.experiments.soot;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingSootTraces extends TestCase {

	public void test1() {
	    String goodRunTrace = "./experiments/soot-database/soot_helloworld_with_keepline.txt";
	    String badRunTrace = "./experiments/soot-database/soot_helloworld_no_keepline.txt";
//	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.0f);
	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.10196239f);
	}
	
}