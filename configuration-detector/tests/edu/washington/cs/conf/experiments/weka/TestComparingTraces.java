package edu.washington.cs.conf.experiments.weka;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingTraces extends TestCase {
	
	public void test1() {
	    String goodRunTrace = "./experiments/weka-database/good-iris.txt";
	    String badRunTrace = "./experiments/weka-database/bad-labor.txt";
//	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.0f);
	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.16477811f);
	}
	
}