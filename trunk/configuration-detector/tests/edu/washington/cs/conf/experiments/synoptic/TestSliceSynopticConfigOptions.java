package edu.washington.cs.conf.experiments.synoptic;

import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.SynopticExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import edu.washington.cs.conf.util.Log;
import junit.framework.TestCase;

public class TestSliceSynopticConfigOptions extends TestCase {
	
	public static String synoptic_instrument_file = "./synoptic_option_instr_ser.dat";

	public void testInitAllOptionsInSynoptic() {
		String path = "./subjects/synoptic/synoptic.jar;"
			+ "./subjects/synoptic/libs/plume.jar;"
			+ "./subjects/synoptic/libs/commons-io-2.0.1.jar;"
			+ "./subjects/synoptic/libs/commons-fileupload-1.2.2.jar;"
			+ "./subjects/synoptic/libs/junit-4.9b2.jar";
//	    String mainClass = "Lsynoptic/main/Main";
	    List<ConfEntity> synopticConfList = SynopticExpUtils.getSynopticList();
	    ConfEntityRepository repo = new ConfEntityRepository(synopticConfList);
	    repo.initializeTypesInConfEntities(path);
	    for(ConfEntity conf : synopticConfList) {
	    	System.out.println(conf);
	    }
	    assertEquals(37, synopticConfList.size());
	}
	
	public void testSliceOptionsInSynoptic() {
		Log.logConfig("./synoptic-options-log.txt");
		getSynopticConfOutputs();
		Log.removeLogging();
	}
	
	public void testCreateInstrumentSchema() {
        Collection<ConfPropOutput> outputs = getSynopticConfOutputs();
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE);
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, synoptic_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, "./synoptic_option_instr.txt");
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(synoptic_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public static Collection<ConfPropOutput> getSynopticConfOutputs() {
		String path = "./subjects/synoptic/synoptic.jar;"
			+ "./subjects/synoptic/libs/plume.jar;"
			+ "./subjects/synoptic/libs/commons-io-2.0.1.jar;"
			+ "./subjects/synoptic/libs/commons-fileupload-1.2.2.jar;"
			+ "./subjects/synoptic/libs/junit-4.9b2.jar";
	    String mainClass = "Lsynoptic/main/Main";
        List<ConfEntity> synopticConfList = SynopticExpUtils.getSynopticList();
        Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, synopticConfList, false);
        return confs;
	}
}