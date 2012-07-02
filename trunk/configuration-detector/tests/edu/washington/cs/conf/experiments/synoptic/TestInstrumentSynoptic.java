package edu.washington.cs.conf.experiments.synoptic;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestInstrumentSynoptic extends TestCase {
	
	public static String synoptic_jar = "./subjects/synoptic/synoptic.jar";
	public static String synoptic_jar_instrument = "./output/synoptic-instrumented.jar";
	public static String synoptic_jar_instrument_full_slice = "./output/synoptic-instrumented-full-slice.jar";
	
	public void testInstrumentSynoptic() throws Exception {
		String filePath = TestSliceSynopticConfigOptions.synoptic_instrument_file;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(synoptic_jar, synoptic_jar_instrument);
		
		InstrumentStats.showInstrumentationStats();
	}

	public void testInstrumentSynopticFullSlice() throws Exception {
		String filePath = TestSliceSynopticConfigOptions.synoptic_instrument_file_full_slice;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(synoptic_jar, synoptic_jar_instrument_full_slice);
		
		InstrumentStats.showInstrumentationStats();
	}
}
