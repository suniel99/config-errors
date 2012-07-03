package edu.washington.cs.conf.experiments.jchord;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.MethodBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.experiments.randoop.TestSliceRandoopConfigOptions;
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
	
	public void testDiagnoseOptionsByInvariantAnalysis() throws Exception {
		String goodInvFile1 = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\datarace.inv.gz";
		String goodInvFile2 = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\deadlock.inv.gz";
		String goodInvFile3 = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\dlog.inv.gz";
		
		String badInvFile = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\datarace-norace.inv.gz";
		//Set<String> affectedMethods = getAffectedMethods(goodInvFile, badInvFile);
		Collection<ConfPropOutput> confs = TestSliceJChordConfigOptions.getJChordConfOutputs();
		
		List<ConfEntity> entities = MethodBasedDiagnoser.computeResponsibleOptions(Arrays.asList(goodInvFile1, goodInvFile2, goodInvFile3), 
				badInvFile, confs);
		
		System.out.println(entities.size());
		int i = 0;
		for(ConfEntity entity : entities) {
			System.out.println((i+1) + ". " + entity);
			i++;
		}
		
	}
	
}
