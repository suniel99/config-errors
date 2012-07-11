package edu.washington.cs.conf.experiments.jchord;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.diagnosis.CrashingErrorDiagnoser;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.PredicateProfileTuple;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.jchord.TestCrashingErrorDiagnosisExperimental.DiagnosisType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class TestCrashingErrorDiagnosisThinSlice extends TestCase {

	boolean similarSelection = false;
	
	boolean randomSelection = false;
	
	public void testAll() {
		testInvalidCtxtKind();
		testInvalidReflectKind();
		testInvalidScopeKind();
		testNoMainMethod();
		testNoMainInClass();
		testNoPrintRels ();
		testNoSuchAnalysis();
		testPrintNonexist();
		testWrongClasspath();
	}
	
	public void testRunAll() {
		testAll();
	}
	
	public void testRunSimilar() {
		similarSelection = true;
		testAll();
	}
	
	/***
	 * The above code is for experimental purpose
	 * all shoudl be ranked number 1
	 * */
	public void testTuned() {
		testReflectKindSimilar();
		testScopeKindSimilar();
		testContextKindSimilar();
		testNoMainMethodInClassSimilar();
	}
	
	//the only covered by the stack
	public void testReflectKindSimilar() {
		similarSelection = true;
		testInvalidReflectKind();
	}
	
	//just use the final score
	public void testScopeKindSimilar() {
		similarSelection = true;
		testInvalidScopeKind();
	}
	
	//ranked number 1
	public void testContextKindSimilar() {
		similarSelection = true;
		testInvalidCtxtKind();
	}
	
	//seems to be hopeless
	public void testNoMainMethodSimilar() {
		similarSelection = true;
		testNoMainMethod();
	}
	
	public void testNoMainMethodInClassSimilar() {
		similarSelection = true;
		testNoMainInClass();
	}
	
	public void testClassPathSimilar() {
		similarSelection = true;
		testWrongClasspath();
	}
	
	public void testRunRandom() {
		randomSelection = true;
		testAll();
	}
	
	public String[] getDb() {
		String[] db = TestCrashingErrorDiagnosisExperimental.goodRunDb;;
		if(randomSelection) {
			Object[] randomArray = Utils.randomSubArray(db);
			String[] array = new String[randomArray.length];
			for(int i = 0 ; i < randomArray.length; i++) {
				array[i] = randomArray[i].toString();
			}
			return array;
		}
		return db;
	}
	
	static Collection<ConfPropOutput> slices = null;
	
	void diagnoseCauses(String badTraceFile, String stackTraceFile, String[] goodTraceDb) {
		
		System.out.println("Start diagnosing...");
		float threshold = similarSelection ? MainAnalyzer.default_threshold : CrashingErrorDiagnoser.default_experiment_value;
		List<ConfDiagnosisOutput> results = TestCrashingErrorDiagnosisExperimental.doDiagnosis(DiagnosisType.CRASHING, badTraceFile, stackTraceFile,
				goodTraceDb,  threshold);
		
		//if thre is a tie, use stack trace distance for comparison
		System.out.println("Start to rank by stack trace coverage...");
		if(slices == null) {
			System.out.println("Compute slice...");
			//thin slicing here
			slices = TestSliceJChordConfigOptions.sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
		}
		results = TestCrashingErrorDiagnosisExperimental.rankByStackTraceCoverage(stackTraceFile, results, slices); //make it number 1
		
		//dump out the results
		StringBuilder sb = new StringBuilder();
		int i = 1; 
		for(ConfDiagnosisOutput o : results) {
			sb.append(i++ + " " + o.getConfEntity().getFullConfName() + ",   comparison num: " + o.getExplanations().size());
			sb.append(Globals.lineSep);
    	}
	
	    String outputFile = badTraceFile;
	    if(randomSelection) {
	    	outputFile = badTraceFile + "_random";
	    }
	    if(similarSelection) {
	    	outputFile = badTraceFile + "_similar";
	    }
	    outputFile = outputFile + "_result.txt";
		try  {
		    Files.createIfNotExist(outputFile);
		    Files.writeToFile(sb.toString(), outputFile);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		//reclaim memory
		results.clear();
	}
	
	@Override
	public void tearDown() {
		PredicateProfileTuple.USE_CACHE = false;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = false;
	}
	
	//individual tests
	public void testInvalidCtxtKind() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(TestCrashingErrorDiagnosisExperimental.noCtxtKind,
				TestCrashingErrorDiagnosisExperimental.noCtxtKindStackTrace, getDb());
	}
	public void testInvalidReflectKind () {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(TestCrashingErrorDiagnosisExperimental.invalidReflectKind,
				TestCrashingErrorDiagnosisExperimental.invalidReflectKindStackTrace, getDb());
	}
	public void testInvalidScopeKind() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(TestCrashingErrorDiagnosisExperimental.invalidScopeKind,
				TestCrashingErrorDiagnosisExperimental.invalidScopeKindStackTrace, getDb());
	}
	public void testNoMainMethod() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(TestCrashingErrorDiagnosisExperimental.noMainClass,
				TestCrashingErrorDiagnosisExperimental.noMainClassStackTrace, getDb());
	}
	public void testNoMainInClass() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(TestCrashingErrorDiagnosisExperimental.noMainMethodInClass,
				TestCrashingErrorDiagnosisExperimental.noMainMethodInClassStackTrace, getDb());
	}
	public void testNoPrintRels () {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(TestCrashingErrorDiagnosisExperimental.printInvalidRels,
				TestCrashingErrorDiagnosisExperimental.printInvalidRelsStackTrace, getDb());
	}
	public void testNoSuchAnalysis() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(TestCrashingErrorDiagnosisExperimental.noSuchAnalysis,
				TestCrashingErrorDiagnosisExperimental.noSuchAnalysisStackTrace, getDb());
	}
	public void testPrintNonexist() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(TestCrashingErrorDiagnosisExperimental.printNoClass,
				TestCrashingErrorDiagnosisExperimental.printNoClassStackTrace, getDb());
	}
	public void testWrongClasspath() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(TestCrashingErrorDiagnosisExperimental.wrongClassPath,
				TestCrashingErrorDiagnosisExperimental.wrongClassPathStackTrace, getDb());
	}
	
}