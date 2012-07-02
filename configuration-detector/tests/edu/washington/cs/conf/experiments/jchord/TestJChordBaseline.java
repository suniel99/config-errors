package edu.washington.cs.conf.experiments.jchord;

import java.util.Arrays;
import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
import edu.washington.cs.conf.experiments.soot.TestSliceSootConfigOptions;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestJChordBaseline extends TestCase {

	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
		
		Collection<String> skippedClasses
		    = Arrays.asList("joeq.Class.jq_ClassFileConstants",
		    		"javassist.bytecode.Opcode");
		instrumenter.setSkippedClasses(skippedClasses);
		
        instrumenter.instrument("./subjects/jchord/chord-no-trace.jar", "./subjects/jchord/chord-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testDiagnoseStmt() {
        Collection<ConfPropOutput> outputs = TestSliceJChordConfigOptions.getJChordConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/jchord-baseline-stmt/bad_no_race.txt"};
		String[] goodStmtFiles = new String[]{"./experiments/jchord-baseline-stmt/good_datarace.txt",
				"./experiments/jchord-baseline-stmt/good_deadlock.txt",
				"./experiments/jchord-baseline-stmt/good_dlog.txt"};
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	
	}
	
}
