package edu.washington.cs.conf.experiments.randoop;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestInstrumentRandoop extends TestCase {
	
	public void testInstrument() throws Exception {
		String filePath = "./randoop_option_instr_ser.dat";
		String randoopSrcDir = "./subjects/randoop/randoop-src/"; //NOT used yet
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument("./subjects/randoop-jamie.jar", "./output/randoop-instrumented.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentWithContext() throws Exception {
		String filePath = "./randoop_option_instr_ser.dat";
		String randoopSrcDir = "./subjects/randoop/randoop-src/"; //NOT used yet
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.turnOnContextInstrumentation();
		
		instrumenter.instrument("./subjects/randoop-jamie.jar", "./output/randoop-context-instrumented.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
        instrumenter.instrument("./subjects/randoop-jamie.jar", "./output/randoop-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}