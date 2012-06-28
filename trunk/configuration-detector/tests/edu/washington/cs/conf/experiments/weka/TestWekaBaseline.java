package edu.washington.cs.conf.experiments.weka;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
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
		Collection<ConfPropOutput> outputs = TestSliceWekaConfigOptions.getWekaConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/weka-baseline/bad_stmt_labor.txt"};
		String[] goodStmtFiles = new String[]{"./experiments/weka-baseline/good_stmt_iris.txt",
				"./experiments/weka-baseline/good_stmt_weather.txt"};
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
	
	/**
	 * Diagnose by invariants
	 * */
	public void testDiagnoseByInvariant() {
		
	}

}
