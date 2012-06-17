package edu.washington.cs.conf.experiments.randoop;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfigurationSlicer;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtExecuted;
import edu.washington.cs.conf.diagnosis.StmtFileReader;
import edu.washington.cs.conf.diagnosis.TestStmtExecutedDiffer;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import junit.framework.TestCase;

public class TestRandoopBaseline extends TestCase {
	
	public void testExecutedLine() {
		List<StmtExecuted> good1 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-arraylist-60s.txt");
		List<StmtExecuted> good2 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-treeset-60s.txt");
		List<StmtExecuted> good3 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-show-help.txt");
		List<StmtExecuted> bad1 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/bad-nano-xml-100s.txt");
		
		Collection<Collection<StmtExecuted>> goodRuns
	        = new LinkedList<Collection<StmtExecuted>>();
		goodRuns.add(good1);
		goodRuns.add(good2);
		goodRuns.add(good3);
		
		Collection<Collection<StmtExecuted>> badRuns
            = new LinkedList<Collection<StmtExecuted>>();
		badRuns.add(bad1);
		
		//do diff
		Map<String, Float> scores = TestStmtExecutedDiffer.computeScore(goodRuns, badRuns);
		
		Collection<ConfPropOutput> outputs = this.getRandoopConfOutputs();
		
		StmtCoverageBasedDiagnoser diagnoser = new StmtCoverageBasedDiagnoser(outputs, scores);
		
		System.out.println("start to diagnose options: ....");
		
		List<ConfEntity> results = diagnoser.computeResponsibleOptions();
		
		for(ConfEntity result : results) {
			System.out.println(result);
		}
	}
	
	public void testAffectedMethods() {
		
	}
	
	public Collection<ConfPropOutput> getRandoopConfOutputs() {
		String path = "./subjects/randoop-jamie.jar;./subjects/plume.jar";
		String mainClass = "Lrandoop/main/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
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
			System.out.println(" - " + output.statements.size());
		}

		System.out.println("size: " + outputs.size());
		assertEquals(randoopConfList.size(), outputs.size());
		
		return outputs;
	}

}

