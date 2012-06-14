package edu.washington.cs.conf.experiments.randoop;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.SlicingHelper;
import edu.washington.cs.conf.analysis.SlicingHelper.CG;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import junit.framework.TestCase;

public class TestSliceRandoopConfigOptions extends TestCase {
	
	public void testSliceRandoopCheaply() {
		getConfPropOutputs();
	}
	
	public void testCreateInstrumentSchema() {
       Collection<ConfPropOutput> outputs = getConfPropOutputs();
		
		//save as configuration schema
		InstrumentSchema schema = new InstrumentSchema();
		schema.addInstrumentationPoint(outputs);
		
		String filePath = "./randoop_option_instr_ser.dat";
		ConfOutputSerializer.serializeSchema(schema, filePath);
		ConfOutputSerializer.writeToFileAsText(schema, "./randoop_option_instr.txt");
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(filePath);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public static Collection<ConfPropOutput> getConfPropOutputs() {
		String path = "./subjects/randoop-jamie.jar;./subjects/plume.jar";
		String mainClass = "Lrandoop/main/Main";
		SlicingHelper helper = new SlicingHelper(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
//		helper.setCGType(CG.ZeroOneCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
		helper.setContextSensitive(false); //context-insensitive
		helper.buildAnalysis();
		
		List<ConfEntity> randoopConfList = RandoopExpUtils.getRandoopConfList();
		
		Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		for(ConfEntity entity : randoopConfList) {
//		  helper.setExcludeStringBuilder(true); //FIXME
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			outputs.add(output);
			System.out.println("  statement in slice: " + output.statements.size());
		}

		assertEquals(randoopConfList.size(), outputs.size());
		
		return outputs;
	}
}