package edu.washington.cs.conf.experiments.weka;

import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestWekaBaseline extends TestCase {
	
	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
        instrumenter.instrument("./subjects/weka/weka-no-trace.jar", "./output/weka-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testDiagnoseByStmt() {
		
	}
	
	/**
	 * Test by invariants
	 * */
	public void testDiagnoseByInvariant() {
		
	}

}
