package edu.washington.cs.conf.experiments.weka;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestInstrumentWeka extends TestCase {

	public void testInstrumentWeka() throws Exception {
		String filePath = TestSliceWekaConfigOptions.weka_instrument_file;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
//		instrumenter.instrument("./subjects/randoop-jamie.jar", "./output/randoop-instrumented.jar");
		instrumenter.instrument("./subjects/weka/weka-no-trace.jar", "./output/weka-instrumented-no-trace.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
