package edu.washington.cs.conf.experiments.soot;

import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestSootBaseline extends TestCase {
	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
        instrumenter.instrument("./subjects/soot-2.5/soot.jar", "./output/soot-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
}
