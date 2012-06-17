package edu.washington.cs.conf.experiments.randoop;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.analysis.IRStatement;
import edu.washington.cs.conf.analysis.SlicingHelper;
import edu.washington.cs.conf.analysis.SlicingHelper.CG;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import junit.framework.TestCase;

public class TestSliceRandoopConfigOptions extends TestCase {
	
	public void testRandoopOptionTypes() {
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
		List<ConfEntity> randoopConfList = RandoopExpUtils.getRandoopConfList();
		ConfEntityRepository repo = new ConfEntityRepository(randoopConfList);
		repo.initializeTypesInConfEntities(path);
		for(ConfEntity conf : randoopConfList) {
			System.out.println(conf);
		}
	}
	
	public void testSliceRandoopCheaply() {
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
		getConfPropOutputs(path, RandoopExpUtils.getRandoopConfList());
	}
	
	public void testLargeSliceRandoopOptions() {
		String randoopBin = "D:\\research\\configurations\\workspace\\nanoxml-jamie\\bin";
		String plumeJar = "./subjects/plume.jar";
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
		path = randoopBin + ";" + plumeJar;
		getConfPropOutputs(path, RandoopExpUtils.getLargeSliceConfList());
//		getConfPropOutputs(path, RandoopExpUtils.getFakeOptions());
	}
	
	public void testCreateInstrumentSchema() {
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
       Collection<ConfPropOutput> outputs = getConfPropOutputs(path, RandoopExpUtils.getRandoopConfList());
		
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
	
	public static Collection<ConfPropOutput> getConfPropOutputs(String path, List<ConfEntity> confList) {
//		String path = "./subjects/randoop-jamie.jar;./subjects/plume.jar";
		
		
		String mainClass = "Lrandoop/main/Main";
		SlicingHelper helper = new SlicingHelper(path, mainClass);
//		helper.setCGType(CG.ZeroCFA);
//		helper.setCGType(CG.CFA);
//		helper.setCFAPrecision(2);
		helper.setCGType(CG.OneCFA);
//		helper.setCGType(CG.ZeroOneCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
		helper.setContextSensitive(false); //context-insensitive
		helper.buildAnalysis();
		
		List<ConfEntity> randoopConfList = confList;
		
		//get all type info
		ConfEntityRepository repo = new ConfEntityRepository(randoopConfList);
		repo.initializeTypesInConfEntities(path);
		
		Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		for(ConfEntity entity : randoopConfList) {
//		  helper.setExcludeStringBuilder(true); //FIXME
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			outputs.add(output);
			System.err.println("  statement in slice: " + output.statements.size());
			Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(output.statements);
			System.err.println("  statements after filtering: " + filtered.size());
			
			Set<IRStatement> sameStmts = ConfUtils.removeSameStmtsInDiffContexts(filtered);// filterSameStatements(filtered);
			System.err.println("  filtered statements: " + sameStmts.size());
			
			Set<IRStatement> branchStmts = ConfPropOutput.extractBranchStatements(sameStmts);
			System.err.println("  branching statements: " + branchStmts.size());
//			System.err.println("   numbered statements: " + output.getNumberedBranches().size());
//			System.err.println("   number of src branching statements: " + output.getNumberedBranchesInSource().size());
			
			dumpStatements(branchStmts);
		}

		assertEquals(randoopConfList.size(), outputs.size());
		
		return outputs;
	}
	
	
	
	static void dumpStatements(Collection<IRStatement> stmts) {
		for(IRStatement stmt : stmts) {
			System.err.println(" >> " + stmt.toString());
		}
	}
}