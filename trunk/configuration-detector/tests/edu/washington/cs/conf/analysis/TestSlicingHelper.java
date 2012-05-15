package edu.washington.cs.conf.analysis;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.util.CancelException;

import edu.washington.cs.conf.analysis.SlicingHelper.CG;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.experiments.WekaExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.util.WALAUtils;

import junit.framework.TestCase;

public class TestSlicingHelper extends TestCase {
	
	public void testSlice1() throws IllegalArgumentException, CancelException {
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\depfield";
		String mainClass = "Ltest/slice/depfield/FieldDeps";
		SlicingHelper helper = new SlicingHelper(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setContextSensitive(false);
		helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
		helper.buildAnalysis();
		
		ConfEntity entity = new ConfEntity("test.slice.depfield.FieldDeps", "field_value", true);
		Statement seed = helper.extractConfStatement(entity);
		System.out.println("Seed is: " + seed);
		Collection<Statement> slices = helper.sliceConfOption(entity); 
			//helper.computeContextSensitiveForwardSlice(seed);
		WALAUtils.dumpSlice(slices, new PrintWriter(System.out));
		
		for(Statement s : slices) {
			String fullMethodName = WALAUtils.getFullMethodName(s.getNode().getMethod());
			if(fullMethodName.startsWith("test.slice.depfield.FieldDeps.compute_result2")) {
				System.out.println(WALAUtils.getAllIRAsString(s.getNode()));
			    break;
			}
		}
		
		Collection<IRStatement> irs = SlicingHelper.convert(slices);
		for(IRStatement ir : irs) {
			System.out.println("  " + ir);
		}
		
		ConfPropOutput output = helper.outputSliceConfOption(entity);
		System.out.println(output);
	}

	public void testSliceRandoopCheaply() {
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
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			outputs.add(output);
			System.out.println(" - " + output.statements.size());
		}

		System.out.println("size: " + outputs.size());
		assertEquals(randoopConfList.size(), outputs.size());
		
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
	
	public void testSliceWekaCheaply() {
		String path = "./subjects/weka/weka.jar;./subjects/weka/JFlex.jar;" +
		    "./subjects/weka/java-cup.jar";
		String mainClass = "Lweka/classifiers/trees/J48";
		SlicingHelper helper = new SlicingHelper(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
		helper.setContextSensitive(false); //context-insensitive
		helper.buildAnalysis();
		
		List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
		
		Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		for(ConfEntity entity : wekaConfList) {
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			outputs.add(output);
			System.out.println(" - " + output.statements.size());
		}

		System.out.println("size: " + outputs.size());
		assertEquals(wekaConfList.size(), outputs.size());
		
		//save as configuration schema
		InstrumentSchema schema = new InstrumentSchema();
		schema.addInstrumentationPoint(outputs);
		
		String filePath = "./weka_option_instr_ser.dat";
		ConfOutputSerializer.serializeSchema(schema, filePath);
		ConfOutputSerializer.writeToFileAsText(schema, "./weka_option_instr.txt");
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(filePath);
		assertEquals(schema.toString(), newSchema.toString());
	}
}
