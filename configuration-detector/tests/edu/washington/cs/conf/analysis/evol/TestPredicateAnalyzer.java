package edu.washington.cs.conf.analysis.evol;

import java.util.Map;

import edu.washington.cs.conf.util.Utils;

import plume.Pair;
import junit.framework.TestCase;

/**
 * Check the predicate behaviors
 * */
public class TestPredicateAnalyzer extends TestCase {

	public void testRandoop() {
		MethodMatcher.debug = false;
		
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldAnalyzer.buildAnalysis();
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getRandoop132Analyzer();
		newAnalyzer.buildAnalysis();
		String oldTraceFile = "./evol-experiments/randoop/randoop-1.2.1.txt";
		String newTraceFile = "./evol-experiments/randoop/randoop-1.3.2.txt";
		AnalysisScope scope = AnalysisScopeRepository.createRandoopScore();
		AnalysisCache cache = AnalysisCache.createCache(oldAnalyzer, newAnalyzer, scope);
		
		//analyze the predicate
		PredicateExecInfoAnalyzer analyzer
		    = new PredicateExecInfoAnalyzer(oldAnalyzer, newAnalyzer, scope, cache,
				oldTraceFile, newTraceFile);
		
		//only exact matching
//		MethodMatchingLogics.USE_FUZZING_MATCHING = false;
		
		//find the deviation pairs
		Map<Pair<PredicateExecInfo, PredicateExecInfo>, Float> predicatePairs
		    = analyzer.findBehaviorDeviatedPredicatePairs();
		
		predicatePairs = Utils.sortByValue(predicatePairs, false);
		
		for(Pair<PredicateExecInfo, PredicateExecInfo> p : predicatePairs.keySet()) {
			PredicateExecInfo oldP = p.a;
			PredicateExecInfo newP = p.b;
			System.out.println(oldP + " ==> " + newP);
			System.out.println("   " + predicatePairs.get(p));
			
			//only for experiment
//			p.a.showContext(oldAnalyzer.getCallGraph());
//			p.b.showContext(newAnalyzer.getCallGraph());
//			System.out.println("---------------------");
			
//			for(Pair<PredicateExecInfo, PredicateExecInfo> pair : predicatePairs.keySet()) {
//				if(pair.a == p.a) {
//					System.out.println("=== " + pair.b);
//				}
//			}
//			
//			break;
		}
 	}
	
	public void tearDown() {
		MethodMatchingLogics.USE_FUZZING_MATCHING = true;
		MethodMatcher.debug = true;
	}
}
