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
		this.doInstrumentation(inputJar, outputJar, false, false);
	}
	
	public void testInstrumentJMeter29() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\original\\ApacheJMeter_core.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_core-instrumetned.jar";
		this.doInstrumentation(inputJar, outputJar, false, false);
		InstrumentStats.writeInstrumentedPositions("./tmp-output-folder/inst_points.txt");
	}
	
	public void testInstrumentRandoop1_2_1() {
//		useSigMap = true;
		String inputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("randoop."), false, false);
	}
	
	public void testInstrumentRandoop1_3_2() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("randoop."), false, false);
	}
	
	public void testSynoptic0_5() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\synoptic.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\synoptic-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("synoptic."), false, false);
	}
	
	public void testSynoptic1_0() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\synoptic.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\synoptic-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("synoptic."), false, false);
	}
	
	public void testWeka_3_6_1() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-1\\weka-3-6-1\\weka.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-1\\weka-3-6-1\\weka-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("weka."), false, false);
	}
	
	public void testWeka_3_6_2() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-2\\weka-3-6-2\\weka.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-2\\weka-3-6-2\\weka-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("weka."), false, false);
	}
	
	public void testJChord2_0() {
		String inputJar = "D:\\research\\configurations\\workspace\\chord-2.0\\chord.jar";
		String outputJar = "D:\\research\\configurations\\workspace\\chord-2.0\\chord-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("chord."), false, false);
	}
	
    public void testJChord2_1() {
    	String inputJar = "D:\\research\\configurations\\workspace\\chord-2.1\\chord.jar";
		String outputJar = "D:\\research\\configurations\\workspace\\chord-2.1\\chord-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("chord."), false, false);
	}
    
    public void testJChord_trunk() {
    	String inputJar = "D:\\research\\configurations\\workspace\\chord-trunk\\chord.jar";
		String outputJar = "D:\\research\\configurations\\workspace\\chord-trunk\\chord-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("chord."), false, false);
	}

    private void doInstrumentation(String input, String output, Collection<String> appPkgs,
    		boolean disasm, boolean useSigMap) {
		PredicateInstrumenter instrumenter = new PredicateInstrumenter(appPkgs, Collections.<String> emptyList());
		instrumenter.setDisasm(disasm);
		instrumenter.setUseSigMap(useSigMap);
		try {
		    instrumenter.instrument(input, output);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if(useSigMap) {
			instrumenter.saveSigMappings();
		}
		InstrumentStats.showInstrumentationStats();
	}
    
	private void doInstrumentation(String input, String output, boolean disasm, boolean useSigMap) {
		this.doInstrumentation(input, output, Collections.<String> emptyList(), disasm, useSigMap);
	}
}
