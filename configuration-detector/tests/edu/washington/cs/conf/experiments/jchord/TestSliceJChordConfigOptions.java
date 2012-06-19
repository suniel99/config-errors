package edu.washington.cs.conf.experiments.jchord;

import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.util.Log;
import junit.framework.TestCase;

public class TestSliceJChordConfigOptions extends TestCase {
	public static String jchord_instrument_file = "./jchord_option_instr_ser.dat";
	
	public void testInitAllConfigOptions() {
		String path = "./subjects/jchord/chord.jar";
//		String mainClass = "Lchord/project/Main"
		List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		ConfEntityRepository repo = new ConfEntityRepository(jchordConfList);
		repo.initializeTypesInConfEntities(path);
		for(ConfEntity conf : jchordConfList) {
			System.out.println(conf);
		}
		assertEquals(76, jchordConfList.size());
	}
	
	public void testSliceOptionsInJChordNoPrune() {
		sliceOptionsInJChord(false);
	}
	
	public void testSliceOptionsInJChordWithPrune() {
		sliceOptionsInJChord(true);
	}
	
	public Collection<ConfPropOutput> sliceOptionsInJChord(boolean prune) {
		String path = "./subjects/jchord/chord.jar";
		String mainClass = "Lchord/project/Main";
		String exFile = "ChordExclusions.txt";
		List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		
		Log.logConfig("./jchord-config-slice.txt");
		Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, jchordConfList, exFile, prune);
		Log.removeLogging();
		
		return confs;
	}
	
	public void testCreateInstrumentSchema() {
		Collection<ConfPropOutput> outputs = this.sliceOptionsInJChord(false);
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, jchord_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, "./jchord_option_instr.txt");
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(jchord_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
}
