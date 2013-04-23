package edu.washington.cs.conf.instrument.evol;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestInstrumentPrograms extends TestCase {

	public void testInstrumentJMeter28() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\original\\ApacheJMeter_core.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_core-instrumetned.jar";
		this.doInstrumentation(inputJar, outputJar, true);
	}
	
	public void testInstrumentJMeter29() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\original\\ApacheJMeter_core.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_core-instrumetned.jar";
		this.doInstrumentation(inputJar, outputJar, true);
		InstrumentStats.writeInstrumentedPositions("./tmp-output-folder/inst_points.txt");
	}
	
	public void testInstrumentRandoop1_2_1() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("randoop."), false);
	}
	
	public void testInstrumentRandoop1_3_2() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("randoop."), false);
	}
	
	public void testSynoptic0_5() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\synoptic.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\synoptic-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("synoptic."), false);
	}
	
	public void testSynoptic1_0() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\synoptic.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\synoptic-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("synoptic."), false);
	}
	
	public void testLog4J_1_x() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\log4j\\log4j-1.x-driver\\log4j-1.2.17.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\log4j\\log4j-1.x-driver\\log4j-1.2.17-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("org.apache.log4j"), false);
	}
	
	public void testLog4J2_0() {
		String dir = "D:\\research\\confevol\\subject-programs\\log4j\\log4j-2.0-driver\\";
		String inputJar1 = dir + "log4j-core-2.0-beta4.jar";
		String outputJar1 = dir + "log4j-core-2.0-beta4-instrumented.jar";
		String inputJar2 = dir + "log4j-api-2.0-beta4.jar";
		String outputJar2 = dir + "log4j-api-2.0-beta4-instrumented.jar";
		this.doInstrumentation(inputJar1, outputJar1, Arrays.asList("org.apache.logging.log4j"), false);
		this.doInstrumentation(inputJar2, outputJar2, Arrays.asList("org.apache.logging.log4j"), false);
	}
	
	public void testWeka_3_6_1() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-1\\weka-3-6-1\\weka.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-1\\weka-3-6-1\\weka-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("weka."), false);
	}
	
	public void testWeka_3_6_2() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-2\\weka-3-6-2\\weka.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-2\\weka-3-6-2\\weka-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("weka."), false);
	}
	
	public void testJChord2_0() {
		String inputJar = "D:\\research\\configurations\\workspace\\chord-2.0\\chord.jar";
		String outputJar = "D:\\research\\configurations\\workspace\\chord-2.0\\chord-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("chord."), false);
	}
	
    public void testJChord2_1() {
    	String inputJar = "D:\\research\\configurations\\workspace\\chord-2.1\\chord.jar";
		String outputJar = "D:\\research\\configurations\\workspace\\chord-2.1\\chord-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("chord."), false);
	}
    
    public void testJChord_trunk() {
    	String inputJar = "D:\\research\\configurations\\workspace\\chord-trunk\\chord.jar";
		String outputJar = "D:\\research\\configurations\\workspace\\chord-trunk\\chord-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("chord."), false);
	}

    private void doInstrumentation(String input, String output, Collection<String> appPkgs,
    		boolean disasm) {
		PredicateInstrumenter instrumenter = new PredicateInstrumenter(appPkgs, Collections.<String> emptyList());
		instrumenter.setDisasm(disasm);
		try {
		    instrumenter.instrument(input, output);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		InstrumentStats.showInstrumentationStats();
	}
    
	private void doInstrumentation(String input, String output, boolean disasm) {
		this.doInstrumentation(input, output, Collections.<String> emptyList(), disasm);
	}
}
