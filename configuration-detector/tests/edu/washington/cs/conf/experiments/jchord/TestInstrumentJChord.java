package edu.washington.cs.conf.experiments.jchord;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.util.Log;
import junit.framework.TestCase;

public class TestInstrumentJChord extends TestCase {
	
	static String jchord_notrace = "./subjects/jchord/chord-no-trace.jar";
	static String jchord_instrument = "./subjects/jchord/chord-no-trace-instrumented.jar";
	static String jchord_instrument_full_slice = "./subjects/jchord/chord-no-trace-instrumented-full-slice.jar";
	
	public void testInstrument() throws Exception {
		String filePath = TestSliceJChordConfigOptions.jchord_instrument_file;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		Log.logConfig("./instrument-jchord-log.txt");
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(jchord_notrace, jchord_instrument);
		
		Log.removeLogging();
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentFullSlice() throws Exception {
		String filePath = TestSliceJChordConfigOptions.jchord_instrument_file_full_slice;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
//		Log.logConfig("./instrument-jchord-log.txt");
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(jchord_notrace, jchord_instrument_full_slice);
		
//		Log.removeLogging();
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
