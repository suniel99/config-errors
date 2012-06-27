package edu.washington.cs.conf.experiments.weka;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtExecuted;
import edu.washington.cs.conf.diagnosis.StmtFileReader;
import edu.washington.cs.conf.diagnosis.TestStmtExecutedDiffer;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestWekaBaseline extends TestCase {
	
	public void testStmtInstrumentation() throws Exception {
		String[] skippedClasses = new String[] {"weka.classifiers.evaluation.Prediction"};
		
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
		instrumenter.setSkippedClasses(Arrays.asList(skippedClasses));
		
        instrumenter.instrument("./subjects/weka/weka-no-trace.jar", "./output/weka-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
	/**
	 * Diagnose by statements
	 * */
	public void testDiagnoseByStmt() {
		List<StmtExecuted> good1 = StmtFileReader.readStmts("./experiments/weka-baseline/good_stmt_iris.txt");
		List<StmtExecuted> good2 = StmtFileReader.readStmts("./experiments/weka-baseline/good_stmt_weather.txt");
		List<StmtExecuted> bad1 = StmtFileReader.readStmts("./experiments/weka-baseline/bad_stmt_labor.txt");
		
		Collection<Collection<StmtExecuted>> goodRuns
	        = new LinkedList<Collection<StmtExecuted>>();
		goodRuns.add(good1);
		goodRuns.add(good2);
		
		Collection<Collection<StmtExecuted>> badRuns
            = new LinkedList<Collection<StmtExecuted>>();
		badRuns.add(bad1);
		
		//do diff
		Map<String, Float> scores = TestStmtExecutedDiffer.computeScore(goodRuns, badRuns);
		
		Collection<ConfPropOutput> outputs = TestSliceWekaConfigOptions.getWekaConfOutputs();
		StmtCoverageBasedDiagnoser diagnoser = new StmtCoverageBasedDiagnoser(outputs, scores);
		
		System.out.println("start to diagnose options: ....");
		
		List<ConfEntity> results = diagnoser.computeResponsibleOptions();
		
		List<String> entities = new LinkedList<String>();
		for(ConfEntity result : results) {
//			System.out.println(result);
			if(!entities.contains(result.toString())) {
				entities.add(result.toString());
			}
		}
		
		for(int i = 0; i  < entities.size(); i++) {
			System.out.println(i+1 + ". " + entities.get(i));
		}
	}
	
	/**
	 * Diagnose by invariants
	 * */
	public void testDiagnoseByInvariant() {
		
	}

}
