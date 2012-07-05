package edu.washington.cs.conf.experiments.soot;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.InvariantDiffAnalyzer;
import edu.washington.cs.conf.diagnosis.MethodBasedDiagnoser;
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
	
	public void testDiagnoseByInvariants() {
		
		String badSootFile = "D:\\research\\configurations\\daikon\\bin\\soot\\soot_helloworld-no-linenum.inv.gz";
		String goodSootFile = "D:\\research\\configurations\\daikon\\bin\\soot\\soot_helloworld-has-linenum.inv.gz";
		
		
		Collection<ConfPropOutput> confs = TestSliceSootConfigOptions.getSootConfOutputs();
		
		System.out.println("start diagnosing... ");
		
        List<ConfEntity> entities
            = MethodBasedDiagnoser.computeResponsibleOptions(Arrays.asList(goodSootFile),
            		badSootFile, confs);
		
		System.out.println(entities.size());
		int i = 0;
		for(ConfEntity entity : entities) {
			System.out.println((i+1) + ". " + entity);
			i++;
		}
	}
	
	public void testAnalyzeInvariants() {
		String badSootFile = "D:\\research\\configurations\\daikon\\bin\\soot\\soot_helloworld-no-linenum.inv.gz";
		String goodSootFile = "D:\\research\\configurations\\daikon\\bin\\soot\\soot_helloworld-has-linenum.inv.gz";
		InvariantDiffAnalyzer analyzer = new InvariantDiffAnalyzer(Collections.singleton(goodSootFile), badSootFile);
		Map<String, Float> scores = analyzer.getMethodsWithDiffInvariants();
	}
}
