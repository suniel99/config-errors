package edu.washington.cs.conf.experiments.soot;

import java.util.Arrays;
import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
import edu.washington.cs.conf.experiments.weka.TestSliceWekaConfigOptions;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestSootBaseline extends TestCase {
	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
		//add the skip class
		Collection<String> classes = Arrays.asList("soot.JastAddJ.SimpleSet",
				"soot.jbco.IJbcoTransform");
		instrumenter.setSkippedClasses(classes);
		//do instrumentation
        instrumenter.instrument("./subjects/soot-2.5/soot.jar", "./output/soot-everystmt.jar");
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testDiagnoseByStmt() {
		Collection<ConfPropOutput> outputs = TestSliceSootConfigOptions.getSootConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/soot-baseline/stmt_coverage_helloworld_no_line.txt"};
		String[] goodStmtFiles = new String[]{"./experiments/soot-baseline/stmt_coverage_helloworld_keeplinenumber.txt"};
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
}
