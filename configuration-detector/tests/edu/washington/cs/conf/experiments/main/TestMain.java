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
	
}