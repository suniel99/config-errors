package edu.washington.cs.conf.experiments.jchord;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingJChordTraces extends TestCase {

	public void test1() {
		String goodRunTrace = "./experiments/jchord-database/have-race.txt";
		String badRunTrace = "./experiments/jchord-database/no-race.txt";
		
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.16477811f);
		
	}
	
}