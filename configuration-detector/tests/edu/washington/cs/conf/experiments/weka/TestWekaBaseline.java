package edu.washington.cs.conf.experiments.weka;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.MethodBasedDiagnoser;
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
		
		String[] goodStmtFiles = new String[]{
				"./experiments/weka-baseline/good_stmt_iris.txt",
				"./experiments/weka-baseline/good_stmt_weather.txt",
				"./experiments/weka-baseline/good-discretize-iris.txt",
				"./experiments/weka-baseline/good-soybean-instance.txt",
				"./experiments/weka-baseline/iris-simplified-last.txt",
				"./experiments/weka-baseline/iris-simplified.txt",
				"./experiments/weka-baseline/nomToBinary-contact-lenses.txt",
				"./experiments/weka-baseline/resample-soybean-uniform.txt",
				"./experiments/weka-baseline/resample-soybean.txt",
				"./experiments/weka-baseline/stra-remove-folds-soybean-nov.txt",
				"./experiments/weka-baseline/stra-remove-folds-soybean.txt",
				"./experiments/weka-baseline/weather-j48.txt"
				};
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
	
	/**
	 * Diagnose by invariants
	 * */
	public void testDiagnoseByInvariant() {
		String badInv = "D:\\research\\configurations\\daikon\\bin\\weka\\labor.inv.gz";
		String goodIrisInv = "D:\\research\\configurations\\daikon\\bin\\weka\\iris.inv.gz";
		String goodWeatherInv = "D:\\research\\configurations\\daikon\\bin\\weka\\weather.inv.gz";
		
		Collection<ConfPropOutput> confs = TestSliceWekaConfigOptions.getWekaConfOutputs();
		
		System.out.println("start diagnosing... ");
		
        List<ConfEntity> entities
            = MethodBasedDiagnoser.computeResponsibleOptions(Arrays.asList(goodIrisInv, goodWeatherInv),
            		badInv, confs);
		
		System.out.println(entities.size());
		int i = 0;
		for(ConfEntity entity : entities) {
			System.out.println((i+1) + ". " + entity);
			i++;
		}
	}

}
