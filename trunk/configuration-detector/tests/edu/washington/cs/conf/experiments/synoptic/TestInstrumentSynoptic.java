package edu.washington.cs.conf.experiments.synoptic;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestInstrumentSynoptic extends TestCase {
	
	public void testInstrumentSynoptic() throws Exception {
		String filePath = TestSliceSynopticConfigOptions.synoptic_instrument_file;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument("./subjects/synoptic/synoptic.jar", "./output/synoptic-instrumented.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
