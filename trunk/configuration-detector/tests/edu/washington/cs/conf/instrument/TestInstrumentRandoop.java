package edu.washington.cs.conf.instrument;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import junit.framework.TestCase;

public class TestInstrumentRandoop extends TestCase {
	
	public void testInstrument() throws Exception {
		String filePath = "./randoop_option_instr_ser.dat";
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument("./subjects/randoop-jamie.jar", "./output/randoop-instrumented.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
