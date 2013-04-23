package edu.washington.cs.conf.instrument.evol;

import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestPredicateInstrumenter extends TestCase {

	public void testSimpleInstrumenter() throws Exception {
		PredicateInstrumenter instrumenter = new PredicateInstrumenter();
		instrumenter.setDisasm(true);
		instrumenter.instrument("./subjects/testdata.jar", "./output.jar");
		InstrumentStats.showInstrumentationStats();
	}
	
}
