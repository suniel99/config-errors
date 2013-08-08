package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

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
		
	    //XXX for each unmatched predicate, find its corresponding one in the new
		//version
		Set<PredicateExecInfo> onlyInOldPredicates = TraceComparator.mins(oldPredExecs, newPredExecs);
		System.out.println("=====================");
		System.out.println("== total num in old: " + onlyInOldPredicates.size());
		Set<PredicateExecInfo> onlyInNewPredicates = TraceComparator.mins(newPredExecs, oldPredExecs);
		System.out.println("== total num in new: " + onlyInNewPredicates.size());
		
        //the matched predicates
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getRandoop132Analyzer();
		newCoder.buildAnalysis();
		Set<PredicateBehaviorAcrossVersions> matchedPreds
		    = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, oldCoder, newCoder);
		System.out.println("Number of matched preds: " + matchedPreds.size());
		
		//check the change degrees
		System.out.println("-------------");
		//check the trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.randoopOldHistoryDump, 
				TraceRepository.randoopOldSig, TraceRepository.randoopOldPredicateDump);
		
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.randoopNewHistoryDump, 
				TraceRepository.randoopNewSig, TraceRepository.randoopNewPredicateDump);
		
		matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
		for(PredicateBehaviorAcrossVersions predBehavior : matchedPreds) {
			float degree = predBehavior.getDifferenceDegree();
			if(degree < 0.1f) {
				continue;
			}
			System.out.println(predBehavior);
			System.out.println("      diff: " + degree);
			if(predBehavior.isExecutedOnOldVersion()) {
				Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, predBehavior.createOldPredicateExecInfo());
				System.out.println("     executed: " + set.size());
			} else {
				System.out.println("     not executed on old version.");
			}
			
			if(predBehavior.isExecutedOnNewVersion()) {
				Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, predBehavior.createNewPredicateExecInfo());
				System.out.println("    executed: " + set.size());
			} else {
				System.out.println("     not executed on new version");
			}
			
			System.out.println();
		}
	}
	
	public void testParseRandoop_predicate_in_trace() {
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
		
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		oldCoder.buildAnalysis();
		newCoder.buildAnalysis();
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs,
				oldCoder, newCoder);
		
		System.out.println("Number of matched preds: " + matchedPreds.size());
		matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
		
		//check the trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.synopticOldHistoryDump, 
				TraceRepository.synopticOldSig, TraceRepository.synopticOldPredicateDump);
		
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.synopticNewHistoryDump, 
				TraceRepository.synopticNewSig, TraceRepository.synopticNewPredicateDump);
		
		for(PredicateBehaviorAcrossVersions pred : matchedPreds) {
			if(pred.isBehaviorChanged()) {
//				if(pred.getDifferenceDegree() < 0.1f) {
//					continue;
//				}
				System.out.println("  " + pred);
//				System.out.println("       " + pred.compareBehaviors());
				System.out.println("        behavior diff: " + pred.getDifferenceDegree());
				
				if(pred.isExecutedOnOldVersion()) {
					Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, pred.createOldPredicateExecInfo());
					System.out.println("     executed: " + set.size());
				} else {
					System.out.println("     not executed on old version.");
				}
				
				if(pred.isExecutedOnNewVersion()) {
					Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, pred.createNewPredicateExecInfo());
					System.out.println("    executed: " + set.size());
				} else {
					System.out.println("     not executed on new version");
				}
				
				System.out.println();
			}
		}
		
		
		
	}
	
	public void testParseSynoptic_predicate_in_trace() {
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
		
		ExecutionTrace.enable_cache_trace = false;
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
			
			//see the executed instructions
			Set<InstructionExecInfo> set =
				oldTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
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
			
			//see the executed instructions
			Set<InstructionExecInfo> set =
				newTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
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
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, null, null);
		System.out.println("Number of matched preds: " + matchedPreds.size());
		
		//see all behaviorally different
		int num = 0;
		for(PredicateBehaviorAcrossVersions pred : matchedPreds) {
			if(pred.isBehaviorChanged()) {
				System.out.println("   " + pred);
				System.out.println();
				num++;
			}
		}
		assertEquals(num, 1);
	}
	
	public void testParseWeka_predicate_in_trace() {
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
			
			//find all instructions between
			Set<InstructionExecInfo> set =
				oldTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
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
			
			//find all instructions between
			Set<InstructionExecInfo> set =
				newTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
		}
	}
	
	//--------------------------------------
	//need to check which predicate behaves differently than exepcted
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
		
		//see the deviated behaviors
		CodeAnalyzer oldCoder = null;
		CodeAnalyzer newCoder = null;
		
		oldCoder = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		oldCoder.buildAnalysis();
		newCoder = CodeAnalyzerRepository.getJMeterNewAnalyzer();
		newCoder.buildAnalysis();
		
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.jmeterOldHistoryDump, 
				TraceRepository.jmeterOldSig, TraceRepository.jmeterOldPredicateDump);
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.jmeterNewHistoryDump, 
				TraceRepository.jmeterNewSig, TraceRepository.jmeterNewPredicateDump);

		Set<PredicateBehaviorAcrossVersions> matchedPreds
		    = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, oldCoder, newCoder);
		System.out.println("Number of matched preds: " + matchedPreds.size());
		
		matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
		for(PredicateBehaviorAcrossVersions predBehavior : matchedPreds) {
			float degree = predBehavior.getDifferenceDegree();
			if(degree < 0.1f) {
				continue;
			}
			System.out.println(predBehavior);
			System.out.println("      diff: " + degree);
			if(predBehavior.isExecutedOnOldVersion()) {
				Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, predBehavior.createOldPredicateExecInfo());
				System.out.println("     executed: " + set.size());
			} else {
				System.out.println("     not executed on old version.");
			}
			
			if(predBehavior.isExecutedOnNewVersion()) {
				Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, predBehavior.createNewPredicateExecInfo());
				System.out.println("    executed: " + set.size());
			} else {
				System.out.println("     not executed on new version");
			}
			
			System.out.println();
		}
	}
	
	public void testParseJMeter_predicate_in_trace() {
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
			
			//find all instructions between
			Set<InstructionExecInfo> set =
				oldTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
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
			
			Set<InstructionExecInfo> set =
				newTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
		}
		System.out.println("------------");
		System.out.println("The missiing predicate number: " + missingSSAs.size());
		System.out.println("They are: ");
		Utils.dumpCollection(missingSSAs, System.out);
	}
	
	//----------------see file size
	public void testFileSize_for_experiment() {
		new ExecutionTrace(TraceRepository.randoopOldHistoryDump,
				TraceRepository.randoopOldSig, TraceRepository.randoopOldPredicateDump);
		new ExecutionTrace(TraceRepository.randoopNewHistoryDump,
				TraceRepository.randoopNewSig, TraceRepository.randoopNewPredicateDump);
		new ExecutionTrace(TraceRepository.synopticOldHistoryDump,
				TraceRepository.synopticOldSig, TraceRepository.synopticOldPredicateDump);
		new ExecutionTrace(TraceRepository.synopticNewHistoryDump,
				TraceRepository.synopticNewSig, TraceRepository.synopticNewPredicateDump);
		new ExecutionTrace(TraceRepository.wekaOldHistoryDump,
				TraceRepository.wekaOldSig, TraceRepository.wekaOldPredicateDump);
		new ExecutionTrace(TraceRepository.wekaNewHistoryDump,
				TraceRepository.wekaNewSig, TraceRepository.wekaNewPredicateDump);
		new ExecutionTrace(TraceRepository.jmeterOldHistoryDump,
				TraceRepository.jmeterOldSig, TraceRepository.jmeterOldPredicateDump);
		new ExecutionTrace(TraceRepository.jmeterNewHistoryDump,
				TraceRepository.jmeterNewSig, TraceRepository.jmeterNewPredicateDump);
	}
}