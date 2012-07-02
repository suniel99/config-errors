package edu.washington.cs.conf.experiments.soot;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestInstrumentSoot extends TestCase {
	
	public static String soot_jar = "./subjects/soot-2.5/soot.jar";
	public static String soot_jar_instrument = "./output/soot-instrumented.jar";
	public static String soot_jar_instrument_full_slice = "./output/soot-instrumented-full-slice.jar";
	
	public void testInstrumentSoot() throws Exception {
		String filePath = TestSliceSootConfigOptions.soot_instrument_file;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(soot_jar, soot_jar_instrument);
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentSootFullSlice() throws Exception {
		String filePath = TestSliceSootConfigOptions.soot_instrument_file_full_slice;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(soot_jar, soot_jar_instrument_full_slice);
		
		InstrumentStats.showInstrumentationStats();
	}
}
