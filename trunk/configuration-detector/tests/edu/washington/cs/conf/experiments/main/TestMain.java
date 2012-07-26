package edu.washington.cs.conf.experiments.main;

import java.io.FileNotFoundException;

import junit.framework.TestCase;

import edu.washington.cs.conf.diagnosis.Main;

public class TestMain extends TestCase {

	public void testRandoopNonCrashing() throws FileNotFoundException {
		String[] args = new String[]{
				"--config_options=./tests/edu/washington/cs/conf/experiments/main/randoop.options.txt",
				"--source_dir=./subjects/randoop/randoop-src",
				"--classpath_for_slicing=./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar",
				"--main_for_slicing=Lrandoop/main/Main",
				"--db_dir=./experiments/randoop-database/main",
				"--bad_run_trace=./experiments/randoop-database/bad-nano-xml-100s-pruned.txt"
		};
		Main.main(args);
	}
	
	public void testWekaNonCrashing() throws FileNotFoundException {
		String[] args = new String[]{
				"--config_options=./tests/edu/washington/cs/conf/experiments/main/weka.options.txt",
				"--source_dir=D:\\research\\configurations\\workspace\\weka-3.6\\main\\java",
				"--classpath_for_slicing=./subjects/weka/weka-no-trace.jar;./subjects/weka/JFlex.jar;./subjects/weka/java-cup.jar",
				"--main_for_slicing=Lweka/classifiers/trees/J48",
				"--db_dir=./experiments/weka-database/main",
				"--bad_run_trace=./experiments/weka-database/bad-labor.txt"
		};
		Main.main(args);
	}
	
	public void testSootNonCrashing() throws FileNotFoundException {
		
	}
	
	public void testJChordNonCrashing() throws FileNotFoundException {
		
	}
	
	public void testSynopticNonCrashing() throws FileNotFoundException {
		
	}
}