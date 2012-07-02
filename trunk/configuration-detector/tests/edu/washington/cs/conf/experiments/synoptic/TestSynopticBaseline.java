package edu.washington.cs.conf.experiments.synoptic;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
import edu.washington.cs.conf.experiments.soot.TestSliceSootConfigOptions;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestSynopticBaseline extends TestCase {
	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
        instrumenter.instrument("./subjects/synoptic/synoptic.jar", "./output/synoptic-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testDiagnoseByStmt() {
		Collection<ConfPropOutput> outputs = TestSliceSynopticConfigOptions.getSynopticConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/synoptic-baseline/bad_100tx_stmt.txt"};
		String[] goodStmtFiles = new String[]{"./experiments/synoptic-baseline/good_100tx_stmt.txt",
				"./experiments/synoptic-baseline/good_5tx_stmt.txt"};
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
}
