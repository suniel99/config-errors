package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.util.Utils;

import junit.framework.TestCase;

public class TestParseExecInstructionInfo extends TestCase {

	public void testParseRandoop_predicate() {
		Collection<PredicateExecInfo>  oldPredExecs
           = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.randoopOldPredicateDump,
        		TraceRepository.randoopOldSig);
	    Collection<PredicateExecInfo> newPredExecs
	       = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.randoopNewPredicateDump,
	    		TraceRepository.randoopNewSig);
	    Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
	    Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
	    System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
//	    System.out.println(oldUnmatchedMethods);
	    System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
//	    System.out.println(newUnmatchedMethods);
	    
	    Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs);
		System.out.println("Number of matched preds: " + matchedPreds.size());
	}
	
	public void testParseRandoop_trace() {
		List<InstructionExecInfo> oldInstructions
		    = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.randoopOldHistoryDump,
		    		TraceRepository.randoopOldSig);
		List<InstructionExecInfo> newInstructions
	        = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.randoopNewHistoryDump,
	        		TraceRepository.randoopNewSig);
		System.out.println("old instruction num: " + oldInstructions.size());
		System.out.println("new instruction num: " + newInstructions.size());
	}
	
	
	public void testCheckRandoop_predicate_in_old_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.randoopOldHistoryDump, 
				TraceRepository.randoopOldSig, TraceRepository.randoopOldPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = oldTrace.getImmediatePostDominator(oldCoder, pred);
			if(postDomExec == null) {
				continue;
			}
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
	}
	
	public void testCheckRandoop_predicate_in_new_trace() {
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getRandoop132Analyzer();
		newCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.randoopNewHistoryDump, 
				TraceRepository.randoopNewSig, TraceRepository.randoopNewPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in new trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = oldTrace.getImmediatePostDominator(newCoder, pred);
			if(postDomExec == null) {
				continue;
			}
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
	}

	//---------------------------
	
	public void testParseSynoptic_predicate() {
		Collection<PredicateExecInfo>  oldPredExecs
	        = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.synopticOldPredicateDump,
	        		TraceRepository.synopticOldSig);
		Collection<PredicateExecInfo> newPredExecs
		    = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.synopticNewPredicateDump,
		    		TraceRepository.synopticNewSig);
		Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
//		System.out.println(oldUnmatchedMethods);
		System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
//		System.out.println(newUnmatchedMethods);
		
		Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs);
		System.out.println("Number of matched preds: " + matchedPreds.size());
	}
	
	public void testParseSynoptic_trace() {
		List<InstructionExecInfo> oldInstructions
		    = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.synopticOldHistoryDump,
		    		TraceRepository.synopticOldSig);
		List<InstructionExecInfo> newInstructions
	        = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.synopticNewHistoryDump,
	        		TraceRepository.synopticNewSig);
		System.out.println("old instruction num: " + oldInstructions.size());
		System.out.println("new instruction num: " + newInstructions.size());
	}
	
	public void testCheckSynoptic_predicate_in_old_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		oldCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.synopticOldHistoryDump, 
				TraceRepository.synopticOldSig, TraceRepository.synopticOldPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = oldTrace.getImmediatePostDominator(oldCoder, pred);
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
	}
	
	
	public void testCheckSynoptic_predicate_in_new_trace() {
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		newCoder.buildAnalysis();
		//new trace
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.synopticNewHistoryDump, 
				TraceRepository.synopticNewSig, TraceRepository.synopticNewPredicateDump);
		Set<PredicateExecInfo> predSet = newTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = newTrace.getImmediatePostDominator(newCoder, pred);
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
	}
	
	//----------------------------------
	
	public void testParseWeka_predicate() {
		Collection<PredicateExecInfo>  oldPredExecs
          = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.wekaOldPredicateDump,
		    TraceRepository.wekaOldSig);
		System.out.print("read old predicates: " + oldPredExecs.size());
        Collection<PredicateExecInfo> newPredExecs
          = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.wekaNewPredicateDump,
		    TraceRepository.wekaNewSig);
        Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
        Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
        System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
        System.out.println(oldUnmatchedMethods);
        System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
        System.out.println(newUnmatchedMethods);
        
        Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs);
		System.out.println("Number of matched preds: " + matchedPreds.size());
	}
	
	public void testParseWeka_trace() {
		List<InstructionExecInfo> oldInstructions
		    = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.wekaOldHistoryDump,
		    		TraceRepository.wekaOldSig);
		List<InstructionExecInfo> newInstructions
	        = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.wekaNewHistoryDump,
	        		TraceRepository.wekaNewSig);
		System.out.println("old instruction num: " + oldInstructions.size());
		System.out.println("new instruction num: " + newInstructions.size());
	}
	
	public void testCheckWeka_predicate_in_old_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getWekaOldAnalyzer();
		oldCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.wekaOldHistoryDump, 
				TraceRepository.wekaOldSig, TraceRepository.wekaOldPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = oldTrace.getImmediatePostDominator(oldCoder, pred);
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
	}
	
	public void testCheckWeka_predicate_in_new_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getWekaNewAnalyzer();
		oldCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.wekaNewHistoryDump, 
				TraceRepository.wekaNewSig, TraceRepository.wekaNewPredicateDump);
		Set<PredicateExecInfo> predSet = newTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in new trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = newTrace.getImmediatePostDominator(oldCoder, pred);
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
	}
	
	//--------------------------------------
	
	//need to analyze different behaviors of the predicate
	public void testParseJMeter_predicate() {
		Collection<PredicateExecInfo>  oldPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.jmeterOldPredicateDump,
    		    TraceRepository.jmeterOldSig);
        Collection<PredicateExecInfo> newPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.jmeterNewPredicateDump,
    		    TraceRepository.jmeterNewSig);
        Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
	    Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
	    System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
	    System.out.println(oldUnmatchedMethods);
	    System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
        System.out.println(newUnmatchedMethods);
        
        Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		

		Set<PredicateBehaviorAcrossVersions> matchedPreds = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs);
		System.out.println("Number of matched preds: " + matchedPreds.size());
	}
	
	public void testParseJMeter_trace() {
		List<InstructionExecInfo> oldInstructions
		    = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.jmeterOldHistoryDump,
		    		TraceRepository.jmeterOldSig);
		List<InstructionExecInfo> newInstructions
	        = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.jmeterNewHistoryDump,
	        		TraceRepository.jmeterNewSig);
		System.out.println("old instruction num: " + oldInstructions.size());
		System.out.println("new instruction num: " + newInstructions.size());
		
	}
	
	public void testCheckJMeter_predicate_in_old_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		oldCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.jmeterOldHistoryDump, 
				TraceRepository.jmeterOldSig, TraceRepository.jmeterOldPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		List<String> missingSSAs = new LinkedList<String>();
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = oldTrace.getImmediatePostDominator(oldCoder, pred);
			if(postDomExec == null) {
				missingSSAs.add(pred.toString());
				continue;
			}
			if(pred.getIndex() == postDomExec.getIndex()) {
				System.out.println("The pred dom: " + pred + ", post dom: " + postDomExec);
				Utils.fail("");
			}
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
		System.out.println("------------");
		System.out.println("The missiing predicate number: " + missingSSAs.size());
		System.out.println("They are: ");
		Utils.dumpCollection(missingSSAs, System.out);
	}
	
	public void testCheckJMeter_predicate_in_new_trace() {
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJMeterNewAnalyzer();
		newCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.jmeterNewHistoryDump, 
				TraceRepository.jmeterNewSig, TraceRepository.jmeterNewPredicateDump);
		Set<PredicateExecInfo> predSet = newTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in new trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		List<String> missingSSAs = new LinkedList<String>();
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = newTrace.getImmediatePostDominator(newCoder, pred);
			if(postDomExec == null) {
				missingSSAs.add(pred.toString());
				continue;
			}
			if(pred.getIndex() == postDomExec.getIndex()) {
				System.out.println("The pred dom: " + pred + ", post dom: " + postDomExec);
				Utils.fail("");
			}
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
		System.out.println("------------");
		System.out.println("The missiing predicate number: " + missingSSAs.size());
		System.out.println("They are: ");
		Utils.dumpCollection(missingSSAs, System.out);
	}
}