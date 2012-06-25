package edu.washington.cs.conf.experiments.jchord;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingJChordTraces extends TestCase {

	public void test1() {
		String goodRunTrace = "./experiments/jchord-database/simpletest-has-race.txt";
		String badRunTrace = "./experiments/jchord-database/simpletest-no-race.txt";
		
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.28348106f);
		
	}
	
	public void test2() {
		String goodRunTrace = "./experiments/jchord-database/ctxtsanalysis_default.txt";
		String badRunTrace = "./experiments/jchord-crashing-error/chord-crash-no-ctxt-kind.txt";
		
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.16580969f);
	}
	
	public void test3() {
		String goodRunTrace = "./experiments/jchord-database/simpletest-has-race.txt";
		String badRunTrace = "./experiments/jchord-crashing-error/chord-crash-no-ctxt-kind.txt";
		
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.16580969f);
	}
	
}