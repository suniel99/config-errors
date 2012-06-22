package edu.washington.cs.conf.experiments.jchord;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.diagnosis.CrashingErrorDiagnoser;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.PredicateProfileTuple;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.diagnosis.TraceAnalyzer;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestCrashingErrorDiagnosis extends TestCase {

	public String invalidReflectKind
	    = "./experiments/jchord-crashing-error/chord-crash-invalid-reflect-kind.txt";
	public String invalidReflectKindStackTrace
    = "./experiments/jchord-crashing-error/chord-crash-invalid-reflect-kind-stacktrace.txt";
	
	public String invalidScopeKind
       = "./experiments/jchord-crashing-error/chord-crash-invalid-scope-kind.txt";
	public String invalidScopeKindStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-invalid-scope-kind-stacktrace.txt";
	
	public String noMainMethod
       = "./experiments/jchord-crashing-error/chord-crash-no-main-method.txt";
	public String noMainMethodStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-no-main-method-stacktrace.txt";
	
	public String noSuchAnalysis
       = "./experiments/jchord-crashing-error/chord-crash-no-such-analyses.txt";
	public String noSuchAnalysisStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-no-such-analyses-stacktrace.txt";
	
	public String printInvalidRels
       = "./experiments/jchord-crashing-error/chord-crash-print-invalid-rels.txt";
	public String printInvalidRelsStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-print-invalid-rels-stacktrace.txt";
	
	public String wrongClassPath
       = "./experiments/jchord-crashing-error/chord-crash-wrong-class-path.txt";
	public String wrongClassPathStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-wrong-class-path-stacktrace.txt";
	
	//the good run
	public String goodRunTrace
	   = "./experiments/jchord-database/simpletest-has-race.txt";
	
	public void testCalcDistances() {
		CommonUtils.compareTraceDistance(goodRunTrace, invalidReflectKind, DistanceType.INTERPRODUCT, 0.9998555f);
		CommonUtils.compareTraceDistance(goodRunTrace, invalidScopeKind, DistanceType.INTERPRODUCT, 0.6472167f);
		CommonUtils.compareTraceDistance(goodRunTrace, noMainMethod, DistanceType.INTERPRODUCT, 0.6461625f);
		CommonUtils.compareTraceDistance(goodRunTrace, noSuchAnalysis, DistanceType.INTERPRODUCT, 0.6580911f);
		CommonUtils.compareTraceDistance(goodRunTrace, printInvalidRels, DistanceType.INTERPRODUCT, 0.10745859f);
		CommonUtils.compareTraceDistance(goodRunTrace, wrongClassPath, DistanceType.INTERPRODUCT, 0.6448741f);
	}
	
	//rank 18 reflectKind
	public void testUsingNonCrashingDiagnosis1() {
		List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.NONCRASHING, invalidReflectKind, invalidReflectKindStackTrace,
				new String[]{goodRunTrace});
		dumpOutputs(results);
	}
	
	//rank 39 scopeKind
    public void testUsingNonCrashingDiagnosis2() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.NONCRASHING, invalidScopeKind, invalidScopeKindStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //rank 23 for mainClassName
    public void testUsingNonCrashingDiagnosis3() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.NONCRASHING, noMainMethod, noMainMethodStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //rank 10, runAnalyses
    public void testUsingNonCrashingDiagnosis4() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.NONCRASHING, noSuchAnalysis, noSuchAnalysisStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //rank 10 printRels
    public void testUsingNonCrashingDiagnosis5() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.NONCRASHING, printInvalidRels, printInvalidRelsStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //rank 22 userClassPathName
    public void testUsingNonCrashingDiagnosis6() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.NONCRASHING, wrongClassPath, wrongClassPathStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    enum DiagnosisType {NONCRASHING, CRASHING, STACKTRACE}
    
    List<ConfDiagnosisOutput>  doDiagnosis(DiagnosisType t, String badTrace, String badStackTrace, String[] goodTraces) {
    	//create the repo
    	List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		ConfEntityRepository repo = new ConfEntityRepository(jchordConfList);
		
		//create profile tuples
    	PredicateProfileTuple badTuple = TraceAnalyzer.createBadProfileTuple(badTrace, "badTrace1");
    	List<PredicateProfileTuple> goodTuples = new LinkedList<PredicateProfileTuple>();
    	for(String goodTrace : goodTraces) {
    		goodTuples.add(TraceAnalyzer.createGoodProfileTuple(goodTrace, "goodTrace"));
    	}
    	
    	//start the diagnosis
    	CrashingErrorDiagnoser diagnoser = new CrashingErrorDiagnoser(goodTuples, badTuple, repo);
    	diagnoser.setStackTraces(badStackTrace);

    	if(t.equals(DiagnosisType.NONCRASHING)) {
    		return diagnoser.computeResponsibleOptionsAsNonCrashingErrors();
    	} else if (t.equals(DiagnosisType.CRASHING)) {
    		return diagnoser.computeResponsibleOptionsInCrashingTrace();
    	} else if (t.equals(DiagnosisType.STACKTRACE)) {
    		return diagnoser.computeResponsibleOptionsWithStackTrace();
    	} else {
    		throw new Error();
    	}
    }
    
    void dumpOutputs(List<ConfDiagnosisOutput> results) {
    	System.out.println("Number: " + results.size());
    	for(int i = 0; i < results.size(); i++) {
    		ConfDiagnosisOutput o = results.get(i);
    		System.out.println(i + ". " + o.getConfEntity());
    		System.out.println("  " + o.getBriefExplanation());
    	}
    }
    
    /**
     * Focus on the wrong trace only
     * */
    //18, reflectKind
    public void testDiagnoseWithCrashingTrace1() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.CRASHING, invalidReflectKind, invalidReflectKindStackTrace,
    			new String[]{goodRunTrace});
		dumpOutputs(results);
	}
	
    //1, scopeKind
    public void testDiagnoseWithCrashingTrace2() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.CRASHING, invalidScopeKind, invalidScopeKindStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //23, mainClassName
    public void testDiagnoseWithCrashingTrace3() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.CRASHING, noMainMethod, noMainMethodStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //10 runAnalyses
    public void testDiagnoseWithCrashingTrace4() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.CRASHING, noSuchAnalysis, noSuchAnalysisStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //8, printRels
    public void testDiagnoseWithCrashingTrace5() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.CRASHING, printInvalidRels, printInvalidRelsStackTrace, 
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //22 userClassPathName
    public void testDiagnoseWithCrashingTrace6() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.CRASHING, wrongClassPath, wrongClassPathStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    /**
     * Use the stack trace
     * */
    //N/A reflectKind
    public void testDiagnoseWithCrashingStackTrace1() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.STACKTRACE, invalidReflectKind, invalidReflectKindStackTrace,
    			new String[]{goodRunTrace});
		dumpOutputs(results);
	}
	
    //31, scopeKind
    public void testDiagnoseWithCrashingStackTrace2() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.STACKTRACE, invalidScopeKind, invalidScopeKindStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //mainClassName
    public void testDiagnoseWithCrashingStackTrace3() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.STACKTRACE, noMainMethod, noMainMethodStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //13 runAnalyses
    public void testDiagnoseWithCrashingStackTrace4() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.STACKTRACE, noSuchAnalysis, noSuchAnalysisStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //12 printRels
    public void testDiagnoseWithCrashingStackTrace5() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.STACKTRACE, printInvalidRels, printInvalidRelsStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //25 userClassPathName
    public void testDiagnoseWithCrashingStackTrace6() {
    	List<ConfDiagnosisOutput> results = this.doDiagnosis(DiagnosisType.STACKTRACE, wrongClassPath, wrongClassPathStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
}