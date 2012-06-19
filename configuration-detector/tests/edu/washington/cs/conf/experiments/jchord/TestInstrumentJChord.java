package edu.washington.cs.conf.experiments.jchord;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestInstrumentJChord extends TestCase {
	
	public void testInstrument() throws Exception {
		String filePath = TestSliceJChordConfigOptions.jchord_instrument_file;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument("./subjects/jchord/chord.jar", "./subjects/jchord/chord-instrumented.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
