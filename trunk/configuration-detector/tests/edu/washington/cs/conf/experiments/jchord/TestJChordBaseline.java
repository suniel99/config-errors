package edu.washington.cs.conf.experiments.jchord;

import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestJChordBaseline extends TestCase {

	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
        instrumenter.instrument("./subjects/jchord/chord-no-trace.jar", "./subjects/jchord/chord-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
