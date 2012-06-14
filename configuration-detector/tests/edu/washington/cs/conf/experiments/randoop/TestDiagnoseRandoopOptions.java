package edu.washington.cs.conf.experiments.randoop;

import java.util.Arrays;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
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
