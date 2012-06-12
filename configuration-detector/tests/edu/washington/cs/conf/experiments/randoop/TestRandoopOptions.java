package edu.washington.cs.conf.experiments.randoop;

import java.util.Arrays;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import junit.framework.TestCase;

public class TestRandoopOptions extends TestCase {

	public void testFindOptions() {
		ConfEntityRepository repo = RandoopExpUtils.getRandoopConfRepository();
		
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		
		MainAnalyzer analyzer = new MainAnalyzer(badRunTrace, Arrays.asList(goodRunTrace), repo);
		analyzer.setThreshold(0.2f);
		
		List<ConfDiagnosisOutput> outputs = analyzer.computeResponsibleOptions();
		
		for(ConfDiagnosisOutput output : outputs) {
		    System.out.println(output);
		}
		
		//---
		goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		badRunTrace = "./experiments/randoop-database/show_help.txt";
		
		analyzer = new MainAnalyzer(badRunTrace, Arrays.asList(goodRunTrace), repo);
		analyzer.setThreshold(0.2f);
		
		outputs = analyzer.computeResponsibleOptions();
		
		for(ConfDiagnosisOutput output : outputs) {
		    System.out.println(output);
		}
		
	}
	
}
