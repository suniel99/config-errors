package edu.washington.cs.conf.experiments.synoptic;

import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestSynopticBaseline extends TestCase {
	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
        instrumenter.instrument("./subjects/synoptic/synoptic.jar", "./output/synoptic-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
}
