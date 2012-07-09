package edu.washington.cs.conf.experiments.jchord;

import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.jchord.TestCrashingErrorDiagnosis.DiagnosisType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import junit.framework.TestCase;

public class TestCrashingErrorDiagnosisFullSlice extends TestCase {

	String invalidCtxtKind = "./experiments/jchord-crashing-error-full-slice/chord-crash-invalid-ctxt-kind-full-slice.txt";
	String invalidReflectKind = "./experiments/jchord-crashing-error-full-slice/chord-crash-invalid-reflect-kind-full-slice.txt";
	String invalidScopeKind = "./experiments/jchord-crashing-error-full-slice/chord-crash-invalid-scope-kind-full-slice.txt";
	String noMainMethod = "./experiments/jchord-crashing-error-full-slice/chord-crash-no-main-method-full-slice.txt";
	String noMainInClass = "./experiments/jchord-crashing-error-full-slice/chord-crash-no-main-method-in-class-full-slice.txt";
	String noPrintRels = "./experiments/jchord-crashing-error-full-slice/chord-crash-no-print-rels-full-slice.txt";
	String noSuchAnalysis = "./experiments/jchord-crashing-error-full-slice/chord-crash-no-such-analysis-full-slice.txt";
	String printNonexist = "./experiments/jchord-crashing-error-full-slice/chord-crash-print-nonexist-class-full-slice.txt";
	String wrongClasspath = "./experiments/jchord-crashing-error-full-slice/chord-crash-wrong-classpath-full-slice.txt";
	
	
	
	String ctxtAnalysis = "./experiments/jchord-database/simpletest-ctxt-analysis-full-slice.txt";
	String datarace = "./experiments/jchord-database/simpletest-has-race-full-slice.txt";
	String deadlock = "./experiments/jchord-database/simpletest-deadlock-full-slice.txt";
	String dlog = "./experiments/jchord-database/simpletest-dlog-full-slice.txt";
	String printproject = "./experiments/jchord-database/simpletest-print-project-full-slice.txt";
	String donothing = "./experiments/jchord-database/simpletest-do-nothing-full-slice.txt";
	
	String[] db = new String[]{ctxtAnalysis, datarace, deadlock,
			dlog, printproject, donothing};
	
	static Collection<ConfPropOutput> slices = TestSliceJChordConfigOptions.sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
	
	void diagnoseCauses(String badTraceFile, String stackTraceFile, String[] goodTraceDb) {
		List<ConfDiagnosisOutput> results = TestCrashingErrorDiagnosis.doDiagnosis(DiagnosisType.CRASHING, badTraceFile, stackTraceFile,
				goodTraceDb);
		List<ConfDiagnosisOutput> outputs = TestCrashingErrorDiagnosis.rankByStackTraceCoverage(stackTraceFile, results, slices); //make it number 1
		StringBuilder sb = new StringBuilder();
		
		int i = 1; 
		for(ConfDiagnosisOutput o : outputs) {
			sb.append(i++ + " " + o.getConfEntity().getFullConfName());
			sb.append(Globals.lineSep);
    	}
	
	    String outputFile = badTraceFile + "_result.txt";
		try  {
		    Files.createIfNotExist(outputFile);
		    Files.writeToFile(sb.toString(), outputFile);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void testRunALl() {
		testInvalidCtxtKind();
		testInvalidReflectKind ();
		testInvalidScopeKind();
		testNoMainMethod();
		testNoMainInClass();
		testNoPrintRels ();
		testNoSuchAnalysis();
		testPrintNonexist();
		testWrongClasspath();
	}
	
	//individual tests
	public void testInvalidCtxtKind() {
		diagnoseCauses(invalidCtxtKind, TestCrashingErrorDiagnosis.noCtxtKindStackTrace, db);
	}
	public void testInvalidReflectKind () {
		diagnoseCauses(invalidReflectKind, TestCrashingErrorDiagnosis.invalidReflectKindStackTrace, db);
	}
	public void testInvalidScopeKind() {
		diagnoseCauses(invalidScopeKind, TestCrashingErrorDiagnosis.invalidScopeKindStackTrace, db);
	}
	public void testNoMainMethod() {
		diagnoseCauses(noMainMethod, TestCrashingErrorDiagnosis.noMainClassStackTrace, db);
	}
	public void testNoMainInClass() {
		diagnoseCauses(noMainInClass, TestCrashingErrorDiagnosis.noMainMethodInClassStackTrace, db);
	}
	public void testNoPrintRels () {
		diagnoseCauses(noPrintRels, TestCrashingErrorDiagnosis.printInvalidRelsStackTrace, db);
	}
	public void testNoSuchAnalysis() {
		diagnoseCauses(noSuchAnalysis, TestCrashingErrorDiagnosis.noSuchAnalysisStackTrace, db);
	}
	public void testPrintNonexist() {
		diagnoseCauses(printNonexist, TestCrashingErrorDiagnosis.printNoClassStackTrace, db);
	}
	public void testWrongClasspath() {
		diagnoseCauses(wrongClasspath, TestCrashingErrorDiagnosis.wrongClassPathStackTrace, db);
	}
}
