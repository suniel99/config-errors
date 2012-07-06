package edu.washington.cs.conf.experiments.synoptic;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.MethodBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
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
	
	public void testDiagnoseByInvariant() {
		String badSynopticFile = "D:\\research\\configurations\\daikon\\bin\\synoptic\\bad_100tx.inv.gz";
		String goodSynopticFile1 = "D:\\research\\configurations\\daikon\\bin\\synoptic\\good_100tx.inv.gz";
		String goodSynopticFile2 = "D:\\research\\configurations\\daikon\\bin\\synoptic\\good_5tx.inv.gz";
		
		Collection<ConfPropOutput> confs = TestSliceSynopticConfigOptions.getSynopticConfOutputs();
		
		System.out.println("start diagnosing... ");
		
        List<ConfEntity> entities
            = MethodBasedDiagnoser.computeResponsibleOptions(Arrays.asList(goodSynopticFile1, goodSynopticFile2),
            		badSynopticFile, confs);
		
		System.out.println(entities.size());
		int i = 0;
		for(ConfEntity entity : entities) {
			System.out.println((i+1) + ". " + entity);
			i++;
		}
	}
}
