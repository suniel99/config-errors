package edu.washington.cs.conf.experiments.randoop;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.util.Log;
import junit.framework.TestCase;

public class TestDiagnoseRandoopOptions extends TestCase {
	
	@Override
	public void tearDown() {
		MainAnalyzer.amortizeNoise = false;
		MainAnalyzer.doFiltering = false;
	}

	public void test1() {
		ConfEntityRepository repo = RandoopExpUtils.getRandoopConfRepository();
		
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		
		MainAnalyzer analyzer = new MainAnalyzer(badRunTrace, Arrays.asList(goodRunTrace), repo);
		analyzer.setThreshold(0.3f);
		
		List<ConfDiagnosisOutput> outputs = analyzer.computeResponsibleOptions();
		
		for(ConfDiagnosisOutput output : outputs) {
		    System.out.println(output);
		}
	}
	
	/**
	 * check the value, check the instance, check the slice size, and how to eliminate it
	 * */
	public void test2() {
		MainAnalyzer.amortizeNoise = true;
		MainAnalyzer.doFiltering = true;
		
		ConfEntityRepository repo = RandoopExpUtils.getRandoopConfRepository();
		
		String goodRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		
		MainAnalyzer analyzer = new MainAnalyzer(badRunTrace, Arrays.asList(goodRunTrace), repo);
		analyzer.setRankType(RankType.SINGLE_IMPORT); //use single import is OK
		analyzer.setThreshold(0.3f);
		
		List<ConfDiagnosisOutput> outputs = analyzer.computeResponsibleOptions();
		
		for(ConfDiagnosisOutput output : outputs) {
		    System.out.println(output);
		    System.out.println("   " + output.getBriefExplanation());
//		    output.showExplanations();
		}
	}
	
	public void test2_basedOn1CFA() {
		MainAnalyzer.amortizeNoise = false;
		MainAnalyzer.doFiltering = true;
		
		//ConfUtils.setUpLineNumberAndSource(sourceDir, propOutputs, profiles)
		
		ConfEntityRepository repo = RandoopExpUtils.getRandoopConfRepository();
		
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s-1-cfa-dump.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-1-cfa-dump.txt";
		
//		String sourceDir = "./subjects/randoop/randoop-src/";
//		Collection<ConfPropOutput> configOutputs = TestSliceRandoopConfigOptions.getConfPropOutputs(); 
		
		MainAnalyzer analyzer = new MainAnalyzer(badRunTrace, Arrays.asList(goodRunTrace), repo);//, sourceDir, configOutputs);
		analyzer.setRankType(RankType.IMPORT_RANK_CHANGE); //use single import is OK
//		analyzer.setSourceDir(sourceDir);
//		analyzer.setOutputs(configOutputs);
		analyzer.setThreshold(0.4f);
		
		List<ConfDiagnosisOutput> outputs = analyzer.computeResponsibleOptions();
		
		Log.logConfig("./config_output.txt");
		for(ConfDiagnosisOutput output : outputs) {
		    System.out.println(output);
//		    output.
		    System.out.println("   " + output.getBriefExplanation());
		    System.out.println();
		    
		    Log.logln(output.toString());
		    Log.logln("exp num: " + output.getExplanations().size());
		    for(String exp : output.getExplanations()) {
		    	Log.logln("    " + exp);
		    }
		    Log.logln("");
//		    output.showExplanations();
		}
		
		Log.removeLogging();
	}
	
	public void test3() {
        ConfEntityRepository repo = RandoopExpUtils.getRandoopConfRepository();
        
		String goodRunTrace = "./experiments/randoop-database/show_help.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		
		MainAnalyzer analyzer = new MainAnalyzer(badRunTrace, Arrays.asList(goodRunTrace), repo);
		analyzer.setThreshold(1.0f);
		
		List<ConfDiagnosisOutput> outputs = analyzer.computeResponsibleOptions();
		
		for(ConfDiagnosisOutput output : outputs) {
		    System.out.println(output);
		}
	}
	
}
