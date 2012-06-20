package edu.washington.cs.conf.experiments.jchord;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingJChordTraces extends TestCase {

	public void test1() {
		String goodRunTrace = "./experiments/jchord-database/simpletest-has-race.txt";
		String badRunTrace = "./experiments/jchord-database/simpletest-no-race.txt";
		
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.095016f);
		
	}
	
}