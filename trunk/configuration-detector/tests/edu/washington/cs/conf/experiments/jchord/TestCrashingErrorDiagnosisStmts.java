package edu.washington.cs.conf.experiments.jchord;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
import junit.framework.TestCase;

public class TestCrashingErrorDiagnosisStmts extends TestCase {

	String ctxtAnalysis = "./experiments/jchord-baseline-stmt/ctxts-analysis-stmt.txt";
	String doNothing = "./experiments/jchord-baseline-stmt/do-nothing-stmt.txt";
	String good_datarace = "./experiments/jchord-baseline-stmt/good_datarace.txt";
	String good_deadlock = "./experiments/jchord-baseline-stmt/good_deadlock.txt";
	String good_dlog = "./experiments/jchord-baseline-stmt/good_dlog.txt";
	String print_project = "./experiments/jchord-baseline-stmt/print-project-stmt.txt";
	
	String[] db = new String[]{ctxtAnalysis, doNothing, good_datarace, good_deadlock, good_dlog, print_project};
	
	//crashing error coverage
	String invalidCtxtKind = "./experiments/jchord-crashing-error-linecoverage/chord-crash-invalid-ctxt-kind-stmt.txt";
	String invalidReflectKind = "./experiments/jchord-crashing-error-linecoverage/chord-crash-invalid-reflect-kind-stmt.txt";
	String invalidScopeKind = "./experiments/jchord-crashing-error-linecoverage/chord-crash-invalid-scope-kind-stmt.txt";
	String noMainMethodInClass = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-main-method-in-class-stmt.txt";
	String noMainMethod = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-main-method-stmt.txt";
	String noPrintRel = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-print-rels-stmt.txt";
	String noSuchAnalysis = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-such-analyses-stmt.txt";
	String printNonexist = "./experiments/jchord-crashing-error-linecoverage/chord-crash-print-nonexist-class-stmt.txt";
	String wrongClasspath = "./experiments/jchord-crashing-error-linecoverage/chord-crash-wrong-classpath-stmt.txt";
	
	static Collection<ConfPropOutput> outputs = TestSliceJChordConfigOptions.getJChordConfOutputs();
	
	void diagnoseByStmt(String stmtFile) {
		String outputFile = stmtFile + "_result.txt";
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, new String[]{stmtFile}, db,
				outputFile, new String[]{"chord."});
	}
	
	public void testAll() {
		testInvalidCtxtKind();
		testInvalidReflectKind();
		testInvalidScopeKind();
		testNoMainMethodInClass();
		testNoMainMethod();
		testNoPrintRel();
		testPrintNonexist();
		testNoSuchAnalysis();
		testWrongClasspath();
	}
	
	public void testInvalidCtxtKind() {
		this.diagnoseByStmt(invalidCtxtKind);
	}
	
	public void testInvalidReflectKind() {
		this.diagnoseByStmt(invalidReflectKind);
	}
	
	public void testInvalidScopeKind() {
		this.diagnoseByStmt(invalidScopeKind);
	}
	
	public void testNoMainMethodInClass() {
		this.diagnoseByStmt(noMainMethodInClass);
	}
	
    public void testNoMainMethod() {
    	this.diagnoseByStmt(noMainMethod);
	}
    
    public void testNoPrintRel() {
    	this.diagnoseByStmt(noPrintRel);
	}
    
    public void testPrintNonexist() {
    	this.diagnoseByStmt(printNonexist);
	}
    
    public void testNoSuchAnalysis() {
    	this.diagnoseByStmt(noSuchAnalysis);
	}
    
    
    public void testWrongClasspath() {
    	this.diagnoseByStmt(wrongClasspath);
	}
}

