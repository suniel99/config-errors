package edu.washington.cs.conf.experiments.soot;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestInstrumentSoot extends TestCase {
	
	public void testInstrumentSoot() throws Exception {
		String filePath = TestSliceSootConfigOptions.soot_instrument_file;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument("./subjects/soot-2.5/soot.jar", "./output/soot-instrumented.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
