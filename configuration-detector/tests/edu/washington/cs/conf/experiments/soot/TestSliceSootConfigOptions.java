package edu.washington.cs.conf.experiments.soot;

import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.SootExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import junit.framework.TestCase;

public class TestSliceSootConfigOptions extends TestCase {
	public static String soot_instrument_file = "./soot_option_instr_ser.dat";
	
	public void testInitAllOptionsInSynoptic() {
		String dir = "./subjects/soot-2.5/";
		String path = dir + "soot.jar;" +
		        dir + "libs/coffer.jar;" +
		        dir + "libs/jasminclasses-2.5.0.jar;" +
		        dir + "libs/java_cup.jar;" +
		        dir + "libs/JFlex.jar;" +
		        dir + "libs/pao.jar;" +
		        dir + "libs/polyglot.jar;" +
		        dir + "libs/pth.jar";
		List<ConfEntity> sootConfigs = SootExpUtils.getSootConfList();
		ConfEntityRepository repo = new ConfEntityRepository(sootConfigs);
		repo.initializeTypesInConfEntities(path);
		for(ConfEntity conf : sootConfigs) {
	    	System.out.println(conf);
	    }
	    assertEquals(49, sootConfigs.size());
	}
	
	public void testSliceOptionsInSoot() {
		getSootConfOutputs();
	}
	
	public void testCreateInstrumentSchema() {
        Collection<ConfPropOutput> outputs = getSootConfOutputs();
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE);
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, soot_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, "./soot_option_instr.txt");
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(soot_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public static Collection<ConfPropOutput> getSootConfOutputs() {
		String dir = "./subjects/soot-2.5/";
		String path = dir + "soot.jar;" +
		        dir + "libs/coffer.jar;" +
		        dir + "libs/jasminclasses-2.5.0.jar;" +
		        dir + "libs/java_cup.jar;" +
		        dir + "libs/JFlex.jar;" +
		        dir + "libs/pao.jar;" +
		        dir + "libs/polyglot.jar;" +
		        dir + "libs/pth.jar";
		String mainClass = "Lsoot/Main";
		List<ConfEntity> sootConfigs = SootExpUtils.getSootConfList();
		Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, sootConfigs, "SootExclusions.txt",
				CG.ZeroCFA, false); //cannot use 1-CFA, which is too expensive
        return confs;
	}
}