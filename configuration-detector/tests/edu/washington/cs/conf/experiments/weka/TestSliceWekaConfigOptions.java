package edu.washington.cs.conf.experiments.weka;

import java.util.Collection;
import java.util.List;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.WekaExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import junit.framework.TestCase;

public class TestSliceWekaConfigOptions extends TestCase {
	
	public static String weka_instrument_file = "./weka_option_instr_ser.dat";

	public void testInitAllConfigOptions() {
		String path = "./subjects/weka/weka-no-trace.jar;./subjects/weka/JFlex.jar;" +
		"./subjects/weka/java-cup.jar";
        //String mainClass = "Lweka/classifiers/trees/J48";
		List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
		ConfEntityRepository repo = new ConfEntityRepository(wekaConfList);
		repo.initializeTypesInConfEntities(path);
		for(ConfEntity conf : wekaConfList) {
			System.out.println(conf);
		}
		assertEquals(14, wekaConfList.size());
	}
	
	public void testSliceOptionsInWeka() {
		getWekaConfOutputs();
	}
	
	public void testCreateInstrumentSchema() {
		Collection<ConfPropOutput> outputs = getWekaConfOutputs();
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, weka_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, "./weka_option_instr.txt");
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(weka_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	static String path = "./subjects/weka/weka-no-trace.jar;./subjects/weka/JFlex.jar;" +
	    "./subjects/weka/java-cup.jar";
    static String mainClass = "Lweka/classifiers/trees/J48";
	
	public static Collection<ConfPropOutput> getWekaConfOutputs() {
        List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
        Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, wekaConfList, false);
        return confs;
	}
	
	public static Collection<ConfPropOutput> getWekaConfOutputsFullSlice() {
        List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
        Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, wekaConfList,
        		"JavaAllExclusions.txt", CG.OneCFA, false,
        		DataDependenceOptions.FULL, ControlDependenceOptions.FULL);
        return confs;
	}
}